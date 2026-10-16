from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];report=json.loads((r/'validation.json').read_text());cp=':'.join(str(r/'output'/v['output'])for v in report.values());exec(compile((r/'fixtures.py').read_text(),'fixtures.py','exec'),{'__file__':str(r/'fixtures.py')});(r/'build/test').mkdir(exist_ok=True)
def run(*a):
 p=subprocess.run(list(map(str,a)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
run(*ecj,'-cp',str(r/'build/api')+':'+cp,'-d',r/'build/test',*(r/'tests').rglob('*.java'))
result=run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.SocialChecks')
checks=[result,run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'dev.zomboid.firstaid.AidChecks')]
checks.append(run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.JobChecks'))
checks.append(run('java','-Xverify:all','-cp',str(r/'build/test')+':'+cp+':'+str(r/'build/api'),'mod.cdv.tnpc.tactical.AdvancedChecks'))
for kind,info in report.items():
 with zipfile.ZipFile(r/'base'/json.loads((r/'inputs.json').read_text())[kind]['file'])as old,zipfile.ZipFile(r/'output'/info['output'])as new:
  assert new.testzip()is None
  for n in old.namelist():
   if n=='META-INF/neoforge.mods.toml' or (kind=='aid' and n=='dev/zomboid/firstaid/UnconsciousScreen.class'):continue
   if '/textures/'in n or '/sounds/'in n or n.endswith('SpawnClaims15.class') or n.startswith('dev/zomboid/npcprobe/') or kind=='aid':assert old.read(n)==new.read(n),n
  if kind=='npc':
   es=json.loads(new.read('assets/tacznpcs/lang/es_es.json'));en=json.loads(new.read('assets/tacznpcs/lang/en_us.json'));assert set(k for k in es if k.startswith(('soc.','dialog.soc.')))<=en.keys();assert sum(k.startswith('dialog.soc.')for k in es)>=148
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Inspect.java')
checks.append(run('java','-Xverify:all','-cp',str(r/'build/tool')+':'+str(r/'build/test')+':'+cp+':'+str(r/'build/api')+':'+asm,'Inspect',str(r/'output'/report['npc']['output']),str(r/'output'/report['clan']['output'])))
checks.append(run('python',r/'ui-test.py'))
(r/'test-results.json').write_text(json.dumps({'checks':checks,'native_assets_and_medical_player_rules_preserved':True,'minecraft_runtime_tested':False},indent=2)+'\n')
