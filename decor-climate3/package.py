from pathlib import Path
import hashlib,zipfile,json,shutil
r=Path(__file__).resolve().parent;root=r.parent;v=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert tests['checks'][0]=='Simulated checks passed: 245' and tests['checks'][1].startswith('CLIMATE_STORAGE_CHECKS=52;')
entries={}
for data in v.values():
 p=r/'output'/data['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==data['output_sha256'];shutil.copy2(p,root/'mods'/p.name);entries['mods/'+p.name]=p.read_bytes()
for n in ['GUIA.md','validation.json','test-results.json']:entries[n]=(r/n).read_bytes()
entries['LEEME.txt']='Cierra Minecraft y el servidor. Reemplaza Doomsday Decoration, Homeostatic, Tough As Nails y Zomboid Hordes por los cuatro JAR de mods. Hazlo en clientes y servidor. No dejes versiones duplicadas. Lee GUIA.md para combustible, crafteos y controles.\n'.encode()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
p=root/'distribuciones/Zombies-Poldo-Muebles-Aires-Tablones-3.zip'
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as z:assert z.testzip()is None and len(z.namelist())==9
print(p.name,hashlib.sha256(p.read_bytes()).hexdigest())
