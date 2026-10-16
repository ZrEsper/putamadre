from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;validation=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert tests['checks']=='FLASHLIGHT_BATTERY_CHECKS=78'and tests['native_assets_and_survivor_features_preserved'] and tests['native_hook_checks']==9;entries={}
for v in validation.values():
 p=r/'output'/v['output'];data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==v['sha256'];entries['mods/'+p.name]=data;shutil.copyfile(p,root/'mods'/p.name)
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes();entries['SHA256SUMS.txt']=''.join(hashlib.sha256(data).hexdigest()+'  '+name+'\n'for name,data in sorted(entries.items())).encode();out=root/'distribuciones/Zombies-Poldo-Linternas-Baterias-4.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for name,data in sorted(entries.items()):info=zipfile.ZipInfo(name,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,data)
with zipfile.ZipFile(out)as z:assert z.testzip()is None
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':2,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
