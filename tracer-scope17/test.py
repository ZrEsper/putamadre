from pathlib import Path
import os,sys,subprocess,json
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';v=json.loads(Path('validation.json').read_text());cp=os.pathsep.join(str(Path('output')/d['output'])for d in v.values());Path('build/tests').mkdir(exist_ok=True)
def run(*a):return subprocess.run(list(map(str,a)),check=True,text=True,capture_output=True)
result=run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',cp,'-d','build/tests',*Path('tests').rglob('*.java'));print(result.stdout)
result=run('java','-Xverify:all','-cp','build/tests'+os.pathsep+cp,'checks.ScopeVisualChecks');print(result.stdout);behavior=result.stdout.strip()
asm=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',asm,'-d','build/tool','Verify.java');result=run('java','-cp',asm+os.pathsep+'build/tool','Verify',Path(sys.argv[1]).resolve(),Path('output')/v['tacz']['output']);print(result.stdout)
Path('test-results.json').write_text(json.dumps({'behavior':behavior,'bytecode':result.stdout.strip(),'minecraft_runtime_tested':False},indent=2)+'\n')
