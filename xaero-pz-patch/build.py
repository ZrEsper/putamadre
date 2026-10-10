from pathlib import Path
import hashlib,subprocess,os,sys,json,zipfile,tempfile
r=Path(__file__).resolve().parent
world,mini,tools=map(Path,sys.argv[1:4]);out=r/'output';out.mkdir(exist_ok=True)
for path,expected in [(world,'6a42b6f01bd96b496e5fcc07d4541bad1a699d9ec8ad58ad40d5dcfe23a7d4c7'),(mini,'d3398221b2262abfe2d394b588122e5b3fd2e4d8c4bf66a5fd357352fcf51b2e')]:
 assert hashlib.sha256(path.read_bytes()).hexdigest()==expected,'Unexpected input JAR: '+str(path)
asm=os.pathsep.join(str(tools/n) for n in ('asm.jar','asm-tree.jar','asm-analysis.jar'))
def run(*args):subprocess.run(args,check=True)
with tempfile.TemporaryDirectory() as d:
 d=Path(d);helper=d/'helper';patcher=d/'patcher'
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-d',str(helper),str(r/'Fog.java'),str(r/'Checks.java'))
 run('java','-Xverify:all','-cp',str(helper)+os.pathsep+str(mini),'xaero.pz.Checks')
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-cp',asm,'-d',str(patcher),str(r/'Patch.java'))
 # Package helper only; test classes are not shipped.
 (helper/'xaero/pz/Checks.class').unlink()
 results=[]
 for mode,source,name in [('world',world,'xaeroworldmap-neoforge-1.21.1-1.47.0-pz-exploration.jar'),('mini',mini,'xaerominimap-neoforge-1.21.1-26.6.0-pz-exploration.jar')]:
  dest=out/name;run('java','-cp',str(patcher)+os.pathsep+asm,'Patch',mode,str(source),str(dest),str(helper))
  with zipfile.ZipFile(source) as a,zipfile.ZipFile(dest) as b:
   assert b.testzip() is None
   changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
   assert len(changed)==(2 if mode=='world' else 5),changed
   results.append(dict(file=name,sha256=hashlib.sha256(dest.read_bytes()).hexdigest(),input_sha256=hashlib.sha256(source.read_bytes()).hexdigest(),modified_entries=changed))
(r/'validation.json').write_text(json.dumps(dict(artifacts=results,minecraft_runtime_tested=False),indent=2)+'\n')
