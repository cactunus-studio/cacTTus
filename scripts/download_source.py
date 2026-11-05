#!/usr/bin/env python3
"""Download the exact source APK, including Drive's large-file confirmation."""
import hashlib
import os
from html.parser import HTMLParser
from pathlib import Path
import sys
from urllib.parse import urlencode, urlsplit
from urllib.request import urlopen

EXPECTED = '3b68b645a9d6ab69336152c613e644aaaf8d09aa001413518f27b4517be07b0c'
DEFAULT = 'https://drive.google.com/uc?export=download&id=1aH1pim1lBTxq2YiOy2QQNQHLw-0bY5o8&confirm=t'

class Confirmation(HTMLParser):
    action = None
    fields = None
    def __init__(self):
        super().__init__()
        self.fields = {}
    def handle_starttag(self, tag, attrs):
        a = dict(attrs)
        if tag == 'form' and a.get('id') == 'download-form':
            self.action = a.get('action')
        if tag == 'input' and a.get('type') == 'hidden':
            self.fields[a['name']] = a.get('value', '')

url = os.environ.get('APK_URL') or DEFAULT
if urlsplit(url).scheme != 'https':
    raise SystemExit('APK_URL must use HTTPS')
destination = Path(sys.argv[1])
partial = destination.with_suffix('.part')
for attempt in range(2):
    with urlopen(url, timeout=120) as response:
        if urlsplit(response.url).scheme != 'https':
            raise SystemExit('Insecure download redirect')
        if 'text/html' in response.headers.get('Content-Type', ''):
            parser = Confirmation()
            parser.feed(response.read(1024 * 1024).decode())
            if parser.action != 'https://drive.usercontent.google.com/download':
                raise SystemExit('Expected an APK, received an HTML page')
            url = parser.action + '?' + urlencode(parser.fields)
            continue
        checksum = hashlib.sha256()
        with partial.open('wb') as output:
            while chunk := response.read(1024 * 1024):
                checksum.update(chunk)
                output.write(chunk)
    if checksum.hexdigest() != EXPECTED:
        partial.unlink(missing_ok=True)
        raise SystemExit('Source APK checksum mismatch')
    partial.replace(destination)
    print('Source APK checksum verified')
    break
else:
    raise SystemExit('Could not resolve download confirmation')
