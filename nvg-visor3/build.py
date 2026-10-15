from pathlib import Path
import os,json,hashlib,zipfile,subprocess
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
base=root/'mods/fracturepoint-1.21.1-3.0.0-PT2-nvg-linked-visor.2.jar';assert sha(base)==json.loads(Path('inputs.json').read_text())['base']
pins=json.loads((root/'spas-reload18/inputs.json').read_text())['tools']
for n,h in pins.items():assert sha(t/(n+'.jar'))==h
for d in ['build/classes','build/tool','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*compiler,'-cp',base,'-d','build/classes',*Path('src').rglob('*.java'));run(*compiler,'-cp',cp,'-d','build/tool','Patch.java')
with zipfile.ZipFile(base)as z:entries={n:z.read(n)for n in z.namelist()}
for index,n in enumerate(['com/raiiiden/warborn/common/item/WBArmorItem.class','com/raiiiden/warborn/mixin/client/ShaderProtectionMixin.class']):
 p=Path('build')/str(index);p.write_bytes(entries[n]);run('java','-cp',cp+os.pathsep+'build/tool','Patch',p,str(p)+'.out');entries[n]=Path(str(p)+'.out').read_bytes()
for p in Path('build/classes').rglob('*.class'):entries[p.relative_to('build/classes').as_posix()]=p.read_bytes()
out=Path('output/fracturepoint-1.21.1-3.0.0-PT2-nvg-manual-visor.3.jar')
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
 changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));assert len(changed)==4;assert set(a.namelist())==set(b.namelist());assert b.testzip()is None
Path('validation.json').write_text(json.dumps({'input_sha256':sha(base),'output':out.name,'output_sha256':sha(out),'changed':changed,'minecraft_runtime_tested':False},indent=2)+'\n')
