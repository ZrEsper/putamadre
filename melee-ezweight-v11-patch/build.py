from pathlib import Path
import zipfile,hashlib,json,sys,tomllib
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]);out=r/'zomboid-global-melee-animations-2.3.1-range-plus-one-ezweight-v11.jar'
assert hashlib.sha256(base.read_bytes()).hexdigest()==json.loads((r/'inputs.sha256.json').read_text())['melee']
with zipfile.ZipFile(base) as z:
 metadata=z.read('META-INF/neoforge.mods.toml').decode();assert metadata.count('versionRange="[1.8.4-stamina-v9]"')==1
 metadata=metadata.replace('versionRange="[1.8.4-stamina-v9]"','versionRange="[1.8.4-stamina-v11]"')
 d=tomllib.loads(metadata);dep=[x for x in d['dependencies']['zomboid_global_melee'] if x['modId']=='ezweight'];assert len(dep)==1 and dep[0]['versionRange']=='[1.8.4-stamina-v11]'
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as w:
  for entry in z.infolist():w.writestr(entry,metadata.encode() if entry.filename=='META-INF/neoforge.mods.toml' else z.read(entry))
with zipfile.ZipFile(base) as z,zipfile.ZipFile(out) as w:
 assert w.testzip() is None and set(z.namelist())==set(w.namelist())
 assert all(z.read(n)==w.read(n) for n in z.namelist() if n!='META-INF/neoforge.mods.toml')
print('PASS dependency set to exact supplied v11; all other entries byte-identical')
