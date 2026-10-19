from pathlib import Path
import subprocess,zipfile,hashlib,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
inputs={
'gore':(root/'gore-anatomy20/output/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-emf-wounds.jar','a2d6c415d213d248a31fc3e069c79e49a147b984f71d4f0b38dc954610d19be6'),
'rag':(root/'gore-anatomy20/output/ragdollified-1.21.1-1.1.5-gore-light.16-emf-compat.jar','0c9f15a9b1e51efd07e4f608cf2689a8ff0a42c4a8a250b965d44319dcb67551'),
'flash':(root/'flashlight-recovery17/output/flashlight-5.1-neoforge-1.21.1-native-capture-recovery.17.jar','44202d6398edd22cb872b02af14a89086c15d4feca2500267e39c0985aba5f7c')}
for p,h in inputs.values():assert hashlib.sha256(p.read_bytes()).hexdigest()==h
(r/'build/classes').mkdir(parents=True,exist_ok=True);(r/'output').mkdir(exist_ok=True)
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar')) for n in ['asm','asm-tree','asm-analysis'])
subprocess.run([*ecj,'-cp',':'.join(str(p)for p,h in inputs.values()),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/classes'),str(r/'Patch.java')],check=True)
def merge(key,name,prefix):
 with zipfile.ZipFile(inputs[key][0])as z:e={n:z.read(n)for n in z.namelist()}
 for p in (r/'build/classes'/prefix).rglob('*.class'):
  if p.name!='Capture21.class':e[p.relative_to(r/'build/classes').as_posix()]=p.read_bytes()
 with zipfile.ZipFile(r/'output'/name,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
merge('gore','zomboid-zombie-gore-1.0.20-neoforge-1.21.1-layer-capture-fix.21.jar','dev/zomboid/gore')
merge('flash','flashlight-5.1-neoforge-1.21.1-gl-error-isolation.18.jar','net/flashlight/battery')
subprocess.run(['java','-cp',str(r/'build/classes')+':'+asm,'Patch',str(inputs['rag'][0]),str(r/'build/classes/dev/zomboid/gore/Capture21.class'),str(r/'output/ragdollified-1.21.1-1.1.5-humanoid-texture-cache.17.jar')],check=True)
(r/'validation.json').write_text(json.dumps({'inputs':{k:{'path':str(p.relative_to(root)),'sha256':h}for k,(p,h)in inputs.items()},'outputs':{p.name:hashlib.sha256(p.read_bytes()).hexdigest()for p in(r/'output').glob('*.jar')},'minecraft_runtime_tested':False},indent=2)+'\n')
