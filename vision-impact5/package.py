from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;reports=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert 'PATCHED_FILTER_CHECKS=23' in '\n'.join(tests['checks']) and 'NATIVE_CAPACITY_CHECKS=3005' in '\n'.join(tests['checks']);entries={}
for key,report in reports.items():
 p=r/'output'/report['output'];assert hashlib.sha256(p.read_bytes()).hexdigest()==report['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries['mods/'+p.name]=p.read_bytes()
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes();entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode();out=root/'distribuciones/Zombies-Poldo-Vision-N-200-Marcas-85s.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len([n for n in z.namelist()if n.endswith('.jar')])==3
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':3,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
