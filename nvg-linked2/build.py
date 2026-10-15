from pathlib import Path
import os,json,hashlib,zipfile,subprocess
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a,**kw):return subprocess.run(list(map(str,a)),check=True,**kw)
source=next(Path('base').glob('*.jar'));assert sha(source)==json.loads(Path('inputs.sha256.json').read_text())['base']
for n,h in {'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}.items():assert sha(t/(n+'.jar'))==h
for d in ['build/classes','build/tool','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);run(*compiler,'-cp',source,'-d','build/classes',*Path('src').rglob('*.java'));run(*compiler,'-cp',cp,'-d','build/tool','Patch.java');out=Path('output/fracturepoint-1.21.1-3.0.0-PT2-nvg-linked-visor.2.jar');run('java','-cp',cp+os.pathsep+'build/tool','Patch',source,out,'build/classes')
with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
for locale,label in [('es_es','Subir/bajar visor y visión nocturna'),('en_us','Raise/lower visor and night vision'),('de_de','Visier und Nachtsicht umschalten')]:
 n='assets/fracturepoint/lang/'+locale+'.json';data=json.loads(entries[n]);data['key.warborn.toggle_helmet_top']=label;entries[n]=json.dumps(data,ensure_ascii=False,indent=2).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None and set(a.namelist())<=set(b.namelist());assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml');changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()));assert all(n.endswith('.class')or n.startswith('assets/fracturepoint/lang/')for n in changed+added)
 assert [n for n in changed if n.endswith('.class')]==['com/raiiiden/warborn/client/event/ClientKeyEvents.class','com/raiiiden/warborn/client/event/ModKeybindings.class','com/raiiiden/warborn/client/network/ClientNVGBatteryEmptyHandler.class','com/raiiiden/warborn/client/shader/ShaderRegistry.class','com/raiiiden/warborn/common/event/HelmetBatteryTickHandler.class'];assert added==['com/raiiiden/warborn/client/util/NvgClient2.class','com/raiiiden/warborn/common/util/NvgLink2.class']
Path('validation.json').write_text(json.dumps({'input_sha256':sha(source),'output':out.name,'output_sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
