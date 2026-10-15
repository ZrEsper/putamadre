/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  dev.zomboid.tacz.Bridge
 *  dev.zomboid.tacz.R
 */
package dev.zomboid.tacz;

import dev.zomboid.tacz.Bridge;
import dev.zomboid.tacz.R;

public final class ScopeZoom {
    public static double magnification(double d) {
        return !Double.isFinite(d) || d <= 1.05 ? 1.0 : d;
    }

    public static double effective(double d, double d2) {
        if (!Double.isFinite(d2)) {
            d2 = 0.0;
        }
        return 1.0 + (ScopeZoom.magnification(d) - 1.0) * Math.max(0.0, Math.min(1.0, d2));
    }

    public static double fov(double d, double d2, double d3) {
        if (!Double.isFinite(d) || d <= 0.0 || d >= 180.0) {
            return d;
        }
        if (!Double.isFinite(d2) || d2 < 1.0) {
            d2 = 1.0;
        }
        if (!Double.isFinite(d3)) {
            d3 = 0.0;
        }
        return Math.toDegrees(2.0 * Math.atan(Math.tan(Math.toRadians(d) * 0.5) / (1.0 + (d2 - 1.0) * Math.max(0.0, Math.min(1.0, d3)))));
    }

    public static void apply(Object object) {
        if (!Bridge.isoActive()) {
            ScopeZoom.applyWorld(object);
        }
    }

    public static void applyIso(Object object) {
        if (!Bridge.isoActive()) {
            return;
        }
        try {
            Object object2 = R.call((Object)R.get((Object)R.type((String)"com.cleannrooster.dungeons_iso.config.Config"), (String)"GSON"), (String)"instance", (Object[])new Object[0]);
            if (!R.yes((Object)R.get((Object)object2, (String)"ortho"))) {
                ScopeZoom.applyWorld(object);
            }
        }
        catch (Throwable throwable) {
            Bridge.warn((String)"isometric scope magnification", (Throwable)throwable);
        }
    }

    private static void applyWorld(Object object) {
        try {
            if (!R.yes((Object)R.call((Object)object, (String)"usedConfiguredFov", (Object[])new Object[0]))) {
                return;
            }
            Object object2 = Bridge.mc();
            Object object3 = R.get((Object)object2, (String)"player");
            if (object3 == null || R.call((Object)R.call((Object)object, (String)"getCamera", (Object[])new Object[0]), (String)"getEntity", (Object[])new Object[0]) != object3) {
                return;
            }
            Object object4 = R.call((Object)object3, (String)"getMainHandItem", (Object[])new Object[0]);
            Object object5 = R.call((Object)R.type((String)"com.tacz.guns.api.item.IGun"), (String)"getIGunOrNull", (Object[])new Object[]{object4});
            if (object5 == null) {
                return;
            }
            Object object6 = R.call((Object)R.type((String)"com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator"), (String)"fromLocalPlayer", (Object[])new Object[]{object3});
            double d = R.num((Object)R.call((Object)object6, (String)"getClientAimingProgress", (Object[])new Object[]{Float.valueOf((float)R.num((Object)R.call((Object)object, (String)"getPartialTick", (Object[])new Object[0])))}));
            double d2 = R.num((Object)R.call((Object)object5, (String)"getAimingZoom", (Object[])new Object[]{object4}));
            Object object7 = R.get((Object)object2, (String)"options");
            boolean bl = R.yes((Object)R.call((Object)R.call((Object)object7, (String)"getCameraType", (Object[])new Object[0]), (String)"isFirstPerson", (Object[])new Object[0]));
            if (bl) {
                double d3 = R.num((Object)R.call((Object)R.call((Object)object7, (String)"fov", (Object[])new Object[0]), (String)"get", (Object[])new Object[0]));
                if (Double.isFinite(d2) && d2 >= 1.0 && d > 0.0) {
                    R.call((Object)object, (String)"setFOV", (Object[])new Object[]{ScopeZoom.fov(d3, d2, d)});
                }
            } else {
                R.call((Object)object, (String)"setFOV", (Object[])new Object[]{ScopeZoom.fov(R.num((Object)R.call((Object)object, (String)"getFOV", (Object[])new Object[0])), ScopeZoom.magnification(d2), d)});
            }
        }
        catch (Throwable throwable) {
            Bridge.warn((String)"scope magnification", (Throwable)throwable);
        }
    }
}
