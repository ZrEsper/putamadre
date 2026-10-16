from pathlib import Path
import subprocess,hashlib,json,zipfile,os
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
src=root/'mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.30-medical-input-defib.jar';assert sha(src)==json.loads(Path('input.json').read_text())['sha256']
for p in ['build/tool','build/classes','output']:Path(p).mkdir(parents=True,exist_ok=True)
ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);run(*ecj,'-cp',cp,'-d','build/tool','Patch.java');run(*ecj,'-cp',src,'-d','build/classes',*Path('src').rglob('*.java'));out=Path('output/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.31-room-audio.jar');run('java','-cp',cp+':build/tool','Patch',src,out,'build/classes')
with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
config=json.loads(entries['dungeons_iso.mixins.json']);entry='compat.sound_physics_remastered.PlayerAcoustics31Mixin';assert entry not in config['client'];config['client'].append(entry);entries['dungeons_iso.mixins.json']=json.dumps(config,indent=2).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None and a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml');changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));assert changed==['dev/zomboid/isotacz/PlayerAudio29.class','dungeons_iso.mixins.json'];added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith('net/minecraft/')for n in added)
Path('validation.json').write_text(json.dumps({'input_sha256':sha(src),'output':out.name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
