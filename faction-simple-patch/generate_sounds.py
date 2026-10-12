"""Generate original mono OGG cues; no external sound samples."""
from pathlib import Path
import json,numpy as np,subprocess,tempfile,wave
root=Path(__file__).resolve().parent/'resources/assets/factionfriction';output=root/'sounds';output.mkdir(parents=True,exist_ok=True)
sr=24000;rng=np.random.default_rng(728)
def tone(t,f):return np.sin(2*np.pi*f*t)
def cue(name,length):
 t=np.arange(int(length*sr))/sr
 if name=='generator':
  noise=rng.normal(0,1,len(t));fan=np.convolve(noise,np.ones(9)/9,'same');y=(.16*tone(t,55)+.08*tone(t,110)+.035*tone(t,220))*(.75+.25*tone(t,13))+.035*fan
 elif name=='nexus_hit':y=.4*tone(t,660)*np.exp(-t*3)+.18*tone(t,440)*np.exp(-t*7)+.08*rng.normal(0,1,len(t))*np.exp(-t*20)
 elif name in ('victory','phase','start'):
  notes={'victory':[392,494,587,784],'phase':[330,440,554],'start':[220,330,440]}[name];y=np.zeros(len(t));span=length/len(notes)
  for i,f in enumerate(notes):
   local=t-i*span;mask=(local>=0)&(local<span+.15);y+=np.where(mask,.25*np.sin(2*np.pi*f*local)*np.exp(-np.maximum(local,0)*4),0)
 elif name=='defeat':y=.22*tone(t,180-80*t/length)*np.exp(-t*1.4)+.16*tone(t,130)*np.exp(-t*2)
 elif name=='deposit':y=.3*tone(t,350)*np.exp(-t*18)+.2*tone(t,900)*np.exp(-np.maximum(0,t-.12)*12)*(t>.12)
 else:y=.28*tone(t,650)*np.exp(-t*12)+.18*tone(t,430)*np.exp(-np.maximum(0,t-.15)*14)*(t>.15)
 y*=np.minimum(1,t/.015)*np.minimum(1,(length-t)/.04);return np.clip(y,-.95,.95)
lengths={'generator':3.,'nexus_hit':.7,'phase':1.,'victory':1.5,'defeat':1.5,'deposit':.5,'withdraw':.5,'start':1.2}
with tempfile.TemporaryDirectory() as tmp:
 for name,length in lengths.items():
  wav=Path(tmp)/f'{name}.wav'
  with wave.open(str(wav),'wb') as w:w.setnchannels(1);w.setsampwidth(2);w.setframerate(sr);w.writeframes((cue(name,length)*32767).astype('<i2').tobytes())
  subprocess.run(['ffmpeg','-loglevel','error','-y','-i',str(wav),'-c:a','libvorbis','-q:a','4',str(output/(name+'.ogg'))],check=True)
(root/'sounds.json').write_text(json.dumps({name:{'subtitle':'subtitle.factionfriction.'+name,'sounds':[{'name':'factionfriction:'+name,'stream':False}]} for name in lengths},indent=2)+'\n')
