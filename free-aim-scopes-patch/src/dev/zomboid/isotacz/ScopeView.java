package dev.zomboid.isotacz;
/** Changes orthographic projection scale only, never camera position or cursor ray bias. */
public final class ScopeView {
    public static float scale(float base) {
        try {
            Object mc=Bridge.mc(),player=R.get(mc,"player");
            if(!Bridge.active()||player==null||R.get(mc,"screen")!=null||!Bridge.gun(player))return base;
            Object stack=R.call(player,"getMainHandItem"),gun=R.call(R.type("com.tacz.guns.api.item.IGun"),"getIGunOrNull",stack);
            Object op=R.call(R.type("com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator"),"fromLocalPlayer",player);
            Object camera=R.call(R.get(mc,"gameRenderer"),"getMainCamera");
            double progress=R.num(R.call(op,"getClientAimingProgress",(float)R.num(R.call(camera,"getPartialTickTime"))));
            double factor=dev.zomboid.tacz.ScopeZoom.effective(R.num(R.call(gun,"getAimingZoom",stack)),progress);
            return (float)(base/factor);
        }catch(Throwable e){Bridge.warn("orthographic scope magnification",e);return base;}
    }
}
