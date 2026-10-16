package dev.survivorcreator.spawn;
public final class StarterPower34 {
 public static void equip(Object player){try{Class.forName("net.flashlight.battery.Power").getMethod("starter",Object.class).invoke(null,player);}catch(ClassNotFoundException missing){System.err.println("[SurvivorCreator] Flashlight battery mod absent; starter inventory retained");}catch(ReflectiveOperationException error){throw new IllegalStateException("Starter flashlight",error);}}
}
