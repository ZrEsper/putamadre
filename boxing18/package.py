from pathlib import Path
import zipfile,json,hashlib,shutil
r=Path(__file__).resolve().parent;root=r.parent;t=json.loads((r/'test-results.json').read_text());assert len(t['checks'])==9
entries={'LEEME.txt':(r/'GUIA.txt').read_bytes(),'VALIDACION.json':(r/'test-results.json').read_bytes()}
for name in ['validation.json','gore-validation.json']:
 v=json.loads((r/name).read_text());p=r/'output'/v['output'];b=p.read_bytes();assert hashlib.sha256(b).hexdigest()==v['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries['mods/'+p.name]=b
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Boxeo-18-Derribos-Pisotones.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
