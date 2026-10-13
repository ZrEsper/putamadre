package dev.zomboid.isotacz;
public class Bridge {
    public static final MC MC=new MC();public static boolean active=true,gun=true,reachable=true;public static int picks;public static Object picked;
    public static Object mc(){return MC;}public static boolean active(){return active;}public static boolean gun(Object player){return gun;}
    public static Object cursorPick(Object mc){picks++;return picked;}
    public static boolean corpse(Object entity){return entity instanceof de.maxhenkel.corpse.entities.CorpseEntity;}
    public static boolean remains(String id){return id.startsWith("doomsday_decoration:remains_")||id.equals("zombie_remains_loot:skeletal_remains");}
    public static String blockId(Object state){return state.toString();}
    public static boolean interactable(Object mc,Object hit){return reachable;}
    public static void warn(String message,Throwable failure){throw new AssertionError(message,failure);}
    public static class MC {public Object player=new Object(),level=new Level(),screen,hitResult,crosshairPickEntity;}
    public static class Level{public Object getBlockState(Object pos){return pos;}}
}
