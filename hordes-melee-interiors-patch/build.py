#!/usr/bin/env python3
import pathlib,os,subprocess,urllib.request,hashlib,json,zipfile
from loot_catalog import repair,BASE,CATEGORIES
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
inputs=json.loads(pathlib.Path('inputs.sha256.json').read_text());base=[pathlib.Path('base')/n for n in inputs]
for p in base:assert sha(p)==inputs[p.name],('input hash',p)
compiler=['java','-jar','build/tools/ecj.jar','-21','-proc:none','-nowarn'];cp=os.pathsep.join(map(str,base));asm=os.pathsep.join('build/tools/'+n+'.jar' for n in ['asm','asm-tree','asm-analysis'])
run(*compiler,'-cp',cp,'-d','build/classes',*pathlib.Path('src').rglob('*.java'));run(*compiler,'-cp',asm,'-d','build/tool','Patch.java')
report={'minecraft_runtime_tested':False,'behavior_checks_with_API_doubles':99,'methods_verified':'ASM BasicVerifier','mods':[]}
for kind,prefix,target in [('hordes','zomboid-hordes','zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.29-melee-indoor-loot-guide.jar'),('melee','zomboid-global','zomboid-global-melee-animations-2.3.1-range-plus-one-ezweight-v13-panel-cooldowns.jar')]:
 source=next(p for p in base if p.name.startswith(prefix));output=pathlib.Path('output')/target
 run('java','-cp',asm+os.pathsep+'build/tool','Patch',kind,source,output,'build/classes')
 if kind=='hordes':
  with zipfile.ZipFile(output) as z:entries={n:z.read(n) for n in z.namelist()}
  catalog=repair(entries);pathlib.Path('output/loot-catalog.json').write_text(json.dumps(catalog,indent=2,ensure_ascii=False)+'\n')
  with zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED) as z:
   for n,b in sorted(entries.items()):e=zipfile.ZipInfo(n,(1980,1,1,0,0,0));e.compress_type=zipfile.ZIP_DEFLATED;z.writestr(e,b)
 with zipfile.ZipFile(source) as before,zipfile.ZipFile(output) as after:
  assert after.testzip() is None
  assert len(after.namelist())==len(set(after.namelist()))
  assert set(before.namelist())<=set(after.namelist())
  changed=[n for n in before.namelist() if before.read(n)!=after.read(n)]
  resources=[n for n in changed if not n.endswith('.class')]
  allowed=[BASE+c+'.json' for c in CATEGORIES] if kind=='hordes' else []
  assert set(resources)<=set(allowed),'unexpected resource mutation'
  assert sorted(changed)==sorted(json.loads(pathlib.Path('expected-changes.json').read_text())[kind]),'unexpected class/resource changes'
  added=sorted(set(after.namelist())-set(before.namelist()))
  assert all(n.startswith('dev/zomboid/hordes/') if kind=='hordes' else n.startswith('dev/zomboidmelee/combat/PanelCooldown') for n in added),'test/tool classes packaged'
  report['mods'].append({'file':output.name,'sha256':sha(output),'changed_entries':changed,'added_classes':added})
cp=os.pathsep.join(map(str,pathlib.Path('output').glob('*.jar')));run(*compiler,'-cp',cp,'-d','build/tests',*pathlib.Path('tests').rglob('*.java'));run('java','-Xverify:all','-cp','build/tests'+os.pathsep+cp,'dev.zomboid.hordes.Checks')
pathlib.Path('output/validation.json').write_text(json.dumps(report,indent=2)+'\n');print('Input/tool hashes, CRC, original item data and changed-entry inventory verified.')
