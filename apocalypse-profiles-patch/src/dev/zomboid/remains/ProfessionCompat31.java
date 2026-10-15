package dev.zomboid.remains;
public final class ProfessionCompat31{
 public static void corpse(Object corpse,Object world,Object event){try{Class.forName("dev.zomboid.hordes.CorpseProfessions").getMethod("corpse",Object.class,Object.class,Object.class).invoke(null,corpse,world,event);}catch(ClassNotFoundException e){try{ProfileCompat.class.getMethod("originalCorpse31",Object.class,Object.class,Object.class).invoke(null,corpse,world,event);}catch(ReflectiveOperationException x){throw new IllegalStateException(x);}}catch(ReflectiveOperationException e){System.getLogger("Apocalypse31").log(System.Logger.Level.ERROR,"No se pudo aplicar profesion del cadaver; inventario conservado",e);}}
}
