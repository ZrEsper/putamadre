package dev.zomboidmelee.combat;
import java.lang.reflect.*;
/** Optional Hordes integration; defaults to original behavior if Hordes is absent. */
public final class PanelCooldown {
 static boolean warned;
 public static double configured(String key){try{return ((Number)Class.forName("dev.zomboid.hordes.CombatSync").getMethod("multiplier",String.class).invoke(null,key)).doubleValue();}catch(ClassNotFoundException e){return 1;}catch(ReflectiveOperationException|LinkageError e){if(!warned){warned=true;System.err.println("[Melee panel] Could not read synchronized cooldowns: "+e);}return 1;}}
 static Object call(Object object,String method){try{return object.getClass().getMethod(method).invoke(object);}catch(ReflectiveOperationException e){return null;}}
 public static String family(Object stack){if(stack==null)return "otros";Object item=call(stack,"getItem");String text=(String.valueOf(call(stack,"getDescriptionId"))+" "+(item==null?"":item.getClass().getSimpleName())).toLowerCase(java.util.Locale.ROOT);if(text.contains("sword"))return "espadas";if(text.contains("axe")&&!text.contains("pickaxe"))return "hachas";if(text.contains("skillet")||text.contains("frying")||text.contains("sarten"))return "sartenes";return "otros";}
 public static int scaled(int ticks,double factor){return Math.max(1,Math.min(1200,(int)Math.ceil(Math.max(0,ticks)*factor)));}
 public static int attack(int original,boolean heavy,Object stack){return scaled(original,configured(heavy?"melee_pesado":"melee_normal")*configured("melee_"+family(stack)));}
 public static float vanilla(float original,Object player){return (float)Math.max(1,Math.min(1200,original*configured("melee_normal")*configured("melee_"+family(call(player,"getMainHandItem")))));}
 public static int wrap(int original,boolean heavy,Object stack){return attack(CombatRules.cooldown(original,heavy),heavy,stack);}
 public static int delay(int original,boolean heavy,Object stack){return Math.max(0,(int)Math.ceil(CombatRules.delay(original,heavy)*configured(heavy?"melee_pesado":"melee_normal")*configured("melee_"+family(stack))));}
 public static int shove(){return scaled(15,configured("melee_empujon"));}
}
