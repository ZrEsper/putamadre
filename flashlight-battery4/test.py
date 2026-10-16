from pathlib import Path
import subprocess,json,zipfile
r=Path(__file__).resolve().parent;exec(compile((r/'fixtures.py').read_text(),'fixtures.py','exec'),{'__file__':str(r/'fixtures.py')});tools=r.parent/'hordes-melee-interiors-patch/build/tools';report=json.loads((r/'validation.json').read_text());jar=r/'output'/report['flash']['output'];(r/'build/tests').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(jar),'-d',str(r/'build/tests'),*map(str,(r/'tests').rglob('*.java'))],check=True)
p=subprocess.run(['java','-Xverify:all','-cp',str(r/'build/tests')+':'+str(jar),'fixture.Checks'],capture_output=True,text=True);print(p.stdout,p.stderr);p.check_returncode()
with zipfile.ZipFile(jar)as z,zipfile.ZipFile(next((r/'base').glob('flashlight*')))as old:
 assert z.testzip()is None
 for n in old.namelist():
  if '/textures/'in n or '/sounds/'in n or '/block/'in n or '/network/'in n or n.endswith('SafeLight.class'):assert z.read(n)==old.read(n),n
 for tier in ['expedition','eco','tactical']:assert json.loads(z.read('data/flashlight/recipe/'+tier+'.json'))['result']['id']=='flashlight:'+tier+'_off'
 assert set(v['item']for v in json.loads(z.read('data/flashlight/recipe/batterycraft.json'))['ingredients'])=={'minecraft:copper_ingot','minecraft:redstone','minecraft:lapis_lazuli'}
with zipfile.ZipFile(r/'output'/report['survivor']['output'])as z,zipfile.ZipFile(next((r/'base').glob('survivor*')))as old:
 for n in old.namelist():
  if n!='dev/survivorcreator/spawn/Flashlight.class':assert z.read(n)==old.read(n),n


asm=':'.join(str(tools/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
subprocess.run(['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',asm,'-d',str(r/'build/tool'),str(r/'Inspect.java')],check=True)
subprocess.run(['java','-cp',str(r/'build/tool')+':'+asm,'Inspect',str(next((r/'base').glob('flashlight*'))),str(jar)],check=True)

(r/'test-results.json').write_text(json.dumps({'checks':p.stdout.strip(),'native_hook_checks':9,'native_assets_and_survivor_features_preserved':True,'minecraft_runtime_tested':False},indent=2)+'\n')
