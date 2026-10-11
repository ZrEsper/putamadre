package dev.zomboid.gundb;
public final class Bridge {
 static Object started,owner;
 static Object mc(){return R.call(R.type("net.minecraft.client.Minecraft"),"getInstance");}
 static boolean jammed(Object stack){return stack!=null&&R.yes(R.call(stack,"getOrDefault",R.get(R.type("mod.cdv.gdb.resource.ModDataComponents"),"JAMMED"),false));}
 public static boolean blockedFlag(){return started!=null;}
 public static void tick(){try{Object player=R.get(mc(),"player");if(player!=owner||player==null){owner=player;started=null;return;}Object stack=R.call(player,"getMainHandItem");if(stack!=started||!jammed(stack))started=null;}catch(Throwable e){R.warn("unjam reset",e);}}
 public static void shot(Object event){try{if(!R.yes(R.call(R.call(event,"getLogicalSide"),"isClient")))return;Object mc=mc(),player=R.get(mc,"player");if(player==null||player!=R.call(event,"getShooter")||R.get(mc,"screen")!=null)return;Object stack=R.call(player,"getMainHandItem");if(!jammed(stack)){started=null;return;}if(player!=owner){owner=player;started=null;}if(started==stack)return;if(R.yes(R.call(R.type("com.tacz.guns.client.input.InspectKey"),"onInspectControllerPress",true))){started=stack;R.set(R.type("mod.cdv.gdb.ClientEvents"),"jamBlocked",true);}}catch(Throwable e){R.warn("left click unjam",e);}}
}
