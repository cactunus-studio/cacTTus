import pathlib
import subprocess
import sys
import tempfile
import unittest

SCRIPT = pathlib.Path(__file__).resolve().parents[1]/'scripts/inject_menu.py'

class InjectionTest(unittest.TestCase):
    def fixture(self, base, valid=True):
        root=base/'decoded'
        paths={
          'smali_classes9/com/ss/android/ugc/aweme/splash/SplashActivity.smali': '    invoke-super {p0, p1}, Lbase/Activity;->onCreate(Landroid/os/Bundle;)V',
          'smali_classes32/com/ss/android/ugc/aweme/setting/ui/SettingContainerActivity.smali': '    invoke-super {p0}, Lbase/Activity;->onResume()V' if valid else 'unexpected lifecycle',
          'smali_classes2/X/03NM.smali': '    const-string v7, "kz"',
          'smali_classes4/X/06VU.smali': '    const-string v0, "kz"',
          'smali_classes8/com/ss/android/ugc/aweme/discover/model/SearchApiResult.smali': '.method public getSearchNilInfo()Lcom/ss/android/ugc/aweme/discover/model/SearchNilInfo;\n    .locals 1\n.end method\n.method public getSearchNilText()Lcom/ss/android/ugc/aweme/discover/model/SearchNilText;\n    .locals 1\n.end method',
          'smali_classes36/com/ss/android/ugc/aweme/foundation/network/interceptor/CommonParamInterceptor.smali': 'iget-object v3, v0, LX/061i;->LIZJ:Lcom/bytedance/retrofit2/client/Request;\ninvoke-interface {v0}, LX/0EA3;->getCurrentRegionCode()Ljava/lang/String;\n.line 1\nmove-result-object v0',
          'smali_classes9/com/ss/android/ugc/aweme/search/pages/result/common/core/ui/fragment/SearchFragment.smali': '.method public static R00(Lcom/ss/android/ugc/aweme/search/pages/result/common/core/ui/fragment/SearchFragment;ZLcom/ss/android/ugc/aweme/discover/model/SearchApiResult;Ljava/lang/Exception;ZI)V\n    .locals 16',
          'smali_classes20/X/0fk3.smali': '    const-string v0, "kz"',
        }
        for name,text in paths.items():
            p=root/name; p.parent.mkdir(parents=True,exist_ok=True); p.write_text(text)
        menu=base/'menu'; (menu/'smali').mkdir(parents=True)
        (menu/'smali/Menu.smali').write_text('compiled menu fixture')
        return root,menu,paths
    def test_settings_hook_and_separate_region_methods(self):
        with tempfile.TemporaryDirectory() as td:
            root,menu,paths=self.fixture(pathlib.Path(td))
            result=subprocess.run([sys.executable,str(SCRIPT),str(root),str(menu)],capture_output=True,text=True)
            self.assertEqual(result.returncode,0,result.stderr)
            self.assertIn('->settings(Landroid/app/Activity;)V',(root/list(paths)[1]).read_text())
            self.assertIn('->networkCountry(', (root/'smali_classes20/X/0fk3.smali').read_text())
            self.assertIn('->simCountry(', (root/'smali_classes4/X/06VU.smali').read_text())
            self.assertTrue((root/'smali_classes58/Menu.smali').exists())
    def test_incompatible_settings_does_not_modify_checkout(self):
        with tempfile.TemporaryDirectory() as td:
            root,menu,paths=self.fixture(pathlib.Path(td),False)
            result=subprocess.run([sys.executable,str(SCRIPT),str(root),str(menu)],capture_output=True)
            self.assertNotEqual(result.returncode,0)
            for name,original in paths.items(): self.assertEqual((root/name).read_text(),original)
            self.assertFalse((root/'smali_classes58').exists())

if __name__=='__main__': unittest.main()
