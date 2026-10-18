from pathlib import Path
import hashlib,subprocess,json,zipfile,re
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';base=r/'base/npc21.jar';h=hashlib.sha256(base.read_bytes()).hexdigest();pin=r/'inputs.json'
if pin.exists():assert json.loads(pin.read_text())['npc21.jar']==h
else:pin.write_text(json.dumps({'npc21.jar':h},indent=2)+'\n')
for folder in ['build/api','build/classes','build/tools','output']:(r/folder).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
subprocess.run([*ecj,'-d',str(r/'build/api'),*map(str,(r/'api').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',str(r/'build/api')+':'+str(root/'npc-social17/build/api')+':'+str(base),'-d',str(r/'build/classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tools'),str(r/'Patch.java')],check=True)
out=r/'output/tacznpcs-2.1.0-1.21.1-tactical.22-four-eggs-military.jar';subprocess.run(['java','-cp',str(r/'build/tools')+':'+asm,'Patch',str(base),str(out),str(r/'build/classes')],check=True)
with zipfile.ZipFile(out)as z:e={n:z.read(n)for n in z.namelist()}
eggs=['bandit_passive_spawn_egg','bandit_neutral_spawn_egg','bandit_spawn_egg','military_hostile_spawn_egg']
for egg in eggs:e['assets/tacznpcs/models/item/'+egg+'.json']=json.dumps({'parent':'minecraft:item/template_spawn_egg'}).encode()
for loc,labels in [('en_us',['Passive Survivor Spawn Egg','Neutral Survivor Spawn Egg','Hostile Bandit Spawn Egg','Hostile Military Spawn Egg']),('es_es',['Huevo de superviviente pasivo','Huevo de superviviente neutral','Huevo de bandido hostil','Huevo de militar hostil']),('es_mx',['Huevo de superviviente pasivo','Huevo de superviviente neutral','Huevo de bandido hostil','Huevo de militar hostil'])]:
 key='assets/tacznpcs/lang/'+loc+'.json';d=json.loads(e.get(key,b'{}'));d.update({'item.tacznpcs.'+egg:label for egg,label in zip(eggs,labels)});e[key]=json.dumps(d,ensure_ascii=False,indent=2).encode()
e['META-INF/neoforge.mods.toml']=re.sub(r'(?m)^version\s*=\s*"[^"\n]*"','version="2.1.0-tactical.22-roles"',e['META-INF/neoforge.mods.toml'].decode(),count=1).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:assert b.testzip()is None;changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith(('net/','com/tacz/'))for n in added)
(r/'validation.json').write_text(json.dumps(dict(file=out.name,sha256=hashlib.sha256(out.read_bytes()).hexdigest(),changed=changed,added=added,eggs=eggs,minecraft_runtime_tested=False),indent=2)+'\n')
