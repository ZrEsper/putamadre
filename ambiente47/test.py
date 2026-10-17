from pathlib import Path
import subprocess,json,zipfile,hashlib
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';reports=json.loads((r/'validation.json').read_text());cp=':'.join(str(r/'output'/v['output'])for v in reports.values())+':'+str(root/'npc-social17/build/tools/gson-2.10.1.jar');ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn']
def run(*a):
 p=subprocess.run(list(map(str,a)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
exec(compile((r/'fixtures.py').read_text(),'fixtures.py','exec'),{'__file__':str(r/'fixtures.py')});out=r/'build/test';out.mkdir(exist_ok=True)
run(*ecj,'-cp',cp,'-d',out,*(r/'build/fixtures-src').rglob('*.java'),*(r/'tests').rglob('*.java'))
checks=[run('java','-Xverify:all','-cp',str(out)+':'+cp,'dev.zomboid.hordes.EnvironmentChecks47'),run('java','-Xverify:all','-cp',str(out)+':'+cp,'dev.zomboid.scavenging.BridgeChecks47')]
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Inspect.java')
checks.append(run('java','-cp',str(r/'build/tool')+':'+asm,'Inspect',r/'output'/reports['hordes']['output'],r/'output'/reports['quiet']['output']))
for kind,v in reports.items():
 with zipfile.ZipFile(r/'base'/(kind+'.jar'))as old,zipfile.ZipFile(r/'output'/v['output'])as new:
  assert new.testzip()is None
  for n in old.namelist():
   if '/textures/'in n or '/sounds/'in n or n.endswith('/PlayerStorage.class') or n.startswith('dev/zomboid/storage/') or n=='META-INF/neoforge.mods.toml':assert old.read(n)==new.read(n),n
  assert not any(n.startswith('net/minecraft/')for n in new.namelist())
checks.append('UPSTREAM_ASSETS_METADATA_PLAYER_STORAGE_PRESERVED_CHECKS=2')
(r/'test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False},indent=2)+'\n')
