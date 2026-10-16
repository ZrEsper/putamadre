from pathlib import Path
import subprocess,os,json
r=Path(__file__).resolve().parent;os.chdir(r);tools=r.parent/'hordes-melee-interiors-patch/build/tools';cp=os.pathsep.join(str(tools/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn']
def run(*a):return subprocess.run(list(map(str,a)),check=True,text=True,capture_output=True).stdout
run('python','tests/fixtures.py');fixtures=list(Path('build/fixtures-src').rglob('*.java'))
print(run(*ecj,'-cp','build/turret:build/survival','-d','build/tests','tests/dev/zomboid/turretpower/Checks.java',*fixtures),end='')
outputs=[run('java','-cp','build/tests:build/turret:build/survival','dev.zomboid.turretpower.Checks')]
decor='output/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-beds-barricades.4.jar';hordes=r.parent/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.43-infected-player-corpse.jar'
run(*ecj,'-cp','build/tests:'+decor+':'+str(hordes),'-d','build/tests','tests/dev/zomboid/decor/BedChecks.java');outputs.append(run('java','-cp','build/tests:'+decor+':'+str(hordes),'dev.zomboid.decor.BedChecks'))
run(*ecj,'-cp',cp,'-d','build/tool','tests/NativeProbe.java')
for name,cls,kind in [('tacz_turrets-neoforge-3.0.0-1.21.1-doomsday-power-burst.1.jar','com/entropy/tacz_turrets/turret/TurretEntity','turret'),('zomboid-survival-1.21.1-1.1.0-generator-tiers.2.jar','dev/zomboid/survival/GeneratorFuel','fuel')]:run('java','-cp',cp+':build/tool','NativeProbe','output/'+name,'build/native',cls,kind)
run(*ecj,'-cp','build/native:build/tests:build/turret:build/survival','-d','build/tests','tests/dev/zomboid/turretpower/NativeChecks.java');outputs.append(run('java','-cp','build/native:build/tests:build/turret:build/survival','dev.zomboid.turretpower.NativeChecks'))
Path('test-results.json').write_text(json.dumps({'checks':outputs,'minecraft_runtime_tested':False,'method':'Compiled production helpers with API doubles; extracted unchanged patched native methods with fixture superclass; ASM BasicVerifier on patched methods'},indent=2)+'\n')
print(''.join(outputs),end='')
