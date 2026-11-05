#!/usr/bin/env python3
"""Reuse unchanged original DEX files instead of rebuilding them."""
import pathlib
import re
import sys
import zipfile
source, decoded = map(pathlib.Path, sys.argv[1:])
output = decoded/'build/apk'
output.mkdir(parents=True, exist_ok=True)
changed = {'classes36.dex','classes8.dex','classes2.dex','classes4.dex','classes9.dex','classes20.dex','classes32.dex'}
with zipfile.ZipFile(source) as archive:
    for name in archive.namelist():
        if re.fullmatch(r'classes\d*\.dex',name) and name not in changed:
            (output/name).write_bytes(archive.read(name))
