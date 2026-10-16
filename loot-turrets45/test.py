from pathlib import Path
import os,json,subprocess,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';reports=json.loads(Path('validation.json').read_text());cp=':'.join(str(Path('output')/v['output'])for v in reports.values());ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
def run(*a):
 p=subprocess.run(list(map(str,a)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
Path('build/test-loot').mkdir(parents=True,exist_ok=True);run(*ecj,'-cp',cp,'-d','build/test-loot',*Path('tests/loot').rglob('*.java'));checks=[]
for cls in ['dev.zomboid.hordes.FinishChecks','dev.zomboid.scavenging.QuietChecks']:
 args=[str(Path('base')/json.loads(Path('inputs.json').read_text())['quiet']['file'])] if cls.endswith('QuietChecks') else []
 checks.append(run('java','-Xverify:all','-cp','build/test-loot:'+cp,cls,*args))
exec(compile(Path('turret_fixtures.py').read_text(),'turret_fixtures.py','exec'),{'__file__':str(r/'turret_fixtures.py')})
Path('build/test-turret').mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',cp,'-d','build/test-turret',*Path('build/fixtures-src').rglob('*.java'),*Path('tests/turret').rglob('*.java'))
checks.append(run('java','-Xverify:all','-cp','build/test-turret:'+cp,'dev.zomboid.turretpower.TierChecks'))
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*ecj,'-cp',asm,'-d','build/tool','Inspect.java')
checks.append(run('java','-cp','build/tool:'+asm,'Inspect',*[str(Path('output')/reports[k]['output'])for k in ['remains','hordes','quiet','turret']]))
for kind,v in reports.items():
 with zipfile.ZipFile(Path('base')/json.loads(Path('inputs.json').read_text())[kind]['file'])as old,zipfile.ZipFile(Path('output')/v['output'])as new:
  if kind=='remains':
   for n in old.namelist():
    if '/hygiene/'in n or n.endswith('PlayerZombieGuard43.class') or n.endswith('MutantIntegration.class'):assert old.read(n)==new.read(n),n
  if kind=='quiet':
   for n in old.namelist():
    if n.endswith('/PlayerStorage.class')or n.startswith('dev/zomboid/storage/'):assert old.read(n)==new.read(n),n
  if kind=='turret':
   for n in old.namelist():
    if '/sounds/'in n or '/textures/'in n or '/geo/'in n:assert old.read(n)==new.read(n),n
   for tier,label in [('turret_reinforced','industrial'),('turret_advanced','tactical')]:
    recipe=json.loads(new.read('data/tacz_turrets/recipe/'+tier+'.json'));assert recipe['result']=={'id':'tacz_turrets:'+tier,'count':1};assert recipe['key']['T']['item']=='tacz_turrets:'+('turret'if tier=='turret_reinforced'else'turret_reinforced')
    model=json.loads(new.read('assets/tacz_turrets/models/item/'+tier+'.json'));assert model['textures']['layer0']=='tacz_turrets:item/turret_'+label
   for label in ['industrial','tactical']:
    assert new.getinfo('assets/tacz_turrets/geo/entity/turret_'+label+'.geo.json')
    for state in ['active','reloading','no_ammo','no_gun','disabled']:assert new.getinfo('assets/tacz_turrets/textures/entity/turret_'+label+'_'+state+'.png')
checks.append('Native hygiene, player/NPC inventory protection and turret sound/geometry/texture assets preserved; upgrade recipes consume previous tier and all visual states exist')
Path('test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False},indent=2)+'\n')
