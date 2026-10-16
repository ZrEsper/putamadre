from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent
entries={};reports={}
for project in ['iso-audio31','doomsday-board5','far-shots4']:
 p=root/project;tests=json.loads((p/'test-results.json').read_text());assert tests['checks'];report=json.loads((p/'validation.json').read_text());jar=p/'output'/report['output'];assert hashlib.sha256(jar.read_bytes()).hexdigest()==report['sha256'];assert not report['minecraft_runtime_tested'];shutil.copyfile(jar,root/'mods'/jar.name);entries['mods/'+jar.name]=jar.read_bytes();reports[project]={'jar':jar.name,'sha256':report['sha256'],'checks':tests['checks']}
assert '3607' in '\n'.join(reports['far-shots4']['checks'])
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes();entries['SHA256SUMS.txt']=''.join(hashlib.sha256(b).hexdigest()+'  '+n+'\n'for n,b in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Sonido-Lejano-Iso31-Planks5.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(out)as z:
 assert z.testzip()is None and len([n for n in z.namelist()if n.endswith('.jar')])==3
 for n in z.namelist():
  if n.endswith('.jar'):
   from io import BytesIO
   with zipfile.ZipFile(BytesIO(z.read(n))) as jar:assert jar.testzip()is None
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':3,'projects':reports,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
