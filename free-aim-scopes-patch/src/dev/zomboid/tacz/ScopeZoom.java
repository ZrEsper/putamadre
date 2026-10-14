package dev.zomboid.tacz;
/** Optical magnification uses the selected attachment zoom, including variable scopes. */
public final class ScopeZoom {
    public static double magnification(double declared) {
        return !Double.isFinite(declared)||declared<=1.05?1:Math.min(32,declared*1.35);
    }
    public static double effective(double declared,double progress) {
        if(!Double.isFinite(progress))progress=0;
        return 1+(magnification(declared)-1)*Math.max(0,Math.min(1,progress));
    }
    public static double fov(double base,double zoom,double progress) {
        if(!Double.isFinite(base)||base<=0||base>=180)return base;
        if(!Double.isFinite(zoom)||zoom<1)zoom=1;
        if(!Double.isFinite(progress))progress=0;
        return Math.toDegrees(2*Math.atan(Math.tan(Math.toRadians(base)*.5)/(1+(zoom-1)*Math.max(0,Math.min(1,progress)))));
    }
    public static void apply(Object event) {if(!Bridge.isoActive())applyWorld(event);}
    public static void applyIso(Object event) {
        if(!Bridge.isoActive())return;
        try {
            Object config=R.call(R.get(R.type("com.cleannrooster.dungeons_iso.config.Config"),"GSON"),"instance");
            if(!R.yes(R.get(config,"ortho")))applyWorld(event);
        }catch(Throwable e){Bridge.warn("isometric scope magnification",e);}
    }
    private static void applyWorld(Object event) {
        try {
            if(!R.yes(R.call(event,"usedConfiguredFov")))return;
            Object mc=Bridge.mc(),player=R.get(mc,"player");
            if(player==null||R.call(R.call(event,"getCamera"),"getEntity")!=player)return;
            Object stack=R.call(player,"getMainHandItem"),gun=R.call(R.type("com.tacz.guns.api.item.IGun"),"getIGunOrNull",stack);
            if(gun==null)return;
            Object operator=R.call(R.type("com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator"),"fromLocalPlayer",player);
            double progress=R.num(R.call(operator,"getClientAimingProgress",(float)R.num(R.call(event,"getPartialTick"))));
            double zoom=magnification(R.num(R.call(gun,"getAimingZoom",stack)));
            R.call(event,"setFOV",fov(R.num(R.call(event,"getFOV")),zoom,progress));
        }catch(Throwable e){Bridge.warn("scope magnification",e);}
    }
}
