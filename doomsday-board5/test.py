from pathlib import Path
import os,subprocess,json,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';out=next(Path('output').glob('*.jar'));hordes=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.44-electronic-scrap-barrels.jar'
# Unchanged existing API doubles cover worlds, blocks, and sleeping; no Minecraft runtime.
cp=str(out)+':'+str(hordes)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d','build/tests',*map(str,Path('tests').rglob('*.java'))],check=True)
p=subprocess.run(['java','-Xverify:all','-cp','build/tests:'+cp,'dev.zomboid.decor.BedChecks'],capture_output=True,text=True);print(p.stdout,p.stderr);p.check_returncode()
with zipfile.ZipFile(out) as z:
 recipe=json.loads(z.read('data/doomsday_decoration/recipe/board.json'));assert recipe['pattern']==['PPP'] and recipe['key']=={'P':{'tag':'minecraft:planks'}} and recipe['result']=={'id':'doomsday_decoration:board','count':6}
 unlock=json.loads(z.read('data/doomsday_decoration/advancement/recipes/board.json'));assert unlock['criteria']['has_planks']['conditions']['items']==[{'items':'#minecraft:planks'}]
 board=next(x for x in json.loads(z.read('doomsday_decoration/block_specs.json')) if x['id']=='board');assert board['destroy_time']==6 and board['explosion_resistance']==12
 with zipfile.ZipFile(root/'mods/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-beds-barricades.4.jar') as old:
  assert 'data/doomsday_decoration/recipe/board.json' not in old.namelist()
  for name in old.namelist():
   if '/models/' in name or '/textures/' in name or name.endswith('/Beds.class') or name.endswith('/Climate.class'):assert old.read(name)==z.read(name),name
Path('test-results.json').write_text(json.dumps({'checks':[p.stdout.strip(),'Correct board ID, 3 wood-tag ingredients -> 6 reinforcement boards; valid tag item predicate; original models/textures and beds/climate unchanged'],'minecraft_runtime_tested':False},indent=2)+'\n')
