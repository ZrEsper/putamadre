from pathlib import Path
import hashlib,json,zipfile,subprocess,os
R=Path(__file__).resolve().parent
os.chdir(R)
base=next((R.parent/'mods').glob('*pz.17-*.jar'))
tools=R.parent/'hordes-melee-interiors-patch/build/tools'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*args):subprocess.run(list(map(str,args)),check=True)
pins=json.loads(Path('inputs.json').read_text());assert sha(base)==pins['base']
for name,digest in pins['tools'].items():assert sha(tools/(name+'.jar'))==digest
b=Path('build');b.mkdir(exist_ok=True)
with zipfile.ZipFile(base) as z:
 entries={n:z.read(n)for n in z.namelist()}
 for n in ['luaj-core','luaj-jse']:
  data=entries['META-INF/jarjar/'+n+'-3.0.8-figura.jar'];assert hashlib.sha256(data).hexdigest()==pins[n];(b/(n+'.jar')).write_bytes(data)
 original=entries['assets/tacz/custom/tacz_default_gun/data/tacz/scripts/spas_12_gun_logic.lua'].decode().replace('\r\n','\n')
 (b/'original.lua').write_text(original)
 # Resource protection for fresh/default packs; the Java hook also covers existing extracted packs.
 needle='    local interrupted_time = cache.interrupted_time'
 assert original.count(needle)==1
 fixed=original.replace(needle,'''    if type(cache) ~= "table" or type(cache.interrupted_time) ~= "number"
        or type(cache.reloaded_count) ~= "number" or type(cache.needed_count) ~= "number"
        or type(cache.is_tactical) ~= "boolean" then
        return NOT_RELOADING, -1
    end
'''+needle)
 (b/'fixed.lua').write_text(fixed)
 entries['assets/tacz/custom/tacz_default_gun/data/tacz/scripts/spas_12_gun_logic.lua']=fixed.replace('\n','\r\n').encode()
 classes=b/'classes';classes.mkdir(exist_ok=True);toolclasses=b/'tool';toolclasses.mkdir(exist_ok=True)
 cp=os.pathsep.join(str(tools/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis'])
 lua=os.pathsep.join(str(b/(n+'.jar'))for n in ['luaj-core','luaj-jse'])
 compiler=['java','-jar',tools/'ecj.jar','-21','-proc:none','-nowarn']
 run(*compiler,'-cp',lua,'-d',classes,'src/dev/zomboid/tacz/Reload18.java')
 run(*compiler,'-cp',cp,'-d',toolclasses,'Patch.java')
 target='com/tacz/guns/item/ModernKineticGunItem.class'
 (b/'original.class').write_bytes(entries[target]);run('java','-cp',cp+os.pathsep+str(toolclasses),'Patch',b/'original.class',b/'fixed.class');entries[target]=(b/'fixed.class').read_bytes()
 for p in classes.rglob('*.class'):entries[p.relative_to(classes).as_posix()]=p.read_bytes()
 out=Path('output/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.18-spas-reload-guard.jar');out.parent.mkdir(exist_ok=True)
 with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as dest:
  for n,data in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;dest.writestr(i,data)
 with zipfile.ZipFile(base)as old,zipfile.ZipFile(out)as new:
  changed=sorted(n for n in old.namelist()if old.read(n)!=new.read(n));added=sorted(set(new.namelist())-set(old.namelist()));assert changed==sorted([target,'assets/tacz/custom/tacz_default_gun/data/tacz/scripts/spas_12_gun_logic.lua']);assert len(added)==3 and all(n.startswith('dev/zomboid/tacz/Reload18')for n in added);assert new.testzip()is None
 Path('validation.json').write_text(json.dumps({'base_sha256':sha(base),'output_sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
