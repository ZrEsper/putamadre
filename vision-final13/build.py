from pathlib import Path
import subprocess,json,zipfile,hashlib
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';base=r/'base/fracture12.jar';pins={base.name:hashlib.sha256(base.read_bytes()).hexdigest()};pin=r/'inputs.json'
if pin.exists():assert json.loads(pin.read_text())==pins
else:pin.write_text(json.dumps(pins,indent=2)+'\n')
for name in ['build/classes','build/tool','output']:(r/name).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
subprocess.run([*ecj,'-cp',str(base),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tool'),str(r/'Hooks.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tool')+':'+asm,'Hooks',str(r/'build/NvgFinalPass13.class')],check=True)
with zipfile.ZipFile(base)as z:e={n:z.read(n)for n in z.namelist()}
e.pop('com/raiiiden/warborn/hybrid/VisionStage12.class')
e['com/raiiiden/warborn/mixin/client/NvgFinalPass13.class']=(r/'build/NvgFinalPass13.class').read_bytes()
cfg=json.loads(e['fracturepoint.mixins.json']);cfg['client'].append('client.NvgFinalPass13');e['fracturepoint.mixins.json']=json.dumps(cfg,indent=2).encode()
for p in (r/'build/classes').rglob('*.class'):e[p.relative_to(r/'build/classes').as_posix()]=p.read_bytes()
out=r/'output/fracturepoint-1.21.1-3.0.0-PT2-nvg-hands-final-pass.13.jar'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None
 for n in a.namelist():
  if n.startswith('assets/'):assert a.read(n)==b.read(n)
(r/'validation.json').write_text(json.dumps({'file':out.name,'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'filters':json.loads((r.parent/'nvg-static11/validation.json').read_text())['filters'],'all_existing_assets_unchanged':True,'minecraft_runtime_tested':False},indent=2)+'\n')
