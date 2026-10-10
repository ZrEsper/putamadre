from pathlib import Path
import sys,hashlib,json,subprocess,os,tempfile,zipfile
r=Path(__file__).resolve().parent;q=Path(sys.argv[1]).resolve();j=Path(sys.argv[2]).resolve();tools=Path(sys.argv[3]).resolve();manifest=json.loads((r/'inputs.sha256.json').read_text())
for key,path in [('quietly',q),('jade',j)]:assert hashlib.sha256(path.read_bytes()).hexdigest()==manifest[key]
for name,expected in manifest['tools'].items():assert hashlib.sha256((tools/name).read_bytes()).hexdigest()==expected
out=r/'output';out.mkdir(exist_ok=True)
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
def run(*args):subprocess.run(args,check=True)
with tempfile.TemporaryDirectory() as tmp:
 tmp=Path(tmp);patcher=tmp/'patcher';tests=tmp/'tests'
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-cp',asm,'-d',str(patcher),str(r/'Patch.java'),str(r/'Probe.java'))
 results=[]
 for mode,source,name,changed in [('jade',j,'Jade-1.21.1-NeoForge-15.10.6-pending-loot.jar','snownee/jade/addon/universal/ItemStorageProvider.class'),('quietly',q,'quietly-1_0_0-cooldown-v7-pending-loot.jar','cn/kafei/interact/Interaction.class')]:
  dest=out/name;run('java','-cp',str(patcher)+os.pathsep+asm,'Patch',mode,str(source),str(dest))
  with zipfile.ZipFile(source) as a,zipfile.ZipFile(dest) as b:
   assert a.namelist()==b.namelist() and b.testzip() is None
   for entry in a.namelist():
    if entry!=changed:assert a.read(entry)==b.read(entry),entry
  results.append({'file':name,'sha256':hashlib.sha256(dest.read_bytes()).hexdigest(),'only_changed_entry':changed})
 run('java','-cp',str(patcher)+os.pathsep+asm,'Probe',str(out/results[0]['file']),str(tests),str(out/results[1]['file']))
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-d',str(tests),*[str(p) for p in (r/'tests').rglob('*.java')])
 run('java','-Xverify:all','-cp',str(tests),'Checks')
(r/'jar-validation.json').write_text(json.dumps({'artifacts':results,'bytecode_behavior_cases':7,'unrelated_entries_preserved':True,'minecraft_runtime_tested':False},indent=2))
