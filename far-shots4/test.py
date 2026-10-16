from pathlib import Path
import os,subprocess,json,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';out=next(Path('output').glob('*.jar'));Path('build/tests').mkdir(exist_ok=True)
def run(*a):
 p=subprocess.run(list(map(str,a)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',out,'-d','build/tests',*[x for x in Path('tests').rglob('*.java') if x.name!='NativeFarChecks.java']);checks=[run('java','-Xverify:all','-cp','build/tests:'+str(out),'FarChecks')]
cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',cp,'-d','build/tests','tests/NativeFarChecks.java')
checks.append(run('java','-cp','build/tests:'+cp,'NativeFarChecks',root/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.18-spas-reload-guard.jar'))
with zipfile.ZipFile(out)as z,zipfile.ZipFile(root/'mods/tacz-tactical-1.2.4-neoforge-1.21.1-impact-once-180s.3.jar')as old:
 config=json.loads(z.read('tacz_tactical.mixins.json'));assert 'FarShotRangeMixin' in config['mixins'] and 'FarShotRangeMixin' not in config['client']
 for n in old.namelist():
  if n!='tacz_tactical.mixins.json':assert z.read(n)==old.read(n),n
# Re-run the native one-shot impact and expiry regressions against this .4 output.
fixture=root/'impact-once3/tests/fixtures.py'
exec(compile(fixture.read_text(),str(fixture),'exec'),{'__file__':str(r/'tests/fixtures.py')})
run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',out,'-d','build/effects',*Path('build/fixture-src').rglob('*.java'),*list((root/'impact-once3/tests/dev').rglob('*.java')))
checks.append(run('java','-Xverify:all','-cp','build/effects:'+str(out),'dev.tactical.tacz.EffectsChecks','new'))
run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',out,'-d','build/expiry',root/'impact-expiry2/tests/dev/tactical/tacz/ExpiryChecks.java')
checks.append(run('java','-Xverify:all','-cp','build/expiry:'+str(out),'dev.tactical.tacz.ExpiryChecks'))
checks.append('All original .3 class files unchanged; long-shot hook installed on both client and server')
Path('test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False},indent=2)+'\n')
