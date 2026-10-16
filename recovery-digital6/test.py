from pathlib import Path
import os,subprocess,json,zipfile,math
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];reports=json.loads(Path('validation.json').read_text());out={k:Path('output')/v['output']for k,v in reports.items()};checks=[]
def run(*a):
 p=subprocess.run(list(map(str,a)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
# Existing reusable doubles, with no earlier timing assertions.
fixture=root/'turret-power4/tests/fixtures.py';exec(compile(fixture.read_text(),str(fixture),'exec'),{'__file__':str(r/'tests/fixtures.py')})
for d in ['build/tests/turret','build/tests/aid']:Path(d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',out['turret'],'-d','build/tests/turret',*Path('build/fixtures-src').rglob('*.java'),*Path('tests/turret').rglob('*.java'));checks.append(run('java','-Xverify:all','-cp','build/tests/turret:'+str(out['turret']),'dev.zomboid.turretpower.TimingChecks'))
run(*ecj,'-cp',out['aid'],'-d','build/tests/aid',*Path('tests/aid').rglob('*.java'));checks.append(run('java','-Xverify:all','-cp','build/tests/aid:'+str(out['aid']),'dev.zomboid.firstaid.BlackoutChecks'))
with zipfile.ZipFile(out['aid'])as z:
 languages={s:json.loads(z.read('assets/firstaid/lang/'+s+'.json'))for s in ['en_us','es_es','es_mx']};keys=[k for k in languages['en_us']if k.startswith('firstaid.blackout.')];assert len(keys)==9
 assert set(keys)<=set(languages['es_es']) and all(languages['en_us'][k]!=languages['es_es'][k]for k in keys)
with zipfile.ZipFile(out['turret'])as new,zipfile.ZipFile(Path('base')/json.loads(Path('inputs.json').read_text())['turret']['file'])as old:
 for n in old.namelist():
  if n not in ['dev/zomboid/turretpower/Link.class','dev/zomboid/turretpower/Link$Burst.class']:assert old.read(n)==new.read(n),n
checks.append('Original turret native fire/LOS, sounds, scrap recipes and assets unchanged; ES/EN/MX blackout translations complete')
Path('test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False,'method':'Production turret Link with generator/event API doubles; actual blackout Screen with GUI API doubles, real Assist.defib inventory bridge and packet-count checks; JVM -Xverify:all'},indent=2)+'\n')
