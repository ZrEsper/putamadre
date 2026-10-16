from pathlib import Path
import json,hashlib,subprocess,zipfile,os,re
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;tools=root/'hordes-melee-interiors-patch/build/tools';asm=':'.join(str(tools/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn']
def run(*args):subprocess.run(list(map(str,args)),check=True)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
inputs=json.loads(Path('inputs.json').read_text())
for info in inputs.values():assert sha(Path('base')/info['file'])==info['sha256']
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(tools/(n+'.jar'))==h
for d in ['build/tool','build/api','build/npc','build/clan','build/aid','build/integration','build/corpse','output']:Path(d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',asm,'-d','build/tool','Patch.java');run(*ecj,'-d','build/api',*Path('api').rglob('*.java'));reports={}
for kind,src,out in [('npc','src','tacznpcs-2.1.0-1.21.1-tactical.20-identities-reactions.jar'),('clan','src-clans','factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.16-npc-companions.jar'),('aid','src-aid','firstaid-1.21.1-ZOMBOID-v3.6.9-injured-only-body-care.jar'),('integration','src-integration','zomboid-integration-1.7.9-neoforge-1.21.1-injured-body-care.jar'),('corpse','src-corpse','corpse-neoforge-1.21.1-1.1.13-npc-survivor-cargo.3.jar')]:
 base=Path('base')/inputs[kind]['file'];sources=list(Path(src).rglob('*.java'));sources+=list(Path('src-client').rglob('*.java'))if kind=='npc'else list(Path('src-aid-client').rglob('*.java'))if kind=='aid'else[];run(*ecj,'-cp','build/api:'+str(base)+':'+str(Path('base')/inputs['aid']['file']),'-d','build/'+kind,*sources);p=Path('output')/out;run('java','-cp','build/tool:'+asm,'Patch',base,p,'build/'+kind,kind)
 with zipfile.ZipFile(p)as z:entries={n:z.read(n)for n in z.namelist()}
 metadata='META-INF/neoforge.mods.toml'
 versions={'npc':'2.1.0-tactical.20-social','clan':'1.8.0R-zomboid-clans.16','aid':'1.3.3+neoforge1.21.1-zomboid-v3.6.9-body-care','integration':'1.7.9-injured-body-care','corpse':'1.1.13-npc-survivor-cargo.3'}
 if metadata in entries:entries[metadata]=re.sub(r'(?m)^version\s*=\s*"[^"\n]*"','version="'+versions[kind]+'"',entries[metadata].decode(),count=1).encode()
 if kind=='aid':
  for loc,text in [('es_es','Chat: tu tecla de chat | Esc: menú del juego'),('es_mx','Chat: tu tecla de chat | Esc: menú del juego'),('en_us','Chat: your chat key | Esc: game menu')]:
   path='assets/firstaid/lang/'+loc+'.json';data=json.loads(entries.get(path,b'{}'));data['firstaid.blackout.controls']=text;
   labels={'hold_bandage':('Lleva una venda en una mano para tratar la parte.','Hold a bandage in either hand to treat the injury.'),'title':('Salud','Health'),'healthy':('Sin lesiones','No injuries'),'injuries':('Lesiones que puedes tratar','Injuries you can treat'),'injury':('Lesión','Injury'),'treat':('Tratar parte','Treat injury'),'self':('Revisarme','Examine myself'),'other':('Revisar a %s','Examine %s'),'close':('Cerrar','Close'),'patient':('Salud · %s','Health · %s'),'progress':('Tratando: %s%%','Treating: %s%%'),'revive':('Reanimar','Revive'),'down':('Derribado: necesita reanimación','Downed: needs revival')}
   for key,pair in labels.items():data['firstaid.medical.'+key]=pair[1 if loc=='en_us' else 0]
   for key,es,en in [('head','Cabeza','Head'),('body','Torso','Torso'),('left_arm','Brazo izquierdo','Left arm'),('right_arm','Brazo derecho','Right arm'),('left_leg','Pierna izquierda','Left leg'),('right_leg','Pierna derecha','Right leg'),('left_foot','Pie izquierdo','Left foot'),('right_foot','Pie derecho','Right foot')]:data['firstaid.medical.part.'+key]=en if loc=='en_us' else es
   entries[path]=json.dumps(data,ensure_ascii=False,separators=(',',':')).encode()
 if kind=='npc':
  for skin in Path('skins').glob('*.png'):entries['assets/tacznpcs/entity/survivors/'+skin.name]=skin.read_bytes()
  sounds={}
  for mood in ['calm','happy','angry','hostile','afraid']:
   for i,gender in enumerate(['male','female']):sounds['voice_'+mood+'_'+gender]={'subtitle':'soc.sound.'+mood,'sounds':[{'name':'tacznpcs:voice/'+mood+str(i),'weight':1}]}
   for i in range(2):entries['assets/tacznpcs/sounds/voice/'+mood+str(i)+'.ogg']=Path('audio/'+mood+str(i)+'.ogg').read_bytes()
  entries['assets/tacznpcs/sounds.json']=json.dumps(sounds,separators=(',',':')).encode()
  translations=json.loads(Path('translations.json').read_text())
  for loc,values in translations.items():
   path='assets/tacznpcs/lang/'+loc+'.json';data=json.loads(entries.get(path,b'{}'));data.update(values);entries[path]=json.dumps(data,ensure_ascii=False,separators=(',',':')).encode()
 with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(entries.items()):info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 with zipfile.ZipFile(base)as old,zipfile.ZipFile(p)as new:changed=sorted(n for n in old.namelist()if old.read(n)!=new.read(n));added=sorted(set(new.namelist())-set(old.namelist()));assert not any(n.startswith('net/minecraft/')for n in added)
 reports[kind]={'input_sha256':sha(base),'output':out,'sha256':sha(p),'changed':changed,'added':added}
Path('validation.json').write_text(json.dumps(reports,indent=2)+'\n')
