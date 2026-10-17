from pathlib import Path
import subprocess,hashlib,json,zipfile,struct,zlib
r=Path(__file__).resolve().parent;root=r.parent;t=root/'hordes-melee-interiors-patch/build/tools';src=root/'mods/fracturepoint-1.21.1-3.0.0-PT2-digital-exposure.5.jar';asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);ecj=['java','-jar',t/'ecj.jar','-21','-proc:none','-nowarn']
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(*a):subprocess.run(list(map(str,a)),check=True)
for n,h in json.loads((root/'spas-reload18/inputs.json').read_text())['tools'].items():assert sha(t/(n+'.jar'))==h
assert sha(src)==json.loads((r/'inputs.json').read_text())['input_sha256']
for d in ['build/tool','build/classes','output']:(r/d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Patch.java');run(*ecj,'-cp',src,'-d',r/'build/classes',*(r/'src').rglob('*.java'));out=r/'output/fracturepoint-1.21.1-3.0.0-PT2-hybrid-thermal.6.jar';run('java','-cp',str(r/'build/tool')+':'+asm,'Patch',src,out,r/'build/classes')
with zipfile.ZipFile(out)as z:e={n:z.read(n)for n in z.namelist()}
def js(n,d):e[n]=json.dumps(d,ensure_ascii=False,separators=(',',':')).encode()
e['assets/fracturepoint/shaders/program/hybrid-thermal-vision.fsh']=(r/'hybrid-thermal-vision.fsh').read_bytes()
program=json.loads(e['assets/fracturepoint/shaders/program/thermal-vision.json']);program['fragment']='fracturepoint:hybrid-thermal-vision';program['samplers']=[{'name':'DiffuseSampler'}];program['uniforms']=[u for u in program['uniforms']if u['name']in ['ProjMat','InSize','OutSize']];js('assets/fracturepoint/shaders/program/hybrid-thermal-vision.json',program)
js('assets/fracturepoint/shaders/post/hybrid-thermal-vision.json',{'targets':['swap'],'passes':[{'name':'fracturepoint:hybrid-thermal-vision','intarget':'minecraft:main','outtarget':'swap'},{'name':'blit','intarget':'swap','outtarget':'minecraft:main'}]})
js('assets/fracturepoint/models/item/hybrid_thermal_goggles.json',{'parent':'fracturepoint:item/thermal_goggles'})
recipe=json.loads(e['data/fracturepoint/recipe/ballistics_bench/goggles/thermal_goggles.json']);recipe['result']['id']='fracturepoint:hybrid_thermal_goggles';js('data/fracturepoint/recipe/ballistics_bench/goggles/hybrid_thermal_goggles.json',recipe)
# Canonical opaque white data texel used by the mesh encoder, not an artwork edit.
def chunk(k,b):return struct.pack('>I',len(b))+k+b+struct.pack('>I',zlib.crc32(k+b)&0xffffffff)
e['assets/fracturepoint/textures/misc/hybrid-white.png']=b'\x89PNG\r\n\x1a\n'+chunk(b'IHDR',struct.pack('>2I5B',1,1,8,6,0,0,0))+chunk(b'IDAT',zlib.compress(b'\0\xff\xff\xff\xff'))+chunk(b'IEND',b'')
for locale,label in [('es_es','Gafas térmicas híbridas'),('es_mx','Gafas térmicas híbridas'),('en_us','Hybrid Thermal Goggles'),('de_de','Hybrid-Wärmebildbrille')]:
 n='assets/fracturepoint/lang/'+locale+'.json';d=json.loads(e.get(n,b'{}'));d['item.fracturepoint.hybrid_thermal_goggles']=label;js(n,d)
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(src)as a,zipfile.ZipFile(out)as b:assert b.testzip()is None;changed=sorted(n for n in a.namelist()if a.read(n)!=b.read(n));added=sorted(set(b.namelist())-set(a.namelist()))
(r/'validation.json').write_text(json.dumps({'input_sha256':sha(src),'output':out.name,'sha256':sha(out),'changed':changed,'added':added,'minecraft_runtime_tested':False},indent=2)+'\n')
