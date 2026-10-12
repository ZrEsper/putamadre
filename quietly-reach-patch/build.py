from pathlib import Path
import sys,tempfile,subprocess,os,zipfile,json,hashlib
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]);tools=Path(sys.argv[2]);out=r.parent/'mods/quietly-1_0_0-cooldown-v11-five-block-reach.jar';asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
with tempfile.TemporaryDirectory() as tmp:
 t=Path(tmp);c=t/'classes';tests=t/'tests';p=t/'patch';mixin=t/'InteractionReachMixin.class';ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
 for command in [ecj+['-cp',str(base),'-d',str(c)]+[str(x) for x in (r/'src').rglob('*.java')],ecj+['-cp',str(base)+os.pathsep+str(c),'-d',str(tests)]+[str(x) for x in (r/'tests').rglob('*.java')],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(c)+os.pathsep+str(base),'dev.zomboid.quietly.Checks'],ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java')],['java','-cp',str(p)+os.pathsep+asm,'Patch',str(mixin)]]:subprocess.run(command,check=True)
 with zipfile.ZipFile(base) as z:data={n:z.read(n) for n in z.namelist()}
 config=json.loads(data['quietly.mixins.json']);config['mixins'].append('InteractionReachMixin');data['quietly.mixins.json']=(json.dumps(config,indent=2)+'\n').encode();data['cn/kafei/mixin/InteractionReachMixin.class']=mixin.read_bytes()
 for f in c.rglob('*.class'):data[str(f.relative_to(c))]=f.read_bytes()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];assert changed==['quietly.mixins.json'],changed
 (r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changed,'simulated_checks_passed':4,'minecraft_runtime_tested':False},indent=2)+'\n')
 print('Verified JAR integrity; original code and resources preserved')
