from pathlib import Path
r=Path(__file__).resolve().parent.parent
old=r.parent/'turret-power4'
import shutil
shutil.rmtree(r/'build/fixtures-src',ignore_errors=True)
# Shared public fixture shapes; no previous production output is modified.
exec(compile((old/'tests/fixtures.py').read_text(),__file__,'exec'),{'__file__':__file__})
s=(old/'tests/dev/zomboid/turretpower/Checks.java').read_text().split(' public static void main')[0]+'\n}\n';s=s.replace('public int invulnerableTime;','public int invulnerableTime;public float yaw,pitch;public float getYHeadRot(){return yaw;}public float getXRot(){return pitch;}');p=r/'build/fixtures-src/dev/zomboid/turretpower/Checks.java';p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
sources={
'net/minecraft/world/item/Item.java':'''package net.minecraft.world.item;public class Item{public Properties props;public Item(Properties p){props=p;}public static class Properties{public int max;public Properties stacksTo(int n){max=n;return this;}}}''',
'net/minecraft/world/item/ItemStack.java':'''package net.minecraft.world.item;public class ItemStack{public Item item;public int count;public ItemStack(Item item,int n){this.item=item;count=n;}public boolean isEmpty(){return item==null||count<=0;}}''',
'net/neoforged/neoforge/registries/RegisterEvent.java':'''package net.neoforged.neoforge.registries;import java.util.*;public class RegisterEvent{public Object key;public Map<Object,Object> registered=new HashMap<>();public RegisterEvent(Object key){this.key=key;}public Object getRegistryKey(){return key;}public void register(Object k,Object id,java.util.function.Supplier<?> make){if(k!=key||registered.put(id,make.get())!=null)throw new AssertionError("wrong/duplicate registry");}}''',
'net/minecraft/core/registries/Registries.java':'''package net.minecraft.core.registries;public class Registries{public static final Object SOUND_EVENT=new Object(),ITEM=new Object();}''',
'com/tacz/guns/init/ModCreativeTabs.java':'''package com.tacz.guns.init;public class ModCreativeTabs{public static final Tab OTHER_TAB=new Tab();public static class Tab{public Object getKey(){return this;}}}''',
'dev/zomboid/hordes/WorldTuning.java':'''package dev.zomboid.hordes;import java.util.*;public class WorldTuning{public static class Defaults extends HashMap<String,Double>{public Defaults(){clear();}public void clear(){super.clear();for(String p:new String[]{"cofres","cadaveres"}){put("panel_"+p+"_pilas",1.0);put(p+"_cantidad",1.0);put(p+"_materiales",1.0);}}}public static Map<String,Double> data=new Defaults();public static Map<String,Double> snapshot(){return data;}}''',
'dev/zomboid/hordes/CorpseBalance35.java':'''package dev.zomboid.hordes;public class CorpseBalance35{public static int calls;public static void body(Object c,Object w,Object e){calls++;}}''',
'dev/zomboid/hordes/SimpleLoot.java':'''package dev.zomboid.hordes;public class SimpleLoot{public static int calls;public static Object container(Object original,Object block,Object params){calls++;return original;}}'''
}
for n,s in sources.items():p=r/'build/fixtures-src'/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
