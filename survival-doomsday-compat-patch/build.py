from pathlib import Path
import zipfile,hashlib,json,tomllib,sys
r=Path(__file__).resolve().parent
base=Path(sys.argv[1]).resolve()
sha=hashlib.sha256(base.read_bytes()).hexdigest()
expected=json.loads((r/'input.sha256.json').read_text())['sha256']
assert sha==expected,'Expected supplied Survival 1.1.0 FF integrated'
out=r.parent/'mods/zomboid-survival-1.21.1-1.1.0-ff-preservation-doomsday-compat.1.jar'
with zipfile.ZipFile(base) as z:
 assert z.testzip() is None
 entries={n:z.read(n) for n in z.namelist()}
name='META-INF/neoforge.mods.toml';original=entries[name].decode()
parsed=tomllib.loads(original)
dep=next(d for d in parsed['dependencies']['zomboid_survival'] if d['modId']=='doomsday_decoration')
assert dep['versionRange']=='[1.1.4.2]' and dep['ordering']=='AFTER'
old='modId="doomsday_decoration"\ntype="required"\nversionRange="[1.1.4.2]"'
new='modId="doomsday_decoration"\ntype="required"\nversionRange="[1.1.4.2,1.1.4.3)"'
assert original.count(old)==1
entries[name]=original.replace(old,new).encode()
new_parsed=tomllib.loads(entries[name].decode())
assert new_parsed['mods']==parsed['mods']
assert new_parsed['dependencies']['fffoodspoilageandpreservation']==parsed['dependencies']['fffoodspoilageandpreservation']
assert new_parsed['mixins']==parsed['mixins']
other=[d for d in parsed['dependencies']['zomboid_survival'] if d['modId']!='doomsday_decoration']
assert other==[d for d in new_parsed['dependencies']['zomboid_survival'] if d['modId']!='doomsday_decoration']
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,data in entries.items():z.writestr(zipfile.ZipInfo(n,(1980,1,1,0,0,0)),data,compress_type=zipfile.ZIP_DEFLATED)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 assert set(a.namelist())==set(b.namelist())
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert changed==[name]
 for n in b.namelist():
  if n.endswith('.json'):json.loads(b.read(n))
(r/'validation.json').write_text(json.dumps({'input_sha256':sha,'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_entries':changed,'all_classes_assets_configs_and_embedded_libraries_preserved':True,'minecraft_runtime_tested':False},indent=2)+'\n')
print('Verified metadata-only change; all classes, FF data, resources and embedded libraries byte-for-byte preserved')
