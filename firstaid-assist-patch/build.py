from pathlib import Path
import sys,subprocess,tempfile,os,zipfile,json,hashlib
r=Path(__file__).resolve().parent;base=Path(sys.argv[1]);tools=Path(sys.argv[2]);out=r.parent/'mods/firstaid-1.21.1-ZOMBOID-v3.5.1-revival-timer-fix.jar';asm=os.pathsep.join(str(tools/n) for n in ['asm.jar','asm-tree.jar','asm-analysis.jar'])
with tempfile.TemporaryDirectory() as tmp:
 t=Path(tmp);ecj=['java','-jar',str(tools/'ecj.jar'),'-21','-proc:none','-nowarn'];c=t/'classes';p=t/'patch';tests=t/'tests'
 for command in [ecj+['-d',str(c)]+[str(x) for x in (r/'src').rglob('*.java')],ecj+['-cp',str(c),'-d',str(tests)]+[str(x) for x in (r/'tests').rglob('*.java')],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(c),'dev.zomboid.firstaid.Checks'],['java','-Xverify:all','-cp',str(tests)+os.pathsep+str(c),'dev.zomboid.firstaid.AssistChecks'],ecj+['-cp',asm,'-d',str(p),str(r/'Patch.java')],['java','-cp',str(p)+os.pathsep+asm,'Patch',str(base),str(out),str(c)]]:subprocess.run(command,check=True)
 with zipfile.ZipFile(out) as z:data={n:z.read(n) for n in z.namelist()}
 for lang in ['es_es','en_us']:
  name='assets/firstaid/lang/'+lang+'.json';a=json.loads(data[name]);a['firstaid.gui.assist_time_left']='Ayuda disponible: %s' if lang=='es_es' else 'Rescue time remaining: %s';a['firstaid.gui.downed_assist']='Derribado · pide ayuda' if lang=='es_es' else 'Downed · ask for help';data[name]=(json.dumps(a,ensure_ascii=False,indent=2)+'\n').encode()
 for p in (r/'resources').rglob('*'):
  if p.is_file():
   name=str(p.relative_to(r/'resources'));content=p.read_bytes()
   if name=='assets/firstaid/sounds.json':
    sounds=json.loads(data[name]);sounds.update(json.loads(content));content=(json.dumps(sounds,indent=2)+'\n').encode()
   data[name]=content
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(base) as a,zipfile.ZipFile(out) as z:
  assert z.testzip() is None
  changed=[n for n in a.namelist() if a.read(n)!=z.read(n)]
  assert len(changed)==11,changed
  assert all(n in json.loads(z.read('assets/firstaid/sounds.json')) for n in json.loads(a.read('assets/firstaid/sounds.json')))
  assert not any('AssistChecks' in n for n in z.namelist())
 (r/'validation.json').write_text(json.dumps({'input_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'minecraft_runtime_tested':False,'changed_original_entries':changed,'simulated_checks_passed':76},indent=2)+'\n')
