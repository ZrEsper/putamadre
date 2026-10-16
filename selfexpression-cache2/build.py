from pathlib import Path
import os,subprocess,hashlib,json,zipfile
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';inputs=json.loads(Path('inputs.json').read_text())
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):return subprocess.run(list(map(str,a)),check=True,text=True,capture_output=True).stdout
for item in inputs.values():assert sha(Path('base')/item['file'])==item['sha256']
for name,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(name+'.jar'))==h
src=Path('base')/inputs['selfexpression']['file'];ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
for d in ['build/tool','build/api','build/classes','output']:Path(d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-d','build/api',*Path('api').rglob('*.java'));run(*ecj,'-cp',str(src)+':build/api','-d','build/classes',*Path('src').rglob('*.java'));run(*ecj,'-cp',cp,'-d','build/tool','Patch.java')
out=Path('output/selfexpression-2.22a-neoforge-1.21.1-shared-armor-cache.2.jar');result=run('java','-cp','build/tool:'+cp,'Patch',src,out,'build/classes');print(result)
with zipfile.ZipFile(src)as old,zipfile.ZipFile(out)as new:
 assert new.testzip()is None;changed=sorted(n for n in old.namelist()if old.read(n)!=new.read(n));added=sorted(set(new.namelist())-set(old.namelist()));assert len(changed)==876 and all(n.startswith('net/mcreator/selfexpression/item/')and n.endswith('.class')for n in changed)
 assert added==['net/mcreator/selfexpression/client/SharedArmorFactories.class','net/mcreator/selfexpression/client/SharedArmorModels.class']
Path('validation.json').write_text(json.dumps({'input_sha256':sha(src),'output':out.name,'sha256':sha(out),'changed_classes':len(changed),'added':added,'native_factory_audit':result.splitlines(),'all_other_entries_byte_identical':True,'minecraft_runtime_tested':False},indent=2)+'\n')
