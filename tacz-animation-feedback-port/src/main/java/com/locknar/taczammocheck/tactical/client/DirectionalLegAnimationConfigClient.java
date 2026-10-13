package com.locknar.taczammocheck.tactical.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.locknar.taczammocheck.tactical.LocknarDoorKickMod;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public final class DirectionalLegAnimationConfigClient {
    public static final int FRAME_COUNT = 5;
    private static final int UNIQUE_FRAME_COUNT = 4;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String RESOURCE = "/assets/locknar_doorkick/doorkick_data/directional_leg_animations.json";
    private static volatile Data data;

    private DirectionalLegAnimationConfigClient() {
    }

    public static Data get() {
        Data current = data;
        if (current != null) {
            return current;
        }
        Class<DirectionalLegAnimationConfigClient> clazz = DirectionalLegAnimationConfigClient.class;
        synchronized (DirectionalLegAnimationConfigClient.class) {
            if (data == null) {
                data = DirectionalLegAnimationConfigClient.loadData();
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return data;
        }
    }

    private static Data loadData() {
        return DirectionalLegAnimationConfigClient.loadBuiltIn();
    }

    private static Data loadBuiltIn() {
        Data loaded;
        block14: {
            loaded = null;
            try (InputStream stream = DirectionalLegAnimationConfigClient.class.getResourceAsStream(RESOURCE);){
                if (stream == null) {
                    LocknarDoorKickMod.LOGGER.warn("Missing built-in directional leg animation resource '{}'. Using fallback defaults.", (Object)RESOURCE);
                    break block14;
                }
                try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
                    loaded = (Data)GSON.fromJson((Reader)reader, Data.class);
                }
            }
            catch (Throwable throwable) {
                LocknarDoorKickMod.LOGGER.warn("Failed to read built-in directional leg animation resource '{}'. Using fallback defaults.", (Object)RESOURCE, (Object)throwable);
            }
        }
        Data fallback = DirectionalLegAnimationConfigClient.defaults();
        return DirectionalLegAnimationConfigClient.normalize(loaded == null ? fallback : loaded, fallback);
    }

    public static Animation animation(Motion motion) {
        Data value = DirectionalLegAnimationConfigClient.get();
        return switch (motion) {
            default -> throw new IncompatibleClassChangeError();
            case Motion.GUN_IDLE -> value.gunIdle;
            case Motion.WALK_FORWARD -> value.walkForward;
            case Motion.WALK_FORWARD_LEFT -> value.walkForwardLeft;
            case Motion.WALK_LEFT -> value.walkLeft;
            case Motion.WALK_BACKWARD_LEFT -> value.walkBackwardLeft;
            case Motion.WALK_BACKWARD -> value.walkBackward;
            case Motion.WALK_BACKWARD_RIGHT -> value.walkBackwardRight;
            case Motion.WALK_RIGHT -> value.walkRight;
            case Motion.WALK_FORWARD_RIGHT -> value.walkForwardRight;
            case Motion.NONE -> null;
        };
    }

    public static Sample sample(Motion motion, double progress) {
        Animation animation = DirectionalLegAnimationConfigClient.animation(motion);
        if (animation == null || animation.frames == null || animation.frames.length != 5) {
            return Sample.ZERO;
        }
        double wrapped = progress - Math.floor(progress);
        double scaled = wrapped * 4.0;
        int segment = (int)Math.floor(scaled) % 4;
        double t = scaled - Math.floor(scaled);
        Frame p0 = animation.frames[(segment + 4 - 1) % 4];
        Frame p1 = animation.frames[segment];
        Frame p2 = animation.frames[(segment + 1) % 4];
        Frame p3 = animation.frames[(segment + 2) % 4];
        return new Sample(Transform.catmullRom(p0.leftLeg, p1.leftLeg, p2.leftLeg, p3.leftLeg, t), Transform.catmullRom(p0.rightLeg, p1.rightLeg, p2.rightLeg, p3.rightLeg, t), Transform.catmullRom(p0.body, p1.body, p2.body, p3.body, t));
    }

    private static Data normalize(Data value, Data fallback) {
        if (fallback == null) {
            fallback = DirectionalLegAnimationConfigClient.defaults();
        }
        if (value == null) {
            value = fallback;
        }
        value.gunIdle = DirectionalLegAnimationConfigClient.normalizeAnimation(value.gunIdle, fallback.gunIdle);
        for (int i = 1; i < 5; ++i) {
            value.gunIdle.frames[i] = value.gunIdle.frames[0].copy();
        }
        value.walkForward = DirectionalLegAnimationConfigClient.normalizeAnimation(value.walkForward, fallback.walkForward);
        value.walkForwardLeft = DirectionalLegAnimationConfigClient.normalizeAnimation(value.walkForwardLeft, fallback.walkForwardLeft);
        value.walkLeft = DirectionalLegAnimationConfigClient.normalizeAnimation(value.walkLeft, fallback.walkLeft);
        value.walkBackwardLeft = DirectionalLegAnimationConfigClient.normalizeAnimation(value.walkBackwardLeft, fallback.walkBackwardLeft);
        value.walkBackward = DirectionalLegAnimationConfigClient.normalizeAnimation(value.walkBackward, fallback.walkBackward);
        value.walkBackwardRight = DirectionalLegAnimationConfigClient.normalizeAnimation(value.walkBackwardRight, fallback.walkBackwardRight);
        value.walkRight = DirectionalLegAnimationConfigClient.normalizeAnimation(value.walkRight, fallback.walkRight);
        value.walkForwardRight = DirectionalLegAnimationConfigClient.normalizeAnimation(value.walkForwardRight, fallback.walkForwardRight);
        return value;
    }

    private static Animation normalizeAnimation(Animation animation, Animation fallback) {
        if (animation == null) {
            return fallback;
        }
        animation.duration = DirectionalLegAnimationConfigClient.clampFinite(animation.duration, fallback.duration, 0.2, 2.0);
        if (animation.frames == null || animation.frames.length != 5) {
            Frame[] old = animation.frames;
            animation.frames = new Frame[5];
            if (old != null) {
                System.arraycopy(old, 0, animation.frames, 0, Math.min(old.length, 5));
            }
        }
        for (int i = 0; i < 5; ++i) {
            if (animation.frames[i] == null) {
                animation.frames[i] = fallback.frames[i].copy();
            }
            animation.frames[i].normalize(fallback.frames[i]);
        }
        animation.frames[4] = animation.frames[0].copy();
        return animation;
    }

    private static double clampFinite(double value, double fallback, double min, double max) {
        if (!Double.isFinite(value)) {
            value = fallback;
        }
        return Math.max(min, Math.min(max, value));
    }

    private static Data defaults() {
        Data d = new Data();
        d.gunIdle = DirectionalLegAnimationConfigClient.animation(1.0, DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(-0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.body(0.0, 0.0, 0.0, 0.0)), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(-0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.body(0.0, 0.0, 0.0, 0.0)), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(-0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.body(0.0, 0.0, 0.0, 0.0)), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(-0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.body(0.0, 0.0, 0.0, 0.0)), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(-0.2, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.body(0.0, 0.0, 0.0, 0.0)));
        d.walkForward = DirectionalLegAnimationConfigClient.animation(0.8, DirectionalLegAnimationConfigClient.frame(0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(-0.15, -0.35, -0.25, -34.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(0.05, 0.1, 0.2, 27.0, 0.0, 0.0), -0.18), DirectionalLegAnimationConfigClient.frame(0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.05, 0.1, 0.2, 27.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(0.15, -0.35, -0.25, -34.0, 0.0, 0.0), -0.18), DirectionalLegAnimationConfigClient.frame(0.0, 0.0, 0.0));
        d.walkBackward = DirectionalLegAnimationConfigClient.animation(0.9, DirectionalLegAnimationConfigClient.frame(0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(-0.05, -0.18, 0.55, 22.0, 2.0, 0.0), DirectionalLegAnimationConfigClient.leg(0.02, 0.06, -0.18, -17.0, -1.0, 0.0), -0.1), DirectionalLegAnimationConfigClient.frame(0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(-0.02, 0.06, -0.18, -17.0, 1.0, 0.0), DirectionalLegAnimationConfigClient.leg(0.05, -0.18, 0.55, 22.0, -2.0, 0.0), -0.1), DirectionalLegAnimationConfigClient.frame(0.0, 0.0, 0.0));
        d.walkLeft = DirectionalLegAnimationConfigClient.animation(0.86, DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.body(0.0, 0.0, -6.0, -2.0)), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.95, -0.3, -0.05, -8.0, -5.0, -12.0), DirectionalLegAnimationConfigClient.leg(0.35, 0.05, 0.08, 10.0, -2.0, -5.0), DirectionalLegAnimationConfigClient.body(-0.16, 0.0, -8.0, -4.0)), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.35, 0.0, 0.0, 0.0, 0.0, -4.0), DirectionalLegAnimationConfigClient.leg(0.25, 0.0, 0.0, 0.0, 0.0, -3.0), DirectionalLegAnimationConfigClient.body(0.0, 0.0, -6.0, -2.0)), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.2, 0.05, 0.08, 10.0, 2.0, 4.0), DirectionalLegAnimationConfigClient.leg(0.62, -0.22, -0.02, -8.0, 4.0, 8.0), DirectionalLegAnimationConfigClient.body(-0.12, 0.0, -8.0, -4.0)), DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), DirectionalLegAnimationConfigClient.body(0.0, 0.0, -6.0, -2.0)));
        d.walkRight = DirectionalLegAnimationConfigClient.mirrorAnimation(d.walkLeft);
        d.walkForwardLeft = DirectionalLegAnimationConfigClient.blendAnimation(d.walkForward, d.walkLeft, 0.83);
        d.walkForwardRight = DirectionalLegAnimationConfigClient.blendAnimation(d.walkForward, d.walkRight, 0.83);
        d.walkBackwardLeft = DirectionalLegAnimationConfigClient.blendAnimation(d.walkBackward, d.walkLeft, 0.88);
        d.walkBackwardRight = DirectionalLegAnimationConfigClient.blendAnimation(d.walkBackward, d.walkRight, 0.88);
        return d;
    }

    private static Animation animation(double duration, Frame ... frames) {
        Animation a = new Animation();
        a.duration = duration;
        a.frames = frames;
        return a;
    }

    private static Animation mirrorAnimation(Animation source) {
        Animation result = new Animation();
        result.duration = source.duration;
        result.frames = new Frame[5];
        for (int i = 0; i < 5; ++i) {
            result.frames[i] = source.frames[i].mirrored();
        }
        return result;
    }

    private static Animation blendAnimation(Animation first, Animation second, double duration) {
        Animation result = new Animation();
        result.duration = duration;
        result.frames = new Frame[5];
        for (int i = 0; i < 5; ++i) {
            result.frames[i] = Frame.lerp(first.frames[i], second.frames[i], 0.5);
        }
        result.frames[4] = result.frames[0].copy();
        return result;
    }

    private static Frame frame(double leftRotX, double rightRotX, double bodyY) {
        return DirectionalLegAnimationConfigClient.frame(DirectionalLegAnimationConfigClient.leg(0.0, 0.0, 0.0, leftRotX, 0.0, 0.0), DirectionalLegAnimationConfigClient.leg(0.0, 0.0, 0.0, rightRotX, 0.0, 0.0), DirectionalLegAnimationConfigClient.body(bodyY, 0.0, 0.0, 0.0));
    }

    private static Frame frame(Transform left, Transform right, double bodyY) {
        return DirectionalLegAnimationConfigClient.frame(left, right, DirectionalLegAnimationConfigClient.body(bodyY, 0.0, 0.0, 0.0));
    }

    private static Frame frame(Transform left, Transform right, Transform body) {
        return new Frame(left, right, body);
    }

    private static Transform leg(double x, double y, double z, double rotX, double rotY, double rotZ) {
        return new Transform(x, y, z, rotX, rotY, rotZ);
    }

    private static Transform body(double y, double rotX, double rotY, double rotZ) {
        return new Transform(0.0, y, 0.0, rotX, rotY, rotZ);
    }

    private static Animation animationFrom(Data data, Motion motion) {
        return switch (motion) {
            default -> throw new IncompatibleClassChangeError();
            case Motion.GUN_IDLE -> data.gunIdle;
            case Motion.WALK_FORWARD -> data.walkForward;
            case Motion.WALK_FORWARD_LEFT -> data.walkForwardLeft;
            case Motion.WALK_LEFT -> data.walkLeft;
            case Motion.WALK_BACKWARD_LEFT -> data.walkBackwardLeft;
            case Motion.WALK_BACKWARD -> data.walkBackward;
            case Motion.WALK_BACKWARD_RIGHT -> data.walkBackwardRight;
            case Motion.WALK_RIGHT -> data.walkRight;
            case Motion.WALK_FORWARD_RIGHT -> data.walkForwardRight;
            case Motion.NONE -> data.gunIdle;
        };
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double catmullRom(double p0, double p1, double p2, double p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;
        return 0.5 * (2.0 * p1 + (-p0 + p2) * t + (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3) * t2 + (-p0 + 3.0 * p1 - 3.0 * p2 + p3) * t3);
    }

    public static final class Data {
        public Animation gunIdle;
        public Animation walkForward;
        public Animation walkForwardLeft;
        public Animation walkLeft;
        public Animation walkBackwardLeft;
        public Animation walkBackward;
        public Animation walkBackwardRight;
        public Animation walkRight;
        public Animation walkForwardRight;
    }

    public static enum Motion {
        NONE("\u65e0", Double.NaN),
        GUN_IDLE("\u6301\u67aa\u7ad9\u7acb", Double.NaN),
        WALK_FORWARD("\u524d\u8fdb", 0.0),
        WALK_FORWARD_RIGHT("\u53f3\u524d", 45.0),
        WALK_RIGHT("\u53f3\u79fb", 90.0),
        WALK_BACKWARD_RIGHT("\u53f3\u540e", 135.0),
        WALK_BACKWARD("\u540e\u9000", 180.0),
        WALK_BACKWARD_LEFT("\u5de6\u540e", -135.0),
        WALK_LEFT("\u5de6\u79fb", -90.0),
        WALK_FORWARD_LEFT("\u5de6\u524d", -45.0);

        private final String displayName;
        private final double directionAngle;

        private Motion(String displayName, double directionAngle) {
            this.displayName = displayName;
            this.directionAngle = directionAngle;
        }

        public String displayName() {
            return this.displayName;
        }

        public double directionAngle() {
            return this.directionAngle;
        }

        public static Motion[] directionalValues() {
            return new Motion[]{WALK_FORWARD, WALK_FORWARD_LEFT, WALK_LEFT, WALK_BACKWARD_LEFT, WALK_BACKWARD, WALK_BACKWARD_RIGHT, WALK_RIGHT, WALK_FORWARD_RIGHT};
        }

        public static Motion nearest(double angleDegrees) {
            Motion best = WALK_FORWARD;
            double bestDistance = Double.MAX_VALUE;
            for (Motion motion : Motion.directionalValues()) {
                double distance = Math.abs(Motion.wrapDegrees(angleDegrees - motion.directionAngle));
                if (!(distance < bestDistance)) continue;
                best = motion;
                bestDistance = distance;
            }
            return best;
        }

        private static double wrapDegrees(double angle) {
            if ((angle %= 360.0) >= 180.0) {
                angle -= 360.0;
            }
            if (angle < -180.0) {
                angle += 360.0;
            }
            return angle;
        }
    }

    public static final class Animation {
        public double duration = 0.8;
        public Frame[] frames = new Frame[5];
    }

    public static final class Frame {
        public Transform leftLeg;
        public Transform rightLeg;
        public Transform body;
        public Double bodyY;

        public Frame() {
        }

        private Frame(Transform leftLeg, Transform rightLeg, Transform body) {
            this.leftLeg = leftLeg;
            this.rightLeg = rightLeg;
            this.body = body;
            this.bodyY = null;
        }

        public Frame copy() {
            return new Frame(this.leftLeg.copy(), this.rightLeg.copy(), this.body.copy());
        }

        public Frame mirrored() {
            return new Frame(this.rightLeg.mirrored(), this.leftLeg.mirrored(), this.body.mirrored());
        }

        public static Frame lerp(Frame a, Frame b, double t) {
            return new Frame(Transform.lerp(a.leftLeg, b.leftLeg, t), Transform.lerp(a.rightLeg, b.rightLeg, t), Transform.lerp(a.body, b.body, t));
        }

        private void normalize(Frame fallback) {
            if (this.leftLeg == null) {
                this.leftLeg = fallback.leftLeg.copy();
            }
            if (this.rightLeg == null) {
                this.rightLeg = fallback.rightLeg.copy();
            }
            if (this.body == null) {
                this.body = fallback.body.copy();
                if (this.bodyY != null && Double.isFinite(this.bodyY)) {
                    this.body.y = this.bodyY;
                }
            }
            this.bodyY = null;
            this.leftLeg.sanitize();
            this.rightLeg.sanitize();
            this.body.sanitizeBody(fallback.body);
        }
    }

    public record Sample(Transform leftLeg, Transform rightLeg, Transform body) {
        public static final Sample ZERO = new Sample(new Transform(), new Transform(), new Transform());

        public static Sample lerp(Sample a, Sample b, double t) {
            return new Sample(Transform.lerp(a.leftLeg, b.leftLeg, t), Transform.lerp(a.rightLeg, b.rightLeg, t), Transform.lerp(a.body, b.body, t));
        }
    }

    public static final class Transform {
        public double x;
        public double y;
        public double z;
        public double rotX;
        public double rotY;
        public double rotZ;

        public Transform() {
        }

        private Transform(double x, double y, double z, double rotX, double rotY, double rotZ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rotX = rotX;
            this.rotY = rotY;
            this.rotZ = rotZ;
        }

        public Transform copy() {
            return new Transform(this.x, this.y, this.z, this.rotX, this.rotY, this.rotZ);
        }

        public Transform mirrored() {
            return new Transform(-this.x, this.y, this.z, this.rotX, -this.rotY, -this.rotZ);
        }

        private void sanitize() {
            this.x = DirectionalLegAnimationConfigClient.clampFinite(this.x, 0.0, -3.0, 3.0);
            this.y = DirectionalLegAnimationConfigClient.clampFinite(this.y, 0.0, -3.0, 3.0);
            this.z = DirectionalLegAnimationConfigClient.clampFinite(this.z, 0.0, -3.0, 3.0);
            this.rotX = DirectionalLegAnimationConfigClient.clampFinite(this.rotX, 0.0, -90.0, 90.0);
            this.rotY = DirectionalLegAnimationConfigClient.clampFinite(this.rotY, 0.0, -60.0, 60.0);
            this.rotZ = DirectionalLegAnimationConfigClient.clampFinite(this.rotZ, 0.0, -60.0, 60.0);
        }

        private void sanitizeBody(Transform fallback) {
            this.x = 0.0;
            this.z = 0.0;
            this.y = DirectionalLegAnimationConfigClient.clampFinite(this.y, fallback.y, -1.5, 1.5);
            this.rotX = DirectionalLegAnimationConfigClient.clampFinite(this.rotX, fallback.rotX, -15.0, 15.0);
            this.rotY = DirectionalLegAnimationConfigClient.clampFinite(this.rotY, fallback.rotY, -25.0, 25.0);
            this.rotZ = DirectionalLegAnimationConfigClient.clampFinite(this.rotZ, fallback.rotZ, -20.0, 20.0);
        }

        public static Transform lerp(Transform a, Transform b, double t) {
            return new Transform(DirectionalLegAnimationConfigClient.lerp(a.x, b.x, t), DirectionalLegAnimationConfigClient.lerp(a.y, b.y, t), DirectionalLegAnimationConfigClient.lerp(a.z, b.z, t), DirectionalLegAnimationConfigClient.lerp(a.rotX, b.rotX, t), DirectionalLegAnimationConfigClient.lerp(a.rotY, b.rotY, t), DirectionalLegAnimationConfigClient.lerp(a.rotZ, b.rotZ, t));
        }

        public static Transform catmullRom(Transform p0, Transform p1, Transform p2, Transform p3, double t) {
            return new Transform(DirectionalLegAnimationConfigClient.catmullRom(p0.x, p1.x, p2.x, p3.x, t), DirectionalLegAnimationConfigClient.catmullRom(p0.y, p1.y, p2.y, p3.y, t), DirectionalLegAnimationConfigClient.catmullRom(p0.z, p1.z, p2.z, p3.z, t), DirectionalLegAnimationConfigClient.catmullRom(p0.rotX, p1.rotX, p2.rotX, p3.rotX, t), DirectionalLegAnimationConfigClient.catmullRom(p0.rotY, p1.rotY, p2.rotY, p3.rotY, t), DirectionalLegAnimationConfigClient.catmullRom(p0.rotZ, p1.rotZ, p2.rotZ, p3.rotZ, t));
        }
    }
}
