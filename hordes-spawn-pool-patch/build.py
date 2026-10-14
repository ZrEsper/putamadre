#!/usr/bin/env python3
import pathlib,os,subprocess,urllib.request,hashlib,json,zipfile
ROOT=pathlib.Path(__file__).resolve().parent;os.chdir(ROOT)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*args):subprocess.run(list(map(str,args)),check=True)
TOOLS={
 'ecj':('org/eclipse/jdt/ecj/3.39.0/ecj-3.39.0.jar','01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948'),
 'asm':('org/ow2/asm/asm/9.7.1/asm-9.7.1.jar','8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281'),
 'asm-tree':('org/ow2/asm/asm-tree/9.7.1/asm-tree-9.7.1.jar','9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f'),
 'asm-analysis':('org/ow2/asm/asm-analysis/9.7.1/asm-analysis-9.7.1.jar','85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2')}
for d in ['build/tools','build/tool','build/classes','build/tests','output']:pathlib.Path(d).mkdir(parents=True,exist_ok=True)
for name,(url,digest) in TOOLS.items():
 p=pathlib.Path('build/tools')/(name+'.jar')
 if not p.exists():urllib.request.urlretrieve('https://repo.maven.apache.org/maven2/'+url,p)
 assert sha(p)==digest,('tool hash',name)
inputs=json.loads(pathlib.Path('inputs.sha256.json').read_text());assert len(inputs)==1
base=pathlib.Path('base')/next(iter(inputs));assert sha(base)==inputs[base.name]
compiler=['java','-jar','build/tools/ecj.jar','-21','-proc:none','-nowarn']
asm=os.pathsep.join('build/tools/'+n+'.jar' for n in ['asm','asm-tree','asm-analysis'])
run(*compiler,'-cp',base,'-d','build/classes',*pathlib.Path('src').rglob('*.java'))
run(*compiler,'-cp',asm,'-d','build/tool','Patch.java','Fixture.java')
output=pathlib.Path('output/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.28-spawn-personality-pool.jar')
run('java','-cp',asm+os.pathsep+'build/tool','Patch',base,output,'build/classes')
run('java','-cp',asm+os.pathsep+'build/tool','Fixture',output,'build/tests')
run(*compiler,'-cp',output,'-d','build/tests',*pathlib.Path('tests').rglob('*.java'))
for name in ['Checks','NativeChecks']:run('java','-Xverify:all','-cp','build/tests'+os.pathsep+str(output),name)
with zipfile.ZipFile(base) as before,zipfile.ZipFile(output) as after:
 assert after.testzip() is None
 assert len(after.namelist())==len(set(after.namelist()))
 assert set(before.namelist())<=set(after.namelist())
 changed=[n for n in before.namelist() if before.read(n)!=after.read(n)]
 assert all(n.endswith('.class') for n in changed)
 added=sorted(set(after.namelist())-set(before.namelist()))
 assert added==['dev/zomboid/hordes/SpawnPool.class','dev/zomboid/hordes/SpawnPoolUi.class']
 expected=json.loads(pathlib.Path('expected-changes.json').read_text())
 assert sorted(changed)==sorted(expected),'Unexpected class change'
report={'file':output.name,'sha256':sha(output),'behavior_checks':88,'native_methods_executed_with_API_doubles':True,'minecraft_runtime_tested':False,'modified_methods_verified':'ASM BasicVerifier','resources_unchanged':True,'changed_classes':changed,'added_classes':added}
pathlib.Path('output/validation.json').write_text(json.dumps(report,indent=2)+'\n')
print('Integrity and exact changed-class inventory checks passed.')
