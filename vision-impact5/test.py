from pathlib import Path
import os,subprocess,json,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';reports=json.loads(Path('validation.json').read_text());inputs=json.loads(Path('inputs.json').read_text());ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);out={k:Path('output')/v['output']for k,v in reports.items()};checks=[]
def run(*a):
 p=subprocess.run(list(map(str,a)),text=True,capture_output=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
for d in ['build/tests/nvg','build/tests/tac','build/tests/integration','build/probe/old','build/probe/new']:Path(d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',out['nvg'],'-d','build/tests/nvg',*Path('tests/nvg').rglob('*.java'));checks.append(run('java','-Xverify:all','-cp','build/tests/nvg:'+str(out['nvg']),'checks.NvgChecks'))
fixture=root/'impact-once3/tests/fixtures.py';exec(compile(fixture.read_text(),str(fixture),'exec'),{'__file__':str(r/'tests/fixtures.py')});run(*ecj,'-cp',out['tac'],'-d','build/tests/tac',*Path('build/fixture-src').rglob('*.java'),*Path('tests/tac').rglob('*.java'))
for cls in ['EffectsChecks','ExpiryChecks','CapacityChecks']:checks.append(run('java','-Xverify:all','-cp','build/tests/tac:'+str(out['tac']),'dev.tactical.tacz.'+cls,'new'))
# Compile a native-method extraction tool, preserving the filter instructions and fields.
run(*ecj,'-cp',asm,'-d','build/tool','tests/ExtractPain.java')
for kind,jar in [('old',Path('base')/inputs['aid']['file']),('new',out['aid'])]:run('java','-cp','build/tool:'+asm,'ExtractPain',jar,'build/probe/'+kind)
cp=':'.join(str(p)for p in out.values());run(*ecj,'-cp',cp,'-d','build/tests/integration',*Path('tests/integration').rglob('*.java'))
for kind in ['old','new']:checks.append(run('java','-Xverify:all','-cp','build/tests/integration:build/probe/'+kind+':'+cp,'checks.FilterChecks',kind))
checks.append(run('java','-Xverify:all','-cp','build/tests/integration:'+str(out['aid']),'checks.AbsentChecks'))
Path('test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False,'method':'Actual native ImpactMarks/Mark with API particle doubles; manual visor/battery regression; actual native ShaderRegistry plus extracted unchanged native FirstAid updateEffect/forceShutdown/load selection instructions; original conflict reproduced and compared to patched methods; API renderer counts instead of OpenGL'},indent=2)+'\n')
