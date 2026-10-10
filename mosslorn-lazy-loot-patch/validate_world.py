from pathlib import Path
import nbtlib as n,io,gzip,zlib,json,hashlib,zipfile
import sys
SRC=Path(sys.argv[1]).resolve();DST=Path(sys.argv[2]).resolve()
r=json.loads((DST/'mosslorn-v8-conversion-report.json').read_text());allowed=set(r['changes']);removed=r['old_packs_replaced'];checked=0
for p in SRC.rglob('*'):
 if not p.is_file():continue
 rel=p.relative_to(SRC).as_posix()
 if rel in allowed or any(rel.startswith('datapacks/'+x.removeprefix('file/')+'/') or rel=='datapacks/'+x.removeprefix('file/') for x in removed):continue
 q=DST/rel
 assert q.is_file() and hashlib.sha256(p.read_bytes()).digest()==hashlib.sha256(q.read_bytes()).digest(),rel
 checked+=1
def records(p):
 b=p.read_bytes();result={}
 for i in range(1024):
  o=int.from_bytes(b[i*4:i*4+4],'big')>>8
  if not o:continue
  length=int.from_bytes(b[o*4096:o*4096+4],'big');c=b[o*4096+4];payload=b[o*4096+5:o*4096+4+length]
  result[i]=gzip.decompress(payload) if c==1 else zlib.decompress(payload) if c==2 else payload
 return result
verified_chunks=0;verified_entities=0
for rel in allowed:
 if not rel.endswith('.mca'):continue
 a=records(SRC/rel);b=records(DST/rel);assert a.keys()==b.keys()
 for i,raw in a.items():
  verified_chunks+=1
  if raw==b[i]:continue
  left=n.File.parse(io.BytesIO(raw));right=n.File.parse(io.BytesIO(b[i]))
  le=left.get('Level',left).get('block_entities',[]);re=right.get('Level',right).get('block_entities',[]);assert len(le)==len(re)
  for x,y in zip(le,re):
   if str(x.get('id','')) not in {'minecraft:barrel','minecraft:chest','minecraft:trapped_chest'}:continue
   verified_entities+=1
   assert not y.get('Items',[]) and str(y.get('LootTable','')).startswith('mosslorn:'),(rel,i,'Loot not pending')
   for field in ('Items','LootTable','LootTableSeed'):x.pop(field,None);y.pop(field,None)
  aa=io.BytesIO();bb=io.BytesIO();left.write(aa);right.write(bb);assert aa.getvalue()==bb.getvalue(),(rel,i,'Unexpected chunk content change')
st=n.load(DST/'data/command_storage_mosslorn.dat');entries=st['data']['contents']['registro']['cofres'];assert len(entries)==r['registered']
keys={(str(e['dim']),int(e['x']),int(e['y']),int(e['z'])) for e in entries};assert len(keys)==len(entries)
with zipfile.ZipFile(next((DST/'datapacks').glob('mosslorn-expanded.8*.zip'))) as z:
 tables={'mosslorn:'+p.removeprefix('data/mosslorn/loot_table/').removesuffix('.json') for p in z.namelist() if p.startswith('data/mosslorn/loot_table/') and p.endswith('.json')}
 assert all(str(e['tabla']) in tables for e in entries)
level_a=n.load(SRC/'level.dat');level_b=n.load(DST/'level.dat')
level_a['Data'].pop('DataPacks');level_b['Data'].pop('DataPacks');aa=io.BytesIO();bb=io.BytesIO();level_a.write(aa);level_b.write(bb);assert aa.getvalue()==bb.getvalue()
result={'unchanged_files_sha256_verified':checked,'chunks_in_changed_regions_verified':verified_chunks,'containers_in_changed_chunks_verified':verified_entities,'registry_entries':len(entries),'registry_keys_unique':True,'registry_loot_tables_valid':True,'custom_names_and_unrelated_chunk_data_preserved':True,'pending_after_conversion':r['counts']['pending_after_conversion'],'occupied_inventories_converted_to_pending':r['counts']['occupied_replaced_with_pending_loot'],'level_data_unchanged_except_pack_selection':True,'minecraft_runtime_tested':False}
(Path(__file__).resolve().parent/'world-validation.json').write_text(json.dumps(result,indent=2));print(json.dumps(result,indent=2),flush=True)
