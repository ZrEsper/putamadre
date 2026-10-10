from pathlib import Path
import io,json,gzip,zlib,struct,shutil,hashlib,collections,copy,csv
import nbtlib as n
import sys
BASE=Path(__file__).resolve().parent
if len(sys.argv)>1:SRC=Path(sys.argv[1]).resolve()
else:
 import tkinter as tk
 from tkinter import filedialog
 ui=tk.Tk();ui.withdraw();selected=filedialog.askdirectory(title='Minecraft cerrado: elige el mundo para convertir a loot pendiente')
 if not selected:sys.exit(0)
 SRC=Path(selected).resolve()
if not (SRC/'level.dat').is_file():raise ValueError('Selecciona la carpeta que contiene level.dat')
if (SRC/'mosslorn-v8-conversion-report.json').exists():raise ValueError('Este mundo ya fue convertido a v8. No se sustituirá su loot otra vez; selecciona la copia anterior.')
DST=SRC.with_name(SRC.name+'-v8-loot-pendiente')
PACK=BASE/'mosslorn-expanded.8-loot-al-abrir.zip'
TARGETS={'minecraft:chest','minecraft:trapped_chest','minecraft:barrel'}
import zipfile
with zipfile.ZipFile(PACK) as z:
 TABLES={'mosslorn:'+p.removeprefix('data/mosslorn/loot_table/').removesuffix('.json') for p in z.namelist() if p.startswith('data/mosslorn/loot_table/') and p.endswith('.json')}
registry={};counts=collections.Counter();contexts=collections.Counter();names=collections.Counter();changes=[]
history={}
previous=SRC/'data/command_storage_mosslorn.dat'
if previous.exists():
 previous_nbt=n.load(previous)
 for entry in previous_nbt.get('data',{}).get('contents',{}).get('registro',{}).get('cofres',[]):
  if str(entry.get('tabla','')) in TABLES:history[(str(entry.get('dim','minecraft:overworld')),int(entry['x']),int(entry['y']),int(entry['z']))]=str(entry['tabla'])
def plain(value):
 if isinstance(value,str):return value
 if isinstance(value,list):return ''.join(plain(v) for v in value)
 if isinstance(value,dict):return str(value.get('text',value.get('translate','')))+''.join(plain(v) for v in value.get('extra',[]))
 return ''
exact_names={'office cabinet':'crate_office','kitchen cabinet':'crate_kitchen','fish crate':'crate_fish','mossy mass':'moss_megastructure','car trunk':'crate_car','library shelf':'crate_books','fridge':'crate_fridge','bathroom crate':'crate_bathroom','big 7 drink shelf':'crate_kitchen','big 7 freezer':'crate_fridge','the bronze jame bar crate':'crate_kitchen','big 7 snack shelf':'crate_kitchen','"civilian grade" office supplies':'crate_office','"civilian grade" research equipment':'crate_electronics'}
name_context={'office cabinet':'crate_office','kitchen':'crate_kitchen','fridge':'crate_fridge','fish':'crate_fish','car trunk':'crate_car','pharmacy':'crate_pharmacy','medicine cabinet':'crate_pharmacy','bathroom':'crate_bathroom','bookshelf':'crate_books','workshop':'crate_workshop','toolbox':'crate_workshop'}
def dimension(path):
 parts=path.relative_to(SRC).parts
 if parts[0]=='DIM-1':return 'minecraft:the_nether'
 if parts[0]=='DIM1':return 'minecraft:the_end'
 if parts[0]=='dimensions':return parts[1]+':'+ '/'.join(parts[2:-2])
 return 'minecraft:overworld'
def transform(raw,dim):
 root=n.File.parse(io.BytesIO(raw));data=root.get('Level',root);changed=False
 for be in data.get('block_entities',data.get('TileEntities',[])):
  if str(be.get('id','')) not in TARGETS:continue
  counts['containers']+=1;counts[str(be['id'])]+=1
  xyz=tuple(int(be[k]) for k in ('x','y','z'));key=(dim,*xyz)
  if key in registry:raise ValueError('Duplicate container position')
  old=str(be.get('LootTable',''));table=old if old in TABLES else 'mosslorn:chests/world_mix'
  rawname=str(be.get('CustomName',''))
  try:custom=plain(json.loads(rawname)) if rawname else ''
  except (ValueError,TypeError):custom=rawname
  if custom:names[custom]+=1;counts['named_containers']+=1
  context=exact_names.get(custom.lower())
  if context:
   table='mosslorn:chests/'+context;counts['category_from_name']+=1
  elif not old:
   historical=history.get((dim,*xyz))
   if historical:table=historical;counts['category_recovered_from_v7_registry']+=1
   else:
    for needle,context in name_context.items():
     if needle in custom.lower():table='mosslorn:chests/'+context;counts['category_from_name']+=1;break
  if table not in TABLES:raise ValueError('Unknown table '+table)
  contexts[table]+=1
  occupied=bool(be.get('Items',[]))
  if occupied:
   counts['occupied_replaced_with_pending_loot']+=1;counts['removed_item_stacks']+=len(be['Items'])
  counts['pending_after_conversion']+=1
  before_table=str(be.get('LootTable',''))
  if occupied or before_table!=table:
   be['Items']=n.List[n.Compound]([])
   be['LootTable']=n.String(table)
   be['LootTableSeed']=n.Long(int.from_bytes(hashlib.sha256(repr(key).encode()).digest()[:8],'big',signed=True) or 1)
   changed=True;counts['table_assigned_or_updated']+=1
  else:counts['pending_table_preserved']+=1
  registry[key]=n.Compound({'dim':n.String(dim),'x':n.Int(xyz[0]),'y':n.Int(xyz[1]),'z':n.Int(xyz[2]),'tabla':n.String(table),'ultima':n.Int(0),'generado':n.Byte(1)})
 if not changed:return raw
 out=io.BytesIO();root.write(out);return out.getvalue()
