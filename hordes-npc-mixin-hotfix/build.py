from pathlib import Path
import hashlib,subprocess,os,json,zipfile
R=Path(__file__).resolve().parent;os.chdir(R);root=R.parent;tools=root/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*args,**kw):return subprocess.run(list(map(str,args)),check=True,**kw)
checks={'ecj':'01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948','asm':'8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281','asm-tree':'9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f'}
for k,v in checks.items():assert sha(tools/(k+'.jar'))==v
source=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.33-scarce-loot-bandit-progression.jar'
expected=json.loads((root/'loot-bandit-balance-patch/validation.json').read_text())['mods'][0]['sha256'];assert sha(source)==expected
out=root/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.34-npc-mixin-fix.jar'
Path('build').mkdir(exist_ok=True);cp=os.pathsep.join(str(tools/(n+'.jar'))for n in ['asm','asm-tree'])
run('java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn','-cp',cp,'-d','build','Fix.java')
cmd=['java','-cp',cp+os.pathsep+'build','Fix']
run(*cmd,'patch',source,out)
npcs=[root/'mods/tacznpcs-2.1.0-1.21.1-tactical.16-unarmed-scavenger-progression.jar',root/'mods/tacznpcs-2.1.0-1.21.1-tactical.15-playerlike-test-npc.jar']
negative=subprocess.run([*cmd,'check',str(source),str(npcs[0])],capture_output=True,text=True)
assert negative.returncode!=0 and 'balance$military: 0/1 injection sites' in negative.stderr
passed=[]
for p in npcs:
 result=run(*cmd,'check',out,p,capture_output=True,text=True);assert 'COMPATIBILITY_CHECKS=3' in result.stdout;passed.append({'npc':p.name,'sha256':sha(p),'injections_verified':3})
with zipfile.ZipFile(source)as a,zipfile.ZipFile(out)as b:
 assert set(a.namelist())==set(b.namelist());assert b.testzip()is None
 changed=[n for n in a.namelist()if a.read(n)!=b.read(n)]
 assert changed==['dev/zomboid/hordes/mixin/MilitaryBanditBalanceMixin.class']
report={'input_sha256':sha(source),'output':out.name,'output_sha256':sha(out),'changed_entries':changed,'original_failure_reproduced':True,'compatibility':passed,'minecraft_runtime_tested':False,'scope':'ASM annotation selectors, instruction targets and unchanged executable methods. Not a full Mixin transformer or Minecraft run.'}
Path('validation.json').write_text(json.dumps(report,indent=2)+'\n');print('Original failure reproduced; 3 injections verified against each of NPC .15 and .16; only military selector annotation changed.')
