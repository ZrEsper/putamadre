from pathlib import Path
import sys,subprocess,tempfile,os,json,hashlib,zipfile
r=Path(__file__).resolve().parent;repo=r.parent;base=Path(sys.argv[1]);tools=Path(sys.argv[2]);asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
def run(*a):subprocess.run(list(map(str,a)),check=True)
with tempfile.TemporaryDirectory(prefix='fa-zero-') as temp:
 t=Path(temp);classes=t/'classes';patch=t/'patch';tests=t/'tests';ecj=['java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn']
 run(*ecj,'-d',classes,*sorted((r/'src').rglob('*.java')));run(*ecj,'-cp',asm,'-d',patch,r/'Patch.java');run(*ecj,'-cp',classes,'-d',tests,*sorted((r/'tests').rglob('*.java')));run('java','-Xverify:all','-cp',str(tests)+os.pathsep+str(classes),'dev.zomboid.firstaid.Checks');run('java','-Xverify:all','-cp',str(tests)+os.pathsep+str(classes),'dev.zomboid.firstaid.RevivalChecks')
 out=repo/'mods/firstaid-1.21.1-ZOMBOID-v3.4.9-global-health-revival.jar';run('java','-cp',str(patch)+os.pathsep+asm,'Patch',base,out,classes)
 with zipfile.ZipFile(out) as z:
  data={n:z.read(n) for n in z.namelist()}
 for lang in ['en_us','es_es']:
  name='assets/firstaid/lang/'+lang+'.json';messages=json.loads(data[name]);es=lang=='es_es'
  for chance in range(25,46,2):messages['firstaid.gui.downed_'+str(chance)]=('Derribado · puedes ser reanimado' if es else 'Downed · revival available')
  data[name]=(json.dumps(messages,ensure_ascii=False,indent=2)+'\n').encode()
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
  assert b.testzip() is None
  changed=[n for n in a.namelist() if a.read(n)!=b.read(n)];assert set(changed)=={'ichttt/mods/firstaid/common/damagesystem/PlayerDamageModel.class','ichttt/mods/firstaid/client/StatusEffectLayer.class','assets/firstaid/lang/en_us.json','assets/firstaid/lang/es_es.json','ichttt/mods/firstaid/common/util/CommonUtils.class','ichttt/mods/firstaid/survival/ClinicalHooks.class','ichttt/mods/firstaid/compat/lrtactical/TreatmentV33.class'},changed
  assert all(n.startswith('dev/zomboid/firstaid/') for n in set(b.namelist())-set(a.namelist()))
 (r/'validation.json').write_text(json.dumps({'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed':changed,'minecraft_runtime_tested':False,'simulated_health_and_penalty_cases':47,'simulated_revival_cases':14,'original_mixins_unchanged':True},indent=2)+'\n')
