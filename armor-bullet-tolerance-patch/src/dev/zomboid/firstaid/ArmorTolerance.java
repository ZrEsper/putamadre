package dev.zomboid.firstaid;
import java.util.*;
public final class ArmorTolerance {
 static final ThreadLocal<Deque<Object>>SOURCES=ThreadLocal.withInitial(ArrayDeque::new);static boolean fallbackLogged;
 static Object c(Object o,String n,Object...a){return R.call(o,n,a);}static boolean yes(Object o){return Boolean.TRUE.equals(o);}public static void enter(Object source){SOURCES.get().push(source);}public static void exit(){Deque<Object>d=SOURCES.get();if(!d.isEmpty())d.pop();if(d.isEmpty())SOURCES.remove();}
 public static boolean bullet(){return yes(((ThreadLocal<?>)R.get(R.type("ichttt.mods.firstaid.survival.ClinicalHooks"),"BULLET")).get());}
 public static float restoreArmorDamage(float reduced,float raw){return bullet()?raw:reduced;}
 static float[]reference(){try{Class<?>api=R.type("com.tacz.guns.api.TimelessAPI"),rl=R.type("net.minecraft.resources.ResourceLocation");Object key=c(rl,"parse","tacz:glock_17");Optional<?>index=(Optional<?>)c(api,"getCommonGunIndex",key);Object chosen=index.orElse(null);
   if(chosen==null){List<Map.Entry<?,?>>pistols=new ArrayList<>();for(Object e:(Iterable<?>)c(api,"getAllCommonGunIndex"))if(e instanceof Map.Entry<?,?>entry){String type=String.valueOf(c(entry.getValue(),"getType")).toLowerCase(Locale.ROOT);if(type.contains("pistol")||type.contains("handgun"))pistols.add(entry);}pistols.sort(Comparator.comparing(e->e.getKey().toString()));if(!pistols.isEmpty())chosen=pistols.get(0).getValue();}
   if(chosen!=null){Object b=c(c(chosen,"getGunData"),"getBulletData");float damage=((Number)c(b,"getDamageAmount")).floatValue(),head=((Number)c(c(b,"getExtraDamage"),"getHeadShotMultiplier")).floatValue();if(Float.isFinite(damage)&&damage>0&&Float.isFinite(head)&&head>0)return new float[]{damage,head};}
  }catch(RuntimeException e){if(!fallbackLogged){fallbackLogged=true;System.getLogger("ArmorTolerance").log(System.Logger.Level.WARNING,"No se pudo leer pistola de referencia; usando 5 de dano y multiplicador de cabeza 2.",e);}}return new float[]{5,2};}
 public static double targetShots(String part,float max,float pistol,double armor){double base=part.equals("HEAD")?8:part.equals("BODY")?15:max/Math.max(.001,pistol*.4);return Math.max(base,2*Math.max(0,armor));}
 public static float healthDamage(String part,float max,float raw,float pistol,float head,double armor){if(!Float.isFinite(raw)||raw<=0||max<=0)return 0;double relative=raw/(Math.max(.001,pistol)*(part.equals("HEAD")?Math.max(.001,head):1));double damage=max*relative/targetShots(part,max,pistol,armor);return (float)Math.min(Float.MAX_VALUE,damage*(1+1.e-6));}
 static boolean bypass(){Deque<Object>d=SOURCES.get();if(d.isEmpty())return false;return yes(c(d.peek(),"is",R.get(R.type("net.minecraft.tags.DamageTypeTags"),"BYPASSES_ARMOR")));}
 public static float damage(Object part,float raw,Object player){if(!bullet())return BallisticHealth.damage(part,raw);String name=String.valueOf(R.get(part,"part"));float max=((Number)c(part,"getMaxHealth")).floatValue();double armor=0;
  if(player!=null&&!bypass()){Object slot=R.get(R.get(part,"part"),"slot"),stack=c(player,"getItemBySlot",slot);if(!yes(c(stack,"isEmpty")))armor=Math.max(0,((Number)c(R.type("ichttt.mods.firstaid.common.util.ArmorUtils"),"getArmor",stack,slot)).doubleValue());}
  float[]ref=reference();return healthDamage(name,max,raw,ref[0],ref[1],armor);
 }
 public static float leftover(float healthLeft,float input,float applied){if(!bullet())return healthLeft;if(applied<=0||input<=0)return 0;return Math.min(input,Math.max(0,healthLeft)*input/applied);}
}
