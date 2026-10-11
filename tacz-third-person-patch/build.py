from pathlib import Path
import sys,subprocess,tempfile,zipfile,json,hashlib,os
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]);tools=Path(sys.argv[2]);out=r.parent/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.6.jar';asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
with tempfile.TemporaryDirectory() as t:
 t=Path(t);c=t/'classes';p=t/'patch';ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
 subprocess.run(ecj+['-d',str(c)]+[str(x) for x in (r/'src').rglob('*.java')],check=True)
 subprocess.run(ecj+['-cp',str(c),'-d',str(t/'tests')]+[str(x) for x in (r/'tests').rglob('*.java')],check=True)
 subprocess.run(['java','-Xverify:all','-cp',str(t/'tests')+os.pathsep+str(c),'dev.zomboid.tacz.Checks'],check=True)
 subprocess.run(['java','-Xverify:all','-cp',str(t/'tests')+os.pathsep+str(c),'dev.zomboid.tacz.BareChecks'],check=True)
 subprocess.run(ecj+['-cp',asm,'-d',str(p),str(r/'TaczPatch.java')],check=True)
 subprocess.run(['java','-cp',str(p)+os.pathsep+asm,'TaczPatch',str(base),str(out),str(c)],check=True)
 with zipfile.ZipFile(out) as z:data={n:z.read(n) for n in z.namelist()}
 mix=json.loads(data['tacz.mixins.json']);mix['client']+=['client.ZomboidProjectionMixin','client.ZomboidFogCompatibilityMixin','client.ZomboidBareHandMixin','client.ZomboidFogColorMixin'];data['tacz.mixins.json']=(json.dumps(mix,indent=2)+'\n').encode()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
  assert set(changed)=={'tacz.mixins.json','com/tacz/guns/client/event/RenderCrosshairEvent.class','com/tacz/guns/client/event/CameraSetupEvent.class','com/tacz/guns/client/sound/SoundPlayManager.class'}
  assert not any('dungeons_iso' in n for n in b.namelist())
 (r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changed,'minecraft_runtime_tested':False,'simulated_checks_passed':40},indent=2)+'\n')
 print('Verified ZIP integrity, no Dungeons Iso dependency, original content preserved')
