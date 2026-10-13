package com.locknar.taczammocheck.tactical.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.model.geom.ModelPart;

public final class ActionAnimationSampler {
    private static final Map<Profile, RigConfig> CACHE = new ConcurrentHashMap<Profile, RigConfig>();

    private ActionAnimationSampler() {
    }

    public static void clear() {
        CACHE.clear();
    }

    public static Transform sample(Profile profile, String objectId, float normalizedProgress) {
        RigConfig rig = ActionAnimationSampler.getRig(profile);
        return rig.sample(profile, objectId, ActionAnimationSampler.clamp01(normalizedProgress) * profile.durationSeconds);
    }

    public static void applyPose(PoseStack poseStack, Transform transform) {
        if (poseStack == null || transform == null) {
            return;
        }
        poseStack.translate(transform.x, transform.y, transform.z);
        poseStack.mulPose(Axis.XP.rotationDegrees((float)transform.rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees((float)transform.rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float)transform.rotZ));
        float scale = (float)Math.max(0.01, transform.scale);
        poseStack.scale(scale, scale, scale);
    }

    public static void applyModelPart(ModelPart part, Transform transform) {
        if (part == null || transform == null) {
            return;
        }
        part.x += (float)transform.x;
        part.y += (float)transform.y;
        part.z += (float)transform.z;
        part.xRot += (float)Math.toRadians(transform.rotX);
        part.yRot += (float)Math.toRadians(transform.rotY);
        part.zRot += (float)Math.toRadians(transform.rotZ);
    }

    private static RigConfig getRig(Profile profile) {
        return CACHE.computeIfAbsent(profile, ActionAnimationSampler::defaultRig);
    }

    private static RigConfig defaultRig(Profile profile) {
        RigConfig rig = new RigConfig();
        rig.profile = profile.id;
        rig.keyframes = new ArrayList<KeyFrame>();
        for (float time : profile.keyTimes) {
            KeyFrame frame = new KeyFrame();
            frame.time = time;
            frame.transforms = new HashMap<String, Transform>();
            for (String object : profile.objects) {
                frame.transforms.put(object, ActionAnimationSampler.defaultTransform(profile, object, time));
            }
            rig.keyframes.add(frame);
        }
        return rig;
    }

    private static Transform defaultTransform(Profile profile, String object, float time) {
        return switch (profile) {
            default -> throw new IncompatibleClassChangeError();
            case Profile.FIRST_PERSON_KICK -> ActionAnimationSampler.defaultFirstPersonKick(object, time);
            case Profile.DOOR_OPEN -> ActionAnimationSampler.defaultDoorOpen(object, time);
            case Profile.THIRD_PERSON_KICK -> ActionAnimationSampler.defaultThirdPersonKick(object, time);
            case Profile.BACK_KICK -> ActionAnimationSampler.defaultBackKick(object, time);
        };
    }

    private static Transform defaultFirstPersonKick(String object, float time) {
        if (!"RIGHT_LEG".equals(object)) {
            return new Transform();
        }
        if (Math.abs(time - 0.0f) < 0.001f) {
            return new Transform(0.09000000000000002, -1.5200000000000002, -1.05, 28.0, 2.0, -6.0, 1.55);
        }
        if (Math.abs(time - 0.17f) < 0.001f) {
            return new Transform(0.11000000000000001, -1.2400000000000007, -0.9599999999999997, -42.0, -2.0, -4.0, 1.95);
        }
        if (Math.abs(time - 0.29f) < 0.001f) {
            return new Transform(0.11000000000000001, -1.2800000000000007, -1.37, -42.0, -2.0, -4.0, 1.95);
        }
        if (Math.abs(time - 0.52f) < 0.001f) {
            return new Transform(0.12, -1.0799999999999998, -0.059999999999999255, -85.0, 2.0, -6.0, 1.55);
        }
        return new Transform();
    }

    private static Transform defaultDoorOpen(String object, float time) {
        if (!"LEFT_HAND".equals(object)) {
            return new Transform();
        }
        if (Math.abs(time - 0.0f) < 0.001f) {
            return new Transform(0.07000000000000031, 0.03000000000000009, -0.10999999999999935, -7.0, -22.0, -15.0, 0.76);
        }
        if (Math.abs(time - 0.12f) < 0.001f) {
            return new Transform(0.07000000000000003, -0.0999999999999998, -0.26999999999999935, -41.0, -21.0, -28.0, 0.86);
        }
        if (Math.abs(time - 0.26f) < 0.001f) {
            return new Transform(0.07000000000000026, 0.09000000000000008, -0.26999999999999946, -41.0, -21.0, -28.0, 0.76);
        }
        return new Transform();
    }

