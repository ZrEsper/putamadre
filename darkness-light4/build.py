from pathlib import Path
import json,zipfile,hashlib,shutil
r=Path(__file__).resolve().parent;p=r/'base/darkness3.jar'
if not p.exists():shutil.copy2(r.parent/'mods/hardcore_true_darkness-6.1-night-visibility.3-laser-compat.jar',p)
h=hashlib.sha256(p.read_bytes()).hexdigest();inputs=r/'inputs.json'
if inputs.exists():assert json.loads(inputs.read_text())['darkness3.jar']==h
else:inputs.write_text(json.dumps({'darkness3.jar':h},indent=2)+'\n')
n='assets/hardcore_true_darkness/shaders/program/final_darkness.fsh'
with zipfile.ZipFile(p)as z:e={k:z.read(k)for k in z.namelist()}
s=e[n].decode();assert s.count('abs(color.a-251.0/255.0)<0.0015')==1;s=s.replace('abs(color.a-251.0/255.0)<0.0015','abs(color.a-251.0/255.0)<0.0015 || abs(color.a-250.0/255.0)<0.0015');e[n]=s.encode();out=r/'output/hardcore_true_darkness-6.1-night-visibility.4-flashlight-cone.jar'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for k,b in sorted(e.items()):i=zipfile.ZipInfo(k,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(p)as a,zipfile.ZipFile(out)as b:assert [k for k in a.namelist()if a.read(k)!=b.read(k)]==[n];assert b.testzip()is None
(r/'validation.json').write_text(json.dumps({'file':out.name,'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'only_changed':[n],'minecraft_runtime_tested':False},indent=2)+'\n');print('DARKNESS4_SINGLE_SHADER_FLASHLIGHT_TAG')
