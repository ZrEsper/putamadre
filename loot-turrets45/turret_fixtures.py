from pathlib import Path
r=Path(__file__).resolve().parent;root=r.parent
fixture=root/'turret-power4/tests/fixtures.py';exec(compile(fixture.read_text(),str(fixture),'exec'),{'__file__':str(r/'tests/fixtures.py')})
f=r/'build/fixtures-src'
sources={
'com/entropy/tacz_turrets/turret/TurretEntity.java':'''package com.entropy.tacz_turrets.turret;import dev.zomboid.turretpower.TierChecks.*;public class TurretEntity extends dev.zomboid.turretpower.Checks.T{public static final Object ZH_TIER4=new Object();public Data data=new Data();public double health=20;public java.util.Map<Object,Attribute> attributes=new java.util.HashMap<>();public TurretEntity(dev.zomboid.turretpower.Checks.W w){super(w);attributes.put("health",new Attribute(20));attributes.put("range",new Attribute(20));attributes.put("armor",new Attribute(2));}public Data getEntityData(){return data;}public Attribute getAttribute(Object a){return attributes.get(a);}public float getMaxHealth(){return(float)attributes.get("health").getBaseValue();}public float getHealth(){return(float)health;}public void setHealth(float h){health=h;}}''',
'net/minecraft/world/entity/ai/attributes/Attributes.java':'''package net.minecraft.world.entity.ai.attributes;public class Attributes{public static final Object MAX_HEALTH="health",FOLLOW_RANGE="range",ARMOR="armor";}''',
'com/entropy/tacz_turrets/config/TACZTurretsConfig.java':'''package com.entropy.tacz_turrets.config;public class TACZTurretsConfig{public static final Value TURRET_ARMOR=new Value();public static class Value{public Double get(){return 2.;}}}''',
'com/entropy/tacz_turrets/item/TurretItem.java':'package com.entropy.tacz_turrets.item;public class TurretItem{}',
'com/entropy/tacz_turrets/registry/ItemRegistry.java':'package com.entropy.tacz_turrets.registry;public class ItemRegistry{public static final java.util.function.Supplier<Object> TURRET=()->"base";}',
'net/minecraft/core/registries/Registries.java':'package net.minecraft.core.registries;public class Registries{public static final Object ITEM="item",SOUND_EVENT="sound";}',
'net/minecraft/core/registries/BuiltInRegistries.java':'package net.minecraft.core.registries;public class BuiltInRegistries{public static final Registry ITEM=new Registry(),BLOCK=new Registry(),ENTITY_TYPE=new Registry();public static class Registry{public Object getKey(Object value){return value;}}}',
'net/minecraft/resources/ResourceLocation.java':'package net.minecraft.resources;public record ResourceLocation(String id){public static ResourceLocation fromNamespaceAndPath(String n,String p){return new ResourceLocation(n+":"+p);}public String toString(){return id;}}',
'com/entropy/tacz_turrets/turret/TurretModelType.java':'package com.entropy.tacz_turrets.turret;public enum TurretModelType{INDUSTRIAL("industrial"),TACTICAL("tactical");String label;TurretModelType(String s){label=s;}public Object model(){return label+"-model";}public Object texture(Object state){return label+"-active";}}',
'com/entropy/tacz_turrets/turret/TurretState.java':'package com.entropy.tacz_turrets.turret;public class TurretState{public static Object getState(Object t){return "active";}}',
}
for n,s in sources.items():p=f/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s+'\n')
