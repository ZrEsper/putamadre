/*
 * Decompiled with CFR 0.152.
 */
package dev.zomboidmelee.boxing;

import dev.zomboidmelee.boxing.Combat18;
import dev.zomboidmelee.boxing.Net20;
import dev.zomboidmelee.boxing.Parkour20;
import dev.zomboidmelee.boxing.R;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class Slide20 {
    static final Map<Object, State> STATES = new SideMap25<>();
    static final Map<Object, Long> SPRINT = new SideMap25<>();
    static final Map<Object, Long> NEXT = new SideMap25<>();
    static final Map<Object, double[]> MOTION = new SideMap25<>();
    static final Map<Object, State> CLIENT = new SideMap25<>();

    public static double speed(double d, int n) {
        return d * Math.pow(0.92, n);
    }

    public static int duration(double d) {
        return Math.max(8, Math.min(22, (int)Math.round(10.0 + d * 20.0)));
    }

    static double horizontal(Object object) {
        Object object2 = R.call(object, "getDeltaMovement", new Object[0]);
        return Math.hypot(R.num(R.get(object2, "x")), R.num(R.get(object2, "z")));
    }

    static void measure(Object object) {
        double d = R.num(R.call(object, "getX", new Object[0]));
        double d2 = R.num(R.call(object, "getZ", new Object[0]));
        double[] dArray = MOTION.get(object);
        MOTION.put(object, new double[]{d, d2, dArray == null ? 0.0 : d - dArray[0], dArray == null ? 0.0 : d2 - dArray[1]});
    }

    static void advance(Object object, State state) {
        state.speed *= 0.92;
        Object object2 = R.call(object, "getDeltaMovement", new Object[0]);
        R.call(object, "setDeltaMovement", state.x * state.speed, R.num(R.get(object2, "y")), state.z * state.speed);
    }

    public static void clientTick(Object object) {
        Slide20.measure(object);
        State state = CLIENT.get(object);
        if (state == null) {
            return;
        }
        if (!java.util.Objects.equals(state.dimension,R.call(R.call(object,"level"),"dimension")) || !Move25.allowed(object) || state.left-- <= 0 || R.yes(R.get(object, "horizontalCollision")) || !R.yes(R.call(object, "onGround", new Object[0]))) {
            Slide20.clientStop(object);
            return;
        }
        Slide20.advance(object, state);
    }

    static void clientStop(Object object) {
        State state = CLIENT.remove(object);
        if (state != null && R.call(object, "getForcedPose", new Object[0]) == R.get(R.type("net.minecraft.world.entity.Pose"), "CROUCHING")) {
            R.call(object, "setForcedPose", state.oldPose);
        }
    }

    public static void clientStart(Object object, int n) {
        if (n == 0) {
            Slide20.clientStop(object);
            return;
        }
        double[] dArray = MOTION.get(object);
        if (dArray == null) {
            return;
        }
        double d = Math.hypot(dArray[2], dArray[3]);
        if (d < 0.01) {
            return;
        }
        State state = new State();
        state.x = dArray[2] / d;
        state.z = dArray[3] / d;
        state.speed = Math.min(0.75, d * 1.18);
        state.left = n;
        state.oldPose = R.call(object, "getForcedPose", new Object[0]);
        state.dimension=R.call(R.call(object,"level"),"dimension");
        if (state.oldPose != null) {
            return;
        }
        CLIENT.put(object, state);
        R.call(object, "setForcedPose", R.get(R.type("net.minecraft.world.entity.Pose"), "CROUCHING"));
    }

    public static void tick(Object object) {
        if(!Move25.server(object))return;
        State state;
        Slide20.measure(object);
        long l = Combat18.time(object);
        if (R.yes(R.call(object, "isSprinting", new Object[0]))) {
            SPRINT.put(object, l);
        }
        if ((state = STATES.get(object)) == null) {
            return;
        }
        if (!java.util.Objects.equals(state.dimension,R.call(R.call(object,"level"),"dimension")) || state.left-- <= 0 || !R.yes(R.call(object, "isAlive", new Object[0])) || R.yes(R.call(object, "isPassenger", new Object[0])) || R.yes(R.call(object, "isInWater", new Object[0])) || !R.yes(R.call(object, "onGround", new Object[0])) || R.yes(R.get(object, "horizontalCollision"))) {
            Slide20.stop(object);
            return;
        }
        Slide20.advance(object, state);
        if (state.left % 4 == 0) {
            R.set(object, "hurtMarked", true);
        }
        if (state.left % 3 == 0 && R.yes(R.call(object, "onGround", new Object[0]))) {
            Slide20.dust(object);
        }
    }

    public static void request(Object object) {
        if(!Move25.server(object)||!Move25.allowed(object)||Stomp21.PENDING.containsKey(object))return;
        long l = Combat18.time(object);
        if (Parkour20.active(object) || STATES.containsKey(object) || l < NEXT.getOrDefault(object, Long.MIN_VALUE) || !R.yes(R.call(object, "isAlive", new Object[0])) || R.yes(R.call(object, "isSpectator", new Object[0])) || R.yes(R.call(object, "isPassenger", new Object[0])) || !R.yes(R.call(object, "onGround", new Object[0])) || R.yes(R.call(object, "isInWater", new Object[0])) || R.yes(R.call(object, "isUsingItem", new Object[0]))) {
            return;
        }
        if (!R.yes(R.call(object, "isSprinting", new Object[0])) && l - SPRINT.getOrDefault(object, l - 100L) > 6L) {
            return;
        }
        if (R.yes(R.get(R.call(object, "getAbilities", new Object[0]), "flying"))) {
            return;
        }
        double[] dArray = MOTION.get(object);
        if (dArray == null) {
            return;
        }
        double d = Math.hypot(dArray[2], dArray[3]);
        if (d < 0.12 || d > 0.85) {
            return;
        }
        Object object2 = R.call(object, "getForcedPose", new Object[0]);
        if (object2 != null) {
            return;
        }
        State state = new State();
        state.x = dArray[2] / d;
        state.z = dArray[3] / d;
        state.speed = Math.min(0.75, d * 1.18);
        state.left = Slide20.duration(state.speed);
        state.oldPose = object2;
        state.dimension=R.call(R.call(object,"level"),"dimension");
        R.call(object, "setForcedPose", R.get(R.type("net.minecraft.world.entity.Pose"), "CROUCHING"));
        STATES.put(object, state);
        NEXT.put(object, l + 35L);
        Net20.cue(object, 100 + state.left);
    }

    static void stop(Object object) {
        State state = STATES.remove(object);
        if (state == null) {
            return;
        }
        Object object2 = R.get(R.type("net.minecraft.world.entity.Pose"), "CROUCHING");
        if (R.call(object, "getForcedPose", new Object[0]) == object2) {
            R.call(object, "setForcedPose", state.oldPose);
        }
        Net20.cue(object, 99);
    }

    static void dust(Object object) {
        Object object2 = R.call(object, "level", new Object[0]);
        Object object3 = R.call(R.call(object, "blockPosition", new Object[0]), "below", new Object[0]);
        Object object4 = R.call(object2, "getBlockState", object3);
        if (R.yes(R.call(object4, "isAir", new Object[0]))) {
            return;
        }
        Object object5 = R.make("net.minecraft.core.particles.BlockParticleOption", R.get(R.type("net.minecraft.core.particles.ParticleTypes"), "BLOCK"), object4);
        R.call(object2, "sendParticles", object5, R.num(R.call(object, "getX", new Object[0])), R.num(R.call(object, "getY", new Object[0])) + 0.08, R.num(R.call(object, "getZ", new Object[0])), 3, 0.2, 0.03, 0.2, 0.025);
    }

    static final class State {
        double x;
        double z;
        double speed;
        int left;
        Object oldPose;
        Object dimension;

        State() {
        }
    }
}
