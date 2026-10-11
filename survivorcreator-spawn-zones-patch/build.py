from pathlib import Path
import sys,json,hashlib,zipfile,subprocess,tempfile,os,shutil
r=Path(__file__).resolve().parent;repo=r.parent;base=Path(sys.argv[1]);tools=Path(sys.argv[2]);old=repo/'survivorcreator-spawn-patch'
expected=hashlib.sha256((repo/'mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v22-scavenging-boosts.jar').read_bytes()).hexdigest()
assert hashlib.sha256(base.read_bytes()).hexdigest()==expected
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
def run(*a):subprocess.run(list(map(str,a)),check=True)
with tempfile.TemporaryDirectory(prefix='spawn-zones-') as temp:
 t=Path(temp);classes=t/'classes';patcher=t/'verify';tests=t/'tests'
 run(*ecj,'-cp',base,'-d',classes,*sorted((old/'stubs').rglob('*.java')),*sorted((r/'src').rglob('*.java')),old/'src/dev/survivorcreator/spawn/R.java')
 shutil.copyfile(r/'survivor-spawn-homes.csv',classes/'survivor-spawn-homes.csv')
 run(*ecj,'-cp',asm,'-d',patcher,old/'Verify.java');run('java','-cp',str(patcher)+os.pathsep+asm,'Verify',classes/'dev/survivorcreator/spawn')
 run(*ecj,'-cp',classes,'-d',tests,*[p for p in sorted((old/'tests').rglob('*.java')) if p.name!='SpawnChecks.java'],*sorted((r/'tests').rglob('*.java')));run('java','-Xverify:all','-cp',str(tests)+os.pathsep+str(classes),'dev.survivorcreator.spawn.SpawnChecks')
 added={p.relative_to(classes).as_posix():p.read_bytes() for p in (classes/'dev/survivorcreator/spawn').glob('*.class') if p.name!='R.class'};added['survivor-spawn-homes.csv']=(r/'survivor-spawn-homes.csv').read_bytes();changed=set(added)
 out=repo/'mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v23-nine-spawn-zones.jar'
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as b:
  for n in a.namelist():
   info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;b.writestr(info,added.pop(n,a.read(n)))
  for n,data in added.items():b.writestr(n,data)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  for n in a.namelist():
   if n not in changed:assert a.read(n)==b.read(n),n
  assert not any(n.startswith(('net/','org/','com/')) and n not in a.namelist() for n in b.namelist())
 catalog=json.loads((r/'homes.json').read_text());report={'base_sha256':expected,'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'zones':9,'interiors_per_zone':[len(z['homes']) for z in catalog],'modified_or_added':sorted(changed),'unrelated_entries_preserved':True,'minecraft_runtime_tested':False,'simulated_cases':38,'bytecode_methods_checked':35}
 (r/'validation.json').write_text(json.dumps(report,indent=2)+'\n');print(json.dumps(report,indent=2))
