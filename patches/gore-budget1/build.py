from pathlib import Path
import zipfile,subprocess
r=Path('work/gore-budget');flags=['--add-exports','java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED']
cases=[('attachments/625c6244-2cfb-4716-9970-f32980d5feb0/mobdismembermentneoforge-1.21.1-1.4.0.jar','mobdismembermentneoforge-1.21.1-1.4.0-bounded-gibs.1.jar',[('me/ichun/mods/mobdismemberment/client/entity/EntityGib.class','gib')],['Lookup','GibBudget']),('attachments/a34efeee-aabb-4875-ad22-6f5c140a2ef6/splatter-1.21.1-0.3.0-neoforge.jar','splatter-1.21.1-0.3.0-neoforge-light-budget.1.jar',[('net/mehvahdjukaar/splatter/client/decal/DecalSystem.class','light')],['Lookup','LightBudget']),('attachments/e59d3de2-1f74-437e-96d2-1079cb54e10e/luckyswardrobe-2.0.0.jar','luckyswardrobe-2.0.0-model-cache.1.jar',[('net/luckystudios/luckyswardrobe/client/ModModelRegistrations$1.class','clothes'),('net/luckystudios/luckyswardrobe/client/ModModelRegistrations$2.class','clothes')],['Lookup','WardrobeCache'])]
for src,out,targets,helpers in cases:
 with zipfile.ZipFile(src) as z:data={n:z.read(n) for n in z.namelist()}
 for n,kind in targets:
  p=r/'input.class';p.write_bytes(data[n]);subprocess.run(['java',*flags,'-cp',str(r/'tools'),'Patch',str(p),str(r/'patched.class'),kind],check=True);data[n]=(r/'patched.class').read_bytes()
 pack={'gib':'gibbudget','light':'splatterbudget','clothes':'wardrobebudget'}[targets[0][1]]
 for helper in helpers:
  for p in (r/('classes/dev/zomboid/'+pack)).glob(helper+'*.class'):data['dev/zomboid/'+pack+'/'+p.name]=p.read_bytes()
 with zipfile.ZipFile(r/out,'w',zipfile.ZIP_DEFLATED) as z:
  for n,b in data.items():z.writestr(n,b)
 with zipfile.ZipFile(r/out) as z:assert z.testzip() is None
 subprocess.run(['java',*flags,'--add-exports','java.base/jdk.internal.org.objectweb.asm.util=ALL-UNNAMED','--add-exports','java.base/jdk.internal.org.objectweb.asm.tree.analysis=ALL-UNNAMED','-cp','work/tools','Verify',str(r/out)],check=True)
