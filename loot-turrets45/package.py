from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;reports=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());checks='\n'.join(tests['checks']);assert 'PROFESSION_FINAL_LOOT_CHECKS=344'in checks and'QUIET_NATIVE_BEFORE_AFTER_CHECKS=9'in checks and'TURRET_THREE_TIER_CHECKS=50'in checks and'NATIVE_PIPELINE_HOOK_CHECKS=15'in checks
entries={}
for v in reports.values():
 p=r/'output'/v['output'];data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==v['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries['mods/'+p.name]=data
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes();entries['RENDIMIENTO.txt']=(r/'RENDIMIENTO.txt').read_bytes();entries['SHA256SUMS.txt']=''.join(hashlib.sha256(data).hexdigest()+'  '+name+'\n'for name,data in sorted(entries.items())).encode();out=root/'distribuciones/Zombies-Poldo-Loot-45-Torretas-3-Niveles.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for name,data in sorted(entries.items()):i=zipfile.ZipInfo(name,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,data)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len([n for n in z.namelist()if n.endswith('.jar')])==4
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':4,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
