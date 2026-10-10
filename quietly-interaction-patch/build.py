from pathlib import Path
import sys,os,subprocess,json,zipfile,hashlib,shutil
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]).resolve();jade=Path(sys.argv[2]).resolve();tools=Path(sys.argv[3]).resolve()
manifest=json.loads((r/'inputs.sha256.json').read_text())
for key,p in [('quietly',base),('jade',jade)]:assert hashlib.sha256(p.read_bytes()).hexdigest()==manifest[key]
for n,h in manifest['tools'].items():assert hashlib.sha256((tools/n).read_bytes()).hexdigest()==h
for folder in ['classes','patcher','patched','test-classes']:shutil.rmtree(r/folder,ignore_errors=True);(r/folder).mkdir()
ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-nowarn'];asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-util.jar','asm-analysis.jar'])
def run(*a):subprocess.run(a,check=True)
run(*ecj,'-cp',str(jade),'-d',str(r/'classes'),*[str(p) for f in ['src','stubs'] for p in (r/f).rglob('*.java')])
run(*ecj,'-cp',asm,'-d',str(r/'patcher'),str(r/'Patch.java'),str(r/'Verify.java'))
run('java','-cp',str(r/'patcher')+os.pathsep+asm,'Patch',str(base),str(r/'patched'))
run('java','-cp',str(r/'patcher')+os.pathsep+asm,'Verify',str(r/'patched'))
run('java','-cp',str(r/'patcher')+os.pathsep+asm,'Verify',str(r/'classes/cn'))
run(*ecj,'-cp',str(r/'classes'),'-d',str(r/'test-classes'),*[str(p) for p in (r/'tests').rglob('*.java')])
run('java','-Xverify:all','-cp',str(r/'test-classes')+os.pathsep+str(r/'classes'),'cn.kafei.interact.Checks')
add={p.relative_to(r/f).as_posix():p.read_bytes() for f in ['classes','patched'] for p in (r/f/'cn').rglob('*.class')}
with zipfile.ZipFile(base) as z:
 config=json.loads(z.read('quietly.mixins.json'));config['mixins'].append('TransferSwapMixin');config['client']=config.get('client',[])+['InteractionOutlineMixin','InteractionGlowMixin','InteractionColorMixin'];add['quietly.mixins.json']=(json.dumps(config,indent=2)+'\n').encode()
 changed=set(add);out=r/'output/quietly-1_0_0-cooldown-v5-interaction-jade.jar';out.parent.mkdir(exist_ok=True)
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as w:
  for n in z.namelist():
   e=zipfile.ZipInfo(n,(2026,10,10,0,0,0));e.compress_type=zipfile.ZIP_DEFLATED;w.writestr(e,add.pop(n,z.read(n)))
  for n,b in sorted(add.items()):
   e=zipfile.ZipInfo(n,(2026,10,10,0,0,0));e.compress_type=zipfile.ZIP_DEFLATED;w.writestr(e,b)
 with zipfile.ZipFile(out) as w:
  assert w.testzip() is None
  for n in z.namelist():
   if n not in changed:assert z.read(n)==w.read(n),n
  assert not any(n.startswith(('net/','org/','snownee/')) for n in w.namelist())
(r/'validation.json').write_text(json.dumps({'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'helper_cases':30,'unrelated_entries_preserved':True,'jade_jar_modified':False,'minecraft_runtime_tested':False,'modified_or_added':sorted(changed)},indent=2));print(out)
