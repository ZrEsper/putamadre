from pathlib import Path
import hashlib,json,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;reports=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert len(tests['checks'])>=4 and all('CHECKS='in text for text in tests['checks']);entries={}
for info in reports.values():
 p=r/'output'/info['output'];data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==info['sha256'];entries['mods/'+p.name]=data;shutil.copyfile(p,root/'mods'/p.name)
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes();entries['VALIDACION.json']=(r/'test-results.json').read_bytes();entries['SHA256SUMS.txt']=''.join(hashlib.sha256(data).hexdigest()+'  '+name+'\n'for name,data in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Ambiente-47-Loot-Poblacion-Ruido.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,data in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,data)
with zipfile.ZipFile(out)as z:
 assert z.testzip()is None and sum(n.endswith('.jar')for n in z.namelist())==2
 for line in z.read('SHA256SUMS.txt').decode().splitlines():digest,n=line.split('  ',1);assert hashlib.sha256(z.read(n)).hexdigest()==digest
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':2,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
