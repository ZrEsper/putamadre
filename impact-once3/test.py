from pathlib import Path
import subprocess,json,os
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];out=next(Path('output').glob('*.jar'));base=root/'mods/tacz-tactical-1.2.4-neoforge-1.21.1-impact-expiry-180s.2.jar'
def run(*a):
 p=subprocess.run(list(map(str,a)),text=True,capture_output=True)
 if p.returncode:print(p.stdout,p.stderr);p.check_returncode()
 return p.stdout
run('python','tests/fixtures.py');run(*ecj,'-cp',out,'-d','build/tests',*Path('build/fixture-src').rglob('*.java'),*Path('tests/dev').rglob('*.java'));results=[]
for kind,jar in [('old',base),('new',out)]:results.append(run('java','-Xverify:all','-cp','build/tests:'+str(jar),'dev.tactical.tacz.EffectsChecks',kind))
# Existing deadline regression suite, against .3 production classes.
run(*ecj,'-cp',out,'-d','build/expiry',root/'impact-expiry2/tests/dev/tactical/tacz/ExpiryChecks.java');results.append(run('java','-Xverify:all','-cp','build/expiry:'+str(out),'dev.tactical.tacz.ExpiryChecks'))
print(''.join(results),end='');Path('test-results.json').write_text(json.dumps({'checks':results,'minecraft_runtime_tested':False,'method':'Actual native ClientEffects and ImpactMarks, API doubles, original .2 comparison and existing three-minute expiry regression suite'},indent=2)+'\n')
