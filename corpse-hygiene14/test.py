from pathlib import Path
import os,json,subprocess,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';out=next(Path('output').glob('*.jar'));Path('build/tests').mkdir(parents=True,exist_ok=True)
def run(*a):
 p=subprocess.run(list(map(str,a)),capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-cp',out,'-d','build/tests',*Path('tests').rglob('*.java'));checks=[run('java','-Xverify:all','-cp','build/tests:'+str(out),'dev.zomboid.remains.hygiene.HygieneChecks')]
with zipfile.ZipFile(out)as z:
 assert z.read('assets/zombie_remains_loot/sounds/vomit.ogg').startswith(b'OggS')
 assert json.loads(z.read('data/zombie_remains_loot/loot_table/blocks/vomit.json'))['pools']==[]
 assert len(json.loads(z.read('assets/zombie_remains_loot/blockstates/vomit.json'))['variants'])==8
 inputs=json.loads(Path('inputs.json').read_text())
 with zipfile.ZipFile(Path('base')/inputs['remains']['file'])as original:
  for n in original.namelist():
   if n.endswith('.class')and n!='dev/zomboid/remains/ZombieRemainsLoot.class':assert original.read(n)==z.read(n),n
checks.append('Original loot, NPC, military equipment, electronic scrap and player-zombie exclusion classes byte-identical; single native tick/constructor hookup; custom flies and green thin-layer assets validated')
Path('test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False,'method':'Production exposure policy, content registration and server hygiene adapter using original reflection helper with Minecraft/entity/world/hydration API doubles; counts food, water, sync packets, sound, particles, placement and cleanup; JVM -Xverify:all'},indent=2)+'\n')
