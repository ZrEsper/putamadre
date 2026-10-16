from pathlib import Path
import subprocess,json,zipfile,os
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';out=next(Path('output').glob('*.jar'))
# Only API doubles from api are provided; test fixtures supply behavior, production helpers are loaded from the patched JAR.
def run(*args):
 p=subprocess.run(list(map(str,args)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',asm,'-d','build/tool','Inspect.java')
Path('build/tests').mkdir(parents=True,exist_ok=True)
run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',str(out)+':build/api','-d','build/tests',*Path('tests').rglob('*.java'))
checks=[run('java','-Xverify:all','-cp','build/tests:'+str(out)+':build/api','dev.zomboid.decor.needs.Checks')]
checks.append(run('java','-Xverify:all','-cp','build/tests:'+str(out)+':build/api','dev.zomboid.decor.needs.client.ClientChecks'))
checks.append(run('java','-cp','build/tool:'+asm,'Inspect',out))
with zipfile.ZipFile(out)as z:
 for locale in ['es_es','es_mx','en_us','de_de']:
  d=json.loads(z.read('assets/doomsday_decoration/lang/'+locale+'.json'));assert all('doomsday.needs.'+k in d for k in ['hint','pee_due','poop_due','hold_button','held','relieved','progress']);assert 'key.doomsday.needs' in d
 rec=json.loads(z.read('data/doomsday_decoration/recipe/mat.json'));assert rec['pattern']==['WWW']and rec['key']['W']=={'tag':'minecraft:wool'}and rec['result']['count']==1
 for name in ['closestool','wc','urinepool','wiremesh','wiremesh_2','wiremesh_3','cornerwiremesh']:assert json.loads(z.read('data/doomsday_decoration/recipe/'+name+'.json'))['result']['id']=='doomsday_decoration:'+name
 for name,duration in [('needs_urinate',10),('needs_defecate',10),('toilet_flush',3.5),('wire_cut',.45)]:
  p=Path('build')/(name+'-check.ogg');p.write_bytes(z.read('assets/doomsday_decoration/sounds/'+name+'.ogg'));probe=json.loads(subprocess.check_output(['ffprobe','-v','quiet','-show_streams','-of','json',str(p)]));s=probe['streams'][0];assert s['channels']==1 and abs(float(s['duration'])-duration)<.1
 inp=json.loads(Path('input.json').read_text())
 with zipfile.ZipFile(Path('base')/inp['file'])as old:
  for n in old.namelist():
   if any(t in n for t in ['/Climate.class','/Storage.class','/Footprint.class','/Cooling.class','/Boards.class','/models/','/textures/','/recipe/board.json','/block_specs.json']):assert old.read(n)==z.read(n),n
checks.append('Recipes, mono Foley durations, ES/EN/DE text, original climate/storage/boards and models/textures unchanged')
Path('test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False,'method':'Production server adapter, cycle, toilet search, wire damage and packet codec with Minecraft/NeoForge/FirstAid API doubles; JVM -Xverify:all'},indent=2)+'\n')
