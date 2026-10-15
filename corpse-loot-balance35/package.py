from pathlib import Path
import subprocess,json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent
subprocess.run(['python',str(r/'make_guide.py')],cwd=r,check=True)
v=json.loads((r/'validation.json').read_text());assert v['checks'].startswith('CORPSE_CHECKS=16221\n')
entries={}
for m in v['mods']:
 p=r/'output'/m['file'];assert hashlib.sha256(p.read_bytes()).hexdigest()==m['sha256'];entries['mods/'+p.name]=p.read_bytes();shutil.copy2(p,root/'mods'/p.name)
for n in ['GUIA.md','GUIA.html','validation.json']:entries[n]=(r/n).read_bytes()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Loot-Militar-35.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):
  i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None;assert len(z.namelist())==6
print(out.name,hashlib.sha256(out.read_bytes()).hexdigest())
