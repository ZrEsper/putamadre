from pathlib import Path
import subprocess,zipfile,json,hashlib,re
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
for name in ['build/tool','build/nvg','build/integration','output']:(r/name).mkdir(parents=True,exist_ok=True)
pins={p.name:hashlib.sha256(p.read_bytes()).hexdigest()for p in (r/'base').glob('*.jar')};pin=r/'inputs.json'
if pin.exists():assert json.loads(pin.read_text())==pins
else:pin.write_text(json.dumps(pins,indent=2)+'\n')
def run(*a):subprocess.run(list(map(str,a)),check=True)
run(*ecj,'-cp',r/'base/fracture11.jar','-d',r/'build/nvg',*(r/'nvg').rglob('*.java'))
run(*ecj,'-d',r/'build/integration',*(r/'src').rglob('*.java'))
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Patch.java',r/'Hooks.java')
run('java','-cp',str(r/'build/tool')+':'+asm,'Hooks',r/'build/integration',r/'build/nvg')
reports={}
for name,base,out in [('nvg','fracture11.jar','fracturepoint-1.21.1-3.0.0-PT2-nvg-cached-pass.12.jar'),('integration','integration11.jar','zomboid-integration-1.7.12-neoforge-1.21.1-bounded-iris-order.jar')]:
 inp=r/'base'/base;p=r/'output'/out
 if name=='nvg':run('java','-cp',str(r/'build/tool')+':'+asm,'Patch',inp,p,r/'build/nvg')
 else:
  with zipfile.ZipFile(inp)as z:e={n:z.read(n)for n in z.namelist()}
  for f in (r/'build/integration').rglob('*.class'):e[f.relative_to(r/'build/integration').as_posix()]=f.read_bytes()
  e['renderperf12.mixins.json']=json.dumps({'required':False,'minVersion':'0.8','package':'dev.zomboid.perf12','compatibilityLevel':'JAVA_21','client':['IrisGraphMixin12'],'injectors':{'defaultRequire':0}},indent=2).encode()
  toml=e['META-INF/neoforge.mods.toml'].decode();toml=re.sub(r'(?m)^version\s*=\s*"[^"\n]*"','version="1.7.12-bounded-iris-order"',toml,count=1);toml+='\n[[mixins]]\nconfig="renderperf12.mixins.json"\n';e['META-INF/neoforge.mods.toml']=toml.encode()
  with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
   for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(inp)as a,zipfile.ZipFile(p)as b:assert b.testzip()is None;changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith(('net/','org/'))for n in added)
 reports[name]={'file':out,'sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'changed':changed,'added':added,'minecraft_runtime_tested':False}
(r/'validation.json').write_text(json.dumps(reports,indent=2)+'\n')
