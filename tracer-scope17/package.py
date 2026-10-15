from pathlib import Path
import json,hashlib,shutil,zipfile
r=Path(__file__).resolve().parent;root=r.parent;v=json.loads((r/'validation.json').read_text());checks=json.loads((r/'test-results.json').read_text());assert checks['behavior']=='SCOPE_VISUAL_CHECKS=310' and checks['bytecode']=='BYTECODE_POSE_CHECKS=332'
entries={}
for data in v.values():
 p=r/'output'/data['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==data['output_sha256'];shutil.copy2(p,root/'mods'/p.name);entries['mods/'+p.name]=p.read_bytes()
for n in ['GUIA.md','validation.json','test-results.json']:entries[n]=(r/n).read_bytes()
entries['LEEME.txt']='Cierra juego y servidor. Reemplaza los JAR anteriores de TaCZ y tacz-tactical por los DOS de mods, en clientes y servidor. No dejes duplicados. Conserva taczaddon y los packs. Lee GUIA.md.\n'.encode()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
p=root/'distribuciones/Zombies-Poldo-TaCZ-17-Trazadoras-Miras.zip'
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as z:assert z.testzip()is None and len(z.namelist())==7
print(p.name,hashlib.sha256(p.read_bytes()).hexdigest())
