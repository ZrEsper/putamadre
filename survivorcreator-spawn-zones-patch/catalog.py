from pathlib import Path
import sys,io,zlib,gzip,json,math,random
sys.path.insert(0,'/workspace/map-analysis-libs')
import nbtlib
r=Path(__file__).resolve().parent
centers=[(-325,-290),(290,-80),(-340,420),(365,500),(-550,-618),(410,-580),(-490,50),(-470,830),(420,830)]
chunks={};containers=[]
for p in Path('/workspace/map-analysis/regions/region').glob('*.mca'):
 _,rx,rz,_=p.name.split('.');rx=int(rx);rz=int(rz)
 if not (-2<=rx<=1 and -2<=rz<=2):continue
 b=p.read_bytes()
 if len(b)<8192:continue
 for i in range(1024):
  off=int.from_bytes(b[i*4:i*4+4],'big')>>8
  if not off:continue
  q=off*4096;n=int.from_bytes(b[q:q+4],'big');c=b[q+4];raw=b[q+5:q+4+n]
  if c not in (1,2,3):continue
  d=nbtlib.File.parse(io.BytesIO(zlib.decompress(raw) if c==2 else gzip.decompress(raw) if c==1 else raw))
  cx=int(d.get('xPos',0));cz=int(d.get('zPos',0));sections={}
  for sec in d.get('sections',[]):
   bs=sec.get('block_states',{});pal=bs.get('palette',[])
   if not pal:continue
   sections[int(sec['Y'])]=([str(v['Name']) for v in pal],list(map(int,bs.get('data',[]))))
  chunks[cx,cz]=sections
  for e in d.get('block_entities',[]):
   if str(e.get('id','')) in ['minecraft:chest','minecraft:barrel','minecraft:trapped_chest']:
    x,y,z=map(lambda k:int(e[k]),['x','y','z'])
    if -700<x<700 and -850<z<1250 and 45<=y<=160:containers.append((x,y,z,str(e.get('CustomName','')),str(e.get('LootTable',''))))
def block(x,y,z):
 sec=chunks.get((x//16,z//16),{}).get(y//16)
 if not sec:return 'unknown'
 pal,data=sec
 if len(pal)==1:return pal[0]
 bits=max(4,(len(pal)-1).bit_length());per=64//bits;idx=(y%16)*256+(z%16)*16+x%16
 if idx//per>=len(data):return 'unknown'
 n=(data[idx//per]>>((idx%per)*bits))&((1<<bits)-1)
 return pal[n] if n<len(pal) else 'unknown'
air={'minecraft:air','minecraft:cave_air','minecraft:void_air'}
def solid(v):return v!='unknown' and v not in air and not any(k in v for k in ['water','lava','leaves','pane','fence','door','carpet','torch','flower','grass','rail','slab','stairs','chain','sign','bar','vine','ladder','magma','campfire','cactus','fire','powder_snow'])
def interior(x,y,z):
 if block(x,y,z) not in air or block(x,y+1,z) not in air or not solid(block(x,y-1,z)):return False
 if not any(solid(block(x,y+k,z)) for k in range(2,9)):return False
 walls=sum(any(solid(block(x+dx*k,y+1,z+dz*k)) for k in range(1,7)) for dx,dz in [(1,0),(-1,0),(0,1),(0,-1)])
 return walls>=2
zones=[[] for _ in centers];used=set()
for x,y,z,name,table in containers:
 distances=[(x-a)**2+(z-b)**2 for a,b in centers];zone=min(range(9),key=lambda n:distances[n])
 if distances[zone]>170**2:continue
 candidates=[]
 for dy in [0,1,-1]:
  for dx,dz in [(1,0),(-1,0),(0,1),(0,-1),(2,0),(-2,0),(0,2),(0,-2)]:
   pos=(x+dx,y+dy,z+dz)
   if pos not in used and interior(*pos):candidates.append(pos)
 if candidates:
  pos=candidates[0];used.add(pos);zones[zone].append({'position':list(pos),'container':[x,y,z],'name':name,'table':table})
# Retain known original inspected homes as fallback while enriching every zone.
old=json.loads((r.parent/'survivorcreator-spawn-patch/homes.json').read_text())
for i,zone in enumerate(old):
 for h in zone['homes']:
  pos=(h['x'],h['y'],h['z'])
  if pos not in used:zones[i].append({'position':list(pos),'container':h['container'],'name':'existing verified home','table':''});used.add(pos)
for i,z in enumerate(zones):
 groups={}
 for h in z:
  x,y,zz=h['position'];groups.setdefault((x//32,zz//32,y//6),[]).append(h)
 buckets=list(groups.values());rng=random.Random(901+i);rng.shuffle(buckets)
 chosen=[rng.choice(bucket) for bucket in buckets[:192]]
 zones[i]=z=chosen
 z.sort(key=lambda h:(h['position'][0],h['position'][2],h['position'][1]))
 print(i,len(z),'height',min((h['position'][1] for h in z),default=0),max((h['position'][1] for h in z),default=0))
assert all(zones),'A zone has no inspected interior'
(r/'homes.json').write_text(json.dumps([{'zone':i,'center':list(centers[i]),'homes':z} for i,z in enumerate(zones)],ensure_ascii=False,indent=2)+'\n')
(r/'survivor-spawn-homes.csv').write_text(''.join(','.join(map(str,[i]+h['position']+h['container']))+'\n' for i,z in enumerate(zones) for h in z))
