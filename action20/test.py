from pathlib import Path
import json,subprocess,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';v=json.loads((r/'validation.json').read_text());j={k:r/'output'/d['file']for k,d in v['outputs'].items()};ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];results=[]
def run(a):subprocess.run(list(map(str,a)),check=True)
def check(cp,name,*args):
 p=subprocess.run(['java','-Xverify:all','-cp',cp,name,*map(str,args)],text=True,capture_output=True);print(p.stdout,p.stderr,end='');p.check_returncode();results.append(p.stdout.strip())
base=r/'build/test-base';base.mkdir(exist_ok=True);extra=r/'build/test-extra';extra.mkdir(exist_ok=True);h=r/'build/test-hordes';h.mkdir(exist_ok=True)
run([*ecj,'-cp',str(j['melee']),'-d',base,*(r/'tests').rglob('*.java')]);cp=str(base)+':'+str(j['melee'])+':'+str(j['gore'])
run([*ecj,'-cp',cp,'-d',extra,*(r/'tests18').rglob('*.java')]);cp=str(extra)+':'+cp
check(cp,'dev.zomboidmelee.boxing.AnimationRegression');check(cp,'dev.zomboidmelee.boxing.ArmorShadowRegression');check(cp,'dev.zomboidmelee.boxing.LegRenderChecks18');check(cp,'dev.zomboidmelee.boxing.ActionChecks20');check(cp,'dev.zomboidmelee.boxing.ProtocolChecks18');check(cp,'dev.zomboidmelee.boxing.MomentumChecks20')
run([*ecj,'-cp',str(j['hordes']),'-d',h,*(r/'tests49').rglob('*.java')]);check(str(h)+':'+str(j['hordes']),'dev.zomboid.hordes.BudgetChecks49')
asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
run([*ecj,'-cp',asm,'-d',r/'build/patch',r/'Inspect20.java'])
check(str(r/'build/patch')+':'+asm,'Inspect20',j['melee'],j['hordes'])
oldcp=str(extra)+':'+str(base)+':'+str(r/'base/melee19.jar')+':'+str(r/'base/gore9.jar')
p=subprocess.run(['java','-cp',oldcp,'dev.zomboidmelee.boxing.ArmorShadowRegression'],capture_output=True,text=True);assert p.returncode!=0 and 'xRot' in p.stderr
results.append('ORIGINAL_V19_GEOBONE_ARM_CRASH_REPRODUCED_NEGATIVE_CONTROL_PASS')
(r/'test-results.json').write_text(json.dumps({'checks':results,'minecraft_runtime_tested':False},indent=2)+'\n')
