/*
 * Decompiled with CFR 0.152.
 */
package dev.zomboidmelee.boxing;


import dev.zomboidmelee.boxing.BoxingClient16;
import dev.zomboidmelee.boxing.Combat18;
import dev.zomboidmelee.boxing.Motion16;
import dev.zomboidmelee.boxing.Net20;
import dev.zomboidmelee.boxing.R;
import dev.zomboidmelee.boxing.Slide20;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class Parkour20 {
    static final Map<Object, Long> VISUAL = new SideMap25<>();
    static final Map<Object, State> ACTIVE = new SideMap25<>();
    static final Map<Object, Long> NEXT = new SideMap25<>();
    static final Map<Object, double[]> GROUND = new SideMap25<>();

    public static boolean active(Object object) {
        return ACTIVE.containsKey(object);
    }

    static double pos(Object object, String string) {
        return R.num(R.call(object, "get" + string, new Object[0]));
    }

    static boolean forbidden(Object object) {
        return !R.yes(R.call(object, "isAlive", new Object[0])) || R.yes(R.call(object, "isSpectator", new Object[0])) || R.yes(R.call(object, "isPassenger", new Object[0])) || R.yes(R.call(object, "isInWater", new Object[0])) || R.yes(R.call(object, "isSleeping", new Object[0])) || R.yes(R.call(object, "isUsingItem", new Object[0])) || R.yes(R.get(R.call(object, "getAbilities", new Object[0]), "flying")) || R.call(object, "getForcedPose", new Object[0]) != null;
    }

    static Object box(double d, double d2, double d3, double d4) {
        return R.make("net.minecraft.world.phys.AABB", d - d4, d2, d3 - d4, d + d4, d2 + 1.81, d3 + d4);
    }

    static boolean clear(Object object, Object object2, Object object3) {
        return R.yes(R.call(object, "noCollision", object2, object3));
    }

    public static boolean heightAllowed(double d) {
        return d > 0.30 && d <= 2.001;
    }

    static State find(Object object) {
        double d;
        if (Parkour20.forbidden(object) || Slide20.STATES.containsKey(object)) {
            return null;
        }
        Object object2 = R.call(object, "level", new Object[0]);
        Object object3 = R.call(object, "getLookAngle", new Object[0]);
        double d2 = R.num(R.get(object3, "x"));
        double d3 = Math.hypot(d2, d = R.num(R.get(object3, "z")));
        if (d3 < 0.1) {
            return null;
        }
        d2 /= d3;
        d /= d3;
        double d4 = Parkour20.pos(object, "X");
        double d5 = Parkour20.pos(object, "Y");
        double d6 = Parkour20.pos(object, "Z");
        double[] dArray = GROUND.get(object);
        if (dArray == null || Combat18.time(object) - (long)dArray[1] > 20L) {
            return null;
        }
        for (double d7 = 0.45; d7 <= 1.05; d7 += 0.2) {
            int n = (int)Math.floor(d4 + d2 * d7);
            int n2 = (int)Math.floor(d6 + d * d7);
            for (int i = (int)Math.floor(dArray[0]+2.001); i >= (int)Math.floor(dArray[0]-.01); --i) {
                Object object4;
                double d8;
                Object object5 = R.make("net.minecraft.core.BlockPos", n, i, n2);
                if (!R.yes(R.call(object2, "hasChunkAt", object5))) continue;
                Object object6 = R.call(object2, "getBlockState", object5);
                Object object7 = R.call(object6, "getCollisionShape", object2, object5);
                if (R.yes(R.call(object7, "isEmpty", new Object[0])) || !Parkour20.heightAllowed((d8 = (double)i + R.num(R.get(object4 = R.call(object7, "bounds", new Object[0]), "maxY"))) - dArray[0]) || d8 < d5 - 0.90 || R.num(R.get(object4, "maxX")) - R.num(R.get(object4, "minX")) < 0.6 || R.num(R.get(object4, "maxZ")) - R.num(R.get(object4, "minZ")) < 0.6) continue;
                double d9 = (double)n + 0.5;
                double d10 = (double)n2 + 0.5;
                double d11 = d8 + 0.04;
                if (Math.hypot(d9 - d4, d10 - d6) > 1.35 || !Parkour20.clear(object2, object, Parkour20.box(d9, d11, d10, 0.3))) continue;
                Object object8 = R.make("net.minecraft.world.phys.AABB", d4 - 0.3, d5, d6 - 0.3, d4 + 0.3, d11 + 1.81, d6 + 0.3);
                Object object9 = R.make("net.minecraft.world.phys.AABB", Math.min(d4, d9) - 0.3, d11, Math.min(d6, d10) - 0.3, Math.max(d4, d9) + 0.3, d11 + 1.81, Math.max(d6, d10) + 0.3);
                if (!Parkour20.clear(object2, object, object8) || !Parkour20.clear(object2, object, object9)) continue;
                State state = new State();
                state.x = d9;
                state.y = d11;
                state.z = d10;
                state.dimension=R.call(object2,"dimension");
                return state;
            }
        }
        return null;
    }

    public static void request(Object object) {
        if(!Move25.server(object)||!Move25.allowed(object)||Stomp21.PENDING.containsKey(object))return;
        long l = Combat18.time(object);
        if (Parkour20.active(object) || l < NEXT.getOrDefault(object, Long.MIN_VALUE) || R.yes(R.call(object, "onGround", new Object[0]))) {
            return;
        }
        State state = Parkour20.find(object);
        if (state == null) {
            return;
        }
        ACTIVE.put(object, state);
        NEXT.put(object, l + 30L);
        Mantle26.send(object,state.x,state.y,state.z);
    }

    public static void accept(Object object, boolean bl) {
        if (!bl) {
            ACTIVE.remove(object);
            VISUAL.remove(object);
            return;
        }
        VISUAL.put(object, BoxingClient16.CLOCK.getAsLong());
        if (object != R.get(BoxingClient16.mc(), "player")) {
            return;
        }
        State state = Parkour20.find(object);
        if (state != null) {
            ACTIVE.put(object, state);
        }
    }

    public static void target(Object object,double x,double y,double z){VISUAL.put(object,BoxingClient16.CLOCK.getAsLong());if(object!=R.get(BoxingClient16.mc(),"player"))return;State state=new State();state.x=x;state.y=y;state.z=z;state.dimension=R.call(R.call(object,"level"),"dimension");ACTIVE.put(object,state);}

    public static void tick(Object object, boolean bl) {
        if(bl!=Move25.server(object))return;
        State state;
        long l = Combat18.time(object);
        if (R.yes(R.call(object, "onGround", new Object[0])) && !Parkour20.active(object)) {
            GROUND.put(object, new double[]{Parkour20.pos(object, "Y"), l});
        }
        if ((state = ACTIVE.get(object)) == null) {
            return;
        }
        double d = Parkour20.pos(object, "X");
        double d2 = Parkour20.pos(object, "Y");
        double d3 = Parkour20.pos(object, "Z");
        if (!java.util.Objects.equals(state.dimension,R.call(R.call(object,"level"),"dimension")) || ++state.age > 28 || Parkour20.forbidden(object) || !Parkour20.clear(R.call(object, "level", new Object[0]), object, Parkour20.box(state.x, state.y, state.z, 0.3)) || Math.hypot(state.x - d, state.z - d3) > 1.6 || d2 > state.y + 0.5) {
            Parkour20.stop(object, bl);
            return;
        }
        double d4 = state.y - d2;
        double d5 = state.x - d;
        double d6 = state.z - d3;
        double d7 = Math.hypot(d5, d6);
        if (d4 < 0.08 && d7 < 0.12) {
            Parkour20.stop(object, bl);
            return;
        }
        double d8 = d4 > 0.06 ? Math.min(0.24, d4 + 0.025) : 0.025;
        double d9 = 0.0;
        double d10 = 0.0;
        if (d4 < 0.08 && d7 > 0.01) {
            double d11 = Math.min(0.16, d7);
            d9 = d5 / d7 * d11;
            d10 = d6 / d7 * d11;
        }
        R.call(object, "setDeltaMovement", d9, d8, d10);
        if (bl && state.age % 4 == 1) {
            R.set(object, "hurtMarked", true);
        }
    }

    static void stop(Object object, boolean bl) {
        ACTIVE.remove(object);
        if (bl) {
            Net20.cue(object, 201);
        }
    }

    public static boolean body(Object object, Object object2) {
        double d;
        State state = ACTIVE.get(object2);
        Long l = VISUAL.get(object2);
        d = state == null ? (l == null ? 100.0 : (double)(BoxingClient16.CLOCK.getAsLong() - l) / 5.0E7) : (double)state.age;
        if (d > 28.0) {
            return false;
        }
        double d3 = Motion16.smooth(Math.min(d / 3.0, (28.0 - d) / 4.0));
        MoveClient25.part(object, "body", "xRot", 0.18, d3);
        MoveClient25.part(object, "rightArm", "xRot", -2.3, d3);
        MoveClient25.part(object, "leftArm", "xRot", -2.3, d3);
        MoveClient25.part(object, "rightLeg", "xRot", -0.6, d3);
        MoveClient25.part(object, "leftLeg", "xRot", -0.3, d3);
        return true;
    }

    static final class State {
        double x;
        double y;
        double z;
        int age;
        Object dimension;

        State() {
        }
    }
}
