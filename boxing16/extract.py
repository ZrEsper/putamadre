from pathlib import Path
import zipfile,json,math,hashlib,bisect
r=Path(__file__).resolve().parent;epic=r/'base/epicfight.jar'
# Retarget only the arm chain; no root, hip, torso, camera or locomotion track.
def mul(a,b):return [sum(a[y*4+k]*b[k*4+x]for k in range(4))for y in range(4)for x in range(4)]
def sample(track,t):
 times=track['time'];i=max(0,min(len(times)-2,bisect.bisect_right(times,t)-1));a=track['transform'][i];b=track['transform'][i+1];q=max(0,min(1,(t-times[i])/max(1e-6,times[i+1]-times[i])));return [u+(v-u)*q for u,v in zip(a,b)]
def endpoint(tracks,t,side):
 m=sample(tracks['Shoulder_'+side],t)
 for joint in ['Arm_','Hand_']:m=mul(m,sample(tracks[joint+side],t))
 # Knuckle endpoint along the hand's local Y axis, in arm-chain coordinates.
 return [m[k*4+3]+m[k*4+1]*.272858 for k in range(3)]
with zipfile.ZipFile(epic)as z:
 kept={};curves=[];dominant=[];provenance={}
 for name in ['idle','fist_auto1','fist_auto2']:
  path='assets/epicfight/animmodels/animations/biped/'+('living/'if name=='idle'else'combat/')+name+'.json';data=z.read(path);d=json.loads(data);tracks={x['name']:x for x in d['animation']if x['name'].startswith(('Shoulder_','Arm_','Hand_','Elbow_'))};kept[name]={'animation':list(tracks.values())};provenance[path]=hashlib.sha256(data).hexdigest()
  if name=='idle':continue
  channels=[];peaks=[]
  for side in ['R','L']:
   base=endpoint(tracks,0,side);dist=[math.dist(endpoint(tracks,i/60,side),base) for i in range(31)];peak=max(dist);peaks.append(peak)
   # Keep the source timing; blend its last frame into the permanent guard.
   channel=[round((v/max(1e-6,peak))**1.35*min(1,i/2,(30-i)/8),6)for i,v in enumerate(dist)];channels.append(channel)
  dom=0 if peaks[0]>=peaks[1]else 1;channels[1-dom]=[round(v*.10,6)for v in channels[1-dom]];curves.append(channels);dominant.append(dom)
 (r/'assets/epicfight-arm-tracks.json').write_text(json.dumps(kept,separators=(',',':'))+'\n');(r/'assets/LICENSE-ASSETS-EpicFight.txt').write_text(z.read('LICENSE-ASSETS').decode().replace('\r\n','\n'))
 # Bake the extracted normalized trajectories, removing the runtime dependency.
 table='new double[][][]{'+','.join('{'+','.join('{'+','.join(map(str,ch))+'}'for ch in clip)+'}'for clip in curves)+'}'
 java='''package dev.zomboidmelee.boxing;
/** Arm-only curves retargeted from the user-supplied Epic Fight fist clips. */
public final class Motion16{
 static final double[][][] CURVES=TABLE;
 public static final int[] DOMINANT=DOM;
 public static double value(int combo,int side,double phase){if(phase<=0||phase>=1)return 0;double[]v=CURVES[Math.floorMod(combo,2)][side];double x=phase*(v.length-1);int i=(int)x;return v[i]+(v[i+1]-v[i])*(x-i);}
}
'''.replace('TABLE',table).replace('DOM;', 'new int[]{'+','.join(map(str,dominant))+'};')
 (r/'src/dev/zomboidmelee/boxing/Motion16.java').write_text(java)
 (r/'asset-provenance.json').write_text(json.dumps({'epic_input_sha256':hashlib.sha256(epic.read_bytes()).hexdigest(),'original_tracks_sha256':provenance,'kept_joints':list(tracks),'discarded':'All root/body/leg/locomotion tracks; translations affect only hand render transforms, never entity position.','dominant':dominant},indent=2)+'\n')
 print('ARM_CLIPS_RETARGETED',dominant)
