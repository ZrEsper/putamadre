from pathlib import Path
import subprocess,os,hashlib,zipfile,json
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a,**kw):return subprocess.run(list(map(str,a)),check=True,**kw)
source=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.40-simple-effective-loot.jar'
assert sha(source)=='9148040f4463b37b07e22d76d9a8402f1c9298e9349019acf5f664fb08b0b250'
for n,h in {'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}.items():assert sha(t/(n+'.jar'))==h
for d in ['build/classes','build/stubs','build/tool','build/tests','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
run(*compiler,'-d','build/stubs',*Path('tests/net').rglob('*.java'))
run(*compiler,'-cp',str(Path('build/stubs'))+os.pathsep+str(source),'-d','build/classes',*Path('src').rglob('*.java'))
cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*compiler,'-cp',cp,'-d','build/tool','Patch.java')
out=Path('output/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.41-wall-path-breach.jar')
run('java','-cp',cp+os.pathsep+'build/tool','Patch',source,out,'build/classes')
with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None and set(b.namelist())-set(a.namelist())=={"dev/zomboid/hordes/ScreamerStats.class"}
 changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));assert changed==['dev/zomboid/hordes/BreachAI.class','dev/zomboid/hordes/SiegeZombie.class','dev/zomboid/hordes/ZombieIndividual.class']
run(*compiler,'-cp','build/stubs'+os.pathsep+str(out),'-d','build/tests','tests/Checks.java')
result=run('java','-cp',os.pathsep.join(['build/tests','build/stubs',str(out)]),'Checks',capture_output=True,text=True);print(result.stdout)
Path('validation.json').write_text(json.dumps({'input_sha256':sha(source),'output_sha256':sha(out),'changed':changed,'checks':result.stdout.strip(),'minecraft_runtime_tested':False},indent=2)+'\n')
