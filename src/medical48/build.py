from pathlib import Path
import subprocess,zipfile,json
r=Path(__file__).parent;t=Path('/workspace/putamadre/hordes-melee-interiors-patch/build/tools');g=Path('/workspace/medical46/output/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-corpse-bullet-double-fix.31.jar');rag=Path('/workspace/medical45/release/ragdollified-1.21.1-1.1.5-single-corpse.24.jar')
for p in [r/'build/classes',r/'build/tools',r/'output']:p.mkdir(parents=True,exist_ok=True)
e=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
subprocess.run(e+['-cp',str(g)+':/workspace/medical45/release/tacznpcs-2.1.0-1.21.1-tactical.31-single-corpse.jar','-d',str(r/'build/classes')]+list(map(str,(r/'src').rglob('*.java'))),check=True)
subprocess.run(e+['-cp',asm,'-d',str(r/'build/tools'),str(r/'Patch48.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Patch48',str(g),str(rag),str(r/'build/classes'),str(r/'output')],check=True)
