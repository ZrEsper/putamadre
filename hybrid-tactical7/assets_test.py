from pathlib import Path
import zipfile,json,hashlib,subprocess,io
from PIL import Image,ImageStat
r=Path(__file__).resolve().parent;v=json.loads((r/'validation.json').read_text());checks=[]
with zipfile.ZipFile(r/'output'/v['output'])as z:
 model=json.loads(z.read('assets/fracturepoint/models/item/hybrid_thermal_goggles.json'))
 assert 'loader' not in model and model.get('parent')!='builtin/entity' and len(model['elements'])==14
 assert all(len(e['faces'])==6 and all(0<=v<=16 for v in e['from']+e['to'])for e in model['elements'])
 checks.append('BAKED_INVENTORY_MODEL_RESOLVES_WITHOUT_CUSTOM_ARMOR_RENDERER')
 assert len([e for e in model['elements'] if e['faces']['north']['texture']=='#material2'])==4
 checks.append('FOUR_INVENTORY_LENSES')
 png=z.read('assets/fracturepoint/textures/item/hybrid_tactical_atlas.png');im=Image.open(io.BytesIO(png)).convert('RGB');w,h=im.size
 colors=[ImageStat.Stat(im.crop((x*w//2,y*h//2,(x+1)*w//2,(y+1)*h//2))).mean for y in range(2) for x in range(2)]
 metal,olive,dark,cyan=colors
 assert max(metal)<100 and olive[1]>olive[2] and dark[2]>dark[0] and cyan[1]>150 and cyan[2]>150 and sum(cyan)>sum(dark)*3
 checks.append('GENERATED_ATLAS_MATERIAL_AND_POWERED_GLASS_CONTRAST')
 # Do not stitch the standalone non-power-of-two atlas into the block atlas.
 assert all(not value.startswith('fracturepoint:item/hybrid_tactical_atlas') for value in model['textures'].values())
 checks.append('NO_TERRAIN_MIP_LEVEL_REDUCTION')
 sounds=json.loads(z.read('assets/fracturepoint/sounds.json'))
 for name,seconds in [('nvg_tactical_on',1.4),('nvg_tactical_off',.94)]:
  assert sounds[name]['sounds'][0]['name']=='fracturepoint:'+name
  p=r/'build'/f'{name}.ogg';p.write_bytes(z.read('assets/fracturepoint/sounds/'+name+'.ogg'))
  d=json.loads(subprocess.check_output(['ffprobe','-v','error','-show_streams','-show_format','-of','json',str(p)]))
  assert abs(float(d['format']['duration'])-seconds)<.08 and d['streams'][0]['channels']==1 and d['streams'][0]['codec_name']=='vorbis'
  checks.append(name.upper()+'_MONO_VORBIS_AND_TRIM_DURATION')
 for lang in ['es_es','es_mx','en_us','de_de']:
  d=json.loads(z.read('assets/fracturepoint/lang/'+lang+'.json'));assert all(d.get(key) for key in ['item.fracturepoint.hybrid_thermal_goggles','tooltip.fracturepoint.hybrid.mode','tooltip.fracturepoint.hybrid.installed','tooltip.fracturepoint.goggles.requirements'])
  checks.append('TRANSLATION_'+lang)
 assert z.testzip()is None
provenance=json.loads((r/'assets/provenance.json').read_text())
for name,sha in provenance['asset_sha256'].items():assert hashlib.sha256((r/'assets'/name).read_bytes()).hexdigest()==sha
(r/'asset-test-results.json').write_text(json.dumps({'checks':checks,'atlas_material_average_rgb':colors,'minecraft_runtime_tested':False},indent=2)+'\n');print('TACTICAL_ASSET_CHECKS='+str(len(checks)))
