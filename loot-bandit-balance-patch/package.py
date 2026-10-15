#!/usr/bin/env python3
from pathlib import Path
import subprocess,hashlib,json,zipfile,shutil,os
R=Path(__file__).resolve().parent
subprocess.run(['python',str(R/'make_guide.py')],cwd=R,check=True)
report=json.loads((R/'validation.json').read_text())
assert sum(report['behavior_checks'].values())==21845
entries={}
for mod in report['mods']:
 p=R/'output'/mod['file'];assert hashlib.sha256(p.read_bytes()).hexdigest()==mod['sha256']
 shutil.copy2(p,R.parent/'mods'/p.name);entries['mods/'+p.name]=p.read_bytes()
for name in ['GUIA.md','GUIA.html','validation.json']:entries[name]=(R/name).read_bytes()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
p=R.parent/'distribuciones/Zombies-Poldo-Escasez-NVG-Reanimacion-33.zip'
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):
  i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as z:assert z.testzip()is None;assert len(z.namelist())==9
print(p.name,hashlib.sha256(p.read_bytes()).hexdigest(),p.stat().st_size)