def region(path):
 dim=dimension(path);b=path.read_bytes();counts['regions']+=1
 if not b:counts['empty_region_files_preserved']+=1;return
 if len(b)<8192:raise ValueError('Truncated nonempty region '+str(path))
 header=bytearray(b[:8192]);records=[];changed=False
 for i in range(1024):
  val=int.from_bytes(header[i*4:i*4+4],'big');offset=val>>8;size=val&255
  if not offset:records.append(None);continue
  if offset<2 or size==0 or (offset+size)*4096>len(b):raise ValueError('Invalid location')
  length=int.from_bytes(b[offset*4096:offset*4096+4],'big');comp=b[offset*4096+4]
  if length<1 or length+4>size*4096:raise ValueError('Invalid length')
  record=b[offset*4096:offset*4096+4+length]
  if comp not in (1,2,3):raise ValueError('Unsupported compression/external chunk: '+str(comp))
  payload=record[5:];raw=gzip.decompress(payload) if comp==1 else zlib.decompress(payload) if comp==2 else payload
  new=transform(raw,dim);counts['chunks']+=1
  if new!=raw:
   compressed=gzip.compress(new,mtime=0) if comp==1 else zlib.compress(new) if comp==2 else new
   record=struct.pack('>I',len(compressed)+1)+bytes([comp])+compressed;changed=True;counts['changed_chunks']+=1
  records.append(record)
 if changed:
  body=bytearray();offset=2
  for i,record in enumerate(records):
   if record is None:continue
   size=(len(record)+4095)//4096
   if size>255:raise ValueError('Oversized chunk')
   header[i*4:i*4+4]=((offset<<8)|size).to_bytes(4,'big');body+=record+bytes(size*4096-len(record));offset+=size
  dest=DST/path.relative_to(SRC);dest.write_bytes(header+body);changes.append(str(dest.relative_to(DST)))
if DST.exists():raise ValueError('Output exists')
shutil.copytree(SRC,DST)
for i,path in enumerate(sorted(SRC.rglob('r.*.*.mca'))):
 if path.parent.name=='region':region(path)
 if i%30==0:print('Scanned regions',i,'containers',counts['containers'],flush=True)
# Persist the complete registry in the same SavedData format the v7 uses.
storage=DST/'data/command_storage_mosslorn.dat'
root=n.load(storage) if storage.exists() else n.File({'DataVersion':n.Int(3955),'data':n.Compound({'contents':n.Compound()})},gzipped=True)
contents=root['data']['contents'];existing=contents.get('registro',n.Compound()).get('cofres',[])
for entry in existing:
 key=(str(entry.get('dim','minecraft:overworld')),int(entry['x']),int(entry['y']),int(entry['z']))
 if key in registry:
  registry[key]['ultima']=n.Int(int(entry.get('ultima',0)))
contents.setdefault('registro',n.Compound())['cofres']=n.List[n.Compound](list(registry.values()))
contents['runtime']=n.Compound({'cola':n.List[n.Compound]([]),'activo':n.Byte(0),'visitados':n.List[n.Compound]([])})
root.save(storage,gzipped=True);changes.append(str(storage.relative_to(DST)))
# Replace only the old Mosslorn packs; retain all unrelated packs and player data.
dp=DST/'datapacks';removed=[]
for path in dp.iterdir():
 if path.name.startswith('mosslorn-expanded.') or path.name=='mosslorn_loot':
  removed.append('file/'+path.name)
  shutil.rmtree(path) if path.is_dir() else path.unlink()
shutil.copyfile(PACK,dp/PACK.name);changes.append('datapacks/'+PACK.name)
level=n.load(DST/'level.dat');packs=level['Data']['DataPacks'];enabled=[str(x) for x in packs['Enabled'] if str(x) not in removed]
enabled.append('file/'+PACK.name);packs['Enabled']=n.List[n.String](enabled)
packs['Disabled']=n.List[n.String]([str(x) for x in packs['Disabled'] if str(x)!='file/'+PACK.name]);level.save(DST/'level.dat',gzipped=True);changes.append('level.dat')
report={'counts':dict(counts),'categories':dict(contexts),'custom_names':dict(names),'registered':len(registry),'changes':changes,'old_packs_replaced':removed,'source':str(SRC),'minecraft_runtime_tested':False,'items_replaced_with_pending_loot':True,'custom_names_preserved':True}
(DST/'mosslorn-v8-conversion-report.json').write_text(json.dumps(report,indent=2));print(json.dumps(report,indent=2),flush=True);print('LISTO: abre la copia nueva '+str(DST),flush=True)
