# The inventory model uses built-in mipmapped materials; the custom atlas is a
# standalone mounted-optics texture, so its size never lowers terrain mip levels.
# Concrete baked block model: no builtin/entity renderer or missing inherited icon.
model={'ambientocclusion':False,'gui_light':'front','textures':{'material0':'minecraft:block/netherite_block','material1':'minecraft:block/green_terracotta','material2':'minecraft:block/blue_ice','material3':'minecraft:block/sea_lantern','particle':'minecraft:block/netherite_block'},'elements':[],'display':{'gui':{'rotation':[24,145,0],'translation':[0,0,0],'scale':[.95,.95,.95]},'ground':{'translation':[0,3,0],'scale':[.6,.6,.6]},'fixed':{'rotation':[0,180,0],'scale':[.8,.8,.8]},'thirdperson_righthand':{'rotation':[0,90,0],'translation':[0,2,0],'scale':[.65,.65,.65]},'firstperson_righthand':{'rotation':[0,135,0],'translation':[1,0,0],'scale':[.8,.8,.8]}}}
def cube(a,b,material):
 u=(material%2)*8;v=(material//2)*8
 model['elements'].append({'from':a,'to':b,'faces':{face:{'texture':'#material'+str(material),'uv':[0,0,16,16]}for face in ['north','south','east','west','up','down']}})
cube([1.1,8,8],[14.9,10.3,11.2],0);cube([7.2,10,8.5],[8.8,13.2,11],1)
for center in [2.3,6.1,9.9,13.7]:
 cube([center-1.45,5.5,4],[center+1.45,8.4,10],0)
 cube([center-1.6,5.35,3.8],[center+1.6,8.55,4.8],1)
 cube([center-1.25,5.7,3.75],[center+1.25,8.2,3.8],2)
js('assets/fracturepoint/models/item/hybrid_thermal_goggles.json',model)
e['assets/fracturepoint/textures/item/hybrid_tactical_atlas.png']=(r/'assets/hybrid_tactical_atlas.png').read_bytes()
sounds=json.loads(e.get('assets/fracturepoint/sounds.json',b'{}'))
for name in ['nvg_tactical_on','nvg_tactical_off']:
 sounds[name]={'sounds':[{'name':'fracturepoint:'+name,'stream':False}]}
 e['assets/fracturepoint/sounds/'+name+'.ogg']=(r/('assets/'+name+'.ogg')).read_bytes()
js('assets/fracturepoint/sounds.json',sounds)
