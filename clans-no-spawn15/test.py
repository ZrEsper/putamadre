from pathlib import Path
import subprocess,os,json
r=Path(__file__).resolve().parent;os.chdir(r);t=r.parent/'hordes-melee-interiors-patch/build/tools';cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
def run(*a):
 p=subprocess.run(list(map(str,a)),text=True,capture_output=True)
 if p.returncode:print(p.stdout,p.stderr);p.check_returncode()
 return p.stdout
run('python','tests/fixtures.py');base=next(Path('inputs').glob('*.jar'));out=next(Path('output').glob('*.jar'))
run(*ecj,'-cp',str(out)+':build/classes','-d','build/tests','tests/dev/zomboid/clans/Checks.java',*Path('build/fixture-src').rglob('*.java'))
run(*ecj,'-cp',cp,'-d','build/tool','tests/NativeProbe.java')
results=[]
for kind,src in [('old',base),('new',out)]:
 native='build/native-'+kind;run('java','-cp',cp+':build/tool','NativeProbe',src,native)
 results.append(run('java','-cp',native+':build/tests:build/classes:'+str(out),'dev.zomboid.clans.Checks',kind))
print(''.join(results),end='');Path('test-results.json').write_text(json.dumps({'results':results,'method':'Exact original and patched native join / grief bodies, production helper and API doubles','minecraft_runtime_tested':False},indent=2)+'\n')
