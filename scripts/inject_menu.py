#!/usr/bin/env python3
"""Connect compiled menu smali and settings to the version-specific region patch."""
import pathlib
import re
import shutil
import sys
root, compiled = map(pathlib.Path, sys.argv[1:])
activity, = root.glob('smali*/com/ss/android/ugc/aweme/splash/SplashActivity.smali')
s = activity.read_text()
call = '    invoke-static {p0}, Lstudio/cactunus/cacttus/Entry;->install(Landroid/app/Activity;)V'
if call in s:
    raise SystemExit('Menu already installed')
s, count = re.subn(r'(    invoke-super \{p0, p1\}, [^\n]+->onCreate\(Landroid/os/Bundle;\)V)',lambda m:m[1]+'\n\n'+call,s)
if count != 1:
    raise SystemExit(f'Expected one lifecycle hook, found {count}')
changes = [(activity,s)]
for name, register in [('smali_classes2/X/03NM.smali','v7'),('smali_classes4/X/06VU.smali','v0'),('smali_classes20/X/0fk3.smali','v0')]:
    p = root/name
    source = p.read_text()
    old = f'    const-string {register}, "kz"'
    if source.count(old) != 1:
        raise SystemExit(f'{name}: expected one region patch')
    method = 'networkCountry' if 'classes20/' in name else 'simCountry'
    new = f'    invoke-static {{{register}}}, Lstudio/cactunus/cacttus/Entry;->{method}(Ljava/lang/String;)Ljava/lang/String;\n\n    move-result-object {register}'
    changes.append((p,source.replace(old,new)))
settings, = root.glob('smali*/com/ss/android/ugc/aweme/setting/ui/SettingContainerActivity.smali')
source = settings.read_text()
source, count = re.subn(r'(    invoke-super \{p0\}, [^\n]+->onResume\(\)V)',
    lambda m: m[1]+'\n\n    invoke-static {p0}, Lstudio/cactunus/cacttus/Entry;->settings(Landroid/app/Activity;)V', source)
if count != 1:
    raise SystemExit(f'Expected one settings hook, found {count}')
changes.append((settings, source))
response, = root.glob('smali*/com/ss/android/ugc/aweme/discover/model/SearchApiResult.smali')
source=response.read_text()
for getter, result in [('getSearchNilInfo','Lcom/ss/android/ugc/aweme/discover/model/SearchNilInfo;'),('getSearchNilText','Lcom/ss/android/ugc/aweme/discover/model/SearchNilText;')]:
    pattern=r'(\.method public '+getter+r'\(\)'+re.escape(result)+r'\n    \.locals 1)'
    source,count=re.subn(pattern,lambda m:m[1]+'\n\n    invoke-static {p0}, Lstudio/cactunus/cacttus/Entry;->searchResult(Ljava/lang/Object;)V',source)
    if count!=1:raise SystemExit(f'Expected one {getter}, found {count}')
changes.append((response,source))
interceptor, = root.glob('smali*/com/ss/android/ugc/aweme/foundation/network/interceptor/CommonParamInterceptor.smali')
source=interceptor.read_text()
pattern=r'(invoke-interface \{v0\}, LX/0EA3;->getCurrentRegionCode\(\)Ljava/lang/String;\s*(?:\.line \d+\s*)*move-result-object v0)'
source,count=re.subn(pattern,lambda m:m[1]+'\n\n    invoke-static {v3, v0}, Lstudio/cactunus/cacttus/Entry;->searchRegion(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;\n    move-result-object v0',source)
if count!=1:raise SystemExit(f'Expected one priority_region source, found {count}')
changes.append((interceptor,source))
fragment, = root.glob('smali*/com/ss/android/ugc/aweme/search/pages/result/common/core/ui/fragment/SearchFragment.smali')
source=fragment.read_text()
pattern=r'(\.method public static R00\(Lcom/ss/android/ugc/aweme/search/pages/result/common/core/ui/fragment/SearchFragment;ZLcom/ss/android/ugc/aweme/discover/model/SearchApiResult;Ljava/lang/Exception;ZI\)V\n    \.locals 16)'
source,count=re.subn(pattern,lambda m:m[1]+'\n\n    invoke-static/range {p2 .. p3}, Lstudio/cactunus/cacttus/Entry;->searchEvent(Ljava/lang/Object;Ljava/lang/Throwable;)V',source)
if count!=1:raise SystemExit(f'Expected one search error handler, found {count}')
changes.append((fragment,source))
# The menu goes in its own DEX to avoid overflowing existing method references.
destination = root/'smali_classes58'
if destination.exists():
    raise SystemExit('DEX destination already exists')
shutil.copytree(compiled/'smali',destination)
for p, source in changes: p.write_text(source)
print('Menu installed and region calls connected')
