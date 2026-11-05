#!/usr/bin/env bash
set -euo pipefail
compiler=${1:?Usage: test_entry.sh path/to/ecj.jar}
test_classes=$(mktemp -d)
trap 'rm -rf "$test_classes"' EXIT
java -jar "$compiler" -8 -nowarn -d "$test_classes" \
  tests/java/android/app/Activity.java tests/java/android/util/Log.java \
  tests/java/studio/cactunus/cacttus/Menu.java \
  menu/src/studio/cactunus/cacttus/SearchRouting.java \
  tests/java/studio/cactunus/cacttus/ScreenCare.java \
  tests/java/studio/cactunus/cacttus/SearchDiagnostics.java \
  tests/java/studio/cactunus/cacttus/ScreenCarePolicyTest.java \
  menu/src/studio/cactunus/cacttus/ScreenCarePolicy.java \
  tests/java/studio/cactunus/cacttus/CrashReport.java \
  tests/java/studio/cactunus/cacttus/EntryTest.java \
  menu/src/studio/cactunus/cacttus/Entry.java
java -cp "$test_classes" studio.cactunus.cacttus.EntryTest
# Also verify resolution of a missing menu class, not just exceptions inside it.
rm "$test_classes/studio/cactunus/cacttus/Menu.class"
java -cp "$test_classes" studio.cactunus.cacttus.EntryTest

rm "$test_classes/studio/cactunus/cacttus/CrashReport.class"
java -cp "$test_classes" studio.cactunus.cacttus.EntryTest missing-diagnostics

java -cp "$test_classes" studio.cactunus.cacttus.ScreenCarePolicyTest
java -jar "$compiler" -8 -nowarn -cp "$test_classes" -d "$test_classes" \
  tests/java/studio/cactunus/cacttus/CrashReport.java \
  tests/java/studio/cactunus/cacttus/SearchDiagnosticsTest.java \
  menu/src/studio/cactunus/cacttus/SearchDiagnostics.java
java -cp "$test_classes" studio.cactunus.cacttus.SearchDiagnosticsTest

java -jar "$compiler" -8 -nowarn -d "$test_classes" menu/src/studio/cactunus/cacttus/SearchRouting.java tests/java/studio/cactunus/cacttus/SearchRoutingTest.java
java -cp "$test_classes" studio.cactunus.cacttus.SearchRoutingTest
java -jar "$compiler" -8 -nowarn -cp "$test_classes" -d "$test_classes" \
  tests/java/X/Base.java tests/java/X/Frm0.java tests/java/X/Wrap.java \
  tests/java/com/bytedance/retrofit2/mime/TypedOutput.java \
  tests/java/studio/cactunus/cacttus/Menu.java \
  tests/java/studio/cactunus/cacttus/SearchBodyTest.java
# Reproduce the APK's non-Java identifier class names in the compiled fixture.
python3 - "$test_classes" <<'PY'
import pathlib,sys
root=pathlib.Path(sys.argv[1]);mapping={b'X/Base':b'X/05mg',b'X/Frm0':b'X/07Hx',b'X/Wrap':b'X/0OxD'}
for p in root.rglob('*.class'):
    b=p.read_bytes()
    for old,new in mapping.items():b=b.replace(old,new)
    p.write_bytes(b)
for old,new in [('Base','05mg'),('Frm0','07Hx'),('Wrap','0OxD')]:
    (root/'X'/(old+'.class')).rename(root/'X'/(new+'.class'))
PY
java -cp "$test_classes" studio.cactunus.cacttus.SearchBodyTest
