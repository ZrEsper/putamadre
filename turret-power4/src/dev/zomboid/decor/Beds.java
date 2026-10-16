package dev.zomboid.decor;
import java.util.*;
public final class Beds {
 static final ThreadLocal<Use> SLEEP=new ThreadLocal<>();record Use(Object player,Object pos){}
 public static void spawn(Object event){Use use=SLEEP.get();if(use!=null&&R.call(event,"getEntity")==use.player&&use.pos.equals(R.call(event,"getNewSpawn")))R.call(event,"setCanceled",true);}

 public static boolean bed(Object state){String id=Storage.id(state);return id.equals("bed")||id.equals("medical_bed");}
 public static boolean use(Object state,Object world,Object pos,Object player){if(!bed(state))return false;if(R.yes(R.call(world,"isClientSide")))return true;
  if(!R.yes(R.call(R.call(world,"dimensionType"),"bedWorks"))){message(player,"block.minecraft.bed.not_valid");return true;}
  for(Object other:(List<?>)R.call(world,"players")){Object sleeping=R.call(other,"getSleepingPos");if(sleeping instanceof Optional<?> o&&o.isPresent()&&o.get().equals(pos)){message(player,"block.minecraft.bed.occupied");return true;}}
  Use previous=SLEEP.get();SLEEP.set(new Use(player,pos));
  try{Object result=R.call(player,"startSleepInBed",pos);Object left=R.call(result,"left");if(left instanceof Optional<?> o&&o.isPresent()){Object text=R.call(o.get(),"getMessage");if(text!=null)R.call(player,"displayClientMessage",text,true);}}
  finally{if(previous==null)SLEEP.remove();else SLEEP.set(previous);}return true;
 }
 static void message(Object p,String key){R.call(p,"displayClientMessage",R.call(R.type("net.minecraft.network.chat.Component"),"translatable",key,new Object[0]),true);}
}
