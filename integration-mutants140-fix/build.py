from pathlib import Path
import hashlib,subprocess,os,zipfile,tomllib,json
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
for n,h in {'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f'}.items():assert sha(t/(n+'.jar'))==h
for n,h in json.loads(Path('inputs.sha256.json').read_text()).items():assert sha(Path('base')/n)==h
source=next(Path('base').glob('zomboid*.jar'));addon=next(Path('base').glob('mutants*.jar'));out=root/'mods/zomboid-integration-1.7.8-neoforge-1.21.1-mutants-1.4-compat.jar'
cp=os.pathsep.join(str(t/(n+'.jar'))for n in ['asm','asm-tree']);Path('build').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d','build','Check.java'],check=True)
result=subprocess.run(['java','-cp',cp+os.pathsep+'build','Check',str(source),str(addon)],check=True,capture_output=True,text=True);print(result.stdout)
path='META-INF/neoforge.mods.toml'
with zipfile.ZipFile(source)as z:entries={n:z.read(n)for n in z.namelist()}
before=tomllib.loads(entries[path].decode());old=entries[path].decode();assert old.count('versionRange="[1.3.0,1.4)"')==1
entries[path]=old.replace('version="1.7.7"','version="1.7.8"').replace('versionRange="[1.3.0,1.4)"','versionRange="[1.3.0,1.4.0]"').encode();after=tomllib.loads(entries[path].decode())
expected=tomllib.loads(old);expected['mods'][0]['version']='1.7.8'
for d in expected['dependencies']['zomboid_integration']:
 if d['modId']=='mutants_l4d2':d['versionRange']='[1.3.0,1.4.0]'
assert expected==after
# Boundary regression: original excluded 1.4.0; the fix includes that exact release, not future releases.
def allowed(v,new):return (1,3,0)<=v<= (1,4,0) if new else (1,3,0)<=v<(1,4,0)
assert not allowed((1,4,0),False) and allowed((1,4,0),True)
for version,ok in [((1,2,9),False),((1,3,0),True),((1,3,9),True),((1,4,0),True),((1,4,1),False),((1,5,0),False)]:assert allowed(version,True)==ok
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
 assert a.namelist()and set(a.namelist())==set(b.namelist());assert b.testzip()is None;assert [n for n in a.namelist()if a.read(n)!=b.read(n)]==[path]
report={'input_sha256':sha(source),'addon_sha256':sha(addon),'output':out.name,'output_sha256':sha(out),'changed_entries':[path],'bytecode_unchanged':True,'api_and_mixin_structure_checks':result.stdout.splitlines(),'minecraft_runtime_tested':False}
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n');print(out.name)
