package dev.zomboid.scavenging;
public final class VehicleHud {
 static boolean warned;
 public static void render(Object event){try{Class<?> helper;try{helper=Class.forName("dev.zomboid.mts.VehicleInteraction");}catch(ClassNotFoundException absent){return;}R.call(helper,"hint",R.call(event,"getGuiGraphics"));}catch(Throwable problem){if(!warned){warned=true;System.err.println("[Quietly] Vehicle interaction HUD failed: "+problem);}}}
}
