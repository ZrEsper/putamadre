from pathlib import Path
import json,zipfile,hashlib,shutil
r=Path(__file__).resolve().parent;root=r.parent;v=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert len(tests['checks'])==9 and all(tests['checks'])
e={'LEEME.txt':(r/'GUIA.txt').read_bytes(),'VALIDACION.json':(r/'test-results.json').read_bytes(),'DIAGNOSTICO-SPARK.json':(r/'spark-report.json').read_bytes()}
for item in v['outputs'].values():
 p=r/'output'/item['file'];b=p.read_bytes();assert hashlib.sha256(b).hexdigest()==item['sha256'];shutil.copyfile(p,root/'mods'/p.name);e['mods/'+p.name]=b
assert len([n for n in e if n.endswith('.jar')])==3
e['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(e.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Accion-20-Gore-10-Panel-49.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
