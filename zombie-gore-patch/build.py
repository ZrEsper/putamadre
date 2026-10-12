from pathlib import Path
import json,sys,zipfile,hashlib,subprocess,tempfile,os
from models import resources
r=Path(__file__).resolve().parent;tools=Path(sys.argv[1]);pack=Path(sys.argv[2]);out=r.parent/'mods/zomboid-zombie-gore-1.0.0-neoforge-1.21.1.jar';pout=r/'TZP-1.21.1-Zomboid-Gore.1.zip'
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
with tempfile.TemporaryDirectory() as t:
 t=Path(t);c=t/'classes'
 subprocess.run(ecj+['-d',str(c)]+[str(p) for p in (r/'src').rglob('*.java')]+[str(p) for p in (r/'stubs').rglob('*.java')],check=True)
 subprocess.run(ecj+['-cp',str(c),'-d',str(c)]+[str(p) for p in (r/'tests').rglob('*.java')],check=True)
 for name in ['Checks','FlowChecks']:subprocess.run(['java','-Xverify:all','-cp',str(c),'dev.zomboid.gore.'+name],check=True)
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for p in (c/'dev/zomboid/gore').rglob('*.class'):
   if p.name.startswith(('Checks','FlowChecks')):continue
   z.write(p,p.relative_to(c))
  z.writestr('META-INF/neoforge.mods.toml', (r/'neoforge.mods.toml').read_text())
  z.writestr('zomboid_gore.mixins.json',json.dumps({'required':True,'minVersion':'0.8','package':'dev.zomboid.gore.mixin','compatibilityLevel':'JAVA_21','mixins':['ZombieStateMixin','BulletMixin','DimensionsMixin','NoBiteMixin','NoBiteRecordMixin'],'injectors':{'defaultRequire':1}}))
with tempfile.TemporaryDirectory() as tmp:
 asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
 subprocess.run(ecj+['-cp',asm,'-d',tmp,str(r/'Verify.java')],check=True)
 subprocess.run(['java','-cp',tmp+os.pathsep+asm,'Verify',str(out),str(r.parent/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.7.jar'),str(r.parent/'mods/firstaid-1.21.1-ZOMBOID-v3.5.1-revival-timer-fix.jar')],check=True)
with zipfile.ZipFile(pack) as z:data={n:z.read(n) for n in z.namelist() if not n.endswith('/') and not n.lower().endswith('thumbs.db')}
resources(data)
meta=json.loads(data['pack.mcmeta']);meta['pack']['pack_format']=34;meta['pack']['description']='Tissou Zombie Pack + Zomboid injuries: five model variants, targeted wounds and progressive burning';data['pack.mcmeta']=(json.dumps(meta,indent=2)+'\n').encode()
with zipfile.ZipFile(pout,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in data.items():z.writestr(n,b)
for file in [out,pout]:
 with zipfile.ZipFile(file) as z:assert z.testzip() is None
for n,b in data.items():
 if n.endswith('.jem'):
  d=json.loads(b);assert d['textureSize']==[64,64];assert len(d['models'])==7
(r/'validation.json').write_text(json.dumps({'original_pack_sha256':hashlib.sha256(pack.read_bytes()).hexdigest(),'mod_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'pack_sha256':hashlib.sha256(pout.read_bytes()).hexdigest(),'minecraft_runtime_tested':False,'cem_models':20},indent=2)+'\n')
print(out.name,pout.name)
