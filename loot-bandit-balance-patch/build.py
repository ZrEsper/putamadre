#!/usr/bin/env python3
from pathlib import Path
import subprocess,os,json,hashlib,zipfile
R=Path(__file__).resolve().parent;os.chdir(R);T=R.parent/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
checks={'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}
for n,d in checks.items():assert sha(T/(n+'.jar'))==d
inputs=json.loads(Path('inputs.sha256.json').read_text());base=list(Path('base').glob('*.jar'))
for p in base:assert sha(p)==inputs[p.name]
for d in ['build/classes','build/tool','build/tests','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',T/'ecj.jar','-21','-proc:none','-nowarn'];asm=os.pathsep.join(str(T/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);cp=os.pathsep.join(map(str,base))
run(*compiler,'-cp',cp,'-d','build/classes',*Path('src').rglob('*.java'));run(*compiler,'-cp',asm,'-d','build/tool','Patch.java')
targets=[('hordes','zomboid-hordes','zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.33-scarce-loot-bandit-progression.jar'),('remains','zombie-remains','zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.10-scarce-tiers-earned-npc-cargo.jar'),('npc','tacznpcs','tacznpcs-2.1.0-1.21.1-tactical.16-unarmed-scavenger-progression.jar'),('firstaid','firstaid','firstaid-1.21.1-ZOMBOID-v3.5.8-defib-priority-medical-progress.jar'),('fracture','fracturepoint','fracturepoint-1.21.1-3.0.0-PT2-nvg-15min.1.jar')]
report={'minecraft_runtime_tested':False,'methods_verified':'ASM BasicVerifier','mods':[]}
for kind,prefix,name in targets:
 source=next(p for p in base if p.name.startswith(prefix));out=Path('output')/name;run('java','-cp',asm+os.pathsep+'build/tool','Patch',kind,source,out,'build/classes')
 if kind in ['firstaid','fracture']:
  with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
  translations={
   'es_es':('Reanimado. Tienes 20 segundos para vendarte.','Reanimando %s %s%% · %s s','%s %s %s%%','Aplicado: %s · 100%%','Bateria completa: 15 minutos de uso activo. Vision nocturna hasta agotarse.'),
   'en_us':('Revived. You have 20 seconds to bandage.','Reviving %s %s%% · %s s','%s %s %s%%','Applied: %s · 100%%','Full battery: 15 minutes of active use. Night vision until depleted.'),
   'de_de':('Wiederbelebt. Du hast 20 Sekunden zum Verbinden.','Wiederbelebung %s %s%% · %s s','%s %s %s%%','Angewendet: %s · 100%%','Voller Akku: 15 Minuten aktive Nutzung. Nachtsicht bis zur Entladung.')}
  for locale,values in translations.items():
   n=f"assets/{'firstaid' if kind=='firstaid' else 'fracturepoint'}/lang/{locale}.json";d=json.loads(entries.get(n,b'{}'))
   if kind=='firstaid':d.update(dict(zip(['firstaid.revive33.complete','firstaid.revive33.progress','firstaid.medicine33.progress','firstaid.medicine33.complete'],values[:4])))
   else:d['tooltip.fracturepoint.nvg33.duration']=values[4]
   entries[n]=json.dumps(d,ensure_ascii=False,indent=2).encode()
  with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
   for n,b in sorted(entries.items()):info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None;assert set(a.namelist())<=set(b.namelist());assert len(b.namelist())==len(set(b.namelist()));changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert all(n.endswith('.class')or n.startswith(('assets/firstaid/lang/','assets/fracturepoint/lang/'))for n in changed+added);assert all(n.startswith(('dev/zomboid/hordes/','dev/zomboid/remains/','mod/cdv/tnpc/tactical/','dev/zomboid/firstaid/','com/raiiiden/warborn/common/util/','assets/fracturepoint/lang/','assets/firstaid/lang/'))for n in added)
  if Path('expected-changes.json').exists():assert json.loads(Path('expected-changes.json').read_text())[kind]=={'changed':changed,'added':added}
  if kind=='hordes':
   for n in ['dev/zomboid/hordes/ZombieIndividual.class','dev/zomboid/reactive/Reactive.class','dev/zomboid/hordes/SpawnPool.class','dev/zomboid/hordes/MutantPool.class','dev/zomboid/hordes/HordeEvents.class','dev/zomboid/hordes/ChunkPopulation.class']:assert a.read(n)==b.read(n)
  report['mods'].append(dict(file=name,input_sha256=sha(source),sha256=sha(out),changed=changed,added=added))
testcp=os.pathsep.join(str((Path('output')/name).resolve())for _,_,name in targets)
run(*compiler,'-cp',testcp,'-d','build/tests',*Path('tests').rglob('*.java'))
report['behavior_checks']={}
for cls,key in [('dev.zomboid.hordes.NerfChecks','NERF_CHECKS'),('dev.zomboid.firstaid.MedicalChecks','MEDICAL_CHECKS')]:
 result=subprocess.run(['java','-cp',str(Path('build/tests').resolve())+os.pathsep+testcp,cls],check=True,capture_output=True,text=True)
 print(result.stdout.strip());report['behavior_checks'][key]=int(result.stdout.split(key+'=')[1].splitlines()[0])
report['test_scope']='Real patched helpers and selected native methods against API doubles; no Minecraft runtime.'
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n');print('Compiled five mods; zombie bytecode and original resources preserved except the specified medical/NVG language additions.')
