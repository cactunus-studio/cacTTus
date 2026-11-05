#!/usr/bin/env python3
"""Append one private Activity to binary XML without renumbering existing strings/resources."""
import pathlib
import struct as S
import sys
P=lambda *v:S.pack(*v)
path=pathlib.Path(sys.argv[1]); original=path.read_bytes()
chunks=[]; offset=8; strings=None; inserted=False
activity='studio.cactunus.cacttus.SettingsActivity'
while offset<len(original):
    kind,header,size=S.unpack_from('<HHI',original,offset)
    chunk=original[offset:offset+size];offset+=size
    if kind==1:
        _,_,_,count,styles,flags,start,style_start=S.unpack_from('<HHIIIIII',chunk)
        if styles or style_start:raise SystemExit('Styled manifest string pool unsupported')
        offsets=list(S.unpack_from('<'+'I'*count,chunk,header))
        def decode(pos):
            pos+=start
            def length8(pos):
                v=chunk[pos];return (((v&127)<<8)|chunk[pos+1],pos+2) if v&128 else (v,pos+1)
            if flags&256:
                _,pos=length8(pos);n,pos=length8(pos);return chunk[pos:pos+n].decode('utf-8')
            n=S.unpack_from('<H',chunk,pos)[0];pos+=2
            if n&0x8000:n=((n&0x7fff)<<16)|S.unpack_from('<H',chunk,pos)[0];pos+=2
            return chunk[pos:pos+2*n].decode('utf-16le')
        strings=[decode(v) for v in offsets]
        if activity in strings:raise SystemExit('Activity already registered')
        added=[]
        for value in ('activity',activity):
            if value not in strings:added.append(value);strings.append(value)
        data=chunk[start:]
        for value in added:
            offsets.append(len(data))
            if flags&256:
                encoded=value.encode('utf-8');data+=bytes([len(value),len(encoded)])+encoded+b'\0'
            else:data+=P('<H',len(value))+value.encode('utf-16le')+b'\0\0'
        data+=b'\0'*((-len(data))%4)
        new_start=header+len(offsets)*4
        chunk=P('<HHIIIIII',1,header,new_start+len(data),len(offsets),0,flags,new_start,0)+P('<'+'I'*len(offsets),*offsets)+data
    if kind==0x103 and strings[S.unpack_from('<I',chunk,20)[0]]=='application':
        ns=strings.index('http://schemas.android.com/apk/res/android')
        # Attribute name index must match the Android resource map, not a duplicate string.
        name_index=resource_ids.index(0x01010003)
        class_index=strings.index(activity);tag_index=strings.index('activity')
        start_node=P('<HHIII',0x102,16,56,1,0xffffffff)+P('<IIHHHHHH',0xffffffff,tag_index,20,20,1,0,0,0)+P('<IIIHBBI',ns,name_index,class_index,8,0,3,class_index)
        end_node=P('<HHIIIII',0x103,16,24,1,0xffffffff,0xffffffff,tag_index)
        chunks.extend([start_node,end_node]);inserted=True
    if kind==0x180:resource_ids=list(S.unpack_from('<'+'I'*((size-header)//4),chunk,header))
    chunks.append(chunk)
if not inserted:raise SystemExit('Application element not found')
body=b''.join(chunks);path.write_bytes(P('<HHI',3,8,8+len(body))+body)
print('Registered private settings Activity')
