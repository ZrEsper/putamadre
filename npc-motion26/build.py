from pathlib import Path
import hashlib,json,subprocess,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';base=r/'base/npc25.jar';pins=json.loads((r/'inputs.json').read_text());assert all(hashlib.sha256((r/'base'/name).read_bytes()).hexdigest()==digest for name,digest in pins.items())
for d in ['build/classes','build/tools','output']:(r/d).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run([*ecj,'-cp',str(base),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tools'),str(r/'Patch.java'),str(r/'Hooks.java')],check=True)
out=r/'output/tacznpcs-2.1.0-1.21.1-tactical.26-look-slide-flash.jar'
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Patch',str(base),str(out),str(r/'build/classes')],check=True)
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Hooks',str(r/'build/NpcMuzzle26.class')],check=True)
with zipfile.ZipFile(out)as z:e={n:z.read(n)for n in z.namelist()}
e['mod/cdv/tnpc/mixin/NpcMuzzle26.class']=(r/'build/NpcMuzzle26.class').read_bytes();cfg=json.loads(e['tacznpcs.mixins.json']);cfg['client'].append('NpcMuzzle26');e['tacznpcs.mixins.json']=json.dumps(cfg,indent=2).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
(r/'validation.json').write_text(json.dumps(dict(file=out.name,sha256=hashlib.sha256(out.read_bytes()).hexdigest(),minecraft_runtime_tested=False),indent=2))
