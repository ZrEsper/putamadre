from pathlib import Path
import subprocess,zipfile,json,hashlib,re
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';base=r/'base/integration13.jar';pins={base.name:hashlib.sha256(base.read_bytes()).hexdigest()};pin=r/'inputs.json'
if pin.exists():assert json.loads(pin.read_text())==pins
else:pin.write_text(json.dumps(pins,indent=2)+'\n')
for n in ['build/tool','build/classes','output']:(r/n).mkdir(parents=True,exist_ok=True)
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',asm,'-d',str(r/'build/tool'),str(r/'Hooks.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tool')+':'+asm,'Hooks',str(r/'build/classes')],check=True)
with zipfile.ZipFile(base)as z:e={n:z.read(n)for n in z.namelist()}
for p in (r/'build/classes').rglob('*.class'):e[p.relative_to(r/'build/classes').as_posix()]=p.read_bytes()
e['ads14.mixins.json']=json.dumps({'required':False,'minVersion':'0.8','package':'dev.zomboid.ads14.mixin','compatibilityLevel':'JAVA_21','mixins':['PlayerAim14'],'client':['ClientAim14'],'injectors':{'defaultRequire':1}},indent=2).encode()
s=e['META-INF/neoforge.mods.toml'].decode();s=re.sub(r'(?m)^version\s*=\s*"[^"\n]*"','version="1.7.14-fast-player-ads"',s,count=1);s+='\n[[mixins]]\nconfig="ads14.mixins.json"\n';e['META-INF/neoforge.mods.toml']=s.encode()
out=r/'output/zomboid-integration-1.7.14-neoforge-1.21.1-fast-player-ads.jar'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
(r/'validation.json').write_text(json.dumps({'file':out.name,'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'minecraft_runtime_tested':False},indent=2)+'\n')
