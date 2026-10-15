package dev.survivorcreator.skills;
public final class LevelHeal31{
 public static void levelUp(Object player){try{Class.forName("dev.zomboid.hordes.ApocalypseSettings").getMethod("levelUp",Object.class).invoke(null,player);}catch(ClassNotFoundException e){}catch(ReflectiveOperationException e){System.getLogger("Apocalypse31").log(System.Logger.Level.ERROR,"No se pudo aplicar curacion de nivel",e);}}
}
