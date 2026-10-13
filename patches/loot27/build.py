from pathlib import Path
import zipfile,subprocess
r=Path('work/loot27');baseline='attachments/824124c0-84b8-4c84-876f-9309d2327280/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.25-timed-item-cleanup.jar';profile='work/loot26/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.26-loot-profiles.jar';inputh='attachments/1d533549-0099-4782-97c6-951ce5037d6c/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.26-action-sounds.jar'
with zipfile.ZipFile(baseline) as b,zipfile.ZipFile(profile) as p,zipfile.ZipFile(inputh) as s:
 data={n:s.read(n) for n in s.namelist()};changes=[]
 for n in p.namelist():
  if n not in b.namelist() or p.read(n)!=b.read(n):
   if n in data and n in b.namelist() and data[n]!=b.read(n):raise Exception('Merge conflict '+n)
   data[n]=p.read(n);changes.append(n)
 for n in s.namelist():
  if n not in changes:assert data[n]==s.read(n)
 for n in ['dev/zomboid/sounds/ActionSounds.class','dev/zomboid/hordes/BarrierBreakGoal.class','dev/zomboid/hordes/ZombieCombat.class','assets/zomboid_hordes/sounds.json']:assert data[n]==s.read(n)
h=r/'zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.27-action-sounds-loot-profiles.jar'
with zipfile.ZipFile(h,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in data.items():z.writestr(n,b)
inputr='attachments/c239f26c-5b31-4997-88d9-10f1fff4a19a/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.7-npc-full-cargo.jar';out=r/'zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.8-npc-full-cargo-loot-profiles.jar'
updates={'dev/zomboid/remains/ZombieRemainsLoot.class':(r/'ZombieRemainsLoot-fixed.class').read_bytes(),'dev/zomboid/remains/delivery/NpcCorpses.class':(r/'NpcCorpses-fixed.class').read_bytes()}
with zipfile.ZipFile(inputr) as s,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n in s.namelist():z.writestr(n,updates.pop(n,s.read(n)))
 for p in (r/'remains-classes/dev').rglob('*.class'):z.writestr(str(p.relative_to(r/'remains-classes')),p.read_bytes())
with zipfile.ZipFile(inputr) as s,zipfile.ZipFile(out) as z:
 for n in s.namelist():
  if n not in ['dev/zomboid/remains/ZombieRemainsLoot.class','dev/zomboid/remains/delivery/NpcCorpses.class']:assert z.read(n)==s.read(n)
flags=['--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.util=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree.analysis=ALL-UNNAMED']
for p in [h,out]:
 with zipfile.ZipFile(p) as z:assert z.testzip() is None
 subprocess.run(['java',*flags,'-cp','work/tools','Verify',str(p)],check=True)
print('Sound assets, combat changes, NPC cargo persistence and all unmodified input entries preserved byte-for-byte')
