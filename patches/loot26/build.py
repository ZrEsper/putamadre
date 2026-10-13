from pathlib import Path
import zipfile,json,subprocess
r=Path('work/loot26')
base='attachments/824124c0-84b8-4c84-876f-9309d2327280/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.25-timed-item-cleanup.jar'
out=r/'zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.26-loot-profiles.jar'
u={str(p.relative_to(r/'classes')):p.read_bytes() for p in (r/'classes/dev').rglob('*.class')};u.update({str(p.relative_to(r/'resources')):p.read_bytes() for p in (r/'resources').rglob('*.json')})
with zipfile.ZipFile(base) as s,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n in s.namelist():
  b=u.pop(n,s.read(n))
  if n=='zomboid_hordes.mixins.json':j=json.loads(b);j['mixins'].append('ContainerProfileMixin');b=json.dumps(j,indent=2).encode()
  z.writestr(n,b)
 for n,b in u.items():z.writestr(n,b)
with zipfile.ZipFile(base) as s,zipfile.ZipFile(out) as z:
 assert z.testzip() is None
 for n in ['dev/zomboid/hordes/ZoneReserve.class','dev/zomboid/hordes/ItemCleanup.class','dev/zomboid/hordes/WorldTuning.class']:assert z.read(n)==s.read(n)
 assert not any(n.startswith(('net/','it/','com/')) for n in z.namelist())
base='work/loot-expand/dist/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.6-expanded-addons.jar';remains=r/'zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.7-loot-profiles.jar'
with zipfile.ZipFile(base) as s,zipfile.ZipFile(remains,'w',zipfile.ZIP_DEFLATED) as z:
 for n in s.namelist():z.writestr(n,(r/'Remains-fixed.class').read_bytes() if n=='dev/zomboid/remains/ZombieRemainsLoot.class' else s.read(n))
 for p in (r/'remains-classes/dev').rglob('*.class'):z.writestr(str(p.relative_to(r/'remains-classes')),p.read_bytes())
flags=['--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.util=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree.analysis=ALL-UNNAMED']
for p in [out,remains]:subprocess.run(['java',*flags,'-cp','work/tools','Verify',str(p)],check=True)
print('Preserved combat-priority, cleanup and restock classes from uploaded v25')
