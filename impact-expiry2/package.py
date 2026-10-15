from pathlib import Path
import hashlib,json,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;v=json.loads((r/'validation.json').read_text());assert json.loads((r/'test-results.json').read_text())['checks']=='IMPACT_EXPIRY_CHECKS=3607'
p=r/'output'/v['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==v['output_sha256'];shutil.copy2(p,root/'mods'/p.name)
e={'mods/'+p.name:p.read_bytes(),'LEEME.txt':(r/'GUIA.md').read_bytes()};e['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(e.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Marcas-Bala-3-Minutos.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None
print(out.name,hashlib.sha256(out.read_bytes()).hexdigest())
