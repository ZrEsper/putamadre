from pathlib import Path
import subprocess,hashlib,json,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
def run(*a):subprocess.run(list(map(str,a)),check=True)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
for n,h in json.loads((r/'inputs.json').read_text()).items():assert sha(r/'base'/n)==h
for d in ['build/tool','build/melee','build/iso','output']:(r/d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Patch.java')
# Existing compile-only APIs are never included in either output JAR.
run(*ecj,'-cp',str(r/'base/melee34.jar')+':'+str(r.parent/'action34/build/api'),'-d',r/'build/melee',r/'src/dev/zomboidmelee/boxing/Laser32.java')
run(*ecj,'-cp',r/'base/iso31.jar','-d',r/'build/iso',r/'src/dev/zomboid/isotacz/ShotAim32.java')
results=[]
for kind,base,name in [('melee','melee34.jar','zomboid-global-melee-animations-2.3.1-v35-physical-laser.jar'),('iso','iso31.jar','dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.32-cursor-shot-direction.jar')]:
 out=r/'output'/name;run('java','-cp',str(r/'build/tool')+':'+asm,'Patch',r/'base'/base,out,r/'build'/kind,kind)
 if kind=='iso':
  with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
  key='dungeons_iso.mixins.json';cfg=json.loads(entries[key]);cfg['client']+=['compat.tacz.CursorShoot32','compat.tacz.CursorAuto32','ShoulderFire32'];entries[key]=json.dumps(cfg,indent=2).encode()
  with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
   for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(r/'base'/base)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None;changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith(('net/minecraft/','com/mojang/'))for n in added)
 results.append(dict(file=name,sha256=sha(out),changed=changed,added=added))
(r/'validation.json').write_text(json.dumps(dict(outputs=results,minecraft_runtime_tested=False),indent=2)+'\n')
