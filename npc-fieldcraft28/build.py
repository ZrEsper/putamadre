from pathlib import Path
import hashlib,json,subprocess,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';base=r/'base/npc27.jar'
for name,digest in json.loads((r/'inputs.json').read_text()).items():assert hashlib.sha256((r/'base'/name).read_bytes()).hexdigest()==digest
for d in ['build/classes','build/tools','output']:(r/d).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run([*ecj,'-cp',str(base),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tools'),str(r/'Patch.java')],check=True)
out=r/'output/tacznpcs-2.1.0-1.21.1-tactical.28-fieldcraft-hearing.jar'
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Patch',str(base),str(out),str(r/'build/classes')],check=True)
with zipfile.ZipFile(base)as z:old={n:z.read(n)for n in z.namelist()}
with zipfile.ZipFile(out)as z:new={n:z.read(n)for n in z.namelist()}
assets=[n for n in old if n.startswith(('assets/','data/'))];assert all(new[n]==old[n]for n in assets)
assert new['tacznpcs.mixins.json']==old['tacznpcs.mixins.json']
(r/'validation.json').write_text(json.dumps(dict(file=out.name,sha256=hashlib.sha256(out.read_bytes()).hexdigest(),changed=[n for n in old if old[n]!=new[n]],added=[n for n in new if n not in old],assets_unchanged=len(assets),minecraft_runtime_tested=False),indent=2))
