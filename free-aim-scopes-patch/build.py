#!/usr/bin/env python3
"""Rebuild exact uploaded mod variants and verify integrity; Java 21 required."""
import hashlib,json,pathlib,subprocess,urllib.request,zipfile,os
ROOT=pathlib.Path(__file__).resolve().parent
os.chdir(ROOT)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*args):subprocess.run(list(map(str,args)),check=True)
TOOLS={
 'ecj':('org/eclipse/jdt/ecj/3.39.0/ecj-3.39.0.jar','01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948'),
 'asm':('org/ow2/asm/asm/9.7.1/asm-9.7.1.jar','8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281'),
 'asm-tree':('org/ow2/asm/asm-tree/9.7.1/asm-tree-9.7.1.jar','9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f'),
 'asm-analysis':('org/ow2/asm/asm-analysis/9.7.1/asm-analysis-9.7.1.jar','85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2')}
for directory in ['build/tools','build/classes','build/tool','build/tests','output']:pathlib.Path(directory).mkdir(parents=True,exist_ok=True)
for name,(url,digest) in TOOLS.items():
 p=pathlib.Path('build/tools')/(name+'.jar')
 if not p.exists():urllib.request.urlretrieve('https://repo.maven.apache.org/maven2/'+url,p)
 assert sha(p)==digest,('tool checksum',name)
inputs=json.loads(pathlib.Path('inputs.sha256.json').read_text())
for name,digest in inputs.items():
 p=pathlib.Path('base')/name
 if not p.exists():
  import shutil
  shutil.copyfile(ROOT.parent/'mods'/name,p)
 assert sha(p)==digest,('input checksum',name)
base=[pathlib.Path('base')/name for name in inputs]
cp=os.pathsep.join(map(str,base))
compiler=['java','-jar','build/tools/ecj.jar','-21','-proc:none','-nowarn']
run(*compiler,'-cp',cp,'-d','build/classes',*pathlib.Path('src').rglob('*.java'))
asm=os.pathsep.join(str(pathlib.Path('build/tools')/(n+'.jar')) for n in ['asm','asm-tree','asm-analysis'])
run(*compiler,'-cp',asm,'-d','build/tool','Patch.java')
report={'minecraft_runtime_tested':False,'checks':'33 math/pose checks plus ASM BasicVerifier on changed methods, ZIP CRC and unchanged-resource checks','mods':[]}
for kind,needle,old,new in [('iso','dungeons_iso','.27-corpse-outline','.28-free-aim-scopes'),('tps','tp_shooting','fov-compat.1','free-aim-scopes.2'),('tacz','tacz-neoforge','.12-instant-door-kick','.13-free-aim-scopes')]:
 source=next(p for p in base if p.name.startswith(needle));target=pathlib.Path('output')/source.name.replace(old,new)
 run('java','-cp',asm+os.pathsep+'build/tool','Patch',kind,source,target,'build/classes')
 with zipfile.ZipFile(source) as before,zipfile.ZipFile(target) as after:
  assert after.testzip() is None,'ZIP CRC failure'
  assert len(after.namelist())==len(set(after.namelist())),'duplicate entries'
  assert set(before.namelist())<=set(after.namelist()),'lost entries'
  changed=[n for n in before.namelist() if before.read(n)!=after.read(n)]
  assert all(n.endswith('.class') for n in changed),'resources changed'
  expected=json.loads(pathlib.Path('expected-changes.json').read_text())[target.name]
  assert sorted(changed)==sorted(expected['changed_classes']),'unexpected class changes'
  assert sorted(set(after.namelist())-set(before.namelist()))==expected['added_classes'],'unexpected additions'
  report['mods'].append({'file':target.name,'sha256':sha(target),'changed_classes':changed,'added_classes':sorted(set(after.namelist())-set(before.namelist()))})
outputcp=os.pathsep.join(str(p) for p in pathlib.Path('output').glob('*.jar'))
run(*compiler,'-cp',outputcp,'-d','build/tests','tests/Checks.java')
run('java','-Xverify:all','-cp','build/tests'+os.pathsep+outputcp,'Checks')
pathlib.Path('output/validation.json').write_text(json.dumps(report,indent=2)+'\n')
