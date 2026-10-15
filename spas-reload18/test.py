from pathlib import Path
import subprocess,os,json
r=Path(__file__).resolve().parent;os.chdir(r)
b=Path('build');tools=r.parent/'hordes-melee-interiors-patch/build/tools';cp=os.pathsep.join(map(str,[b/'luaj-core.jar',b/'luaj-jse.jar',b/'classes']))
subprocess.run(['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d',str(b/'tests'),'tests/ReloadTest.java'],check=True)
p=subprocess.run(['java','-cp',cp+os.pathsep+str(b/'tests'),'ReloadTest',str(b/'original.lua'),str(b/'fixed.lua')],check=True,capture_output=True,text=True)
print(p.stdout,end='');Path('test-results.json').write_text(json.dumps({'result':p.stdout.strip(),'original_crash_reproduced':True,'minecraft_runtime_tested':False},indent=2)+'\n')
