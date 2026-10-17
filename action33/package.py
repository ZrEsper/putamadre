from pathlib import Path
import hashlib,json,zipfile,shutil
r=Path(__file__).resolve().parent
v=json.loads((r/'validation.json').read_text());jar=r/'output'/v['file'];dest=r.parent/'mods'/jar.name;shutil.copy2(jar,dest)
assert hashlib.sha256(dest.read_bytes()).hexdigest()==v['sha256']
out=r.parent/'distribuciones/Zombies-Poldo-Melee-33-Laser-Estable-Lean-Peso.zip'
files={'mods/'+jar.name:jar,'LEEME.txt':r/'GUIA.txt','validacion.json':r/'validation.json','pruebas.json':r/'test-results.json'}
nv=r.parent/'nvg-laser9';nvmeta=json.loads((nv/'validation.json').read_text());nvjar=nv/'output'/nvmeta['output'];assert hashlib.sha256(nvjar.read_bytes()).hexdigest()==nvmeta['sha256'];shutil.copy2(nvjar,r.parent/'mods'/nvjar.name)
files.update({'mods/'+nvjar.name:nvjar,'validacion-nvg.json':nv/'validation.json','pruebas-nvg.json':nv/'test-results.json'})
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,p in files.items():
  info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,p.read_bytes())
with zipfile.ZipFile(out)as z:assert z.testzip()is None;assert z.read('mods/'+jar.name)==jar.read_bytes()
(r/'release.json').write_text(json.dumps({'zip':str(out.relative_to(r.parent)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'bytes':out.stat().st_size,'jar_sha256':v['sha256']},indent=2)+'\n')
print(out.name,hashlib.sha256(out.read_bytes()).hexdigest())
