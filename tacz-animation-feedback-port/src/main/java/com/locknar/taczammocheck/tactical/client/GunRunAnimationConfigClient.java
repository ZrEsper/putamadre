package com.locknar.taczammocheck.tactical.client;

import com.google.gson.Gson;
import com.locknar.taczammocheck.tactical.LocknarDoorKickMod;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public final class GunRunAnimationConfigClient {
    public static final int FRAME_COUNT = 7;
    private static final Gson GSON = new Gson();
    private static final String RESOURCE = "/assets/locknar_doorkick/doorkick_data/gun_run_animations.json";
    private static volatile Data data;

    private GunRunAnimationConfigClient() {
    }

    public static Data get() {
        Data current = data;
        if (current != null) {
            return current;
        }
        Class<GunRunAnimationConfigClient> clazz = GunRunAnimationConfigClient.class;
        synchronized (GunRunAnimationConfigClient.class) {
            if (data == null) {
                data = GunRunAnimationConfigClient.loadBuiltIn();
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return data;
        }
    }

    private static Data loadBuiltIn() {
        Data loaded;
        block14: {
            loaded = null;
            try (InputStream stream = GunRunAnimationConfigClient.class.getResourceAsStream(RESOURCE);){
                if (stream != null) {
                    try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
                        loaded = (Data)GSON.fromJson((Reader)reader, Data.class);
                        break block14;
                    }
                }
                LocknarDoorKickMod.LOGGER.warn("Missing built-in gun-run animation resource '{}'.", (Object)RESOURCE);
            }
            catch (Throwable throwable) {
                LocknarDoorKickMod.LOGGER.warn("Failed to read built-in gun-run animation resource '{}'.", (Object)RESOURCE, (Object)throwable);
            }
        }
        Data fallback = GunRunAnimationConfigClient.fallbackDefaults();
        return GunRunAnimationConfigClient.normalize(loaded == null ? fallback : loaded, fallback);
    }

    public static Animation animation(Motion motion) {
        Data value = GunRunAnimationConfigClient.get();
        return switch (motion) {
            default -> throw new IncompatibleClassChangeError();
            case Motion.RUN_FORWARD -> value.gunRunForward;
            case Motion.RUN_FORWARD_LEFT -> value.gunRunForwardLeft;
            case Motion.RUN_FORWARD_RIGHT -> value.gunRunForwardRight;
            case Motion.BRAKE_LEFT -> value.gunRunBrakeLeft;
            case Motion.BRAKE_RIGHT -> value.gunRunBrakeRight;
            case Motion.NONE -> null;
        };
    }

    public static Sample sample(Motion motion, double progress) {
        Animation animation = GunRunAnimationConfigClient.animation(motion);
        if (animation == null || animation.frames == null || animation.frames.length != 7) {
            return Sample.ZERO;
        }
        return animation.loop ? GunRunAnimationConfigClient.sampleLoop(animation, progress) : GunRunAnimationConfigClient.sampleOneShot(animation, progress);
    }

    private static Sample sampleLoop(Animation animation, double progress) {
        int unique = 6;
        double wrapped = progress - Math.floor(progress);
        double scaled = wrapped * (double)unique;
        int segment = (int)Math.floor(scaled) % unique;
        double t = scaled - Math.floor(scaled);
        Frame p0 = animation.frames[(segment + unique - 1) % unique];
        Frame p1 = animation.frames[segment];
        Frame p2 = animation.frames[(segment + 1) % unique];
        Frame p3 = animation.frames[(segment + 2) % unique];
        return Sample.catmullRom(p0, p1, p2, p3, t);
    }

    private static Sample sampleOneShot(Animation animation, double progress) {
        double clamped = Math.max(0.0, Math.min(1.0, progress));
        double scaled = clamped * 6.0;
        int segment = Math.min(5, (int)Math.floor(scaled));
        double t = scaled - (double)segment;
        int i0 = Math.max(0, segment - 1);
        int i1 = segment;
        int i2 = Math.min(6, segment + 1);
        int i3 = Math.min(6, segment + 2);
        return Sample.catmullRom(animation.frames[i0], animation.frames[i1], animation.frames[i2], animation.frames[i3], t);
    }

    private static Data normalize(Data value, Data fallback) {
        if (fallback == null) {
            fallback = GunRunAnimationConfigClient.fallbackDefaults();
        }
        if (value == null) {
            value = fallback.copy();
        }
        value.gunRunForward = GunRunAnimationConfigClient.normalizeAnimation(value.gunRunForward, fallback.gunRunForward, true);
        value.gunRunForwardLeft = GunRunAnimationConfigClient.normalizeAnimation(value.gunRunForwardLeft, fallback.gunRunForwardLeft, true);
        value.gunRunForwardRight = GunRunAnimationConfigClient.normalizeAnimation(value.gunRunForwardRight, fallback.gunRunForwardRight, true);
        value.gunRunBrakeLeft = GunRunAnimationConfigClient.normalizeAnimation(value.gunRunBrakeLeft, fallback.gunRunBrakeLeft, false);
        value.gunRunBrakeRight = GunRunAnimationConfigClient.normalizeAnimation(value.gunRunBrakeRight, fallback.gunRunBrakeRight, false);
        return value;
    }

    private static Animation normalizeAnimation(Animation value, Animation fallback, boolean loop) {
        if (value == null) {
            value = fallback.copy();
        }
        value.loop = loop;
        value.duration = GunRunAnimationConfigClient.clampFinite(value.duration, fallback.duration, 0.18, 1.5);
        if (value.frames == null || value.frames.length != 7) {
            Frame[] old = value.frames;
            value.frames = new Frame[7];
            if (old != null) {
                System.arraycopy(old, 0, value.frames, 0, Math.min(old.length, 7));
            }
        }
        for (int i = 0; i < 7; ++i) {
            if (value.frames[i] == null) {
                value.frames[i] = fallback.frames[i].copy();
            }
            value.frames[i].normalize(fallback.frames[i]);
        }
        if (loop) {
            value.frames[6] = value.frames[0].copy();
        }
        return value;
    }

    private static double clampFinite(double value, double fallback, double min, double max) {
        if (!Double.isFinite(value)) {
            value = fallback;
        }
        return Math.max(min, Math.min(max, value));
    }

    private static Data fallbackDefaults() {
        Transform idleLeft = GunRunAnimationConfigClient.transform(0.3, 0.0, -3.0, -5.0, 0.0, -2.0);
        Transform idleRight = GunRunAnimationConfigClient.transform(-0.4, 0.0, 1.6, 10.0, 0.0, 2.0);
        Frame idle = GunRunAnimationConfigClient.frame(idleLeft, idleRight, new Transform(), new Transform());
        Animation forward = GunRunAnimationConfigClient.blankAnimation(true, 0.62);
        for (int i = 0; i < 6; ++i) {
            double angle = Math.PI * 2 * (double)i / 6.0;
            double swing = Math.sin(angle);
            forward.frames[i] = GunRunAnimationConfigClient.frame(GunRunAnimationConfigClient.transform(0.0, -0.5 * Math.max(0.0, -swing), 3.0 * swing, 40.0 * swing, 0.0, 0.0), GunRunAnimationConfigClient.transform(0.0, -0.5 * Math.max(0.0, swing), -3.0 * swing, -40.0 * swing, 0.0, 0.0), GunRunAnimationConfigClient.transform(0.0, -0.4, 0.0, 3.0, 0.0, 0.0), GunRunAnimationConfigClient.transform(0.0, -0.15, 0.0, 4.0, 0.0, 0.0));
        }
        forward.frames[6] = forward.frames[0].copy();
        Animation forwardLeft = GunRunAnimationConfigClient.turnLoop(forward, -1);
        Animation forwardRight = GunRunAnimationConfigClient.turnLoop(forward, 1);
        Animation brakeLeft = GunRunAnimationConfigClient.blankAnimation(false, 0.38);
        Animation brakeRight = GunRunAnimationConfigClient.blankAnimation(false, 0.38);
        for (int i = 0; i < 7; ++i) {
            double p = (double)i / 6.0;
            Frame leftBrakeStart = GunRunAnimationConfigClient.frame(GunRunAnimationConfigClient.transform(0.4, -0.5, 3.0, 25.0, 0.0, -6.0), GunRunAnimationConfigClient.transform(-0.4, -0.6, -1.0, -18.0, 0.0, 6.0), GunRunAnimationConfigClient.transform(0.0, -0.8, 0.0, -5.0, 0.0, -2.0), GunRunAnimationConfigClient.transform(0.0, -0.4, 0.0, -6.0, 0.0, -2.0));
            Frame rightBrakeStart = leftBrakeStart.mirrored();
            brakeLeft.frames[i] = Frame.lerp(leftBrakeStart, idle, p);
            brakeRight.frames[i] = Frame.lerp(rightBrakeStart, idle, p);
        }
        brakeLeft.frames[6] = idle.copy();
        brakeRight.frames[6] = idle.copy();
        Data data = new Data();
        data.gunRunForward = forward;
        data.gunRunForwardLeft = forwardLeft;
        data.gunRunForwardRight = forwardRight;
        data.gunRunBrakeLeft = brakeLeft;
        data.gunRunBrakeRight = brakeRight;
        return data;
    }

    private static Animation blankAnimation(boolean loop, double duration) {
        Animation animation = new Animation();
        animation.loop = loop;
        animation.duration = duration;
        animation.frames = new Frame[7];
        for (int i = 0; i < 7; ++i) {
            animation.frames[i] = new Frame();
        }
        return animation;
    }

    private static Animation turnLoop(Animation source, int side) {
        Animation result = source.copy();
        result.duration = source.duration + 0.02;
        for (Frame frame : result.frames) {
            frame.leftLeg.rotY += 25.0 * (double)side;
            frame.rightLeg.rotY += 25.0 * (double)side;
            frame.body.rotY += 4.0 * (double)side;
            frame.body.rotZ += 2.0 * (double)side;
            frame.root.rotZ += 3.0 * (double)side;
        }
        result.frames[6] = result.frames[0].copy();
        return result;
    }

    private static Animation animationFrom(Data data, Motion motion) {
        return switch (motion) {
            default -> throw new IncompatibleClassChangeError();
            case Motion.RUN_FORWARD -> data.gunRunForward;
            case Motion.RUN_FORWARD_LEFT -> data.gunRunForwardLeft;
            case Motion.RUN_FORWARD_RIGHT -> data.gunRunForwardRight;
            case Motion.BRAKE_LEFT -> data.gunRunBrakeLeft;
            case Motion.BRAKE_RIGHT -> data.gunRunBrakeRight;
            case Motion.NONE -> data.gunRunForward;
        };
    }

    private static Frame frame(Transform left, Transform right, Transform body, Transform root) {
        return new Frame(left, right, body, root);
    }

    private static Transform transform(double x, double y, double z, double rx, double ry, double rz) {
        return new Transform(x, y, z, rx, ry, rz);
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
        public Animation gunRunForward;
        public Animation gunRunForwardLeft;
        public Animation gunRunForwardRight;
        public Animation gunRunBrakeLeft;
        public Animation gunRunBrakeRight;

        public Data copy() {
            Data result = new Data();
            result.gunRunForward = this.gunRunForward.copy();
            result.gunRunForwardLeft = this.gunRunForwardLeft.copy();
            result.gunRunForwardRight = this.gunRunForwardRight.copy();
            result.gunRunBrakeLeft = this.gunRunBrakeLeft.copy();
            result.gunRunBrakeRight = this.gunRunBrakeRight.copy();
            return result;
        }
    }

    public static enum Motion {
        NONE("\u65e0"),
        RUN_FORWARD("\u6301\u67aa\u5411\u524d\u8dd1"),
        RUN_FORWARD_LEFT("\u6301\u67aa\u5de6\u524d\u8dd1"),
        RUN_FORWARD_RIGHT("\u6301\u67aa\u53f3\u524d\u8dd1"),
        BRAKE_LEFT("\u5de6\u811a\u5239\u505c"),
        BRAKE_RIGHT("\u53f3\u811a\u5239\u505c");

        private final String displayName;

        private Motion(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }

        public static Motion[] editableValues() {
            return new Motion[]{RUN_FORWARD, RUN_FORWARD_LEFT, RUN_FORWARD_RIGHT, BRAKE_LEFT, BRAKE_RIGHT};
        }

        public boolean isRunLoop() {
            return this == RUN_FORWARD || this == RUN_FORWARD_LEFT || this == RUN_FORWARD_RIGHT;
        }

        public boolean isBrake() {
            return this == BRAKE_LEFT || this == BRAKE_RIGHT;
        }
    }

    public static final class Animation {
        public boolean loop;
        public double duration = 0.6;
        public Frame[] frames = new Frame[7];

        public Animation copy() {
            Animation result = new Animation();
            result.loop = this.loop;
            result.duration = this.duration;
            result.frames = new Frame[7];
            for (int i = 0; i < 7; ++i) {
                result.frames[i] = this.frames[i].copy();
            }
            return result;
        }
    }

    public static final class Frame {
        public Transform leftLeg = new Transform();
        public Transform rightLeg = new Transform();
        public Transform body = new Transform();
        public Transform root = new Transform();

        public Frame() {
        }

        private Frame(Transform leftLeg, Transform rightLeg, Transform body, Transform root) {
            this.leftLeg = leftLeg;
            this.rightLeg = rightLeg;
            this.body = body;
            this.root = root;
        }

        public Transform transform(Bone bone) {
            return switch (bone) {
                default -> throw new IncompatibleClassChangeError();
                case Bone.LEFT_LEG -> this.leftLeg;
                case Bone.RIGHT_LEG -> this.rightLeg;
                case Bone.BODY -> this.body;
                case Bone.ROOT -> this.root;
            };
        }

        public Frame copy() {
            return new Frame(this.leftLeg.copy(), this.rightLeg.copy(), this.body.copy(), this.root.copy());
        }

        public Frame mirrored() {
            return new Frame(this.rightLeg.mirrored(), this.leftLeg.mirrored(), this.body.mirrored(), this.root.mirrored());
        }

        public static Frame lerp(Frame a, Frame b, double t) {
            return new Frame(Transform.lerp(a.leftLeg, b.leftLeg, t), Transform.lerp(a.rightLeg, b.rightLeg, t), Transform.lerp(a.body, b.body, t), Transform.lerp(a.root, b.root, t));
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
            }
            if (this.root == null) {
                this.root = fallback.root.copy();
            }
            this.leftLeg.sanitizeLeg(fallback.leftLeg);
            this.rightLeg.sanitizeLeg(fallback.rightLeg);
            this.body.sanitizeBody(fallback.body);
            this.root.sanitizeRoot(fallback.root);
        }
    }

    public record Sample(Transform leftLeg, Transform rightLeg, Transform body, Transform root) {
        public static final Sample ZERO = new Sample(new Transform(), new Transform(), new Transform(), new Transform());

        public static Sample lerp(Sample a, Sample b, double t) {
            return new Sample(Transform.lerp(a.leftLeg, b.leftLeg, t), Transform.lerp(a.rightLeg, b.rightLeg, t), Transform.lerp(a.body, b.body, t), Transform.lerp(a.root, b.root, t));
        }

        private static Sample catmullRom(Frame p0, Frame p1, Frame p2, Frame p3, double t) {
            return new Sample(Transform.catmullRom(p0.leftLeg, p1.leftLeg, p2.leftLeg, p3.leftLeg, t), Transform.catmullRom(p0.rightLeg, p1.rightLeg, p2.rightLeg, p3.rightLeg, t), Transform.catmullRom(p0.body, p1.body, p2.body, p3.body, t), Transform.catmullRom(p0.root, p1.root, p2.root, p3.root, t));
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

        private void sanitizeLeg(Transform fallback) {
            this.x = GunRunAnimationConfigClient.clampFinite(this.x, fallback.x, -4.0, 4.0);
            this.y = GunRunAnimationConfigClient.clampFinite(this.y, fallback.y, -4.0, 4.0);
            this.z = GunRunAnimationConfigClient.clampFinite(this.z, fallback.z, -4.0, 4.0);
            this.rotX = GunRunAnimationConfigClient.clampFinite(this.rotX, fallback.rotX, -110.0, 110.0);
            this.rotY = GunRunAnimationConfigClient.clampFinite(this.rotY, fallback.rotY, -70.0, 70.0);
            this.rotZ = GunRunAnimationConfigClient.clampFinite(this.rotZ, fallback.rotZ, -70.0, 70.0);
        }

        private void sanitizeBody(Transform fallback) {
            this.x = 0.0;
            this.z = 0.0;
            this.y = GunRunAnimationConfigClient.clampFinite(this.y, fallback.y, -2.0, 2.0);
            this.rotX = GunRunAnimationConfigClient.clampFinite(this.rotX, fallback.rotX, -25.0, 25.0);
            this.rotY = GunRunAnimationConfigClient.clampFinite(this.rotY, fallback.rotY, -35.0, 35.0);
            this.rotZ = GunRunAnimationConfigClient.clampFinite(this.rotZ, fallback.rotZ, -30.0, 30.0);
        }

        private void sanitizeRoot(Transform fallback) {
            this.x = GunRunAnimationConfigClient.clampFinite(this.x, fallback.x, -4.0, 4.0);
            this.y = GunRunAnimationConfigClient.clampFinite(this.y, fallback.y, -4.0, 4.0);
            this.z = GunRunAnimationConfigClient.clampFinite(this.z, fallback.z, -4.0, 4.0);
            this.rotX = GunRunAnimationConfigClient.clampFinite(this.rotX, fallback.rotX, -20.0, 20.0);
            this.rotY = GunRunAnimationConfigClient.clampFinite(this.rotY, fallback.rotY, -15.0, 15.0);
            this.rotZ = GunRunAnimationConfigClient.clampFinite(this.rotZ, fallback.rotZ, -18.0, 18.0);
        }

        public static Transform lerp(Transform a, Transform b, double t) {
            return new Transform(GunRunAnimationConfigClient.lerp(a.x, b.x, t), GunRunAnimationConfigClient.lerp(a.y, b.y, t), GunRunAnimationConfigClient.lerp(a.z, b.z, t), GunRunAnimationConfigClient.lerp(a.rotX, b.rotX, t), GunRunAnimationConfigClient.lerp(a.rotY, b.rotY, t), GunRunAnimationConfigClient.lerp(a.rotZ, b.rotZ, t));
        }

        public static Transform catmullRom(Transform p0, Transform p1, Transform p2, Transform p3, double t) {
            return new Transform(GunRunAnimationConfigClient.catmullRom(p0.x, p1.x, p2.x, p3.x, t), GunRunAnimationConfigClient.catmullRom(p0.y, p1.y, p2.y, p3.y, t), GunRunAnimationConfigClient.catmullRom(p0.z, p1.z, p2.z, p3.z, t), GunRunAnimationConfigClient.catmullRom(p0.rotX, p1.rotX, p2.rotX, p3.rotX, t), GunRunAnimationConfigClient.catmullRom(p0.rotY, p1.rotY, p2.rotY, p3.rotY, t), GunRunAnimationConfigClient.catmullRom(p0.rotZ, p1.rotZ, p2.rotZ, p3.rotZ, t));
        }
    }

    public static enum Bone {
        LEFT_LEG("\u5de6\u817f"),
        RIGHT_LEG("\u53f3\u817f"),
        BODY("\u8eaf\u5e72"),
        ROOT("\u6574\u4f53\u6839\u8282\u70b9");

        private final String displayName;

        private Bone(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }
}
