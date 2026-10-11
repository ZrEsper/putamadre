from pathlib import Path
import subprocess,sys,os,tempfile,zipfile,hashlib,json,shutil
r=Path(__file__).resolve().parent;inputs=json.loads((r/'inputs.json').read_text());tools=Path(sys.argv[1]);mods=r.parent/'mods'
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
def run(*args):subprocess.run(args,check=True)
with tempfile.TemporaryDirectory() as d:
 d=Path(d);classes=d/'classes';patcher=d/'patcher'
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-d',str(classes),*[str(p) for p in (r/'src').rglob('*.java')],*[str(p) for p in (r/'stubs').rglob('*.java')])
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-cp',asm,'-d',str(patcher),str(r/'Patch.java'))
 results=[]
 for mode in ['hordes','weaker','awareness','survivor']:
  item=inputs[mode];source=Path(item['path']);assert hashlib.sha256(source.read_bytes()).hexdigest()==item['sha256']
  added=d/mode;added.mkdir();package={'hordes':'reactive','weaker':'daylight','awareness':'zabridge'}.get(mode)
  if package:shutil.copytree(classes/'dev/zomboid'/package,added/'dev/zomboid'/package)
  if mode=='hordes':
   (added/'zomboid-reactive.mixins.json').write_text(json.dumps(dict(required=True,minVersion='0.8',package='dev.zomboid.reactive.mixin',compatibilityLevel='JAVA_21',mixins=['MutantBiteMixin'],injectors=dict(defaultRequire=1))))
  if mode=='survivor':
   path=added/'data/survivorcreator/survivor/perks/elusive.json';path.parent.mkdir(parents=True)
   path.write_text(json.dumps(dict(name='Escurridizo',description='Tus pasos se oyen un 45% menos lejos, incluso corriendo. Agacharte reduce aún más el ruido. No silencia disparos ni explosiones.',cost=4,icon='minecraft:leather_boots',requires_professions=[],requires_perks=[],conflicts=[],effects=[]),ensure_ascii=False,indent=2))
  dest=mods/item['output'];run('java','-cp',str(patcher)+os.pathsep+asm,'Patch',mode,str(source),str(dest),str(added))
  with zipfile.ZipFile(source) as a,zipfile.ZipFile(dest) as b:
   assert b.testzip() is None
   changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];new=[n for n in b.namelist() if n not in a.namelist()]
   assert len(changed)=={'hordes':7,'weaker':2,'awareness':1,'survivor':0}[mode],changed
   assert not any(n.startswith(('net/minecraft/','net/neoforged/','org/spongepowered/')) for n in new),'Test stubs must not be packaged'
   results.append(dict(file=dest.name,sha256=hashlib.sha256(dest.read_bytes()).hexdigest(),changed=changed,added=new))
 # Core tests run in a fresh JVM; no Minecraft classes or stubs are shipped in mods.
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-cp',str(classes),'-d',str(classes),str(r/'Checks.java'))
 run('java','-Xverify:all','-cp',str(classes),'dev.zomboid.reactive.Checks')
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-cp',str(classes),'-d',str(classes),*[str(p) for p in (r/'tests').rglob('*.java')])
 run('java','-Xverify:all','-cp',str(classes),'dev.zomboid.reactive.RuntimeChecks')
(r/'validation.json').write_text(json.dumps(dict(artifacts=results,rule_cases=11,simulated_entity_behavior_cases=23,unrelated_entries_preserved=True,minecraft_runtime_tested=False),indent=2)+'\n')
