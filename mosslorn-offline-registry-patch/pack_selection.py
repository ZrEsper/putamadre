"""Edit only datapack selection locally; never bundle the world's level.dat."""
import gzip,struct,io

def update_level(raw,remove,add):
 s=io.BytesIO(gzip.decompress(raw))
 def take(n):
  b=s.read(n)
  if len(b)!=n:raise ValueError('Truncated NBT')
  return b
 def text():return take(struct.unpack('>H',take(2))[0]).decode('utf-8')
 def payload(t):
  if t in (1,2,3,4,5,6):return take({1:1,2:2,3:4,4:8,5:4,6:8}[t])
  if t==8:return text()
  if t in (7,11,12):
   prefix=take(4);count=struct.unpack('>i',prefix)[0]
   if count<0:raise ValueError('Negative array length')
   return prefix+take(count*{7:1,11:4,12:8}[t])
  if t==9:
   child=take(1)[0];count=struct.unpack('>i',take(4))[0]
   if count<0:raise ValueError('Negative list length')
   return child,[payload(child) for _ in range(count)]
  if t==10:
   out=[]
   while True:
    child=take(1)[0]
    if not child:return out
    name=text();out.append((name,(child,payload(child))))
  raise ValueError('Unsupported NBT tag '+str(t))
 t=take(1)[0];name=text();root=payload(t)
 if t!=10:raise ValueError('Expected compound root')
 def field(comp,key):
  return next(v for k,v in comp if k==key)
 data=field(root,'Data');packs=field(data[1],'DataPacks')
 if data[0]!=10 or packs[0]!=10:raise ValueError('Invalid datapack structure')
 for key in ('Enabled','Disabled'):
  tag=field(packs[1],key)
  if tag[0]!=9 or tag[1][0] not in (0,8):raise ValueError('Invalid datapack list')
  values=tag[1][1]
  values[:]=[v for v in values if v not in remove and v!=add] if key=='Enabled' else [v for v in values if v!=add]
  if key=='Enabled':values.append(add)
  for i,(k,v) in enumerate(packs[1]):
   if k==key:packs[1][i]=(key,(9,(8,values)))
 def string(value):
  b=value.encode('utf-8');return struct.pack('>H',len(b))+b
 def encode(t,value):
  if t in (1,2,3,4,5,6,7,11,12):return value
  if t==8:return string(value)
  if t==9:return bytes([value[0]])+struct.pack('>i',len(value[1]))+b''.join(encode(value[0],v) for v in value[1])
  if t==10:return b''.join(bytes([tag[0]])+string(key)+encode(*tag) for key,tag in value)+b'\0'
  raise ValueError('Unsupported NBT tag')
 return gzip.compress(bytes([t])+string(name)+encode(t,root),mtime=0)
