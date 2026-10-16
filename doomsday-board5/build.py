from pathlib import Path
import json,hashlib,zipfile,subprocess,os
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
assert sha(t/'ecj.jar')==json.loads((root/'spas-reload18/inputs.json').read_text())['tools']['ecj']
src=root/'mods/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-beds-barricades.4.jar';assert sha(src)==json.loads(Path('input.json').read_text())['sha256'];Path('build/classes').mkdir(parents=True,exist_ok=True);Path('output').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',str(src),'-d','build/classes',*map(str,Path('src').rglob('*.java'))],check=True)
with zipfile.ZipFile(src)as z:entries={n:z.read(n)for n in z.namelist()}
for p in Path('build/classes').rglob('*.class'):entries[str(p.relative_to('build/classes'))]=p.read_bytes()
recipe={'type':'minecraft:crafting_shaped','category':'building','pattern':['PPP'],'key':{'P':{'tag':'minecraft:planks'}},'result':{'id':'doomsday_decoration:board','count':6}};entries['data/doomsday_decoration/recipe/board.json']=json.dumps(recipe).encode();entries['data/doomsday_decoration/advancement/recipes/board.json']=json.dumps({'parent':'minecraft:recipes/root','criteria':{'has_planks':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':'#minecraft:planks'}]}}},'rewards':{'recipes':['doomsday_decoration:board']}}).encode()
specs=json.loads(entries['doomsday_decoration/block_specs.json']);board=next(s for s in specs if s['id']=='board');board['destroy_time']=6.;board['explosion_resistance']=12.;board['requires_correct_tool']=False;entries['doomsday_decoration/block_specs.json']=json.dumps(specs,separators=(',',':')).encode()
for locale,label in [('es_es','Tablas de refuerzo'),('es_mx','Tablas de refuerzo'),('en_us','Planks'),('de_de','Verstärkungsbretter')]:
 n='assets/doomsday_decoration/lang/'+locale+'.json';d=json.loads(entries.get(n,b'{}'));d['block.doomsday_decoration.board']=label;entries[n]=json.dumps(d,ensure_ascii=False).encode()
out=Path('output/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-board-recipe.5.jar')
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None and a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml');changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith('net/minecraft/')for n in added)
Path('validation.json').write_text(json.dumps({'input_sha256':sha(src),'output':out.name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
