from pathlib import Path
import subprocess,json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent
subprocess.run(['python',str(r/'make_guide.py')],cwd=r,check=True)
v=json.loads((r/'validation.json').read_text());assert v['checks'].startswith('PANEL_CHECKS=70\n');p=r/'output'/v['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==v['output_sha256'];shutil.copy2(p,root/'mods'/p.name)
entries={'mods/'+p.name:p.read_bytes()}
for n in ['GUIA.md','GUIA.html','validation.json']:entries[n]=(r/n).read_bytes()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
p=root/'distribuciones/Zombies-Poldo-Panel-Sencillo-40.zip'
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as z:assert z.testzip()is None;assert len(z.namelist())==5
print(p.name,hashlib.sha256(p.read_bytes()).hexdigest())
