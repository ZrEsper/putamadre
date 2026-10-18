from pathlib import Path
import json,hashlib,zipfile,subprocess
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';base=r/'base/melee35.jar';pins={base.name:hashlib.sha256(base.read_bytes()).hexdigest()};p=r/'inputs.json'
if p.exists():assert json.loads(p.read_text())==pins
else:p.write_text(json.dumps(pins,indent=2)+'\n')
for n in ['build/classes','output']:(r/n).mkdir(parents=True,exist_ok=True)
cp=str(base)+':'+str(r.parent/'action34/build/api');subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
with zipfile.ZipFile(base)as z:e={n:z.read(n)for n in z.namelist()}
for p in (r/'build/classes').rglob('*.class'):e[p.relative_to(r/'build/classes').as_posix()]=p.read_bytes()
out=r/'output/zomboid-global-melee-animations-2.3.1-v36-laser-color-only.jar'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
 changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];assert set(changed)=={'dev/zomboidmelee/boxing/Laser32.class'};assert b.testzip()is None
(r/'validation.json').write_text(json.dumps({'file':out.name,'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'only_existing_class_changed':changed,'minecraft_runtime_tested':False},indent=2)+'\n')
