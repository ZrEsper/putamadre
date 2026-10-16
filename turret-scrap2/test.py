from pathlib import Path
import subprocess,os,json,zipfile,shutil
r=Path(__file__).resolve().parent;os.chdir(r);t=r.parent/'hordes-melee-interiors-patch/build/tools';asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
def run(*a):
 p=subprocess.run(list(map(str,a)),text=True,capture_output=True)
 if p.returncode:print(p.stdout,p.stderr);p.check_returncode()
 return p.stdout
report=json.loads(Path('validation.json').read_text());jars={k:'output/'+v['output']for k,v in report.items()};cp=':'.join(jars.values())
shutil.rmtree('build/tests',ignore_errors=True);shutil.rmtree('build/native',ignore_errors=True);run('python','tests/fixtures.py');run(*ecj,'-cp',cp,'-d','build/tests',*Path('build/fixtures-src').rglob('*.java'),'tests/dev/zomboid/turretpower/ScrapChecks.java');run(*ecj,'-cp',asm,'-d','build/tool','tests/NativeProbe.java');run('java','-cp',asm+':build/tool','NativeProbe',jars['hordes'],'build/native')
result=run('java','-cp','build/native:build/tests:'+cp,'dev.zomboid.turretpower.ScrapChecks');print(result,end='')
with zipfile.ZipFile(jars['turret'])as z:
 recipe=json.loads(z.read('data/tacz_turrets/recipe/electronic_scrap.json'));assert {i['item']for i in recipe['ingredients']}=={'minecraft:lapis_lazuli','minecraft:redstone','minecraft:copper_ingot','minecraft:iron_ingot'};assert recipe['result']['count']==2
 turret=json.loads(z.read('data/tacz_turrets/recipe/turret.json'));assert sum(row.count('S')for row in turret['pattern'])==2 and turret['key']['S']['item']=='tacz_turrets:electronic_scrap'
 with zipfile.ZipFile(r.parent/json.loads(Path('inputs.json').read_text())['turret']['file'])as old:
  for n in ['com/entropy/tacz_turrets/turret/TurretEntity.class','dev/zomboid/turretpower/Link.class','dev/zomboid/turretpower/FireEvents.class']:assert z.read(n)==old.read(n)
 for s in ['servo_idle','servo_move','target_alert']:assert z.read('assets/tacz_turrets/sounds/'+s+'.ogg').startswith(b'OggS')
Path('test-results.json').write_text(json.dumps({'checks':result,'crafting_and_preserved_firing_bytes_verified':True,'minecraft_runtime_tested':False},indent=2)+'\n')
