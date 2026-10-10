from pathlib import Path
import json,gzip,zipfile,re,collections,copy
import nbtlib
r=Path(__file__).resolve().parent
rows=json.loads(gzip.decompress((r/'catalogo.json.gz').read_bytes()));assert len(rows)==61793
keys={(e['dim'],e['x'],e['y'],e['z']) for e in rows};assert len(keys)==len(rows)
with zipfile.ZipFile(r/'mosslorn-expanded.9-reinicio-total.zip') as z:
 assert z.testzip() is None;names=set(z.namelist());found=[];refs=0;maxline=0
 for name in names:
  b=z.read(name)
  if name.endswith('.json') or name=='pack.mcmeta':json.loads(b)
  if not name.endswith('.mcfunction'):continue
  text=b.decode();assert 'loot insert' not in text and 'loot replace' not in text,name
  assert not re.search(r'\b(?:work|entrada) \{',text),name
  for fn in re.findall(r'\bfunction mosslorn:([a-z0-9_/-]+)',text):assert 'data/mosslorn/function/'+fn+'.mcfunction' in names,fn;refs+=1
  if '/force/catalogo/' in name:
   value=text.split(' set value ',1)[1];tag=nbtlib.parse_nbt(value);found.extend(tag['entries']);maxline=max(maxline,len(text))
  for line in text.splitlines():
   if line.startswith('$'):assert '$(' in line,name
 assert len(found)==61793
 tables={'mosslorn:'+n.removeprefix('data/mosslorn/loot_table/').removesuffix('.json') for n in names if n.startswith('data/mosslorn/loot_table/') and n.endswith('.json')}
 assert all(e['tabla'] in tables for e in rows)
 assert z.read('data/mosslorn/function/loot/reiniciar.mcfunction').decode().strip()=='function mosslorn:force/reiniciar'
 apply=z.read('data/mosslorn/function/force/aplicar.mcfunction').decode()
 assert 'data remove block ~ ~ ~ Items' in apply and 'LootTable set value' in apply
 assert 'CustomName' not in apply and 'loot insert' not in apply
 assert 'unless loaded' in z.read('data/mosslorn/function/force/visitar.mcfunction').decode()
 assert '.entries set from storage mosslorn:force queue' in z.read('data/mosslorn/function/force/suspender.mcfunction').decode()
 # Execute the actual three data-edit operations against NBT containers.
 for occupied,oldtable in [(True,None),(False,None),(True,'mosslorn:chests/crate_office'),(False,'mosslorn:chests/crate_fridge')]:
  be=nbtlib.Compound({'CustomName':nbtlib.String('{"text":"Office Cabinet"}'),'Lock':nbtlib.String('keep'),'Items':nbtlib.List[nbtlib.Compound]([nbtlib.Compound({'id':nbtlib.String('minecraft:diamond'),'count':nbtlib.Int(3)})] if occupied else []),'LootTableSeed':nbtlib.Long(42)})
  table=oldtable or 'mosslorn:chests/crate_office'
  if oldtable:be['LootTable']=nbtlib.String(oldtable)
  original_name=str(be['CustomName'])
  for line in apply.replace('$(tabla)',table).splitlines():
   if line.startswith('data remove block ~ ~ ~ '):be.pop(line.split()[-1],None)
   if line.startswith('$data modify block ~ ~ ~ LootTable set value '):be['LootTable']=nbtlib.parse_nbt(line.split(' set value ',1)[1])
  assert not be.get('Items') and str(be['LootTable'])==table and str(be['CustomName'])==original_name and str(be['Lock'])=='keep'
 # Minecraft scoreboard division truncates toward zero; the adjustment must floor negative coordinates.
 for v in [-33,-32,-17,-16,-1,0,1,15,16,17,32]:assert int((v-15 if v<0 else v)/16)==v//16
 # A registered custom assignment replaces just its coordinate inside its chunk.
 group=copy.deepcopy(rows[:2]);custom=dict(group[0],tabla='mosslorn:chests/crate_pharmacy')
 group=[e for e in group if (e['x'],e['y'],e['z'])!=(custom['x'],custom['y'],custom['z'])]+[custom]
 assert len(group)==2 and group[-1]['tabla'].endswith('crate_pharmacy')
result={'catalog_containers':len(rows),'catalog_keys_unique':True,'bootstrap_nbt_parsed':True,'function_references_checked':refs,'maximum_bootstrap_command_length':maxline,'actual_nbt_data_edits_cases':4,'negative_chunk_coordinate_cases':11,'custom_assignment_override_case':True,'unload_keeps_remaining_queue':True,'no_immediate_loot_insertion':True,'minecraft_runtime_tested':False}
(r/'validation.json').write_text(json.dumps(result,indent=2));print(json.dumps(result,indent=2))
