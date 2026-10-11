from pathlib import Path
import re,zipfile,json,hashlib
repo=Path(__file__).resolve().parent.parent;base=repo/'gore-packs-patch/Blues-Better-Zombies-PZ-L4D2-Gore.1.zip';out=repo/'gore-packs-patch/Blues-Better-Zombies-PZ-L4D2-Gore.2-emf-fix.zip';pattern=re.compile(r'(?<![A-Za-z0-9_.])\.(?=\d)');changed=[];total=0
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as b:
 for name in a.namelist():
  data=a.read(name)
  if name.endswith(('.jem','.jpm')):
   text=data.decode();json.loads(text);fixed,count=pattern.subn('0.',text)
   if count:changed.append({'file':name,'fixed_literals':count});total+=count;data=fixed.encode()
   json.loads(data)
   assert not pattern.search(data.decode()),name
  b.writestr(name,data)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 for name in a.namelist():
  if not name.endswith(('.jem','.jpm')):assert a.read(name)==b.read(name),name
report={'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output':out.name,'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'fixed_literals':total,'changed_models':changed,'minecraft_runtime_tested':False}
(repo/'log-fixes-2026-10-10/emf-validation.json').write_text(json.dumps(report,indent=2)+'\n');print('Fixed',total,'decimal literals in',len(changed),'models')
