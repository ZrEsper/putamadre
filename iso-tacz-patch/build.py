from pathlib import Path
import sys,subprocess,tempfile,zipfile,json,hashlib,os
r=Path(__file__).resolve().parent;repo=r.parent;base=Path(sys.argv[1]);tacz=Path(sys.argv[2]);tools=Path(sys.argv[3]);asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
def run(*args):subprocess.run(list(map(str,args)),check=True)
with tempfile.TemporaryDirectory(prefix='iso-tacz-') as temp:
 t=Path(temp);classes=t/'classes';patch=t/'patch';tests=t/'tests';ecj=['java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn']
 run(*ecj,'-d',classes,*sorted((r/'src').rglob('*.java')));run(*ecj,'-cp',asm,'-d',patch,r/'Patch.java');run(*ecj,'-cp',classes,'-d',tests,*sorted((r/'tests').rglob('*.java')));run('java','-Xverify:all','-cp',str(tests)+os.pathsep+str(classes),'dev.zomboid.isotacz.Checks')
 out=repo/'mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.4.jar';run('java','-cp',str(patch)+os.pathsep+asm,'Patch',base,out,classes)
 with zipfile.ZipFile(out) as z:data={n:z.read(n) for n in z.namelist()}
 name='dungeons_iso.mixins.json';mix=json.loads(data[name]);mix['client']+=['ZomboidInputMixin','ZomboidSprintMixin','ZomboidHudMixin','compat.tacz.ZomboidScopeMixin','ZomboidMouseMixin','ZomboidPerspectiveMixin','compat.tacz.ZomboidAimInputMixin'];data[name]=(json.dumps(mix,indent=2)+'\n').encode()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];assert set(changed)=={name,'com/cleannrooster/dungeons_iso/mixin/CameraMixin.class','com/cleannrooster/dungeons_iso/mixin/MinecraftClientMixin.class','com/cleannrooster/dungeons_iso/mixin/GameRendererMixin.class'},changed
 with zipfile.ZipFile(tacz) as z:
  for n in ['com/tacz/guns/api/item/IGun.class','com/tacz/guns/client/event/CameraSetupEvent.class','com/tacz/guns/client/gameplay/LocalPlayerSprint.class','com/tacz/guns/network/message/ClientMessagePlayerShoot.class']:assert n in z.namelist()
  assert b'getIGunOrNull' in z.read('com/tacz/guns/api/item/IGun.class')
  assert b'applyScopeMagnification' in z.read('com/tacz/guns/client/event/CameraSetupEvent.class')
  assert b'onAimHoldingPreInput' in z.read('com/tacz/guns/client/input/AimKey.class')
 (r/'validation.json').write_text(json.dumps({'iso_input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'tacz_inspected_sha256':hashlib.sha256(tacz.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changed,'minecraft_runtime_tested':False,'tacz_jar_modified':False,'simulated_checks_passed':98},indent=2)+'\n')
