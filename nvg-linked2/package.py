from pathlib import Path
import json,zipfile,hashlib,shutil
r=Path(__file__).resolve().parent;root=r.parent;v=json.loads((r/'validation.json').read_text());checks=json.loads((r/'test-results.json').read_text());assert checks['checks']=='NVG_LINK_CHECKS=930';p=r/'output'/v['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==v['output_sha256'];shutil.copy2(p,root/'mods'/p.name)
entries={'mods/'+p.name:p.read_bytes()}
for n in ['GUIA.md','validation.json','test-results.json']:entries[n]=(r/n).read_bytes()
entries['LEEME.txt']='Reemplaza Fracture Point NVG .1 por el JAR de mods, en clientes y servidor. No dupliques versiones. N: subir/bajar visor y activar/desactivar su visión. Consulta GUIA.md.\n'.encode()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
p=root/'distribuciones/Zombies-Poldo-NVG-Visor-N-2.zip'
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as z:assert z.testzip()is None and len(z.namelist())==6
print(p.name,hashlib.sha256(p.read_bytes()).hexdigest())
