from pathlib import Path
import hashlib,json,subprocess,os,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
src=root/'mods/tacz-tactical-1.2.4-neoforge-1.21.1-impact-expiry-180s.2.jar';assert sha(src)==json.loads(Path('input.json').read_text())['sha256']
for p in ['build/tool','build/classes','output']:Path(p).mkdir(parents=True,exist_ok=True)
cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];run(*ecj,'-cp',cp,'-d','build/tool','Patch.java');run(*ecj,'-cp',src,'-d','build/classes',*Path('src').rglob('*.java'));out=Path('output/tacz-tactical-1.2.4-neoforge-1.21.1-impact-once-180s.3.jar');run('java','-cp',cp+':build/tool','Patch',src,out,'build/classes')
with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None;changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert changed==['dev/tactical/tacz/ClientEffects.class','dev/tactical/tacz/ImpactMarks.class']and added==['dev/tactical/tacz/OnceEffects.class'];assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml')
Path('validation.json').write_text(json.dumps({'input_sha256':sha(src),'output':out.name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
