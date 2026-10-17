from pathlib import Path
import subprocess,json,hashlib,zipfile
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
def run(*a):subprocess.run(list(map(str,a)),check=True)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
for name,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(name+'.jar'))==h
for name,h in json.loads((r/'inputs.json').read_text()).items():assert sha(r/'base'/name)==h
for d in ['build/api','build/patch','build/melee','build/gore','build/hordes','output']:(r/d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',asm,'-d',r/'build/patch',r/'Patch20.java');run(*ecj,'-d',r/'build/api',*(r/'compile-api').rglob('*.java'))
outputs={}
for part,src,out,cp in [('melee','src','zomboid-global-melee-animations-2.3.1-v20-impact-search-stomp.jar',str(r/'base/melee19.jar')+':'+str(r/'build/api')),('gore','gore-src','zomboid-zombie-gore-1.0.10-neoforge-1.21.1-directional-momentum.jar',str(r/'base/gore9.jar')),('hordes','hordes-src','zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.49-global-zombie-budget.jar',str(r/'base/hordes48.jar'))]:
 run(*ecj,'-cp',cp,'-d',r/'build'/part,*(r/src).rglob('*.java'))
 base=r/'base'/({'melee':'melee19.jar','gore':'gore9.jar','hordes':'hordes48.jar'}[part]);p=r/'output'/out
 run('java','-cp',str(r/'build/patch')+':'+asm,'Patch20',base,p,r/'build'/part)
 with zipfile.ZipFile(p)as z:entries={n:z.read(n)for n in z.namelist()}
 if part=='melee':
  for lang,message in {'es_es':'Sin resistencia para golpear. Descansa un momento.','en_us':'Too exhausted to strike. Rest for a moment.','de_de':'Zu erschoepft zum Zuschlagen. Ruhe dich kurz aus.'}.items():
   name='assets/zomboid_global_melee/lang/'+lang+'.json';old=json.loads(entries.get(name,b'{}'));old['zomboid_global_melee.exhausted']=message;entries[name]=json.dumps(old,ensure_ascii=False).encode()
 if part=='gore':entries['META-INF/neoforge.mods.toml']=entries['META-INF/neoforge.mods.toml'].replace(b'version="1.0.9"',b'version="1.0.10"')
 with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
  for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
 with zipfile.ZipFile(p)as z:
  assert z.testzip()is None
  assert not any(n.startswith(('net/minecraft/','com/mojang/','org/joml/'))for n in z.namelist())
 with zipfile.ZipFile(base)as a,zipfile.ZipFile(p)as b:changed=[n for n in a.namelist()if a.read(n)!=b.read(n)]
 outputs[part]={'file':out,'sha256':sha(p),'changed':changed}
(r/'validation.json').write_text(json.dumps({'outputs':outputs,'minecraft_runtime_tested':False},indent=2)+'\n')
print('THREE_PATCHED_JARS_COMPILED_AND_BYTECODE_VERIFIED')
