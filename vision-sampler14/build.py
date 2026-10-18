from pathlib import Path
import hashlib,json,subprocess,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';base=r/'base/fracture13.jar';pins={base.name:hashlib.sha256(base.read_bytes()).hexdigest()};pin=r/'inputs.json'
if pin.exists():assert json.loads(pin.read_text())==pins
else:pin.write_text(json.dumps(pins,indent=2)+'\n')
for n in ['build/classes','output']:(r/n).mkdir(parents=True,exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(base),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
with zipfile.ZipFile(base)as z:e={n:z.read(n)for n in z.namelist()}
for p in (r/'build/classes').rglob('*.class'):e[p.relative_to(r/'build/classes').as_posix()]=p.read_bytes()
out=r/'output/fracturepoint-1.21.1-3.0.0-PT2-nvg-sampler-safe.14.jar'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
(r/'validation.json').write_text(json.dumps({'file':out.name,'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'minecraft_runtime_tested':False},indent=2)+'\n')
