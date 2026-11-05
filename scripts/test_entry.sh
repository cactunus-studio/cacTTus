#!/usr/bin/env bash
set -euo pipefail
compiler=${1:?Usage: test_entry.sh path/to/ecj.jar}
test_classes=$(mktemp -d)
trap 'rm -rf "$test_classes"' EXIT
java -jar "$compiler" -8 -nowarn -d "$test_classes" \
  tests/java/android/app/Activity.java tests/java/android/util/Log.java \
  tests/java/studio/cactunus/cacttus/Menu.java \
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
