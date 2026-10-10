from pathlib import Path
import subprocess,sys,os,tempfile,zipfile,hashlib,json
r=Path(__file__).resolve().parent;source,tools=map(Path,sys.argv[1:3]);dest=r.parent/'mods/hardcore_true_darkness-6.1-night-visibility.jar'
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
def run(*args):subprocess.run(args,check=True)
with tempfile.TemporaryDirectory() as d:
 run('java','-jar',str(tools/'ecj.jar'),'-21','-nowarn','-cp',asm,'-d',d,str(r/'Patch.java'),str(r/'Checks.java'))
 run('java','-cp',d+os.pathsep+asm,'Patch',str(source),str(dest))
 run('java','-Xverify:all','-cp',d,'Checks',str(source),str(dest))
with zipfile.ZipFile(source) as a,zipfile.ZipFile(dest) as b:
 assert a.namelist()==b.namelist() and b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert changed==['com/hyrrx/hardcoretruedarkness/client/ClientDarknessState$RenderState.class'],changed
(r/'validation.json').write_text(json.dumps(dict(input_sha256=hashlib.sha256(source.read_bytes()).hexdigest(),output_sha256=hashlib.sha256(dest.read_bytes()).hexdigest(),modified_entries=changed,actual_bytecode_behavior_cases=16,minecraft_runtime_tested=False),indent=2)+'\n')
