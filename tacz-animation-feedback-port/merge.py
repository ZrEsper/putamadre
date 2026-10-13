from pathlib import Path
import zipfile,re,json,hashlib
root=Path(__file__).resolve().parent;base=Path(root.parent/'mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.8-scope-zoom.jar');addon=root/'build/libs/locknar_doorkick-1.2.5-neoforge.1.jar';out=root/'output/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.9-tps-animations-feedback.jar';out.parent.mkdir(exist_ok=True)
with zipfile.ZipFile(base) as z:data={n:z.read(n) for n in z.namelist() if not n.endswith('/')}
original=dict(data)
with zipfile.ZipFile(addon) as z:
 for n in z.namelist():
  if n.endswith('/') or n.startswith('META-INF/') or n=='pack.mcmeta':continue
  data[n]=z.read(n)
addonmeta=(root/'src/main/resources/META-INF/neoforge.mods.toml').read_text();addonmeta=addonmeta[addonmeta.index('[[mods]]'):]
data['META-INF/neoforge.mods.toml']+=('\n'+addonmeta+'\n[[dependencies.tacz]]\nmodId="neoforge"\ntype="required"\nversionRange="[21.1.255,)"\nordering="NONE"\nside="BOTH"\n').encode()
for p in (root/'build/impactClasses').rglob('*.class'):data[str(p.relative_to(root/'build/impactClasses'))]=p.read_bytes()
data['docs/animation-additions-original-metadata.txt']=(root/'docs/animation-additions-original-metadata.txt').read_bytes()
data['docs/feedback-and-animation-port.txt']=b'Animation Additions 1.2.5 by Locknar, MIT, ported to NeoForge 1.21.1. Adaptive third-person aiming inspired by Third Person Shooting: Zero by ChloePrime (MIT). The independent TP Shooting / Shoulder Surfing plugins are not bundled or required. Previous TACZ scope, audio, recoil and jam patches retained. Install this combined JAR on client and server; remove the older TACZ JAR and separate Animation Additions / TP Shooting JARs. Minecraft 1.21.1, NeoForge 21.1.255+, Java 21.'
assert not any('PortGameTests' in n or 'ClientSmoke' in n for n in data)
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as z:
 for n,d in sorted(data.items()):info=zipfile.ZipInfo(n,(1980,1,1,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,d)
changes=[n for n in original if original[n]!=data.get(n)];print('Changed existing entries:',changes);print('Output:',out,out.stat().st_size,hashlib.sha256(out.read_bytes()).hexdigest())
(root/'merge-report.json').write_text(json.dumps({'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changes,'added_entries':len(data)-len(original)},indent=2))

import subprocess,os
jdk=Path(os.environ.get('JAVA_HOME','/workspace/lrarmor-build-tools/jdk/jdk-21.0.12.1'))
java=str(jdk/'bin/java')
asm=root/'tools'
import urllib.request
for name,expected in [('asm','8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281'),('asm-tree','9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f')]:
 path=asm/(name+'.jar')
 if not path.exists():path.write_bytes(urllib.request.urlopen(f'https://repo.maven.apache.org/maven2/org/ow2/asm/{name}/9.7.1/{name}-9.7.1.jar').read())
 if hashlib.sha256(path.read_bytes()).hexdigest()!=expected:raise RuntimeError('ASM hash mismatch: '+name)
cp=os.pathsep.join(map(str,[root/'tools',asm/'asm.jar',asm/'asm-tree.jar']))
subprocess.run([str(jdk/'bin/javac'),'-cp',cp,'-d',str(root/'tools'),str(root/'tools/PinSubscribers.java')],check=True)
pinned=out.with_name('pinned.jar')
with (root/'pin-report.log').open('w') as log:subprocess.run([java,'-cp',cp,'PinSubscribers',str(out),str(pinned)],stdout=log,check=True)
os.replace(pinned,out)
with zipfile.ZipFile(out) as z:final={n:z.read(n) for n in z.namelist()}
changes=[n for n in original if original[n]!=final.get(n)]
(root/'merge-report.json').write_text(json.dumps({'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_original_entries':changes,'added_entries':len(final)-len(original),'pinned_subscribers':38},indent=2))
print('Pinned merged output:',out.stat().st_size,hashlib.sha256(out.read_bytes()).hexdigest())
