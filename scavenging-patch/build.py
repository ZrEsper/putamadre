from pathlib import Path
import os,sys,json,subprocess,tempfile,shutil,zipfile,hashlib
r=Path(__file__).resolve().parent;repo=r.parent;tools=Path(sys.argv[1]);inputs=json.loads((r/'inputs.json').read_text())
asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
def run(*a):subprocess.run([str(x) for x in a],check=True)
with tempfile.TemporaryDirectory(prefix='scavenging-') as root:
 root=Path(root);stubs=root/'stubs';classes=root/'classes';patcher=root/'patcher'
 run(sys.executable,'-B',repo/'generator-patch/tests/make_api_doubles.py',stubs);run(sys.executable,r/'extra_stubs.py',stubs)
 run('java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn','-d',classes,*sorted(stubs.rglob('*.java')),*sorted((r/'src').rglob('*.java')),*sorted((r/'water-src').rglob('*.java')),*sorted((r/'skill-src').rglob('*.java')),*sorted((r/'jade-src').rglob('*.java')))
 run('java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn','-cp',asm,'-d',patcher,r/'Patch.java')
 results=[]
 for mode,item in inputs.items():
  base=Path(item['path']);assert hashlib.sha256(base.read_bytes()).hexdigest()==item['sha256'],base
  added=root/mode;added.mkdir()
  if mode=='quietly':shutil.copytree(classes/'dev/zomboid/scavenging',added/'dev/zomboid/scavenging')
  if mode=='jade':shutil.copytree(classes/'dev/zomboid/scavengingjade',added/'dev/zomboid/scavengingjade')
  if mode=='survivor':
   path=added/'dev/survivorcreator/skills/ScavengingEffect.class';path.parent.mkdir(parents=True);shutil.copyfile(classes/'dev/survivorcreator/skills/ScavengingEffect.class',path)
  if mode=='survival':
   path=added/'dev/zomboid/survival';path.mkdir(parents=True)
   for name in ['WaterItems','WaterSources','WaterReflect']:shutil.copyfile(classes/f'dev/zomboid/survival/{name}.class',path/f'{name}.class')
   shutil.copytree(r/'resources',added,dirs_exist_ok=True)
  with zipfile.ZipFile(base) as z:
   for name in ['assets/survivorcreator/lang/en_us.json','assets/survivorcreator/lang/es_es.json'] if mode=='survivor' else ['assets/zomboid_survival/lang/en_us.json','assets/zomboid_survival/lang/es_es.json'] if mode=='survival' else []:
    lang=json.loads(z.read(name));es='/es_es.' in name
    if mode=='survivor':lang['skill.survivorcreator.scavenging']='Scavenging / Saqueo' if es else 'Scavenging'
    else:
     for key,en,spanish in [('water_bottle','Clean drinking water','Agua potable'),('purified_water','Purified water','Agua purificada'),('mineral_water','Mineral water','Agua mineral'),('contaminated_water','Contaminated water (boil first)','Agua contaminada (hervir)')]:lang['item.zomboid_survival.'+key]=spanish if es else en
    path=added/name;path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(lang,ensure_ascii=False,indent=2)+'\n')
   if mode=='survival':
    for name in z.namelist():
     if name.startswith('data/zomboid_survival/recipe/filter_water_') and name.endswith('.json'):
      recipe=json.loads(z.read(name));recipe['result']['id']='zomboid_survival:purified_water';path=added/name;path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(recipe,indent=2)+'\n')
  dest=repo/'mods'/item['output'];run('java','-cp',str(patcher)+os.pathsep+asm,'Patch',mode,base,dest,added)
  with zipfile.ZipFile(base) as a,zipfile.ZipFile(dest) as b:
   assert b.testzip() is None
   changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];new=[n for n in b.namelist() if n not in a.namelist()]
   assert not any(n.startswith(('net/','org/')) for n in new)
   assert set(n for n in changed if n.endswith('.class'))=={'quietly':{'cn/kafei/QuietlyCommon.class','cn/kafei/SilentOpenManager.class','cn/kafei/neoforge/QuietlyNeoForge.class','cn/kafei/interact/Interaction.class'},'survivor':{'dev/survivorcreator/skills/Skills.class','dev/survivorcreator/skills/ActiveSkills.class'},'survival':{'dev/zomboid/survival/WaterItems.class','dev/zomboid/survival/ClientColors.class'},'jade':{'snownee/jade/addon/universal/ItemStorageProvider.class'}}[mode],changed
   # Original mixin configs and every original mixin are retained byte-for-byte.
   for name in a.namelist():
    if '/mixin/' in name or name.endswith('mixins.json'):assert a.read(name)==b.read(name),name
   results.append({'file':dest.name,'sha256':hashlib.sha256(dest.read_bytes()).hexdigest(),'changed':changed,'added':new})
 run('java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn','-cp',classes,'-d',classes,*sorted((r/'tests').rglob('*.java')))
 run('java','-Xverify:all','-cp',classes,'dev.zomboid.scavenging.Checks')
 run('java','-Xverify:all','-cp',classes,'dev.zomboid.scavenging.BehaviorChecks')
 (r/'validation.json').write_text(json.dumps({'artifacts':results,'minecraft_runtime_tested':False,'rule_cases':21,'simulated_behavior_cases':32,'all_new_classes_asm_verified':True,'original_mixins_unchanged':True},indent=2)+'\n')
