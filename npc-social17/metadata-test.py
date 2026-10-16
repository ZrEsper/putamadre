"""Regression for the loader rejection recorded in the user's log; no game logic changes."""
from pathlib import Path
import json,zipfile,tomllib,hashlib
r=Path(__file__).resolve().parent;root=r.parent;inputs=json.loads((r/'inputs.json').read_text());report=json.loads((r/'validation.json').read_text());new=r/'output'/report['corpse']['output'];old=root/'mods/corpse-neoforge-1.21.1-1.1.13-npc-survivor-cargo.3.jar';meta='META-INF/neoforge.mods.toml'
with zipfile.ZipFile(r/'base'/inputs['remains']['file'])as z:
 dependencies=tomllib.loads(z.read(meta).decode())['dependencies'];requirement=next(d['versionRange']for group in dependencies.values()for d in group if d['modId']=='corpse')
minimum=requirement[1:].split(',')[0];assert requirement.startswith('[')
with zipfile.ZipFile(old)as a,zipfile.ZipFile(new)as b,zipfile.ZipFile(r/'base'/inputs['corpse']['file'])as original:
 previous=tomllib.loads(a.read(meta).decode())['mods'][0]['version'];version=tomllib.loads(b.read(meta).decode())['mods'][0]['version'];assert previous=='1.1.13-npc-survivor-cargo.3' and version==minimum=='1.21.1-1.1.13'
 assert b.read(meta)==original.read(meta), 'Preserve exact original compatibility metadata'
 assert set(a.namelist())==set(b.namelist())
 unchanged=0
 for name in a.namelist():
  if name!=meta:assert a.read(name)==b.read(name),name;unchanged+=1
result={'restored_version':version,'zombie_remains_required_range':requirement,'matches_inclusive_minimum':True,'previous_release_metadata_was_incompatible':True,'unchanged_nonmetadata_entries':unchanged,'sha256':hashlib.sha256(new.read_bytes()).hexdigest(),'minecraft_runtime_tested':False}
(r/'metadata-regression.json').write_text(json.dumps(result,indent=2)+'\n');print('CORPSE_METADATA_REGRESSION_CHECKS=6; unchanged entries='+str(unchanged))
