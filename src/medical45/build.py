from pathlib import Path
import subprocess,json,hashlib,zipfile
r=Path(__file__).resolve().parent;t=Path('/workspace/putamadre/hordes-melee-interiors-patch/build/tools');out=r/'build'
for p in [out/'classes',out/'tools',r/'output']:p.mkdir(parents=True,exist_ok=True)
inputs=json.loads((r/'inputs.json').read_text())
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn']
subprocess.run(ecj+['-cp',':'.join(inputs),'-d',str(out/'classes')]+list(map(str,(r/'src').rglob('*.java'))),check=True)
asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
subprocess.run(ecj+['-cp',asm,'-d',str(out/'tools'),str(r/'Patch45.java')],check=True)
subprocess.run(['java','-cp',str(out/'tools')+':'+asm,'Patch45']+inputs+[str(out/'classes'),str(r/'output')],check=True)
(r/'input-hashes.json').write_text(json.dumps({p:hashlib.sha256(Path(p).read_bytes()).hexdigest()for p in inputs},indent=2))
