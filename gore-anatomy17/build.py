from pathlib import Path
import subprocess,zipfile,json,hashlib
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools'
for name,digest in json.loads((r/'inputs.json').read_text()).items():assert hashlib.sha256((r.parent/'gore-anatomy14/base'/name).read_bytes()).hexdigest()==digest
for p in ['build/classes','build/tools','output']:(r/p).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run([*ecj,'-cp',str(r.parent/'gore-anatomy14/base/gore13.jar'),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tools'),str(r/'Patch.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Patch',str(r.parent/'gore-anatomy14/base/gore13.jar'),str(r.parent/'gore-anatomy14/base/ragdoll2.jar'),str(r/'build/classes'),str(r/'output')],check=True)
# Existing sounds are merged; these clips are short, not long looping ambience.
gore=r/'output/zomboid-zombie-gore-1.0.17-neoforge-1.21.1-reference-skeleton.jar'
with zipfile.ZipFile(gore)as z:entries={n:z.read(n)for n in z.namelist()}
sounds=json.loads(entries.get('assets/zomboid_gore/sounds.json',b'{}'))
for name in ['artery_pulse14','wet_impact14']:
 sounds[name]={'sounds':[{'name':'zomboid_gore:'+name,'volume':1,'pitch':1}]};entries['assets/zomboid_gore/sounds/'+name+'.ogg']=(r/'audio'/ (name+'.ogg')).read_bytes()
entries['assets/zomboid_gore/sounds.json']=json.dumps(sounds,indent=2).encode()
with zipfile.ZipFile(gore,'w',zipfile.ZIP_DEFLATED)as z:
 for name,data in sorted(entries.items()):i=zipfile.ZipInfo(name,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,data)
(r/'validation.json').write_text(json.dumps({p.name:hashlib.sha256(p.read_bytes()).hexdigest()for p in sorted((r/'output').glob('*.jar'))},indent=2))
