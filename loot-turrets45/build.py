from pathlib import Path
import os,subprocess,hashlib,json,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';inp=json.loads(Path('inputs.json').read_text());asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*args):subprocess.run(list(map(str,args)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
for v in inp.values():assert sha(Path('base')/v['file'])==v['sha256']
Path('build/tool').mkdir(parents=True,exist_ok=True);Path('output').mkdir(exist_ok=True);run(*ecj,'-cp',asm,'-d','build/tool','Patch.java')
names={'remains':'zombie-remains-loot-1.6.1-neoforge-1.21.1-profession-clothes-scatter.15.jar','hordes':'zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.46-corpse-access-fix.jar','quiet':'quietly-1_0_0-cooldown-v17-final-loot-tools-scatter.jar','turret':'tacz_turrets-neoforge-3.0.0-1.21.1-three-tiers.4.jar'}
reports={}
for kind in ['hordes','remains','quiet','turret']:
 base=Path('base')/inp[kind]['file'];classes=Path('build')/kind;classes.mkdir(exist_ok=True);run(*ecj,'-cp',base,'-d',classes,*Path('src',kind).rglob('*.java'));out=Path('output')/names[kind];run('java','-cp','build/tool:'+asm,'Patch',base,out,classes,kind)
 with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
 def js(n,v):entries[n]=json.dumps(v,ensure_ascii=False,separators=(',',':')).encode()
 if kind=='hordes':
  for loc,label in [('es_es','Bastón policial'),('es_mx','Bastón policial'),('en_us','Police baton'),('de_de','Polizeischlagstock')]:
   n='assets/zomboid_hordes/lang/'+loc+'.json';d=json.loads(entries.get(n,b'{}'));d['item.zomboid_hordes.police_baton']=label;js(n,d)
 if kind=='turret':
  for tier,label in [('turret_reinforced','industrial'),('turret_advanced','tactical')]:js('assets/tacz_turrets/models/item/'+tier+'.json',{'parent':'minecraft:item/generated','textures':{'layer0':'tacz_turrets:item/turret_'+label}})
  recipes={
  'turret_reinforced':(['IGI','STS','IRI'],{'I':{'item':'minecraft:iron_ingot'},'G':{'item':'minecraft:gold_ingot'},'S':{'item':'tacz_turrets:electronic_scrap'},'T':{'item':'tacz_turrets:turret'},'R':{'item':'minecraft:redstone'}}),
  'turret_advanced':(['DGD','STS','IRI'],{'D':{'item':'minecraft:diamond'},'G':{'item':'minecraft:gold_block'},'S':{'item':'tacz_turrets:electronic_scrap'},'T':{'item':'tacz_turrets:turret_reinforced'},'I':{'item':'minecraft:iron_ingot'},'R':{'item':'minecraft:redstone_block'}})}
  for n,(pattern,key)in recipes.items():
   js('data/tacz_turrets/recipe/'+n+'.json',{'type':'minecraft:crafting_shaped','category':'misc','pattern':pattern,'key':key,'result':{'id':'tacz_turrets:'+n,'count':1}});js('data/tacz_turrets/advancement/recipes/'+n+'.json',{'parent':'minecraft:recipes/root','criteria':{'has_turret':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':key['T']['item']}]}}},'rewards':{'recipes':['tacz_turrets:'+n]}})
  for loc,labels in [('es_es',['Torreta básica','Torreta reforzada','Torreta avanzada']),('es_mx',['Torreta básica','Torreta reforzada','Torreta avanzada']),('en_us',['Basic turret','Reinforced turret','Advanced turret']),('de_de',['Einfache Geschützturm','Verstärkter Geschützturm','Fortschrittlicher Geschützturm'])]:
   n='assets/tacz_turrets/lang/'+loc+'.json';d=json.loads(entries.get(n,b'{}'))
   for index,part in enumerate(['turret','turret_reinforced','turret_advanced']):d['item.tacz_turrets.'+part]=labels[index]
   js(n,d)
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None;changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith('net/minecraft/')for n in added)
 reports[kind]={'input_sha256':sha(base),'output':out.name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False}
Path('validation.json').write_text(json.dumps(reports,indent=2)+'\n')
