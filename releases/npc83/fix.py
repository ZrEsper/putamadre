from pathlib import Path
import zipfile,re,tomllib,hashlib,json
r=Path(__file__).parent
base=Path('/workspace/npc82/release/ragdollified-1.21.1-1.1.5-server-owned-corpses.33.jar')
gore=Path('/workspace/attachments/eb547dd9-d6c7-42b6-be03-76b8234c3be5/zomboid-zombie-gore-1.0.20-neoforge-1.21.1-direct-lethal-damage.43.jar')
out=r/'release/ragdollified-1.21.1-1.1.5-server-owned-corpses.34-dependency-fix.jar'
with zipfile.ZipFile(base) as z:
 entries={n:z.read(n) for n in z.namelist()}
meta='META-INF/neoforge.mods.toml';text=entries[meta].decode();text=text.replace('version="1.1.5-server-owned-corpses.33"','version="1.1.5-gore20-perf57"').replace('displayName="Ragdollified"','displayName="Ragdollified - server-owned corpses .34"');entries[meta]=text.encode()
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in entries.items():
  e=zipfile.ZipInfo(n);e.date_time=(1980,1,1,0,0,0);e.compress_type=zipfile.ZIP_DEFLATED;z.writestr(e,b)
with zipfile.ZipFile(out) as z,zipfile.ZipFile(base) as old,zipfile.ZipFile(gore) as g:
 changed=[n for n in z.namelist() if z.read(n)!=old.read(n)];assert changed==[meta],changed
 gm=tomllib.loads(g.read(meta).decode());rm=tomllib.loads(z.read(meta).decode());rv=rm['mods'][0]['version'];gv=gm['mods'][0]['version']
 dep=next(d for d in gm['dependencies']['zomboid_gore'] if d['modId']=='ragdollified');assert dep['versionRange']=='['+rv+']'
 back=next(d for d in rm['dependencies']['ragdollified'] if d['modId']=='zomboid_gore');assert back['versionRange']=='[1.0.20.57,1.0.21)' and gv=='1.0.20.68'
 print('PASS: exact Gore -> ragdoll dependency, reverse version inside range, all class bytes unchanged')
(r/'release/SHA256SUMS.txt').write_text(hashlib.sha256(out.read_bytes()).hexdigest()+'  '+out.name+'\n')
