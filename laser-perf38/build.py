from pathlib import Path
import zipfile,subprocess,hashlib,json
r=Path(__file__).resolve().parent;base=r/'base/melee37.jar';out=r/'output/zomboid-global-melee-animations-2.3.1-v38-laser-depth-on-demand.jar'
assert all(hashlib.sha256((r/'base'/n).read_bytes()).hexdigest()==h for n,h in json.loads((r/'inputs.json').read_text()).items())
(r/'build/classes').mkdir(parents=True,exist_ok=True);out.parent.mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(r.parent/'hordes-melee-interiors-patch/build/tools/ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(base),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
t=r.parent/'hordes-melee-interiors-patch/build/tools';asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',asm,'-d',str(r/'build/classes'),str(r/'Hooks.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/classes')+':'+asm,'Hooks',str(r/'build/CorpseAnimation38.class')],check=True)
with zipfile.ZipFile(base)as z:e={n:z.read(n)for n in z.namelist()}
for p in (r/'build/classes/dev').rglob('*.class'):e[p.relative_to(r/'build/classes').as_posix()]=p.read_bytes()
e['dev/zomboidmelee/mixin/CorpseAnimation38.class']=(r/'build/CorpseAnimation38.class').read_bytes()
cfg=json.loads(e['zomboid_global_melee.mixins.json']);cfg['client'].append('CorpseAnimation38');e['zomboid_global_melee.mixins.json']=json.dumps(cfg,indent=2).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):z.writestr(n,b)
(r/'validation.json').write_text(json.dumps(dict(file=out.name,base_sha256=hashlib.sha256(base.read_bytes()).hexdigest(),sha256=hashlib.sha256(out.read_bytes()).hexdigest(),minecraft_runtime_tested=False),indent=2))
