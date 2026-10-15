from pathlib import Path
import subprocess,json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent
subprocess.run(['python',str(r/'make_guide.py')],cwd=r,check=True)
entries={}
for project,name in [('screamer-horror-patch','validation-screamer.json'),('armor-bullet-tolerance-patch','validation-armadura.json')]:
 p=root/project;v=json.loads((p/'validation.json').read_text());mod=p/'output'/v['output'];assert hashlib.sha256(mod.read_bytes()).hexdigest()==v['output_sha256'];entries['mods/'+mod.name]=mod.read_bytes();shutil.copy2(mod,root/'mods'/mod.name);entries[name]=(p/'validation.json').read_bytes()
for n in ['GUIA.md','GUIA.html']:entries[n]=(r/n).read_bytes()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
p=root/'distribuciones/Zombies-Poldo-Screamer-Armadura-362.zip'
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as z:assert z.testzip()is None;assert len(z.namelist())==7
print(p.name,hashlib.sha256(p.read_bytes()).hexdigest())
