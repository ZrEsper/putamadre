from pathlib import Path
import hashlib,json,subprocess,os,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
pins=json.loads((root/'spas-reload18/inputs.json').read_text())['tools']
for name,h in pins.items():assert sha(t/(name+'.jar'))==h
src=next(Path('inputs').glob('*.jar'));assert sha(src)==json.loads(Path('input.json').read_text())['sha256']
ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
for p in ['build/tool','build/classes','output']:Path(p).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',cp,'-d','build/tool','Patch.java');run(*ecj,'-cp',src,'-d','build/classes',*Path('src').rglob('*.java'))
out=Path('output/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.15-no-claim-spawns.jar');run('java','-cp',cp+os.pathsep+'build/tool','Patch',src,out,'build/classes')
with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None and set(a.namelist())<=set(b.namelist());assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml')
 changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert changed==['dev/zomboid/clans/Pve.class','dev/zomboid/clans/SurfaceSpawns.class','me/danialisntcool/factionfriction/event/protection/ProtectionEventHandler.class'];assert added==['dev/zomboid/clans/ConversionEvents15.class','dev/zomboid/clans/SpawnClaims15.class']
Path('validation.json').write_text(json.dumps({'input_sha256':sha(src),'output':out.name,'output_sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
