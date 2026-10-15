from pathlib import Path
import os,subprocess,hashlib,json,zipfile,tomllib
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a,**kw):return subprocess.run(list(map(str,a)),check=True,**kw)
for n,h in {'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}.items():assert sha(t/(n+'.jar'))==h
for name,h in json.loads(Path('inputs.sha256.json').read_text()).items():assert sha(Path('base')/name)==h
for d in ['build/classes','build/tool','build/tests','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn'];cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*compiler,'-d','build/classes',*Path('src').rglob('*.java'));run(*compiler,'-cp',cp,'-d','build/tool','Patch.java')
source=next(Path('base').glob('mutants*.jar'));out=Path('output/mutants-l4d2-addon-1.4.0-neoforge-1.21.1-screamer-horror.1.jar')
run('java','-cp',cp+os.pathsep+'build/tool','Patch',source,out,'build/classes')
run('ffmpeg','-hide_banner','-loglevel','error','-y','-i','base/screamer-original.ogg','-ac','1','-c:a','libvorbis','-q:a','5','build/screamer_detect.ogg')
probe=json.loads(run('ffprobe','-v','error','-show_entries','format=duration:stream=channels,codec_name','-of','json','build/screamer_detect.ogg',capture_output=True,text=True).stdout);assert probe['streams'][0]['channels']==1 and probe['streams'][0]['codec_name']=='vorbis';assert 10<float(probe['format']['duration'])<10.2
with zipfile.ZipFile(out)as z:entries={n:z.read(n)for n in z.namelist()}
prefix='assets/mutants_l4d2/'
entries[prefix+'sounds/screamer_detect.ogg']=Path('build/screamer_detect.ogg').read_bytes()
sounds=json.loads(entries.get(prefix+'sounds.json',b'{}'));sounds['screamer_detect']={'subtitle':'subtitles.mutants_l4d2.screamer_detect','sounds':[{'name':'mutants_l4d2:screamer_detect','stream':True}]};entries[prefix+'sounds.json']=json.dumps(sounds,ensure_ascii=False,indent=2).encode()
entries[prefix+'models/item/screamer_spawn_egg.json']=b'{"parent":"minecraft:item/template_spawn_egg"}\n'
for locale,egg,cry in [('es_es','Huevo de aparición de Screamer','El Screamer llama a la horda'),('en_us','Screamer Spawn Egg','Screamer calls the horde'),('de_de','Screamer-Spawn-Ei','Screamer ruft die Horde')]:
 name=prefix+f'lang/{locale}.json';lang=json.loads(entries.get(name,b'{}'));lang.update({'item.mutants_l4d2.screamer_spawn_egg':egg,'subtitles.mutants_l4d2.screamer_detect':cry});entries[name]=json.dumps(lang,ensure_ascii=False,indent=2).encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
 assert set(a.namelist())<=set(b.namelist());assert b.testzip()is None
 changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()))
 assert sorted(n for n in changed if n.endswith('.class'))==['dev/zomboid/mutants/MutantAddon.class','dev/zomboid/mutants/Screamers$Screamer.class','dev/zomboid/mutants/Screamers.class','dev/zomboid/mutants/client/ScreamerClient$Model.class']
 assert all(n.endswith('.class')or n.startswith(prefix)for n in changed+added)
 for n in a.namelist():
  if n.startswith(prefix+'lang/')and n.endswith('.json'):assert all(json.loads(b.read(n)).get(k)==v for k,v in json.loads(a.read(n)).items())
 assert a.read('META-INF/neoforge.mods.toml')==b.read('META-INF/neoforge.mods.toml')
report={'input_sha256':sha(source),'original_audio_sha256':sha(Path('base/screamer-original.ogg')),'output':out.name,'output_sha256':sha(out),'changed':changed,'added':added,'audio':probe,'minecraft_runtime_tested':False}
if Path('expected-changes.json').exists():assert json.loads(Path('expected-changes.json').read_text())=={'changed':changed,'added':added}
if Path('tests').exists():
 run(*compiler,'-cp',out,'-d','build/tests',*Path('tests').rglob('*.java'))
 result=run('java','-cp',str(Path('build/tests').resolve())+os.pathsep+str(out.resolve()),'checks.HorrorChecks',capture_output=True,text=True);print(result.stdout);report['behavior_checks']=result.stdout.strip()
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n')
