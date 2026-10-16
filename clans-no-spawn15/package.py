from pathlib import Path
import hashlib,json,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;report=json.loads((r/'validation.json').read_text());p=r/'output'/report['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==report['output_sha256'];shutil.copyfile(p,root/'mods'/p.name)
entries={'mods/'+p.name:p.read_bytes(),'LEEME.txt':(r/'LEEME.txt').read_bytes()};entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Clanes-15-Sin-Spawn-en-Reclamos.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len(z.namelist())==3
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
