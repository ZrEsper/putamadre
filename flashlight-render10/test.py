from pathlib import Path
import subprocess,json,shutil
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';jar=next((r/'output').glob('*.jar'));ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);out=r/'build/test';out.mkdir(exist_ok=True);fixtures=r/'build/fixtures';shutil.copytree(root/'flashlight-render6/build/fixtures',fixtures,dirs_exist_ok=True)
p=fixtures/'fixture/Checks.java';p.write_text(p.read_text().replace('Inv(){','public Inv(){').replace('public void setChanged(){','public void setItem(int slot,ItemStack s){stacks[slot]=s;}public void setChanged(){'))
cp=str(root/'flashlight-render6/build/test')+':'+str(root/'flashlight-render7/build/test')+':'+str(jar)
subprocess.run([*ecj,'-cp',cp,'-d',str(out),*map(str,fixtures.rglob('*.java')),*map(str,(r/'tests').rglob('*.java'))],check=True);reports=[]
for cls in ['net.flashlight.battery.InventoryChecks9','fixture.Checks','fixture.WakeChecks','net.flashlight.battery.CacheChecks7','net.flashlight.battery.ToggleChecks7','net.flashlight.battery.ReloadChecks6','net.flashlight.battery.ShadowChecks6']:
 p=subprocess.run(['java','-Xverify:all','-cp',str(out)+':'+cp,cls],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
subprocess.run([*ecj,'-cp',asm,'-d',str(r/'build/tool'),str(r/'Inspect.java')],check=True)
p=subprocess.run(['java','-cp',str(r/'build/tool')+':'+asm,'Inspect',str(jar)],capture_output=True,text=True);print(p.stdout,p.stderr,end='');p.check_returncode();reports.append(p.stdout.strip())
(r/'test-results.json').write_text(json.dumps(dict(checks=reports,minecraft_runtime_tested=False),indent=2)+'\n')
