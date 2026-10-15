from pathlib import Path
import json,os,subprocess
r=Path(__file__).resolve().parent;os.chdir(r);tools=r.parent/'hordes-melee-interiors-patch/build/tools';outputs=json.loads(Path('validation.json').read_text());cp=os.pathsep.join(str(Path('output')/outputs[k]['output'])for k in ['hordes','corpse','remains']);Path('build/tests').mkdir(exist_ok=True)
subprocess.run(['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn','-cp',cp,'-d','build/tests',*map(str,Path('tests').rglob('*.java'))],check=True)
result=subprocess.run(['java','-Xverify:all','-cp','build/tests'+os.pathsep+cp,'dev.zomboid.hordes.CorpseChecks'],text=True,capture_output=True);print(result.stdout);print(result.stderr);result.check_returncode()
pins=json.loads(Path('inputs.json').read_text());base=Path('base')/pins['corpse']['file'];new=Path('output')/outputs['corpse']['output'];probes=[]
for artifact,expected in [(base,'HELMET_SLOT=0'),(new,'HELMET_SLOT=5')]:
 p=subprocess.run(['java','-Xverify:all','-cp','build/tests'+os.pathsep+str(artifact),'fixture.EquipmentProbe'],text=True,capture_output=True,check=True);print(p.stdout.strip());assert p.stdout.strip()==expected;probes.append(p.stdout.strip())
Path('test-results.json').write_text(json.dumps({'checks':result.stdout.strip(),'native_corpse_equipment_before_after':probes,'minecraft_runtime_tested':False},indent=2)+'\n')
