from pathlib import Path
import json,hashlib,subprocess,zipfile,os,struct,zlib,wave,math,random
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
inputs=json.loads(Path('input.json').read_text());base=Path('base')/inputs['file'];assert sha(base)==inputs['sha256']
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
for d in ['build/tool','build/api','build/classes','output']:Path(d).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*ecj,'-cp',asm,'-d','build/tool','Patch.java');run(*ecj,'-d','build/api',*Path('api').rglob('*.java'));run(*ecj,'-cp','build/api:'+str(base),'-d','build/classes',*Path('src').rglob('*.java'),*Path('src-client').rglob('*.java'))
out=Path('output/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-needs-bathrooms.6.jar');run('java','-cp','build/tool:'+asm,'Patch',base,out,'build/classes')
with zipfile.ZipFile(out)as z:entries={n:z.read(n) for n in z.namelist()}
def js(n,v):entries[n]=json.dumps(v,ensure_ascii=False,separators=(',',':')).encode()
# Three wool -> mattress; ceramics -> toilet; portable WC; urinal and all wire variants.
recipes={
'mat':(['WWW'],{'W':{'tag':'minecraft:wool'}},1),
'closestool':(['B B','BBB',' B '],{'B':{'item':'minecraft:brick'}},1),
'wc':(['PPP','PTP','III'],{'P':{'tag':'minecraft:planks'},'T':{'item':'doomsday_decoration:closestool'},'I':{'item':'minecraft:iron_ingot'}},1),
'urinepool':(['B B','BBB'],{'B':{'item':'minecraft:brick'}},1),
'wiremesh':(['ISI','ISI'],{'I':{'item':'minecraft:iron_ingot'},'S':{'item':'minecraft:string'}},4)}
for name,(pattern,key,count)in recipes.items():
 js('data/doomsday_decoration/recipe/'+name+'.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':pattern,'key':key,'result':{'id':'doomsday_decoration:'+name,'count':count}})
 tag='minecraft:wool'if name=='mat'else'minecraft:planks'if name=='wc'else None;item='minecraft:iron_ingot'if name=='wiremesh'else'minecraft:brick';trigger={'items':'#'+tag if tag else item}
 js('data/doomsday_decoration/advancement/recipes/'+name+'.json',{'parent':'minecraft:recipes/root','criteria':{'has_material':{'trigger':'minecraft:inventory_changed','conditions':{'items':[trigger]}}},'rewards':{'recipes':['doomsday_decoration:'+name]}})
for n in ['wiremesh_2','wiremesh_3','cornerwiremesh']:
 js('data/doomsday_decoration/recipe/'+n+'.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':[{'item':'doomsday_decoration:wiremesh'}],'result':{'id':'doomsday_decoration:'+n,'count':1}})
js('data/doomsday_decoration/recipe/wiremesh_recover.json',{'type':'minecraft:crafting_shapeless','category':'misc','ingredients':[{'tag':'doomsday_decoration:wire_variants'}],'result':{'id':'doomsday_decoration:wiremesh','count':1}})
js('data/doomsday_decoration/tags/item/wire_variants.json',{'replace':False,'values':['doomsday_decoration:'+n for n in ['wiremesh_2','wiremesh_3','cornerwiremesh']]})
def png(pixels):
 def chunk(t,b):return struct.pack('>I',len(b))+t+b+struct.pack('>I',zlib.crc32(t+b)&0xffffffff)
 raw=b''.join(b'\0'+bytes(sum([list(p)for p in row],[]))for row in pixels);return b'\x89PNG\r\n\x1a\n'+chunk(b'IHDR',struct.pack('>IIBBBBB',16,16,8,6,0,0,0))+chunk(b'IDAT',zlib.compress(raw))+chunk(b'IEND',b'')
for name,color in [('pee_drop',(215,191,61,210)),('feces_drop',(92,58,28,255))]:
 pixels=[[(0,0,0,0)for _ in range(16)]for _ in range(16)]
 for y in range(4,13):
  for x in range(5,11):
   if ((x-7.5)/3)**2+((y-9)/5)**2<1:pixels[y][x]=color
 entries['assets/doomsday_decoration/textures/particle/'+name+'.png']=png(pixels);js('assets/doomsday_decoration/particles/'+name+'.json',{'textures':['doomsday_decoration:'+name]})
# Original procedural mono Foley, not replacement of any original sound.
sounds=json.loads(entries['assets/doomsday_decoration/sounds.json']);rng=random.Random(621);sr=22050
for name,seconds in [('needs_urinate',10),('needs_defecate',10),('toilet_flush',3.5),('wire_cut',.45)]:
 audio=[];low=0.
 for i in range(int(sr*seconds)):
  time=i/sr;noise=rng.uniform(-1,1);low=.91*low+.09*noise;fade=min(1,time*8,(seconds-time)*8)
  if name=='needs_urinate':v=(noise*.13+low*.7)*( .5+.3*math.sin(time*21)**2)*fade
  elif name=='needs_defecate':
   pulse=sum(math.exp(-max(0,time-p)*17)if p<=time<p+.4 else 0 for p in [1.,3.8,6.2,8.7]);v=(low*.7+math.sin(time*150)*.12)*pulse*fade
  elif name=='toilet_flush':v=(low*.8+noise*.08)*(math.sin(math.pi*time/seconds)**.5)*fade
  else:v=(noise*.25+math.sin(time*1800)*.1)*math.exp(-time*9)*fade
  audio.append(int(max(-1,min(1,v))*30000))
 wav=Path('build')/(name+'.wav');ogg=wav.with_suffix('.ogg')
 with wave.open(str(wav),'wb')as w:w.setnchannels(1);w.setsampwidth(2);w.setframerate(sr);w.writeframes(struct.pack('<'+'h'*len(audio),*audio))
 run('ffmpeg','-hide_banner','-loglevel','error','-y','-i',wav,'-c:a','libvorbis','-q:a','4',ogg);entries['assets/doomsday_decoration/sounds/'+name+'.ogg']=ogg.read_bytes();sounds[name]={'subtitle':'subtitles.doomsday.'+name,'sounds':['doomsday_decoration:'+name]}
js('assets/doomsday_decoration/sounds.json',sounds)
translations=json.loads(Path('translations.json').read_text())
for locale,extra in translations.items():
 n='assets/doomsday_decoration/lang/'+locale+'.json';d=json.loads(entries.get(n,b'{}'));d.update(extra);js(n,d)
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None;changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert [n for n in changed if n.endswith('.class')]==['dev/zomboid/decor/Beds.class','net/mcreator/doomsdaydecoration/DoomsdayDecorationMod.class','net/mcreator/doomsdaydecoration/port/PortedBlock.class'];assert not any(n.startswith('net/minecraft/')for n in added)
Path('validation.json').write_text(json.dumps({'input_sha256':sha(base),'output':out.name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
