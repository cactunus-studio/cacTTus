#!/usr/bin/env python3
"""Register the existing common-parameter interceptor on both video search clients."""
import pathlib,re,sys
root=pathlib.Path(sys.argv[1])
p,=root.glob('smali*/com/ss/android/ugc/aweme/search/pages/result/core/repo/SearchApiNew.smali')
s=p.read_text()
if '->videoSearchBuilder(Ljava/lang/Object;)V' in s:
    raise SystemExit('Search client already patched')
old='    invoke-virtual {v3}, LX/04Lh;->build()Lcom/bytedance/ies/ugc/aweme/network/TTRetrofit;'
if s.count(old)!=1:raise SystemExit('Unexpected search converter builder')
s=s.replace(old,'    invoke-static {v3}, Lstudio/cactunus/cacttus/Entry;->videoSearchBuilder(Ljava/lang/Object;)V\n\n'+old)
pattern=r'invoke-interface \{v2, v3\}, Lcom/bytedance/ies/ugc/aweme/network/IRetrofitFactory;->LIZ\(Ljava/lang/String;\)LX/04VK;\s*(?:\.line \d+\s*)*move-result-object v0'
replacement='''invoke-interface {v2, v3}, Lcom/bytedance/ies/ugc/aweme/network/IRetrofitFactory;->createBuilder(Ljava/lang/String;)LX/04Lh;
    move-result-object v0
    invoke-static {v0}, Lstudio/cactunus/cacttus/Entry;->videoSearchBuilder(Ljava/lang/Object;)V
    invoke-virtual {v0}, LX/04Lh;->build()Lcom/bytedance/ies/ugc/aweme/network/TTRetrofit;
    move-result-object v0'''
s,count=re.subn(pattern,replacement,s)
if count!=1:raise SystemExit(f'Expected one default search client branch, found {count}')
p.write_text(s)
print('Video search interceptor registered in both client branches')
