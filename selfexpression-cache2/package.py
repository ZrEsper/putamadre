from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;report=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert '10522' in tests['checks'][1];jar=r/'output'/report['output'];assert hashlib.sha256(jar.read_bytes()).hexdigest()==report['sha256'];shutil.copyfile(jar,root/'mods'/jar.name)
entries={'mods/'+jar.name:jar.read_bytes(),'LEEME.txt':(r/'GUIA.txt').read_bytes()};entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode();out=root/'distribuciones/Zombies-Poldo-Selfexpression-Cache-2.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len([n for n in z.namelist()if n.endswith('.jar')])==1
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':1,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
