from pathlib import Path
import zipfile,json,math,itertools
z=zipfile.ZipFile('/workspace/putamadre/mods/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-board-recipe.5.jar')
rows=[]
for n in z.namelist():
 if '/models/block/remains_' not in n:continue
 model=json.loads(z.read(n))['parent'].split(':')[1];elements=json.loads(z.read('assets/doomsday_decoration/models/'+model+'.json'))['elements'];boxes=[]
 for e in elements:
  points=[]
  for p in itertools.product(*zip(e['from'],e['to'])):
   p=list(p);rot=e.get('rotation',{});a=rot.get('axis','y');theta=math.radians(rot.get('angle',0));origin=rot.get('origin',[0,0,0]);axes={'x':(1,2),'y':(2,0),'z':(0,1)}[a];i,j=axes;u,v=p[i]-origin[i],p[j]-origin[j];p[i]=origin[i]+u*math.cos(theta)-v*math.sin(theta);p[j]=origin[j]+u*math.sin(theta)+v*math.cos(theta);points.append(p)
  boxes.append([min(p[a] for p in points)/16 for a in range(3)]+[max(p[a] for p in points)/16 for a in range(3)])
 bounds=[min(b[a] for b in boxes) for a in range(3)]+[max(b[a+3] for b in boxes) for a in range(3)]
 print(n.split('/')[-1],*[round(x,3) for x in bounds])
 # Only prone models: seated/standing remains would contradict the settled corpse.
 if bounds[4]>.85:continue
 for turn in range(4):
  cells=set();rb=[]
  for b in boxes:
   points=[]
   for x,y,zz in itertools.product((b[0],b[3]),(b[1],b[4]),(b[2],b[5])):
    for _ in range(turn):x,zz=1-zz,x
    points.append((x,y,zz))
   bb=[min(p[a] for p in points) for a in range(3)]+[max(p[a] for p in points) for a in range(3)];rb.append(bb)
   # Decorative models contain small negative-Y pieces. These are embedded in support,
   # not additional cells to replace; no above-floor collision is ignored.
   for x in range(math.floor(bb[0]+1e-5),math.ceil(bb[3]-1e-5)):
    for y in range(max(0,math.floor(bb[1]+1e-5)),max(1,math.ceil(bb[4]-1e-5))):
     for zz in range(math.floor(bb[2]+1e-5),math.ceil(bb[5]-1e-5)):cells.add((x,y,zz))
  bounds2=[min(b[a] for b in rb) for a in range(3)]+[max(b[a+3] for b in rb) for a in range(3)]
  rows.append(n.rsplit('/',1)[1][:-5]+' '+str(turn)+' '+' '.join(str(round(x,5)) for x in ((bounds2[0]+bounds2[3])/2,(bounds2[2]+bounds2[5])/2))+' '+';'.join(','.join(map(str,c)) for c in sorted(cells)))
Path('/workspace/medical48/resources/dev/zomboid/gore/remains48.txt').write_text('\n'.join(rows)+'\n')
