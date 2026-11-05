#!/usr/bin/env python3
"""Experimental patch for a decoded TikTok 47.7.1 APK."""
import pathlib
import re
import sys

root = pathlib.Path(sys.argv[1])
targets = {
    'smali_classes2/X/03NM.smali': 'getSimCountryIso',
    'smali_classes4/X/06VU.smali': 'getSimCountryIso',
    'smali_classes20/X/0fk3.smali': 'getNetworkCountryIso',
}
for name, method in targets.items():
    path = root / name
    source = path.read_text()
    pattern = (r'(invoke-virtual \{[^}]+\}, Landroid/telephony/TelephonyManager;->'
               + method + r'\(\)Ljava/lang/String;(?:(?:\s*\.line \d+)|\s)*move-result-object (v\d+))')
    source, count = re.subn(pattern, lambda m: m.group(1) + '\n\n    const-string ' + m.group(2) + ', "kz"', source)
    if count != 1:
        raise SystemExit(f'{name}: expected one call, found {count}')
    path.write_text(source)
    print(f'patched {name}')
