from pathlib import Path
import subprocess,json,hashlib,zipfile
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(x+'.jar'))for x in ['asm','asm-tree','asm-analysis'])
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((r/'inputs.json').read_text()).items():assert hashlib.sha256((r/'base'/n).read_bytes()).hexdigest()==h
for n in ['build/tool','build/classes','output']:(r/n).mkdir(parents=True,exist_ok=True)
run(*ecj,'-d',r/'build/classes',*(r/'src').rglob('*.java'));run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Patch6.java')
p=r/'output/flashlight-5.1-neoforge-1.21.1-cone-light-battery.6.jar';run('java','-cp',str(r/'build/tool')+':'+asm,'Patch6',r/'base/flash5.jar',p,r/'build/classes')
with zipfile.ZipFile(p)as z:e={n:z.read(n)for n in z.namelist()}
for src,name in [('light.vert','portable6.vert'),('light.frag','portable6.frag')]:e['assets/flashlight/shaders/'+name]=(r/'shaders'/src).read_bytes()
m=json.loads(e['flashlight.mixins.json']);m['client']+=['WhiteLightmap6','AlbedoTarget6'];e['flashlight.mixins.json']=json.dumps(m,indent=2).encode()
for locale in ['en_us','es_es','es_mx']:
 n='assets/flashlight/lang/'+locale+'.json';d=json.loads(e.get(n,b'{}'));spanish=locale.startswith('es');d['tooltip.flashlight.controls']='Clic derecho: encender/apagar. Shift + clic derecho: recargar. Batería sobre linterna + clic derecho en el inventario.'if spanish else'Right-click: toggle. Shift + right-click: reload. Right-click a flashlight with a battery on the inventory cursor.';d['tooltip.flashlight.battery_controls']='Clic derecho sobre la linterna en el inventario, o usa la batería con clic derecho al aire.'if spanish else'Right-click a flashlight in your inventory with this battery, or hold the battery and right-click the air.';d['message.flashlight.no_light']='No tienes una linterna que necesite batería.'if spanish else'You have no flashlight that needs a battery.';e[n]=json.dumps(d,ensure_ascii=False).encode()
with zipfile.ZipFile(p,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(r/'base/flash5.jar')as a,zipfile.ZipFile(p)as b:changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()));assert b.testzip()is None
(r/'validation.json').write_text(json.dumps({'file':p.name,'sha256':hashlib.sha256(p.read_bytes()).hexdigest(),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
