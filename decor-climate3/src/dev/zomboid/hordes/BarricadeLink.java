package dev.zomboid.hordes;
public final class BarricadeLink {
 static boolean reported;
 public static int ticks(int original,Object world,Object pos){try{double f=((Number)Class.forName("dev.zomboid.decor.Boards").getMethod("factor",Object.class,Object.class).invoke(null,world,pos)).doubleValue();return f>1?(int)Math.min(Integer.MAX_VALUE,Math.ceil(original*f)):original;}catch(ClassNotFoundException absent){return original;}catch(ReflectiveOperationException|LinkageError error){if(!reported){reported=true;System.err.println("[Doomsday boards] Bridge failed: "+error);}return original;}}
}
