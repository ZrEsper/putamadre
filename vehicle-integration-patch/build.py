from pathlib import Path
import subprocess,sys,tempfile,zipfile,json,hashlib,os
r=Path(__file__).resolve().parent;tools=Path(sys.argv[1]);base=Path(sys.argv[2]);asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);out=r.parent/'mods/quietly-1_0_0-cooldown-v12-vehicle-interactions.jar';assert hashlib.sha256(base.read_bytes()).hexdigest()=='7789fa19882177efa99c37628f79ba41406027b35f1a9c4b2ca409252ae74935'
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
with tempfile.TemporaryDirectory() as tmp:
 t=Path(tmp)
 for cmd in [ecj+['-cp',str(base),'-d',str(t/'classes')]+[str(f) for f in (r/'src').rglob('*.java')],ecj+['-cp',asm,'-d',str(t/'patch'),str(r/'QuietPatch.java')],['java','-cp',str(t/'patch')+os.pathsep+asm,'QuietPatch',str(base),str(out),str(t/'classes')]]:subprocess.run(cmd,check=True)
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 assert b.testzip() is None
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];assert changed==['dev/zomboid/scavenging/SearchHud.class'],changed
pack=r.parent/'mods/MTS-Official-Pack-1.21.1-V29-zomboid-dashboard-trunks.1.jar';packout=r.parent/'mods/MTS-Official-Pack-1.21.1-V29-zomboid-invisible-trunk.2.jar'
with zipfile.ZipFile(pack) as a:data={n:a.read(n) for n in a.namelist()}
name='assets/mtsofficialpack/jsondefs/parts/zomboid_trunk.json';trunk=json.loads(data[name]);obj=data['assets/mtsofficialpack/objmodels/parts/zomboid_trunk.obj'].decode();names=[line[2:].strip() for line in obj.splitlines() if line.startswith('o ')];assert names
trunk['rendering']['animatedObjects']=[{'objectName':n,'animations':[{'animationType':'visibility','variable':'pz_trunk_model_visible','clampMin':1,'clampMax':1}]} for n in names]
data[name]=(json.dumps(trunk,indent=2)+'\n').encode();assert trunk['interactable']['inventoryUnits']==6
with zipfile.ZipFile(packout,'w',zipfile.ZIP_DEFLATED) as b:
 for n,v in data.items():b.writestr(n,v)
with zipfile.ZipFile(pack) as a,zipfile.ZipFile(packout) as b:
 assert b.testzip() is None;assert [n for n in a.namelist() if a.read(n)!=b.read(n)]==[name]
(r/'validation.json').write_text(json.dumps({'quietly_input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'quietly_output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'quietly_changed_entries':changed,'pack_output_sha256':hashlib.sha256(packout.read_bytes()).hexdigest(),'hidden_model_objects':names,'minecraft_runtime_tested':False},indent=2)+'\n')
print('Quietly native code preserved except HUD hook; all trunk model objects hidden; inventory unchanged')
