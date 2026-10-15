#!/usr/bin/env python3
import pathlib,subprocess,os,json,hashlib,zipfile,shutil
from catalog import generate
from skin_catalog import generate as generate_skins
R=pathlib.Path(__file__).resolve().parent;os.chdir(R);generate(R);generate_skins(R,next((R/'base').glob('TZP*')))
def run(*a):subprocess.run(list(map(str,a)),check=True)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
T=R.parent/'hordes-melee-interiors-patch/build/tools'
CHECKS={'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}
for n,h in CHECKS.items():assert sha(T/(n+'.jar'))==h
inputs=json.loads((R/'inputs.sha256.json').read_text());base=list((R/'base').glob('*.jar'))
for name,digest in inputs.items():assert sha(R/'base'/name)==digest
compiler=['java','-jar',T/'ecj.jar','-21','-proc:none','-nowarn'];asm=os.pathsep.join(str(T/(n+'.jar')) for n in ['asm','asm-tree','asm-analysis']);cp=os.pathsep.join(map(str,base))
run(*compiler,'-cp',cp,'-d','build/classes',*pathlib.Path('src').rglob('*.java'));run(*compiler,'-cp',asm,'-d','build/tool','Patch.java')
targets=[('hordes','zomboid-hordes','zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.32-single-tool-corpses.jar'),('remains','zombie-remains','zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.9-profession-corpses.jar'),('firstaid','firstaid','firstaid-1.21.1-ZOMBOID-v3.5.7-zombie-cure-level-heal.jar'),('survivor','survivorcreator','survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v33-level-heal-toggle.jar')]
report={'minecraft_runtime_tested':False,'mods':[]}
for kind,prefix,name in targets:
 source=next(p for p in base if p.name.startswith(prefix));out=R/'output'/name;run('java','-cp',asm+os.pathsep+'build/tool','Patch',kind,source,out,'build/classes')
 if kind=='hordes':
  with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
  for p in (R/'resources').rglob('*'):
   if p.is_file():entries[p.relative_to(R/'resources').as_posix()]=p.read_bytes()
  with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
   for n,b in sorted(entries.items()):
    info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 if kind=='firstaid':
  with zipfile.ZipFile(out)as z:entries={n:z.read(n) for n in z.namelist()}
  lang={
   'es_es':('Inyección contra la infección zombi','Mantén clic derecho 1,6 segundos. Cura la infección zombi; no cura heridas ni evita futuras mordeduras. Un solo uso.','Infección zombi eliminada.','Nivel nuevo: lesiones curadas, salud restaurada y efectos eliminados.'),
   'en_us':('Zombie infection cure injector','Hold right click for 1.6 seconds. Cures zombie infection; does not heal wounds or prevent future bites. Single use.','Zombie infection removed.','New level: injuries healed, health restored and effects cleared.'),
   'de_de':('Heilinjektion gegen Zombieinfektion','Rechtsklick 1,6 Sekunden halten. Heilt Zombieinfektion, keine Wunden oder zukünftigen Bisse. Einmalgebrauch.','Zombieinfektion entfernt.','Neues Level: Verletzungen geheilt, Gesundheit und Effekte zurückgesetzt.')}
  for locale,(title,desc,used,heal)in lang.items():
   n=f'assets/firstaid/lang/{locale}.json';d=json.loads(entries.get(n,b'{}'));d.update({'item.firstaid.zombie_cure_injector':title,'item.firstaid.zombie_cure_injector.desc':desc,'item.firstaid.zombie_cure_injector.used':used,'firstaid.apocalypse.level_heal':heal});entries[n]=json.dumps(d,ensure_ascii=False,indent=2).encode()
  entries['assets/firstaid/models/item/zombie_cure_injector.json']=json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':'firstaid:item/zombie_cure_injector'}}).encode()
  # Preserve an existing injector shape and make a separate distinctive green texture in the artifact stage.
  from PIL import Image
  import io
  texture=next(n for n in entries if n.endswith('adrenaline_injector.png'))
  im=Image.open(io.BytesIO(entries[texture])).convert('RGBA');pixels=im.load()
  for y in range(im.height):
   for x in range(im.width):
    r,g,b,a=pixels[x,y]
    if a and r>g*1.15 and r>b*1.15:pixels[x,y]=(int(r*.35),min(255,int(r*.95)),int(b*.7),a)
  buf=io.BytesIO();im.save(buf,format='PNG');entries['assets/firstaid/textures/item/zombie_cure_injector.png']=buf.getvalue()
  entries['META-INF/neoforge.mods.toml']=entries['META-INF/neoforge.mods.toml'].replace(b'3.5.6',b'3.5.7')
  with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
   for n,b in sorted(entries.items()):
    info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None;assert set(a.namelist())<=set(b.namelist());assert len(b.namelist())==len(set(b.namelist()))
  changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith(('net/minecraft/','net/neoforged/','org/objectweb/'))for n in added)
  expected=json.loads((R/'expected-changes.json').read_text())[name];assert changed==expected['changed'];assert added==expected['added']
  report['mods'].append({'file':name,'input_sha256':sha(source),'sha256':sha(out),'changed':changed,'added':added})
cp=os.pathsep.join(str(R/'output'/name)for _,_,name in targets);run(*compiler,'-cp',cp,'-d','build/tests',*pathlib.Path('tests').rglob('*.java'))
test=subprocess.run(['java','-Xverify:all','-cp','build/tests'+os.pathsep+cp,'dev.zomboid.hordes.Checks31'],check=True,capture_output=True,text=True);print(test.stdout);print(test.stderr)
report['behavior_checks_with_API_doubles']=int(test.stdout.split('CHECKS31=')[1].strip());report['methods_verified']='ASM BasicVerifier for every changed original method'
(R/'validation.json').write_text(json.dumps(report,indent=2)+'\n');print('Build completed; inputs, tool hashes, CRC and resource preservation verified.')
