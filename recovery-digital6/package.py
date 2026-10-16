from pathlib import Path
import hashlib,json,shutil,zipfile
r=Path(__file__).resolve().parent;root=r.parent;h=root/'corpse-hygiene14';entries={}
a=json.loads((r/'test-results.json').read_text());b=json.loads((h/'test-results.json').read_text())
assert 'TURRET_TIMING_CHECKS=120' in a['checks'] and 'BLACKOUT_UI_AND_NATIVE_INVENTORY_CHECKS=21' in a['checks']
assert 'HYGIENE_SERVER_POLICY_CHECKS=540' in b['checks']
assert (r/'shader-test-results.json').exists()
reports=[(r,v) for v in json.loads((r/'validation.json').read_text()).values()]+[(h,json.loads((h/'validation.json').read_text()))]
for folder,v in reports:
 p=folder/'output'/v['output'];data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==v['sha256'];shutil.copyfile(p,root/'mods'/p.name);entries['mods/'+p.name]=data
entries['LEEME.txt']=(r/'GUIA.txt').read_bytes()
entries['SHA256SUMS.txt']=''.join(hashlib.sha256(d).hexdigest()+'  '+n+'\n' for n,d in sorted(entries.items())).encode()
out=root/'distribuciones/Zombies-Poldo-Torretas-Inconsciente-Digital-Higiene.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for name,data in sorted(entries.items()):
  i=zipfile.ZipInfo(name,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,data)
with zipfile.ZipFile(out) as z:assert z.testzip() is None and sum(n.endswith('.jar') for n in z.namelist())==4
(r/'package-validation.json').write_text(json.dumps({'file':str(out.relative_to(root)),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'jars':4,'minecraft_runtime_tested':False},indent=2)+'\n')
print(out.name)
