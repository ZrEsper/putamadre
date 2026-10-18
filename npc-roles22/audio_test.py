from pathlib import Path
import subprocess,numpy as np,json,hashlib,zipfile
r=Path(__file__).resolve().parent;manifest=json.loads((r/'audio-manifest.json').read_text());info=json.loads((r/'validation.json').read_text());checks=0;rows=[]
assert hashlib.sha256((r/'base'/manifest['source']).read_bytes()).hexdigest()==manifest['source_sha256'];checks+=1
with zipfile.ZipFile(r/'output'/info['file'])as jar:
 sounds=json.loads(jar.read('assets/tacznpcs/sounds.json'))
 for state,count in [('detect',2),('search',2),('patrol',3)]:assert len(sounds['military_radio_'+state]['sounds'])==count;checks+=1
 for clip in manifest['clips']:
  p=r/'audio'/(clip['name']+'.ogg');meta=json.loads(subprocess.check_output(['ffprobe','-v','error','-show_entries','format=duration:stream=channels,sample_rate','-of','json',str(p)]));assert meta['streams'][0]['channels']==1;assert meta['streams'][0]['sample_rate']=='44100';assert .7<float(meta['format']['duration'])<3;checks+=3
  raw=subprocess.check_output(['ffmpeg','-v','error','-i',str(p),'-f','f32le','-ac','1','-']);samples=np.frombuffer(raw,dtype='f4');peak=float(np.max(abs(samples)));rms=float(np.sqrt(np.mean(samples*samples)));assert .015<rms<.25;assert peak<.95;checks+=2
  assert hashlib.sha256(jar.read('assets/tacznpcs/sounds/military22/'+p.name)).hexdigest()==clip['sha256'];checks+=1
  rows.append({'clip':p.name,'duration':float(meta['format']['duration']),'rms':rms,'peak':peak})
(r/'audio-test-results.json').write_text(json.dumps(dict(checks=checks,mono_positional_audio=True,source_hash_verified=True,results=rows,human_listening_verified=False,minecraft_runtime_tested=False),indent=2)+'\n');print('MONO_RADIO_CLIPS_DURATION_LEVEL_NO_CLIPPING_SOUND_ASSET_HASH_CHECKS='+str(checks))
