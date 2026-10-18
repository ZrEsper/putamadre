from pathlib import Path
import subprocess,json,hashlib,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
def run(*a):subprocess.run(list(map(str,a)),check=True)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
for n,h in json.loads((r/'inputs.json').read_text()).items():assert sha(r/'base'/n)==h
for d in ['build/classes','build/tool','output']:(r/d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',r/'base/flash6.jar','-d',r/'build/classes',*(r/'src').rglob('*.java'));run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Patch.java')
results=[]
for kind,base,name in [('flash','flash6.jar','flashlight-5.1-neoforge-1.21.1-cone-state-controls.7.jar'),('integration','integration179.jar','zomboid-integration-1.7.10-neoforge-1.21.1-flashlight-controls.jar')]:
 p=r/'output'/name;run('java','-cp',str(r/'build/tool')+':'+asm,'Patch',r/'base'/base,p,r/'build/classes',kind)
 with zipfile.ZipFile(r/'base'/base)as a,zipfile.ZipFile(p)as b:
  assert b.testzip()is None;changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()))
 results.append(dict(file=name,sha256=sha(p),changed=changed,added=added))
(r/'validation.json').write_text(json.dumps(dict(outputs=results,minecraft_runtime_tested=False),indent=2)+'\n')
