from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';jar=r/'output/zomboid-zombie-gore-1.0.14-neoforge-1.21.1-connected-anatomy.jar';out=r/'build/test';out.mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(jar),'-d',str(out),*map(str,(r/'tests').rglob('*.java'))],check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(jar),'dev.zomboid.gore.AnatomyChecks14',str(r/'build/anatomy.json')],capture_output=True,text=True);print(p.stdout,p.stderr);p.check_returncode()
effects=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(jar),'dev.zomboid.gore.EffectsChecks14'],capture_output=True,text=True);print(effects.stdout,effects.stderr);effects.check_returncode()
model=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+str(jar),'dev.zomboid.gore.ModelChecks14'],capture_output=True,text=True);print(model.stdout,model.stderr);model.check_returncode()
with zipfile.ZipFile(r/'base/gore13.jar')as a,zipfile.ZipFile(jar)as b:
 assert a.read('zomboid_gore.mixins.json')==b.read('zomboid_gore.mixins.json')
 for n in a.namelist():
  if n.startswith(('assets/','data/')) and n!='assets/zomboid_gore/sounds.json':assert a.read(n)==b.read(n)
with zipfile.ZipFile(r/'base/ragdoll2.jar')as a,zipfile.ZipFile(r/'output/ragdollified-1.21.1-1.1.5-gore-light.3.jar')as b:
 changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];assert changed==['META-INF/neoforge.mods.toml','com/raiiiden/ragdollified/client/ClientRagdollRenderer.class']or set(changed)=={'META-INF/neoforge.mods.toml','com/raiiiden/ragdollified/client/ClientRagdollRenderer.class'}
(r/'test-results.json').write_text(json.dumps(dict(result=p.stdout.strip(),effects=effects.stdout.strip(),hierarchy=model.stdout.strip(),ragdoll_changes=changed,minecraft_runtime_tested=False),indent=2))
