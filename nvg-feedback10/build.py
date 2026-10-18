from pathlib import Path
import subprocess,hashlib,json,zipfile,re
r=Path(__file__).resolve().parent;t=r.parent/'hordes-melee-interiors-patch/build/tools';ecj=['java','-jar',str(t/'ecj.jar'),'-21','-proc:none','-nowarn'];asm=':'.join(str(t/(n+'.jar'))for n in ['asm','asm-tree','asm-analysis']);base=r/'base/fracture9.jar'
def run(*a):subprocess.run(list(map(str,a)),check=True)
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
h=sha(base);pin=r/'inputs.json'
if pin.exists():assert json.loads(pin.read_text())['fracture9.jar']==h
else:pin.write_text(json.dumps({'fracture9.jar':h},indent=2)+'\n')
for d in ['build/tool','build/classes','output']:(r/d).mkdir(parents=True,exist_ok=True)
run(*ecj,'-cp',asm,'-d',r/'build/tool',r/'Patch.java');run(*ecj,'-cp',str(base)+':'+str(r.parent/'hybrid-mask8/build/api'),'-d',r/'build/classes',*(r/'src').rglob('*.java'));out=r/'output/fracturepoint-1.21.1-3.0.0-PT2-nvg-interference-audio-textures.10.jar';run('java','-cp',str(r/'build/tool')+':'+asm,'Patch',base,out,r/'build/classes')
with zipfile.ZipFile(out)as z:e={n:z.read(n)for n in z.namelist()}
filters=[]
for n,b in list(e.items()):
 if not(n.startswith('assets/fracturepoint/shaders/program/')and n.endswith('.fsh')and 'scene-brightness'not in n):continue
 s=b.decode();assert 'uniform float Time;'in s,n
 if 'hybrid-thermal-vision'in n:
  s,count=re.subn(r'vec3 heat=.*?;', 'vec3 heat=mix(vec3(luma),source,.25)*.38+vec3(1.,.11,.07)*(.62*(.20+.80*sqrt(clamp(luma,0.,1.))));',s,count=1);assert count==1
 s,count=re.subn(r'void\s+main\s*\(\s*\)\s*\{','void visionMain10(){',s,count=1);assert count==1
 s+='''
// Shared faint sensor grain, scanlines and rolling interference across every vision palette.
float sensorHash10(vec2 p){return fract(sin(dot(p,vec2(127.1,311.7)))*43758.5453);}
void main(){vec4 raw10=texture(DiffuseSampler,texCoord);if(abs(raw10.a-251./255.)<.0015){fragColor=vec4(raw10.rgb,1.);return;}visionMain10();vec2 px=floor(gl_FragCoord.xy);float grain=(sensorHash10(px+floor(Time*1200.))-.5)*.010;float scan=sin(px.y*3.14159265+Time*70.)*.0018;float band=sin(px.y*.025-Time*130.)*.0012;fragColor.rgb=clamp(fragColor.rgb+grain+scan+band,0.,1.);}
''';e[n]=s.encode();filters.append(n)
assert len(filters)==8
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(e.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:assert b.testzip()is None;changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];added=sorted(set(b.namelist())-set(a.namelist()))
(r/'validation.json').write_text(json.dumps(dict(file=out.name,sha256=sha(out),filters=filters,changed=changed,added=added,minecraft_runtime_tested=False),indent=2)+'\n')
