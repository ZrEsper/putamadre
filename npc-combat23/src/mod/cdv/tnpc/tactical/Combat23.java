package mod.cdv.tnpc.tactical;
/** Explicit hostile roles retain native TacZ firing; civilian idle states cannot suppress combat. */
public final class Combat23 {
 public static boolean engaged(Object n){try{return Roles22.hostile(n)&&!Social.client(n)&&!Downed20.down(n)&&!Social.pause(n)&&CalmBrain.alive(CalmBrain.state(n).target);}catch(Exception e){return false;}}
 public static boolean vision(boolean los,Object n,Object target){if(!los)return false;try{if(!Roles22.hostile(n))return false;Object effect=Reflect.field(Reflect.type("net.minecraft.world.effect.MobEffects"),"NIGHT_VISION");return Reflect.yes(Reflect.call(n,"hasEffect",effect))||CalmLoot.id(Reflect.call(n,"getOffhandItem")).equals("flashlight:flashlight");}catch(Exception e){CalmBrain.error("combat23 vision",e);return false;}}
 public static void prepare(Object n){if(!engaged(n))return;try{Object m=TeamBrain.mem(n);for(String key:new String[]{"tidying","sheltering","fleeing"}){var f=m.getClass().getDeclaredField(key);f.setAccessible(true);f.setBoolean(m,false);}}catch(Exception e){CalmBrain.error("combat23 priority",e);}}
}
