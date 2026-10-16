from pathlib import Path
import os,subprocess,json,hashlib,zipfile,struct,zlib
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';inputs=json.loads(Path('inputs.json').read_text());ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
for v in inputs.values():assert sha(Path('base')/v['file'])==v['sha256']
for d in ['build/tool','build/api','build/classes','output']:Path(d).mkdir(parents=True,exist_ok=True)
base=Path('base')/inputs['remains']['file'];run(*ecj,'-cp',asm,'-d','build/tool','Patch.java');run(*ecj,'-d','build/api',*Path('api').rglob('*.java'));run(*ecj,'-cp','build/api:'+str(base),'-d','build/classes',*Path('src').rglob('*.java'),*Path('src-client').rglob('*.java'));out=Path('output/zombie-remains-loot-1.6.1-neoforge-1.21.1-corpse-hygiene.14.jar');run('java','-cp','build/tool:'+asm,'Patch',base,out,'build/classes')
with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
def js(n,v):entries[n]=json.dumps(v,separators=(',',':'),ensure_ascii=False).encode()
def png(pixels):
 def chunk(t,b):return struct.pack('>I',len(b))+t+b+struct.pack('>I',zlib.crc32(t+b)&0xffffffff)
 raw=b''.join(b'\x00'+bytes(sum([list(pixels[y][x])for x in range(16)],[]))for y in range(16));return b'\x89PNG\r\n\x1a\n'+chunk(b'IHDR',struct.pack('>IIBBBBB',16,16,8,6,0,0,0))+chunk(b'IDAT',zlib.compress(raw))+chunk(b'IEND',b'')
tex=[[((84+(x*y)%28),(104+(x*5+y*3)%25),35+(x+y)%18,255)for x in range(16)]for y in range(16)];entries['assets/zombie_remains_loot/textures/block/vomit.png']=png(tex)
for frame in range(2):
 tex=[[(0,0,0,0)for x in range(16)]for y in range(16)]
 for y in range(5,11):
  for x in range(7,9):tex[y][x]=(38,31,18,255)
 for x,y in ([(5,5),(6,6),(9,6),(10,5)]if frame==0 else[(4,7),(5,8),(10,8),(11,7)]):tex[y][x]=(175,182,166,180)
 entries[f'assets/zombie_remains_loot/textures/particle/corpse_fly{frame}.png']=png(tex)
js('assets/zombie_remains_loot/particles/corpse_fly.json',{'textures':['zombie_remains_loot:corpse_fly0','zombie_remains_loot:corpse_fly1']})
js('assets/zombie_remains_loot/blockstates/vomit.json',{'variants':{'layers='+str(i):{'model':'zombie_remains_loot:block/vomit'}for i in range(1,9)}})
js('assets/zombie_remains_loot/models/block/vomit.json',{'parent':'minecraft:block/snow_height2','textures':{'texture':'zombie_remains_loot:block/vomit','particle':'zombie_remains_loot:block/vomit'}})
js('data/zombie_remains_loot/loot_table/blocks/vomit.json',{'type':'minecraft:block','pools':[]})
for locale,title in [('en_us','Vomit'),('es_es','Vómito'),('es_mx','Vómito')]:
 n='assets/zombie_remains_loot/lang/'+locale+'.json';d=json.loads(entries.get(n,b'{}'));d['block.zombie_remains_loot.vomit']=title;d['subtitles.zombie_remains_loot.vomit']='Player vomits'if locale=='en_us'else'Jugador vomitando';js(n,d)
n='assets/zombie_remains_loot/sounds.json';d=json.loads(entries[n]);d['vomit']={'subtitle':'subtitles.zombie_remains_loot.vomit','sounds':['zombie_remains_loot:vomit']};js(n,d)
run('ffmpeg','-hide_banner','-loglevel','error','-y','-i',Path('base')/inputs['sound']['file'],'-ac','1','-c:a','libvorbis','-q:a','4','build/vomit.ogg');entries['assets/zombie_remains_loot/sounds/vomit.ogg']=Path('build/vomit.ogg').read_bytes()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None;changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert [n for n in changed if n.endswith('.class')]==['dev/zomboid/remains/ZombieRemainsLoot.class'];assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml')
Path('validation.json').write_text(json.dumps({'input_sha256':sha(base),'output':out.name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
