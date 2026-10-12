package dev.zomboid.isotacz;
public final class NativeActions {
 public static boolean riding(Object client){Object player=R.get(client,"player");if(player==null)return false;if(R.yes(R.call(player,"isPassenger")))return true;try{Object wrapper=R.call(R.get(R.type("minecrafttransportsimulator.mcinterface.InterfaceManager"),"clientInterface"),"getClientPlayer");return wrapper!=null&&R.call(wrapper,"getEntityRiding")!=null;}catch(IllegalStateException missing){Throwable cause=missing;while(cause.getCause()!=null)cause=cause.getCause();if(cause instanceof ClassNotFoundException)return false;throw missing;}}
 public static boolean food(Object client){Object player=R.get(client,"player");if(player==null)return false;Object hands=R.type("net.minecraft.world.InteractionHand");for(String name:new String[]{"MAIN_HAND","OFF_HAND"}){Object stack=R.call(player,"getItemInHand",R.get(hands,name));if(R.yes(R.call(stack,"isEmpty")))continue;String animation=R.call(stack,"getUseAnimation").toString();if(animation.equals("EAT")||animation.equals("DRINK"))return true;}return false;}
 public static boolean riding(){return riding(Bridge.mc());}
 public static boolean nativeInput(){Object client=Bridge.mc();boolean nativeAction=riding(client)||food(client);if(nativeAction){Bridge.mouseClaimed=false;Bridge.rightAim=false;}return nativeAction;}
}
