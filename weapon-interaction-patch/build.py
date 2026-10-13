from pathlib import Path
import copy,hashlib,json,os,re,shutil,subprocess,urllib.request,zipfile

root=Path(__file__).resolve().parent
build=root/'build';tools=build/'tools';tools.mkdir(parents=True,exist_ok=True)
for name,digest in json.loads((root/'tools.sha256.json').read_text()).items():
    target=tools/name
    if not target.exists() or hashlib.sha256(target.read_bytes()).hexdigest()!=digest:
        artifact='org/eclipse/jdt/ecj/3.39.0/ecj-3.39.0.jar' if name=='ecj.jar' else f'org/ow2/asm/{name[:-4]}/9.7.1/{name[:-4]}-9.7.1.jar'
        data=urllib.request.urlopen('https://repo.maven.apache.org/maven2/'+artifact,timeout=60).read()
        assert hashlib.sha256(data).hexdigest()==digest,name
        target.write_bytes(data)
ecj=['java','-jar',tools/'ecj.jar','-21','-proc:none','-warn:none']
asm=os.pathsep.join(str(p) for p in tools.glob('asm*.jar'))
def run(*args):subprocess.run(list(map(str,args)),cwd=root,check=True)
run(*ecj,'-cp',asm,'-d',build/'patch-tool',root/'Patch.java',root/'Verify.java',root/'Fixtures.java')
inputs=json.loads((root/'inputs.sha256.json').read_text())
names={'tacz':'tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.11-interaction-priority.jar','lr':'LesRaisins-Tactical-Equipements-1.21.1-0.4.3-zomboid-isometric-melee.2-interaction-priority.jar'}
for kind,spec in inputs.items():
    base=root/'base'/spec['name'];assert hashlib.sha256(base.read_bytes()).hexdigest()==spec['sha256'],base.name
    run('java','-cp',str(build/'patch-tool')+os.pathsep+asm,'Patch',kind,base,build/f'{kind}.jar')
classes=build/'classes'
if classes.exists():shutil.rmtree(classes)
run(*ecj,'-cp',os.pathsep.join(str(build/f'{kind}.jar') for kind in inputs),'-d',classes,*sorted((root/'src').rglob('*.java')))
(root/'output').mkdir(exist_ok=True)
report={}
for kind,name in names.items():
    prefix='dev/zomboid/taczinteract/' if kind=='tacz' else 'dev/zomboid/lriso/UsePriority'
    added={p.relative_to(classes).as_posix():p.read_bytes() for p in classes.rglob('*.class') if p.relative_to(classes).as_posix().startswith(prefix)}
    output=root/'output'/name
    with zipfile.ZipFile(build/f'{kind}.jar') as a,zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED) as b:
        for entry in a.infolist():b.writestr(copy.copy(entry),a.read(entry))
        for entry,data in sorted(added.items()):b.writestr(entry,data)
    with zipfile.ZipFile(root/'base'/inputs[kind]['name']) as a,zipfile.ZipFile(output) as b:
        assert b.testzip() is None
        changed={n for n in a.namelist() if a.read(n)!=b.read(n)}
        expected={'com/tacz/guns/client/event/ClientPreventGunClick.class','com/tacz/guns/client/input/AimKey.class'} if kind=='tacz' else {'me/xjqsh/lrtactical/client/ClientEventsHandler.class','me/xjqsh/lrtactical/client/input/AttackKeys.class'}
        assert changed==expected,changed
        assert set(b.namelist())-set(a.namelist())==set(added)
        for n in a.namelist():
            if n.endswith(('.json','.toml')) or 'mixin' in n.lower():assert a.read(n)==b.read(n),n
        report[kind]={'input_sha256':inputs[kind]['sha256'],'output_sha256':hashlib.sha256(output.read_bytes()).hexdigest(),'changed_entries':sorted(changed),'added_entries':sorted(added)}
    run('java','-cp',str(build/'patch-tool')+os.pathsep+asm,'Verify',output)
cp=os.pathsep.join([str(classes),*[str(root/'output'/n) for n in names.values()]])
run(*ecj,'-cp',cp,'-d',build/'tests',*sorted((root/'test-src').rglob('*.java')))
for kind,name in names.items():run('java','-cp',str(build/'patch-tool')+os.pathsep+asm,'Fixtures',kind,root/'output'/name,build/'fixtures')
test=subprocess.run(['java','-Xverify:all','-cp',str(build/'fixtures')+os.pathsep+str(build/'tests')+os.pathsep+cp,'dev.zomboid.taczinteract.Checks'],cwd=root,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(test.stdout,end='');test.check_returncode()
report['simulated_input_checks_passed']=int(re.search(r'Passed (\d+) input-method',test.stdout).group(1))
report['minecraft_runtime_tested']=False
(root/'validation.json').write_text(json.dumps(report,indent=2)+'\n')
for name in names.values():shutil.copy2(root/'output'/name,root.parent/'mods'/name)
print('Validated replacement JARs copied to mods/.')
