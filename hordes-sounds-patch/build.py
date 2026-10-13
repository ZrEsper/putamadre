from pathlib import Path
import subprocess,zipfile,json,hashlib,shutil
r=Path(__file__).resolve().parent;w=Path('/workspace/horde-sound-work');base=r/'base.jar'
if not base.exists():shutil.copy2('/workspace/attachments/21db2480-c87a-4acf-81f5-a32b9102699a/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.25-timed-item-cleanup.jar',base)
cp=':'.join(map(str,(r.parent/'weapon-interaction-patch/build/tools').glob('asm*.jar')))
ecj=['java','-jar','/workspace/onboarding/tools/ecj.jar','-21','-proc:none','-warn:none']
subprocess.run(ecj+['-cp',cp,'-d',str(w/'tool'),str(r/'Patch.java')],check=True)
subprocess.run(ecj+['-cp',str(base),'-d',str(w/'classes'),*map(str,(r/'src').rglob('*.java'))],check=True)
subprocess.run(['java','-cp',str(w/'tool')+':'+cp,'Patch',str(base),str(w/'patched.jar')],check=True)
audios={'window_break':'06b12278-a398-449f-a7ba-901f985cea18','door_break_start':'25b23a76-d76a-4eed-bfa9-05fd0c6ca23e','bite_cloth':'70655df9-38fc-4495-9455-cbd6cb58516e'}
(r/'audio').mkdir(exist_ok=True)
for name,folder in audios.items():
 target=r/'audio'/f'{name}.ogg'
 if not target.exists():subprocess.run(['ffmpeg','-v','error','-i',str(next((Path('/workspace/attachments')/folder).glob('*.ogg'))),'-ac','1','-c:a','libvorbis','-q:a','5',str(target)],check=True)
out=r.parent/'mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.26-action-sounds.jar'
with zipfile.ZipFile(w/'patched.jar') as a,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as b:
 sounds=json.loads(a.read('assets/zomboid_hordes/sounds.json'))
 for name in audios:sounds[name]={'sounds':[{'name':'zomboid_hordes:'+name,'stream':False}],'subtitle':'subtitles.zomboid_hordes.'+name}
 locales={'es_es':['Cristal roto','Zombi golpea una puerta','Tela desgarrada'],'en_us':['Glass shatters','Zombie pounds on a door','Cloth rips']}
 for e in a.infolist():
  data=a.read(e)
  if e.filename=='assets/zomboid_hordes/sounds.json':data=json.dumps(sounds,indent=2).encode()
  for lang,values in locales.items():
   if e.filename=='assets/zomboid_hordes/lang/'+lang+'.json':
    d=json.loads(data);d.update({'subtitles.zomboid_hordes.'+k:v for k,v in zip(audios,values)});data=json.dumps(d,ensure_ascii=False).encode()
  b.writestr(e,data)
 for p in (w/'classes').rglob('*.class'):b.writestr(p.relative_to(w/'classes').as_posix(),p.read_bytes())
 for p in (r/'audio').glob('*.ogg'):b.writestr('assets/zomboid_hordes/sounds/'+p.name,p.read_bytes())
 for lang,values in locales.items():
  path='assets/zomboid_hordes/lang/'+lang+'.json'
  if path not in a.namelist():b.writestr(path,json.dumps({'subtitles.zomboid_hordes.'+k:v for k,v in zip(audios,values)},ensure_ascii=False))
with zipfile.ZipFile(out) as z:assert z.testzip() is None
with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as b:
 changed=[n for n in a.namelist() if a.read(n)!=b.read(n)]
 assert set(n for n in changed if not n.startswith('assets/zomboid_hordes/lang/'))=={'assets/zomboid_hordes/sounds.json','dev/zomboid/hordes/BarrierBreakGoal.class','dev/zomboid/hordes/ZombieCombat.class'},changed
(r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_entries':changed,'minecraft_runtime_tested':False},indent=2)+'\n')
print(out)
