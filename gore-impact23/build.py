from pathlib import Path
import subprocess,hashlib,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';g=root/'render-repair21/output/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-layer-capture-fix.21.jar';rag=root/'render-repair21/output/ragdollified-1.21.1-1.1.5-module-capture-fix.18.jar'
for name,digest in json.loads((r/'inputs.json').read_text()).items():assert hashlib.sha256((root/name).read_bytes()).hexdigest()==digest
for folder in ['build/classes','build/tools','output']:(r/folder).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run([*ecj,'-cp',str(g)+':'+str(rag),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tools'),str(r/'Patch.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Patch',str(g),str(rag),str(r/'build/classes'),str(r/'output/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-visible-tissue-debris.23.jar'),str(r/'output/ragdollified-1.21.1-1.1.5-low-impact.19.jar')],check=True)
(r/'validation.json').write_text(json.dumps({'inputs':{str(p.relative_to(root)):hashlib.sha256(p.read_bytes()).hexdigest()for p in [g,rag]},'outputs':{p.name:hashlib.sha256(p.read_bytes()).hexdigest()for p in(r/'output').glob('*.jar')},'minecraft_runtime_tested':False},indent=2)+'\n')
