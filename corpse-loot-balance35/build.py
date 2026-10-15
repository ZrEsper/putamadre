from pathlib import Path
import subprocess,os,json,hashlib,zipfile
R=Path(__file__).resolve().parent;os.chdir(R);root=R.parent;T=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a,**kw):return subprocess.run(list(map(str,a)),check=True,**kw)
checks={'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}
for n,d in checks.items():assert sha(T/(n+'.jar'))==d
horde=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.34-npc-mixin-fix.jar';remains=root/'mods/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.10-scarce-tiers-earned-npc-cargo.jar'
assert sha(horde)==json.loads((root/'hordes-npc-mixin-hotfix/validation.json').read_text())['output_sha256']
assert sha(remains)==json.loads((root/'loot-bandit-balance-patch/validation.json').read_text())['mods'][1]['sha256']
for d in ['build/classes','build/tool','build/tests','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',T/'ecj.jar','-21','-proc:none','-nowarn'];asm=os.pathsep.join(str(T/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);cp=os.pathsep.join(map(str,[horde,remains]))
run(*compiler,'-cp',cp,'-d','build/classes',*Path('src').rglob('*.java'));run(*compiler,'-cp',asm,'-d','build/tool','Patch.java')
targets=[('hordes',horde,'zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.35-corpse-equipment-balanced.jar'),('remains',remains,'zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.11-owned-equipment-shuffle.jar')];report={'minecraft_runtime_tested':False,'scope':'Real helper pipeline against API doubles; bytecode verification; military mixin preserved.','mods':[]}
for kind,source,name in targets:
 out=Path('output')/name;run('java','-cp',asm+os.pathsep+'build/tool','Patch',kind,source,out,'build/classes')
 if kind=='hordes':
  with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
  for locale,label in [('es_es','Venda militar'),('en_us','Military bandage'),('de_de','Militärverband')]:
   n=f'assets/zomboid_hordes/lang/{locale}.json';lang=json.loads(entries.get(n,b'{}'));lang['item.zomboid_hordes.military_bandage']=label;entries[n]=json.dumps(lang,ensure_ascii=False,indent=2).encode()
  with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
   for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
  assert b.testzip()is None;assert set(a.namelist())<=set(b.namelist());changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));
  expected=['dev/zomboid/hordes/ScarcityControls.class','dev/zomboid/hordes/ScarcityPanel.class','dev/zomboid/hordes/WorldTuning.class'] if kind=='hordes' else ['dev/zomboid/remains/ZombieRemainsLoot.class','dev/zomboid/remains/delivery/NpcCorpses.class']
  assert sorted(n for n in changed if n.endswith('.class'))==expected
  assert all(n.endswith('.class')or n.startswith('assets/zomboid_hordes/lang/')for n in changed+added)
  if kind=='hordes':
   for locale in ['es_es','en_us']:
    path=f'assets/zomboid_hordes/lang/{locale}.json';before=json.loads(a.read(path));after=json.loads(b.read(path));assert all(after.get(k)==v for k,v in before.items())
   assert a.read('dev/zomboid/hordes/mixin/MilitaryBanditBalanceMixin.class')==b.read('dev/zomboid/hordes/mixin/MilitaryBanditBalanceMixin.class')
  if Path('expected-changes.json').exists():assert json.loads(Path('expected-changes.json').read_text())[kind]==dict(changed=changed,added=added)
  report['mods'].append(dict(file=name,input_sha256=sha(source),sha256=sha(out),changed=changed,added=added))
if Path('tests').exists():
 testcp=os.pathsep.join(str((Path('output')/name).resolve())for _,_,name in targets)
 run(*compiler,'-cp',testcp,'-d','build/tests',*Path('tests').rglob('*.java'))
 result=run('java','-cp',str(Path('build/tests').resolve())+os.pathsep+testcp,'dev.zomboid.hordes.CorpseChecks35',capture_output=True,text=True);print(result.stdout);report['checks']=result.stdout.strip()
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n')
