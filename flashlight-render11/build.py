from pathlib import Path
import subprocess,hashlib,json,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';base=r/'base/flash8.jar';assert hashlib.sha256(base.read_bytes()).hexdigest()==json.loads((r/'inputs.json').read_text())['flash8.jar']
(r/'build/tool').mkdir(parents=True,exist_ok=True)
(r/'build/classes').mkdir(parents=True,exist_ok=True);(r/'output').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(base),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',asm,'-d',str(r/'build/tool'),str(r/'Hooks.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tool')+':'+asm,'Hooks',str(r/'build/CaptureStage9.class')],check=True)
with zipfile.ZipFile(base)as z:e={n:z.read(n)for n in z.namelist()}
e['net/flashlight/mixin/CaptureStage9.class']=(r/'build/CaptureStage9.class').read_bytes()
cfg=json.loads(e['flashlight.mixins.json']);cfg['client'].append('CaptureStage9');e['flashlight.mixins.json']=json.dumps(cfg,indent=2).encode()
for p in (r/'build/classes').rglob('*.class'):e[p.relative_to(r/'build/classes').as_posix()]=p.read_bytes()
for ext in ['vert','frag']:e['assets/flashlight/shaders/portable6.'+ext]=(r/'shaders'/('light.'+ext)).read_bytes()
out=r/'output/flashlight-5.1-neoforge-1.21.1-wide-unified-beam.11.jar'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:assert b.testzip()is None;changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()))
(r/'validation.json').write_text(json.dumps(dict(file=out.name,sha256=hashlib.sha256(out.read_bytes()).hexdigest(),changed=changed,added=added,minecraft_runtime_tested=False),indent=2)+'\n')
