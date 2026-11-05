import pathlib,subprocess,sys,tempfile,unittest
SCRIPT=pathlib.Path(__file__).resolve().parents[1]/'scripts/patch_search_client.py'
REL='smali_classes10/com/ss/android/ugc/aweme/search/pages/result/core/repo/SearchApiNew.smali'
SOURCE='''converter_preserved
    invoke-virtual {v3}, LX/04Lh;->build()Lcom/bytedance/ies/ugc/aweme/network/TTRetrofit;
    invoke-interface {v2, v3}, Lcom/bytedance/ies/ugc/aweme/network/IRetrofitFactory;->LIZ(Ljava/lang/String;)LX/04VK;
    .line 1
    move-result-object v0
'''
class ClientTest(unittest.TestCase):
    def run_patch(self,source):
        temp=tempfile.TemporaryDirectory();self.addCleanup(temp.cleanup)
        root=pathlib.Path(temp.name);p=root/REL;p.parent.mkdir(parents=True);p.write_text(source)
        result=subprocess.run([sys.executable,str(SCRIPT),str(root)],capture_output=True)
        return result,p
    def test_both_builder_branches_configured(self):
        result,p=self.run_patch(SOURCE);self.assertEqual(result.returncode,0,result.stderr)
        text=p.read_text();self.assertEqual(text.count('->videoSearchBuilder('),2)
        self.assertEqual(text.count('->build()'),2)
        self.assertIn('converter_preserved',text)
    def test_unknown_branch_does_not_partially_patch(self):
        source=SOURCE.replace('->LIZ(Ljava/lang/String;)LX/04VK;','->other(Ljava/lang/String;)LX/04VK;')
        result,p=self.run_patch(source);self.assertNotEqual(result.returncode,0)
        self.assertEqual(p.read_text(),source)
