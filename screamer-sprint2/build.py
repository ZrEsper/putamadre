from pathlib import Path
import subprocess,os,hashlib,zipfile,json
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a,**kw):return subprocess.run(list(map(str,a)),check=True,**kw)
source=root/'mods/mutants-l4d2-addon-1.4.0-neoforge-1.21.1-screamer-horror.1.jar'
assert sha(source)=='c5d20931e2cb975fa30c89b1e6f8d5da55ea847cc693d0cd588247fd2d45bcba'
for n,h in {'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f','asm-analysis':'85b29371884ba31bb76edf22323c2c24e172c3267a67152eba3d1ccc2e041ef2'}.items():assert sha(t/(n+'.jar'))==h
for d in ['build/classes','build/stubs','build/tool','build/tests','output']:Path(d).mkdir(parents=True,exist_ok=True)
compiler=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
run(*compiler,'-cp',str(source),'-d','build/classes',*Path('src').rglob('*.java'))
cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run(*compiler,'-cp',cp,'-d','build/tool','Patch.java')
out=Path('output/mutants-l4d2-addon-1.4.0-neoforge-1.21.1-screamer-sprint.2.jar')
run('java','-cp',cp+os.pathsep+'build/tool','Patch',source,out,'build/classes')
with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None and set(b.namelist())-set(a.namelist())=={"dev/zomboid/mutants/horror/Rally.class"}
 changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));assert changed==['dev/zomboid/mutants/Screamers$Screamer.class','dev/zomboid/mutants/Screamers.class']
run(*compiler,'-cp',out,'-d','build/tests',*Path('tests').rglob('*.java'))
hordes=root/'wall-breach41/output/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.41-wall-path-breach.jar'
results=[]
for check in ['checks.HorrorChecks','checks.SprintChecks']:
 result=run('java','-cp',os.pathsep.join(['build/tests',str(out),str(hordes)]),check,capture_output=True,text=True);print(result.stdout);results.append(result.stdout.strip())
Path('validation.json').write_text(json.dumps({'input_sha256':sha(source),'output_sha256':sha(out),'changed':changed,'checks':results,'minecraft_runtime_tested':False},indent=2)+'\n')
