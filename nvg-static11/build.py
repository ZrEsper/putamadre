from pathlib import Path
import hashlib,json,zipfile
r=Path(__file__).resolve().parent;base=r/'base/fracture10.jar';expected='b88e404e65db569576b39d19b1d995b9ad5ea2dcc38e73f2db14a39ce5d6d292';assert hashlib.sha256(base.read_bytes()).hexdigest()==expected
with zipfile.ZipFile(base)as z:entries={n:z.read(n)for n in z.namelist()}
filters=[]
for n,b in list(entries.items()):
 if not(n.startswith('assets/fracturepoint/shaders/program/')and n.endswith('.fsh')and 'scene-brightness'not in n):continue
 s=b.decode();marker='// Shared faint sensor grain';assert marker in s,n;s=s.split(marker)[0]
 s+='''// Persistent visible sensor static: every active frame, independent of scene brightness.
float sensorHash11(vec2 p){vec3 q=fract(vec3(p.xyx)*.1031);q+=dot(q,q.yzx+33.33);return fract((q.x+q.y)*q.z);}
void main(){
 vec4 raw11=texture(DiffuseSampler,texCoord);
 if(abs(raw11.a-251./255.)<.0015){fragColor=vec4(raw11.rgb,1.);return;}
 visionMain10();
 vec2 px=floor(gl_FragCoord.xy/1.5);
 float frame=floor(Time*48000.);
 float grain=(sensorHash11(px+vec2(mod(frame,4096.),mod(frame*17.,4096.)))-.5)*.070;
 float scan=sin(gl_FragCoord.y*2.0943951+Time*240.)*.003;
 float band=sin(gl_FragCoord.y*.035-Time*360.)*.002;
 fragColor.rgb=clamp(fragColor.rgb+grain+scan+band,0.,1.);
}
''';entries[n]=s.encode();filters.append(n)
assert len(filters)==8
out=r/'output/fracturepoint-1.21.1-3.0.0-PT2-nvg-persistent-static.11.jar';out.parent.mkdir(exist_ok=True)
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED)as z:
 for n,b in sorted(entries.items()):i=zipfile.ZipInfo(n,(1980,1,1,0,0,0));i.compress_type=zipfile.ZIP_DEFLATED;z.writestr(i,b)
with zipfile.ZipFile(base)as a,zipfile.ZipFile(out)as b:
 assert b.testzip()is None;assert set(a.namelist())==set(b.namelist());changed=[n for n in a.namelist()if a.read(n)!=b.read(n)];assert set(changed)==set(filters),'only eight vision fragment shaders may change'
(r/'validation.json').write_text(json.dumps(dict(file=out.name,sha256=hashlib.sha256(out.read_bytes()).hexdigest(),base_sha256=expected,filters=filters,only_changed=changed,all_java_audio_visor_textures_unchanged=True,minecraft_runtime_tested=False),indent=2)+'\n')
print('EIGHT_VISION_SHADERS_ONLY_PATCHED=8')
