from pathlib import Path
import json,hashlib,zipfile,shutil
r=Path(__file__).resolve().parent;root=r.parent;reports=json.loads((r/'validation.json').read_text());tests=json.loads((r/'test-results.json').read_text());assert len(tests['checks'])==6 and all('CHECKS='in s for s in tests['checks']);assert tests['native_assets_and_medical_player_rules_preserved'];entries={}
for info in reports.values():
 p=r/'output'/info['output'];data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==info['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries['mods/'+p.name]=data
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes();entries['VALIDACION.json']=(r/'test-results.json').read_bytes();entries['SHA256SUMS.txt']=''.join(hashlib.sha256(data).hexdigest()+'  '+name+'\n'for name,data in sorted(entries.items())).encode();out=root/'distribuciones/Zombies-Poldo-Correccion-Pantallas-18.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,data in sorted(entries.items()):info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,data)
with zipfile.ZipFile(out)as z:assert z.testzip()is None and len([n for n in z.namelist()if n.endswith('.jar')])==3
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':3,'minecraft_runtime_tested':False},indent=2)+'\n');print(out.name)
