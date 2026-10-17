from pathlib import Path
import os,subprocess,json,hashlib,zipfile,re,shutil
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
Path('base').mkdir(exist_ok=True)
for kind,source in [('hordes','zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.46-corpse-access-fix.jar'),('quiet','quietly-1_0_0-cooldown-v17-final-loot-tools-scatter.jar')]:
 if not (Path('base')/(kind+'.jar')).exists():shutil.copyfile(root/'mods'/source,Path('base')/(kind+'.jar'))
gson=root/'npc-social17/build/tools/gson-2.10.1.jar'
if not gson.exists():
 gson.parent.mkdir(parents=True,exist_ok=True);run('curl','--fail','--location','--silent','--show-error','https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar','-o',gson)
assert sha(gson)==json.loads((root/'npc-social17/test-dependencies.json').read_text())['gson_sha256']
for n,h in json.loads(Path('inputs.json').read_text()).items():assert sha(Path('base')/n)==h
Path('build/tool').mkdir(parents=True,exist_ok=True);run(*ecj,'-cp',asm,'-d','build/tool','Patch.java');reports={}
for kind,name in [('hordes','zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.47-environment-heat.jar'),('quiet','quietly-1_0_0-cooldown-v18-environment-loot.jar')]:
 out=Path('output')/name;classes=Path('build')/kind;classes.mkdir(exist_ok=True,parents=True);base=Path('base')/(kind+'.jar');cp=str(base)+':'+str(root/'npc-social17/build/tools/gson-2.10.1.jar');run(*ecj,'-cp',cp,'-d',classes,*Path('src',kind).rglob('*.java'));run('java','-cp','build/tool:'+asm,'Patch',base,out,classes,kind)
 with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
 if kind=='hordes':
  for loc,text in [('es_es','Ruido: %s%% · Zombis: %s/%s'),('es_mx','Ruido: %s%% · Zombis: %s/%s'),('en_us','Heat: %s%% · Zombies: %s/%s'),('de_de','Lärm: %s%% · Zombies: %s/%s')]:
   path='assets/zomboid_hordes/lang/'+loc+'.json';d=json.loads(entries.get(path,b'{}'));d['ambiente47.heat']=text;entries[path]=json.dumps(d,ensure_ascii=False,separators=(',',':')).encode()
 # Keep upstream mod versions compatible with dependent version ranges; filenames/protocol identify patches.
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(entries.items()):info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
 with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:assert b.testzip()is None;changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert not any(n.startswith('net/minecraft/')for n in added)
 reports[kind]={'output':name,'input_sha256':sha(base),'sha256':sha(out),'changed':changed,'added':added}
Path('validation.json').write_text(json.dumps(reports,indent=2)+'\n')
