package com.locknar.taczammocheck.tactical.client;

public final class WeaponVisualConfigClient {
    public static final float ANIMATION_DURATION_SECONDS = 0.3f;
    public static final float[] KEY_TIMES = new float[]{0.0f, 0.15f, 0.3f};
    private static final VisualData DATA = WeaponVisualConfigClient.defaults();

    private WeaponVisualConfigClient() {
    }

    public static VisualData get() {
        return DATA;
    }

    public static Transform sample(Animation animation, Bone bone, float normalizedProgress) {
        if (animation == null || animation.frames == null || animation.frames.length != 3) {
            return new Transform();
        }
        float seconds = WeaponVisualConfigClient.clamp01(normalizedProgress) * 0.3f;
        int before = 0;
        int after = animation.frames.length - 1;
        for (int i = 0; i < KEY_TIMES.length; ++i) {
            if (KEY_TIMES[i] <= seconds) {
                before = i;
            }
            if (!(KEY_TIMES[i] >= seconds)) continue;
            after = i;
            break;
        }
        Transform a = animation.frames[before].get(bone);
        Transform b = animation.frames[after].get(bone);
        if (before == after) {
            return a.copy();
        }
        double t = (seconds - KEY_TIMES[before]) / (KEY_TIMES[after] - KEY_TIMES[before]);
        t = t * t * (3.0 - 2.0 * t);
        return Transform.lerp(a, b, t);
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static VisualData defaults() {
        VisualData value = new VisualData();
        value.frontGun = new Transform(0.19, 0.57, -0.13, 0.0, -15.0, 35.0, 0.47);
        value.holster = new Animation(new Frame[]{new Frame(new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0), new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0), new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)), new Frame(new Transform(0.0, 0.0, 0.0, 2.0, 5.0, 0.0, 1.0), new Transform(-0.3, 0.7, 0.0, 25.0, -23.0, 10.0, 1.0), new Transform(0.3, 0.7, 0.0, 22.0, 8.0, -14.0, 1.0)), new Frame(new Transform(0.0, 0.0, 0.0, 5.0, 10.0, 0.0, 1.0), new Transform(-0.8, 1.8, 0.3, 18.0, -50.0, 17.0, 1.0), new Transform(0.8, 1.8, 0.3, 62.0, 15.0, -22.0, 1.0))});
        value.draw = new Animation(new Frame[]{new Frame(new Transform(0.0, 0.0, 0.0, 5.0, 10.0, 0.0, 1.0), new Transform(-0.8, 1.8, 0.3, 68.0, -20.0, 17.0, 1.0), new Transform(0.8, 1.8, 0.3, 62.0, 15.0, -22.0, 1.0)), new Frame(new Transform(0.0, 0.0, 0.0, 2.0, 5.0, 0.0, 1.0), new Transform(-0.3, 0.7, 0.0, 25.0, -8.0, 10.0, 1.0), new Transform(0.3, 0.7, 0.0, 22.0, 8.0, -14.0, 1.0)), new Frame(new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0), new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0), new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0))});
        return value;
    }

    public static final class VisualData {
        public Transform frontGun;
        public Animation holster;
        public Animation draw;
    }

    public static final class Animation {
        public final Frame[] frames;

        private Animation(Frame[] frames) {
            this.frames = frames;
        }
    }

    public static final class Frame {
        public final Transform body;
        public final Transform rightArm;
        public final Transform leftArm;

        private Frame(Transform body, Transform rightArm, Transform leftArm) {
            this.body = body;
            this.rightArm = rightArm;
            this.leftArm = leftArm;
        }

        public Transform get(Bone bone) {
            return switch (bone) {
                default -> throw new IncompatibleClassChangeError();
                case Bone.BODY -> this.body;
                case Bone.RIGHT_ARM -> this.rightArm;
                case Bone.LEFT_ARM -> this.leftArm;
            };
        }
    }

    public static final class Transform {
        public final double x;
        public final double y;
        public final double z;
        public final double rotX;
        public final double rotY;
        public final double rotZ;
        public final double scale;

        public Transform() {
            this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
        }

        public Transform(double x, double y, double z, double rotX, double rotY, double rotZ, double scale) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rotX = rotX;
            this.rotY = rotY;
            this.rotZ = rotZ;
            this.scale = scale;
        }

        public Transform copy() {
            return new Transform(this.x, this.y, this.z, this.rotX, this.rotY, this.rotZ, this.scale);
        }

        public static Transform lerp(Transform a, Transform b, double t) {
            return new Transform(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t, a.rotX + (b.rotX - a.rotX) * t, a.rotY + (b.rotY - a.rotY) * t, a.rotZ + (b.rotZ - a.rotZ) * t, a.scale + (b.scale - a.scale) * t);
        }
    }

    public static enum Bone {
        BODY,
        RIGHT_ARM,
        LEFT_ARM;

    }
}
