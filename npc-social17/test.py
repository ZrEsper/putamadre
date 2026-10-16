from pathlib import Path
import subprocess,json,zipfile,hashlib
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];report=json.loads((r/'validation.json').read_text());cp=':'.join(str(r/'output'/v['output'])for v in report.values())+':'+str(r/'build/tools/gson-2.10.1.jar');exec(compile((r/'fixtures.py').read_text(),'fixtures.py','exec'),{'__file__':str(r/'fixtures.py')});(r/'build/test').mkdir(exist_ok=True)
dep=r/'build/tools/gson-2.10.1.jar'
if not dep.exists():
 dep.parent.mkdir(parents=True,exist_ok=True);subprocess.run(['curl','--fail','--location','--silent','--show-error','https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar','-o',str(dep)],check=True)
assert hashlib.sha256(dep.read_bytes()).hexdigest()==json.loads((r/'test-dependencies.json').read_text())['gson_sha256']
def run(*a):
 p=subprocess.run(list(map(str,a)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
run(*ecj,'-cp',str(r/'build/api')+':'+cp,'-d',r/'build/test',*(r/'tests').rglob('*.java'))
result=run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.SocialChecks')
checks=[result,run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'dev.zomboid.firstaid.AidChecks')]
checks.append(run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.JobChecks'))
checks.append(run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.AdvancedChecks'))
checks.append(run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.Conversation19Checks'))
checks.append(run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.Social21Checks'))
checks.append(run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.Survivor20Checks'))
checks.append(run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.CorpseAppearance20Checks'))
for kind,info in report.items():
 with zipfile.ZipFile(r/'base'/json.loads((r/'inputs.json').read_text())[kind]['file'])as old,zipfile.ZipFile(r/'output'/info['output'])as new:
  assert new.testzip()is None
  for n in old.namelist():
   if kind=='aid' and n in {'ichttt/mods/firstaid/compat/lrtactical/TreatmentV33.class','ichttt/mods/firstaid/client/gui/GuiHealthScreen.class'}:continue
   if n=='META-INF/neoforge.mods.toml' or (kind=='aid' and (n in {'dev/zomboid/firstaid/UnconsciousScreen.class','dev/zomboid/firstaid/UnconsciousClient.class'} or n.startswith('assets/firstaid/lang/'))):continue
   if '/textures/'in n or '/sounds/'in n or n.endswith('SpawnClaims15.class') or n.startswith('dev/zomboid/npcprobe/') or kind=='aid':assert old.read(n)==new.read(n),n
  if kind=='corpse':assert set(info['changed'])<= {'META-INF/neoforge.mods.toml','de/maxhenkel/corpse/entities/CorpseEntity.class','de/maxhenkel/corpse/entities/DummyPlayer.class'}
  if kind=='integration':assert set(info['changed'])<= {'META-INF/neoforge.mods.toml','dev/zomboid/integration/client/CareScreen.class','dev/zomboid/integration/care/Medical.class'}
  if kind=='npc':
   from PIL import Image
   import io
   skins=[n for n in new.namelist()if '/entity/survivors/'in n and n.endswith('.png')];assert len(skins)==48 and len({hashlib.sha256(new.read(n)).hexdigest()for n in skins})==48
   for n in skins:assert Image.open(io.BytesIO(new.read(n))).size==(64,64)
   sounds=json.loads(new.read('assets/tacznpcs/sounds.json'));assert len(sounds)==10
   for event in sounds.values():
    for sample in event['sounds']:assert new.read('assets/tacznpcs/sounds/'+sample['name'].split(':',1)[1]+'.ogg').startswith(b'OggS')
   es=json.loads(new.read('assets/tacznpcs/lang/es_es.json'));en=json.loads(new.read('assets/tacznpcs/lang/en_us.json'));assert set(k for k in es if k.startswith(('soc.','dialog.soc.')))<=en.keys();assert sum(k.startswith('dialog.soc.')for k in es)>=148
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Inspect.java')
checks.append(run('java','-Xverify:all','-cp',str(r/'build/tool')+':'+str(r/'build/test')+':'+cp+':'+str(r/'build/api')+':'+asm,'Inspect',str(r/'output'/report['npc']['output']),str(r/'output'/report['clan']['output'])))
checks.append(run('python',r/'ui-test.py'))
run('python',r/'metadata-test.py')
(r/'test-results.json').write_text(json.dumps({'checks':checks,'native_assets_and_medical_player_rules_preserved':True,'minecraft_runtime_tested':False},indent=2)+'\n')
