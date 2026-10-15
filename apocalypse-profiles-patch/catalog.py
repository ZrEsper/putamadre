# Each SurvivorCreator profession is included; optional mod items are checked at runtime.
CAT=[
('blacksmith','Herrero',3,'iron_ingot@3,coal@3,iron_pickaxe','blacksmith_boots'),
('burger_flipper','Trabajador de hamburguesería',2,'cooked_beef@2,bread@2,bowl','brown_shirt_chestplate'),
('burglar','Ladrón',2,'iron_nugget@4,tripwire_hook,shears','gray_jacket_leggings'),
('butcher','Carnicero',3,'cooked_porkchop@2,iron_axe,bone@2','brown_shirt_chestplate'),
('carpenter','Carpintero',4,'oak_planks@8,stick@4,iron_axe','brown_shirt_chestplate'),
('chef','Cocinero',3,'baked_potato@3,bread@2,bowl','simple_chestplate'),
('construction_worker','Constructor',5,'cobblestone@12,bricks@6,iron_shovel','orange_jacket_leggings'),
('delivery_worker','Repartidor',3,'paper@3,bread@2,string@2','blue_jacket_chestplate'),
('diy_expert','Manitas',3,'iron_nugget@5,redstone@3,lever','brown_shirt_chestplate'),
('doctor','Médico',3,'firstaid:bandage@2,firstaid:plaster@2,firstaid:prophylactic_antibiotic','doctor_helmet'),
('electrician','Electricista',3,'redstone@5,copper_ingot@3,lever','orange_jacket_leggings'),
('engineer','Ingeniero',3,'redstone@4,iron_ingot@2,piston','strict_chestplate'),
('farmer','Agricultor',5,'wheat_seeds@6,carrot@3,iron_hoe','brown_shirt_chestplate'),
('fire_officer','Bombero',2,'iron_axe,bucket,firstaid:bandage','orange_jacket_leggings'),
('fisherman','Pescador',4,'fishing_rod,cooked_cod@3,string@3','brown_shirt_chestplate'),
('fishing_guide','Guía de pesca',2,'fishing_rod,cooked_salmon@3,compass','blue_jacket_chestplate'),
('fitness_instructor','Entrenador físico',2,'cooked_chicken@2,water_bucket,leather','simple_chestplate'),
('hunter','Cazador',3,'bow,arrow@8,cooked_rabbit@2','stalker_leggings'),
('lumberjack','Leñador',4,'iron_axe,oak_log@6,stick@3','brown_shirt_chestplate'),
('maintenance_worker','Personal de mantenimiento',4,'iron_nugget@5,iron_pickaxe,lever','gray_jacket_leggings'),
('mechanic','Mecánico',5,'iron_ingot@3,copper_ingot@3,iron_pickaxe','gray_jacket_leggings'),
('musician','Músico',2,'note_block,string@4,paper@2','strict_chestplate'),
('nurse','Enfermero',3,'firstaid:plaster@2,firstaid:bandage@2,firstaid:painkillers','doctor_helmet'),
('paramedic','Paramédico',2,'firstaid:bandage@2,firstaid:adrenaline_injector,firstaid:prophylactic_antibiotic','doctor_helmet'),
('park_ranger','Guardabosques',3,'compass,iron_axe,bread@3','stalker_leggings'),
('police_officer','Policía',3,'iron_sword,firstaid:bandage,paper@2','police_boots'),
('rancher','Ganadero',3,'lead,cooked_beef@3,wheat@4','brown_shirt_chestplate'),
('scout','Explorador',3,'compass,torch@6,bread@2','stalker_leggings'),
('scrap_worker','Chatarrero',4,'iron_nugget@8,copper_ingot@3,flint@2','gray_jacket_leggings'),
('security_guard','Guardia de seguridad',3,'iron_sword,torch@4,firstaid:plaster','strict_chestplate'),
('supply_runner','Recolector de suministros',4,'bread@3,torch@5,firstaid:plaster@2','survivors_armor_chestplate'),
('tailor','Sastre',3,'shears,string@6,leather@3','simple_chestplate'),
('unemployed','Civil / desempleado',20,'bread,paper@2,stick@2','brown_shirt_chestplate'),
('veteran','Veterano',2,'iron_sword,firstaid:bandage,iron_ingot@3','stalker_leggings'),
('welder','Soldador',3,'iron_ingot@4,copper_ingot@3,coal@3','orange_jacket_leggings'),
('military','Militar',3,'firstaid:bandage@2,iron_sword,iron_ingot@4','survivors_armor_chestplate'),
('nude','Desnudo',1,'rotten_flesh','')]

def generate(root):
 import json,zipfile,pathlib
 base=next((root/'base').glob('survivorcreator*'));z=zipfile.ZipFile(base)
 actual={pathlib.PurePosixPath(n).stem for n in z.namelist() if '/professions/' in n and n.endswith('.json')}
 assert actual=={x[0] for x in CAT}-{'military','nude'}
 rows='\n'.join('  '+json.dumps(i)+',' for i,_,_,_,_ in CAT)
 labels='\n'.join('  '+json.dumps(label,ensure_ascii=False)+',' for _,label,_,_,_ in CAT)
 weights=','.join(str(w) for _,_,w,_,_ in CAT)
 pools='\n'.join('  '+json.dumps(loot)+',' for _,_,_,loot,_ in CAT)
 clothes='\n'.join('  '+json.dumps('selfexpression:'+c if c else '')+',' for _,_,_,_,c in CAT)
 (root/'src/dev/zomboid/hordes/ProfessionCatalog.java').write_text('package dev.zomboid.hordes;\npublic final class ProfessionCatalog {\n public static final String[] IDS={\n'+rows+'};\n public static final String[] LABELS={\n'+labels+'};\n public static final double[] WEIGHTS={'+weights+'};\n public static final String[] LOOT={\n'+pools+'};\n public static final String[] CLOTHES={\n'+clothes+'};\n}\n')
 (root/'professions.json').write_text(json.dumps([dict(id=i,name=label,weight=w,loot=loot.split(','),clothes=c) for i,label,w,loot,c in CAT],indent=2,ensure_ascii=False)+'\n')
if __name__=='__main__':
 import pathlib
 generate(pathlib.Path(__file__).resolve().parent)
