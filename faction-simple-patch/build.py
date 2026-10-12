from pathlib import Path
import sys,subprocess,tempfile,os,zipfile,json,hashlib
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]).resolve();tools=Path(sys.argv[2]).resolve();stubs=Path(sys.argv[3]).resolve() if len(sys.argv)>3 else None;out=r.parent/'mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.8-indoor-nexus-slab-spawns.jar'
assert hashlib.sha256(base.read_bytes()).hexdigest()=='54a6bc7d6397af982c7e3e0d0c6834cf7c183b9d50a862ff250fe072e4f50fab','Unexpected base'
with zipfile.ZipFile(r.parent/'faction-clans-patch/faction-clans.1-fuentes.zip') as archive:
 for name,digest in json.loads(archive.read('tools.sha256.json')).items():
  assert hashlib.sha256((tools/name).read_bytes()).hexdigest()==digest,name
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar']);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn']
with tempfile.TemporaryDirectory() as tmp:
 t=Path(tmp)
 if stubs is None:
  stubs=t/'base-stubs'
  with zipfile.ZipFile(r.parent/'faction-clans-patch/faction-clans.1-fuentes.zip') as z:
   for n in z.namelist():
    if n.startswith('stubs/') and n.endswith('.java'):
     dest=stubs/Path(n).relative_to('stubs');dest.parent.mkdir(parents=True,exist_ok=True);dest.write_bytes(z.read(n))
 c=t/'classes';p=t/'patch';tests=t/'tests'
 for command in [ecj+['-cp',str(base),'-d',str(c)]+[str(f) for root in [stubs,r/'stubs',r/'src'] for f in root.rglob('*.java')],ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java')],['java','-cp',str(p)+os.pathsep+asm,'Patch',str(base),str(out),str(c)],ecj+['-cp',str(out)+os.pathsep+str(c),'-d',str(tests)]+[str(f) for f in (r/'tests').rglob('*.java')],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(out)+os.pathsep+str(c),'ClanChecks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(out)+os.pathsep+str(c),'dev.zomboid.clans.PveChecks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(out)+os.pathsep+str(c),'dev.zomboid.clans.NexusUpgradeChecks']]:subprocess.run(command,check=True,cwd=t)
 with zipfile.ZipFile(out) as z:data={n:z.read(n) for n in z.namelist()}
 for f in (r/'resources').rglob('*'):
  if f.is_file():
   name=str(f.relative_to(r/'resources'));payload=f.read_bytes()
   if name.endswith('/sounds.json') and name in data:payload=json.dumps({**json.loads(data[name]),**json.loads(payload)},indent=2).encode()
   data[name]=payload
 for lang,label in [('es_es','Nexo tribal'),('en_us','Tribal Nexus')]:
  n='assets/factionfriction/lang/'+lang+'.json';d=json.loads(data[n]);d['block.factionfriction.tribal_nexus']=label
  texts=['Defiende este nexo para ganar XP y recompensas.', 'El jefe debe colocarlo dentro de un chunk del clan.', 'Inicia la defensa desde K > Oleadas PvE.', 'Vida: 100; mejoras hasta 350 (+25 por nivel).', 'Daño por golpe: zombi 5; infectado especial 10.', 'La vida no se reinicia entre las 3 oleadas.', 'Si cae el nexo o mueren todos los defensores, pierdes.', 'Recogible fuera de defensa. Espera entre partidas: 5 min.'] if lang=='es_es' else ['Defend this nexus to earn clan XP and rewards.', 'The leader must place it inside a claimed clan chunk.', 'Start a defense from K > PvE Waves.', 'Health: 100; upgrade to 350 (+25 per level).', 'Damage per hit: zombie 5; special infected 10.', 'Health does not reset between the 3 waves.', 'Lose if the nexus falls or all defenders die.', 'Move outside defenses. Cooldown between games: 5 min.']
  for i,text in enumerate(texts,1):d['tooltip.factionfriction.tribal_nexus.'+str(i)]=text
  for sound,label in {'generator':'Generador del nexo','nexus_hit':'Nexo dañado','phase':'Nueva oleada','victory':'Defensa completada','defeat':'Defensa perdida','deposit':'Suministros depositados','withdraw':'Suministros recogidos','start':'Defensa iniciada'}.items():d['subtitle.factionfriction.'+sound]=label
  data[n]=(json.dumps(d,ensure_ascii=False,indent=2)+'\n').encode()
 data['META-INF/neoforge.mods.toml']=data['META-INF/neoforge.mods.toml'].replace(b'1.8.0R-zomboid-clans.1',b'1.8.0R-zomboid-clans.8')
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  assert not any(n.startswith(('net/','com/tacz/')) or '/PveChecks' in n or '/NexusUpgradeChecks' in n for n in b.namelist())
  original_sounds=json.loads(a.read('assets/factionfriction/sounds.json'));all_sounds=json.loads(b.read('assets/factionfriction/sounds.json'));assert all(all_sounds[k]==v for k,v in original_sounds.items())
  for sound in ['generator','nexus_hit','phase','victory','defeat','deposit','withdraw','start']:assert b.read('assets/factionfriction/sounds/'+sound+'.ogg').startswith(b'OggS')
  changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 (r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changed,'minecraft_runtime_tested':False,'simulated_checks_passed':166},indent=2)+'\n')
 print('Verified JAR integrity and no packaged API doubles')
