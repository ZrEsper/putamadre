/*
 * Decompiled with CFR 0.152.
 */
package dev.zomboid.gore;

import dev.zomboid.gore.Collateral18;
import dev.zomboid.gore.Corpse13;
import dev.zomboid.gore.Cuts24;
import dev.zomboid.gore.Damage25;
import dev.zomboid.gore.R;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class Bullet13 {
    static final Map<Object, Set<Integer>> HIT = Collections.synchronizedMap(new WeakHashMap());

    public static boolean tacz(Object object) {
        return object != null && object.getClass().getName().equals("com.tacz.guns.entity.EntityKineticBullet");
    }

    public static double box(double[] dArray, double[] dArray2, double d, double d2, double d3) {
        double d4 = 0.0;
        double d5 = 1.0;
        double[] dArray3 = new double[]{d, d2, d3};
        for (int i = 0; i < 3; ++i) {
            double d6 = dArray2[i] - dArray[i];
            if (Math.abs(d6) < 1.0E-9) {
                if (!(dArray[i] < -dArray3[i]) && !(dArray[i] > dArray3[i])) continue;
                return Double.NaN;
            }
            double d7 = (-dArray3[i] - dArray[i]) / d6;
            double d8 = (dArray3[i] - dArray[i]) / d6;
            if (d7 > d8) {
                double d9 = d7;
                d7 = d8;
                d8 = d9;
            }
            if (!((d4 = Math.max(d4, d7)) > (d5 = Math.min(d5, d8)))) continue;
            return Double.NaN;
        }
        return d4;
    }

    static double[] local(Object object, Object object2) {
        Object object3 = R.get(object2, "position");
        Object object4 = R.get(object2, "rotation");
        double d = R.num(R.get(object, "x")) - R.num(R.get(object3, "x"));
        double d2 = R.num(R.get(object, "y")) - R.num(R.get(object3, "y"));
        double d3 = R.num(R.get(object, "z")) - R.num(R.get(object3, "z"));
        double d4 = R.num(R.get(object4, "x"));
        double d5 = R.num(R.get(object4, "y"));
        double d6 = R.num(R.get(object4, "z"));
        double d7 = R.num(R.get(object4, "w"));
        return new double[]{(1.0 - 2.0 * (d5 * d5 + d6 * d6)) * d + 2.0 * (d4 * d5 + d6 * d7) * d2 + 2.0 * (d4 * d6 - d5 * d7) * d3, 2.0 * (d4 * d5 - d6 * d7) * d + (1.0 - 2.0 * (d4 * d4 + d6 * d6)) * d2 + 2.0 * (d5 * d6 + d4 * d7) * d3, 2.0 * (d4 * d6 + d5 * d7) * d + 2.0 * (d5 * d6 - d4 * d7) * d2 + (1.0 - 2.0 * (d4 * d4 + d5 * d5)) * d3};
    }

    public static boolean tick(Object object2) {
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        Object object7 = R.call(object2, "level", new Object[0]);
        if (R.yes(R.get(object7, "isClientSide"))) {
            return false;
        }
        if (((Map)R.get(R.type("com.raiiiden.ragdollified.server.ServerRagdollSyncManager"), "RETAINED")).isEmpty()) {
            return false;
        }
        Object object8 = R.call(object2, "getDeltaMovement", new Object[0]);
        double d = R.num(R.call(object8, "length", new Object[0]));
        if (d < 0.05) {
            return false;
        }
        Object object9 = R.call(object2, "position", new Object[0]);
        Object object10 = R.make("net.minecraft.world.level.ClipContext", object9, object6 = R.call(object9, "add", object8), R.get(R.type("net.minecraft.world.level.ClipContext$Block"), "COLLIDER"), R.get(R.type("net.minecraft.world.level.ClipContext$Fluid"), "NONE"), object2);
        Object object11 = R.call(object7, "clip", object10);
        if (R.call(object11, "getType", new Object[0]).toString().equals("BLOCK")) {
            object6 = R.call(object11, "getLocation", new Object[0]);
        }
        Set set = HIT.computeIfAbsent(object2, object -> new HashSet());
        double d2 = Double.POSITIVE_INFINITY;
        int n = -1;
        int n2 = -1;
        for (Object t : (Iterable)R.call(R.type("com.raiiiden.ragdollified.server.ServerRagdollSyncManager"), "retainedBodiesNear", R.call(object7, "dimension", new Object[0]), object9, d + 2.0)) {
            double d3;
            double[] dArray;
            double[] dArray2;
            Object object12;
            int n3 = ((Number)R.call(t, "entityId", new Object[0])).intValue();
            if (set.contains(n3) || R.yes(R.call(t, "live", new Object[0])) || (object5 = Corpse13.retained(n3)) == null || !Corpse13.zombie(object4 = R.get(object5, "spawnPacket"))) continue;
            int n4 = ((Number)R.call(object4, "severedMask", new Object[0])).intValue() | Damage25.mask(Cuts24.field(object4));
            object3 = R.call(t, "pose", new Object[0]);
            if (object3 != null) {
                R.set(object5, "gorePose13", object3);
            } else {
                object3 = R.get(object5, "gorePose13");
            }
            if (object3 instanceof Object[]) {
                object12 = (Object[])object3;
                for (int i = 0; i < Math.min(6, ((Object[])object12).length); ++i) {
                    if (((Object[])object12)[i] == null || (n4 & 1 << i) != 0) continue;
                    dArray2 = Bullet13.local(object9, ((Object[])object12)[i]);
                    d3 = Ray45.trace(dArray2, dArray = Bullet13.local(object6, ((Object[])object12)[i]), i, object4);
                    if (!Double.isFinite(d3) || !(d3 < d2)) continue;
                    d2 = d3;
                    n = n3;
                    n2 = i;
                }
                continue;
            }
            object12 = R.call(t, "anchor", new Object[0]);
            dArray2 = new double[]{R.num(R.get(object9, "x")) - R.num(R.get(object12, "x")), R.num(R.get(object9, "y")) - R.num(R.get(object12, "y")) - 0.95, R.num(R.get(object9, "z")) - R.num(R.get(object12, "z"))};
            d3 = Bullet13.box(dArray2, dArray = new double[]{R.num(R.get(object6, "x")) - R.num(R.get(object12, "x")), R.num(R.get(object6, "y")) - R.num(R.get(object12, "y")) - 0.95, R.num(R.get(object6, "z")) - R.num(R.get(object12, "z"))}, 0.4, 0.95, 0.28);
            if (!Double.isFinite(d3) || !(d3 < d2)) continue;
            d2 = d3;
            n = n3;
            double d4 = dArray2[1] + (dArray[1] - dArray2[1]) * d3 + 0.95;
            n2 = d4 > 1.5 ? 1 : 0;
        }
        if (n < 0) {
            return false;
        }
        Object object13 = R.call(R.type("com.tacz.guns.util.EntityUtil"), "findEntityOnPath", object2, object9, object6);
        double d5 = object13 == null ? Double.POSITIVE_INFINITY : R.num(R.call(object9, "distanceToSqr", R.call(object13, "getHitPos", new Object[0])));
        object5 = R.call(object9, "add", R.call(R.call(object6, "subtract", object9), "scale", d2));
        Object[] objectArray = new Object[]{object5};
        if (R.num(R.call(object9, "distanceToSqr", objectArray)) >= d5) {
            return false;
        }
        set.add(n);
        object4 = R.call(object8, "normalize", new Object[0]);
        float f = (float)Math.min(16.0, Math.max(3.0, d * 2.0));
        object3 = R.call(object4, "scale", f);
        R.call(R.type("com.raiiiden.ragdollified.network.RagdollImpulsePacket"), "broadcastOrdered", object7, null, n, n2, object3, object5, Float.valueOf(Collateral18.corpseDamage(object2, n, object5)));
        if (R.yes(R.get(object2, "explosion"))) {
            R.call(object2, "setPos", object5);
            R.call(R.type("com.tacz.guns.util.ExplodeUtil"), "createExplosion", R.call(object2, "getOwner", new Object[0]), object2, R.get(object2, "explosionDamage"), R.get(object2, "explosionRadius"), R.get(object2, "explosionKnockback"), R.get(object2, "explosionDestroyBlock"), object5);
            R.call(object2, "discard", new Object[0]);
            return true;
        }
        int n5 = ((Number)R.get(object2, "pierce")).intValue();
        if (n5 <= 1) {
            R.call(object2, "discard", new Object[0]);
            return true;
        }
        R.set(object2, "pierce", n5 - 1);
        return false;
    }
}

