from pathlib import Path
import subprocess,json,hashlib
r=Path(__file__).resolve().parent;source=r/'base/hecu-radio.ogg';clips=[
 ('detect_1','detect',[(65.65,66.65)]),('detect_2','detect',[(66.76,68.04)]),
 ('search_1','search',[(264.04,264.94),(269.74,270.53)]),('search_2','search',[(265.03,265.84),(270.70,271.43)]),
 ('patrol_1','patrol',[(268.14,268.91),(269.74,270.53),(271.68,272.26)]),
 ('patrol_2','patrol',[(190.10,192.23)]),('patrol_3','patrol',[(194.40,196.52)])]
rows=[]
for name,state,cuts in clips:
 filters=[];pieces=[]
 for i,(start,end)in enumerate(cuts):
  duration=end-start;filters.append(f'[0:a]atrim=start={start}:end={end},asetpts=PTS-STARTPTS,afade=t=in:d=0.008,afade=t=out:st={duration-.012}:d=0.012[a{i}]');pieces.append(f'[a{i}]')
  if i<len(cuts)-1:filters.append(f'anullsrc=r=44100:cl=mono,atrim=duration=0.065[g{i}]');pieces.append(f'[g{i}]')
 filters.append(''.join(pieces)+f'concat=n={len(pieces)}:v=0:a=1,loudnorm=I=-20:TP=-2:LRA=8,aresample=44100,pan=mono|c0=0.5*c0+0.5*c1[out]')
 # All concatenated clips are explicitly converted to mono before concatenation below.
 filters=[f.replace(',afade=t=in',',aformat=channel_layouts=mono,afade=t=in')for f in filters]
 filters[-1]=filters[-1].replace(',pan=mono|c0=0.5*c0+0.5*c1','')
 out=r/'audio'/(name+'.ogg');subprocess.run(['ffmpeg','-v','error','-y','-i',str(source),'-filter_complex',';'.join(filters),'-map','[out]','-ac','1','-ar','44100','-c:a','libvorbis','-q:a','4',str(out)],check=True)
 meta=json.loads(subprocess.check_output(['ffprobe','-v','error','-show_entries','format=duration:stream=channels,sample_rate','-of','json',str(out)]));rows.append(dict(name=name,state=state,source_cuts=cuts,joined_radio_words=len(cuts)>1,duration=float(meta['format']['duration']),channels=meta['streams'][0]['channels'],sha256=hashlib.sha256(out.read_bytes()).hexdigest()))
(r/'audio-manifest.json').write_text(json.dumps(dict(source=source.name,source_sha256=hashlib.sha256(source.read_bytes()).hexdigest(),clips=rows,automatic_transcription_review_only=True,human_listening_verified=False),indent=2)+'\n')
print('VOICE_RADIO_MONO_CUTS='+str(len(rows)))
