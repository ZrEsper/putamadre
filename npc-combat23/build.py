from pathlib import Path
import subprocess,hashlib,json
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';base=r/'base/npc22.jar';assert hashlib.sha256(base.read_bytes()).hexdigest()=='e760038714e6511b92ee46d5191a9c562622743a946ea2a71fdc15255715636f'
for p in ['build/classes','build/tools','output']:(r/p).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar')) for n in ['asm','asm-tree','asm-analysis'])
subprocess.run([*ecj,'-cp',str(base),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tools'),str(r/'Patch.java')],check=True)
out=r/'output/tacznpcs-2.1.0-1.21.1-tactical.23-hostile-night-combat.jar'
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Patch',str(base),str(out),str(r/'build/classes')],check=True)
(r/'validation.json').write_text(json.dumps(dict(base_sha256=hashlib.sha256(base.read_bytes()).hexdigest(),file=out.name,sha256=hashlib.sha256(out.read_bytes()).hexdigest(),minecraft_runtime_tested=False),indent=2))
