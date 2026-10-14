package mod.chloeprime.thirdpersonshooting.client;
public final class FreeCamera {
    public static boolean isIso() {
        try{return Class.forName("com.cleannrooster.dungeons_iso.mod.Mod").getField("enabled").getBoolean(null);}
        catch(ReflectiveOperationException|LinkageError e){return false;}
    }
    public static boolean shoulder(Object api) {
        if(isIso())return false;
        try{return Boolean.TRUE.equals(api.getClass().getMethod("isShoulderSurfing").invoke(api));}
        catch(ReflectiveOperationException e){return false;}
    }
}