    private static Transform defaultThirdPersonKick(String object, float time) {
        boolean peak = time > 0.05f && time < 0.45f;
        double k = peak ? 1.0 : 0.0;
        return switch (object) {
            case "RIGHT_LEG" -> new Transform(0.0, 0.0, 0.0, -117.0 * k, 5.7 * k, -4.0 * k, 1.0);
            case "LEFT_LEG" -> new Transform(0.0, 0.0, 0.0, 13.8 * k, 0.0, 0.0, 1.0);
            case "BODY" -> new Transform(0.0, 0.0, 0.0, -5.7 * k, 4.0 * k, 0.0, 1.0);
            default -> new Transform();
        };
    }

    private static Transform defaultBackKick(String object, float time) {
        if (Math.abs(time - 0.0f) < 0.001f) {
            return switch (object) {
                case "SUPPORT_LEG" -> new Transform(0.0, 0.0, 0.0, -0.0, -0.0, 0.0, 1.0);
                case "KICK_LEG" -> new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
                case "TURN" -> new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
                case "BODY" -> new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
                default -> new Transform();
            };
        }
        if (Math.abs(time - 0.2f) < 0.001f) {
            return switch (object) {
                case "SUPPORT_LEG" -> new Transform(0.0, 0.0, 0.0, -12.5, -2.0, 0.0, 1.0);
                case "KICK_LEG" -> new Transform(0.0, 0.0, 0.0, -4.0, 5.0, 3.0, 1.0);
                case "TURN" -> new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
                case "BODY" -> new Transform(0.0, 0.0, 0.0, 2.8, 5.2, 0.0, 1.0);
                default -> new Transform();
            };
        }
        if (Math.abs(time - 0.3f) < 0.001f) {
            return switch (object) {
                case "SUPPORT_LEG" -> new Transform(0.0, 0.0, 0.0, -12.5, -2.0, 0.0, 1.0);
                case "KICK_LEG" -> new Transform(1.5800000000000012, 0.04, -4.179999999999955, 92.0, 0.0, 3.0, 1.0);
                case "TURN" -> new Transform(0.0, 0.0, 0.0, 0.0, 170.0, 0.0, 1.0);
                case "BODY" -> new Transform(0.0, 0.0, 0.0, 2.8, 5.2, 0.0, 1.0);
                default -> new Transform();
            };
        }
        if (Math.abs(time - 0.55f) < 0.001f) {
            return switch (object) {
                case "SUPPORT_LEG" -> new Transform(0.0, 0.0, 0.0, -12.5, -2.0, 0.0, 1.0);
                case "KICK_LEG" -> new Transform(2.299999999999995, 0.13999999999999999, 1.310000000000001, 88.0, 5.0, 3.0, 1.0);
                case "TURN" -> new Transform(0.0, 0.0, 0.0, 0.0, 170.0, 0.0, 1.0);
                case "BODY" -> new Transform(0.0, 0.0, 0.0, 2.8, 5.2, 0.0, 1.0);
                default -> new Transform();
            };
        }
        if (Math.abs(time - 0.85f) < 0.001f) {
            return switch (object) {
                case "SUPPORT_LEG" -> new Transform(0.0, 0.0, 0.0, -0.0, -0.0, 0.0, 1.0);
                case "KICK_LEG" -> new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
                case "TURN" -> new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
                case "BODY" -> new Transform(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0);
                default -> new Transform();
            };
        }
        return new Transform();
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    public static enum Profile {
        FIRST_PERSON_KICK("first_person_kick", 0.52f, new float[]{0.0f, 0.17f, 0.29f, 0.52f}, new String[]{"RIGHT_LEG"}),
        DOOR_OPEN("door_open", 0.26f, new float[]{0.0f, 0.12f, 0.26f}, new String[]{"LEFT_HAND"}),
        THIRD_PERSON_KICK("third_person_kick", 0.52f, new float[]{0.0f, 0.17f, 0.29f, 0.52f}, new String[]{"BODY", "RIGHT_LEG", "LEFT_LEG"}),
        BACK_KICK("back_kick", 0.85f, new float[]{0.0f, 0.2f, 0.3f, 0.55f, 0.85f}, new String[]{"TURN", "BODY", "KICK_LEG", "SUPPORT_LEG"});

        public final String id;
        public final float durationSeconds;
        public final float[] keyTimes;
        public final String[] objects;

        private Profile(String id, float durationSeconds, float[] keyTimes, String[] objects) {
            this.id = id;
            this.durationSeconds = durationSeconds;
            this.keyTimes = keyTimes;
            this.objects = objects;
        }
    }

    private static final class RigConfig {
        public String profile = "";
        public List<KeyFrame> keyframes = new ArrayList<KeyFrame>();
        private transient boolean normalized;

        private RigConfig() {
        }

        void normalize(Profile profileDef) {
            if (this.normalized) {
                return;
            }
            if (this.keyframes == null) {
                this.keyframes = new ArrayList<KeyFrame>();
            }
            RigConfig defaults = ActionAnimationSampler.defaultRig(profileDef);
            for (float time : profileDef.keyTimes) {
                KeyFrame frame = this.keyframeAt(profileDef, time);
                KeyFrame def = defaults.keyframeAt(profileDef, time);
                for (String object : profileDef.objects) {
                    if (frame.get(object) != null) continue;
                    frame.set(object, def.get(object).copy());
                }
            }
            this.keyframes.sort((a, b) -> Float.compare(a.time, b.time));
            this.normalized = true;
        }

        KeyFrame keyframeAt(Profile profileDef, float time) {
            for (KeyFrame frame : this.keyframes) {
                if (!(Math.abs(frame.time - time) < 0.001f)) continue;
                if (frame.transforms == null) {
                    frame.transforms = new HashMap<String, Transform>();
                }
                return frame;
            }
            KeyFrame frame = new KeyFrame();
            frame.time = time;
            frame.transforms = new HashMap<String, Transform>();
            for (String object : profileDef.objects) {
                frame.transforms.put(object, ActionAnimationSampler.defaultTransform(profileDef, object, time));
            }
            this.keyframes.add(frame);
            this.keyframes.sort((a, b) -> Float.compare(a.time, b.time));
            return frame;
        }

        Transform sample(Profile profileDef, String object, float seconds) {
            this.normalize(profileDef);
            seconds = Math.max(0.0f, Math.min(profileDef.durationSeconds, seconds));
            KeyFrame previous = this.keyframes.get(0);
            KeyFrame next = this.keyframes.get(this.keyframes.size() - 1);
            for (KeyFrame frame : this.keyframes) {
                if (frame.time <= seconds) {
                    previous = frame;
                }
                if (!(frame.time >= seconds)) continue;
                next = frame;
                break;
            }
            if (previous == next || Math.abs(next.time - previous.time) < 0.001f) {
                return previous.get(object).copy();
            }
            double t = (double)(seconds - previous.time) / (double)(next.time - previous.time);
            t = t * t * (3.0 - 2.0 * t);
            return Transform.lerp(previous.get(object), next.get(object), t);
        }
    }

    public static final class Transform {
        public double x;
        public double y;
        public double z;
        public double rotX;
        public double rotY;
        public double rotZ;
        public double scale = 1.0;

        public Transform() {
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

        static Transform lerp(Transform a, Transform b, double t) {
            if (a == null) {
                a = new Transform();
            }
            if (b == null) {
                b = new Transform();
            }
            return new Transform(Transform.lerp(a.x, b.x, t), Transform.lerp(a.y, b.y, t), Transform.lerp(a.z, b.z, t), Transform.lerp(a.rotX, b.rotX, t), Transform.lerp(a.rotY, b.rotY, t), Transform.lerp(a.rotZ, b.rotZ, t), Transform.lerp(a.scale, b.scale, t));
        }

        private static double lerp(double a, double b, double t) {
            return a + (b - a) * t;
        }
    }

    private static final class KeyFrame {
        public float time;
        public Map<String, Transform> transforms = new HashMap<String, Transform>();

        private KeyFrame() {
        }

        Transform get(String object) {
            if (this.transforms == null) {
                this.transforms = new HashMap<String, Transform>();
            }
            return this.transforms.get(object);
        }

        void set(String object, Transform transform) {
            if (this.transforms == null) {
                this.transforms = new HashMap<String, Transform>();
            }
            this.transforms.put(object, transform);
        }
    }
}
