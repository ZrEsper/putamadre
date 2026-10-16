from pathlib import Path
import os,subprocess,zipfile,json
r=Path(__file__).resolve().parent;os.chdir(r);root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';out=next(Path('output').glob('*.jar'));inputs=json.loads(Path('inputs.json').read_text());base=Path('base')/inputs['selfexpression']['file']
def run(*args):
 p=subprocess.run(list(map(str,args)),text=True,capture_output=True);print(p.stdout,p.stderr,end='');p.check_returncode();return p.stdout.strip()
Path('build/tests').mkdir(exist_ok=True)
run('java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn','-d','build/tests',*Path('tests').rglob('*.java'))
with zipfile.ZipFile(base)as z:names=sorted(n[:-6].replace('/','.')for n in z.namelist()if n.startswith('net/mcreator/selfexpression/item/')and n.endswith('.class')and b'getHumanoidArmorModel' in z.read(n))
assert len(names)==876;Path('build/extensions.txt').write_text('\n'.join(names));checks=[]
for name,jar in [('old',base),('new',out)]:checks.append(run('java','-Xverify:all','-cp','build/tests:'+str(jar),'ArmorChecks',name,'build/extensions.txt','build/'+name+'-geometry.txt'))
assert Path('build/old-geometry.txt').read_bytes()==Path('build/new-geometry.txt').read_bytes();checks.append('All 876 native model/limb maps identical before and after; original model class and every texture/item/recipe untouched')
Path('test-results.json').write_text(json.dumps({'checks':checks,'minecraft_runtime_tested':False,'method':'Actual 876 native item-extension methods plus actual Modelcustom_model constructor and ArmorCacheEpoch; Minecraft API doubles; original EMF repeated-layer guard simulated at its exact threshold; ASM BasicVerifier and JVM -Xverify:all'},indent=2)+'\n')
