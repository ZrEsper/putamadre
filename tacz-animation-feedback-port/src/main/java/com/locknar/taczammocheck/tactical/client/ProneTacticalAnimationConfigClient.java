package com.locknar.taczammocheck.tactical.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.locknar.taczammocheck.ConfigMigration;
import com.locknar.taczammocheck.tactical.LocknarDoorKickMod;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public final class ProneTacticalAnimationConfigClient {
    public static final int FRAME_COUNT = 7;
    public static final int ANGLE_MIN = -180;
    public static final int ANGLE_MAX = 180;
    public static final int ANGLE_STEP = 10;
    public static final int ANGLE_COUNT = 37;
    private static final int SCHEMA_VERSION = 11;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String RESOURCE = "/assets/locknar_doorkick/doorkick_data/prone_tactical_animations.json";
    private static final Path FILE = ConfigMigration.animationOverride("prone_tactical_animations.json");
    private static volatile Data data;

    private ProneTacticalAnimationConfigClient() {
    }

    public static Data get() {
        Data current = data;
        if (current != null) {
            return current;
        }
        Class<ProneTacticalAnimationConfigClient> clazz = ProneTacticalAnimationConfigClient.class;
        synchronized (ProneTacticalAnimationConfigClient.class) {
            if (data == null) {
                data = ProneTacticalAnimationConfigClient.loadData();
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return data;
        }
    }

    public static synchronized void load() {
        data = ProneTacticalAnimationConfigClient.loadData();
    }

    public static Path getFile() {
        return FILE;
    }

    public static Animation animation(Motion motion) {
        Data value = ProneTacticalAnimationConfigClient.get();
        return switch (motion) {
            default -> throw new IncompatibleClassChangeError();
            case Motion.PRONE_ENTER -> value.proneEnter;
            case Motion.PRONE_EXIT -> value.proneExit;
            case Motion.NONE -> null;
        };
    }

    public static int angleAtIndex(int index) {
        int safe = Math.max(0, Math.min(36, index));
        return -180 + safe * 10;
    }

    public static int indexForAngle(int angle) {
        int clamped = Math.max(-180, Math.min(180, angle));
        return Math.max(0, Math.min(36, (int)Math.round((double)(clamped - -180) / 10.0)));
    }

    public static Sample sample(Motion motion, double progress) {
        Animation animation = ProneTacticalAnimationConfigClient.animation(motion);
        if (animation == null || animation.frames == null || animation.frames.length != 7) {
            return Sample.ZERO;
        }
        double clamped = ProneTacticalAnimationConfigClient.clamp01(progress);
        double scaled = clamped * 6.0;
        int i1 = Math.max(0, Math.min(6, (int)Math.floor(scaled)));
        int i2 = Math.max(0, Math.min(6, i1 + 1));
        int i0 = Math.max(0, i1 - 1);
        int i3 = Math.min(6, i2 + 1);
        double local = ProneTacticalAnimationConfigClient.clamp01(scaled - (double)i1);
        return Sample.hermite(animation.frames[i0], animation.frames[i1], animation.frames[i2], animation.frames[i3], local);
    }

    public static Sample sampleAngle(double angle) {
        Data current = ProneTacticalAnimationConfigClient.get();
        if (current.anglePoses == null || current.anglePoses.length != 37) {
            return Sample.ZERO;
        }
        double clamped = Math.max(-180.0, Math.min(180.0, angle));
        Frame seam = Frame.circularLerp(current.anglePoses[0].pose, current.anglePoses[36].pose, 0.5);
        if (clamped <= -179.9999999 || clamped >= 179.9999999) {
            return Sample.fromFrame(seam);
        }
        double scaled = (clamped - -180.0) / 10.0;
        int i1 = Math.max(0, Math.min(35, (int)Math.floor(scaled)));
        int i2 = i1 + 1;
        double local = ProneTacticalAnimationConfigClient.clamp01(scaled - (double)i1);
        Frame p1 = ProneTacticalAnimationConfigClient.curveAngleFrame(current, i1, seam);
        Frame p2 = ProneTacticalAnimationConfigClient.curveAngleFrame(current, i2, seam);
        Frame p0 = i1 == 0 ? ProneTacticalAnimationConfigClient.curveAngleFrame(current, 35, seam) : ProneTacticalAnimationConfigClient.curveAngleFrame(current, i1 - 1, seam);
        Frame p3 = i2 == 36 ? ProneTacticalAnimationConfigClient.curveAngleFrame(current, 1, seam) : ProneTacticalAnimationConfigClient.curveAngleFrame(current, i2 + 1, seam);
        return Sample.hermite(p0, p1, p2, p3, local);
    }

    private static Frame curveAngleFrame(Data current, int index, Frame seam) {
        if (index <= 0 || index >= 36) {
            return seam;
        }
        return current.anglePoses[index].pose;
    }

    public static double sampleAngleBodyYaw(double angle) {
        return 0.0;
    }

    private static Data loadData() {
        Data builtIn=loadBuiltIn();if(!Files.isRegularFile(FILE))return builtIn;
        try(BufferedReader reader=Files.newBufferedReader(FILE,StandardCharsets.UTF_8)) {return normalize(GSON.fromJson(reader,Data.class),builtIn);} catch(Exception e){LocknarDoorKickMod.LOGGER.warn("Unable to load prone configuration {}",FILE,e);return builtIn;}
    }

    private static Data loadBuiltIn() {
        Data loaded;
        block13: {
            loaded = null;
            try (InputStream stream = ProneTacticalAnimationConfigClient.class.getResourceAsStream(RESOURCE);){
                if (stream == null) break block13;
                try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
                    loaded = (Data)GSON.fromJson((Reader)reader, Data.class);
                }
            }
            catch (Throwable throwable) {
                LocknarDoorKickMod.LOGGER.warn("Failed to read built-in prone tactical animation resource '{}'.", (Object)RESOURCE, (Object)throwable);
            }
        }
        Data fallback = ProneTacticalAnimationConfigClient.fallbackDefaults();
        return ProneTacticalAnimationConfigClient.normalize(loaded == null ? fallback : loaded, fallback);
    }

    private static Data normalize(Data value, Data fallback) {
        int sourceSchema;
        if (fallback == null) {
            fallback = ProneTacticalAnimationConfigClient.fallbackDefaults();
        }
        if (value == null) {
            value = fallback.copy();
        }
        boolean migrateSupineOrientation = (sourceSchema = value.schemaVersion) < 2;
        boolean migrateRearStraightening = sourceSchema < 3;
        boolean migrateRollOnlyBodyLine = sourceSchema < 4;
        boolean migratePostProneRollAxis = sourceSchema < 5;
        boolean migrateOppositeCameraRoll = sourceSchema < 6;
        boolean migrateTransitionDurations = sourceSchema < 8;
        value.proneEnter = ProneTacticalAnimationConfigClient.normalizeAnimation(value.proneEnter, fallback.proneEnter, 0.8);
        value.proneExit = ProneTacticalAnimationConfigClient.normalizeAnimation(value.proneExit, fallback.proneExit, 0.7);
        if (migrateTransitionDurations) {
            if (Math.abs(value.proneEnter.duration - 0.58) < 1.0E-4) {
                value.proneEnter.duration = 0.8;
            }
            if (Math.abs(value.proneExit.duration - 0.5) < 1.0E-4) {
                value.proneExit.duration = 0.7;
            }
        }
        if ((value.anglePoses == null || value.anglePoses.length == 0) && value.hasLegacyAimAnimations()) {
            value.anglePoses = ProneTacticalAnimationConfigClient.convertLegacyAngles(value);
        }
        value.anglePoses = ProneTacticalAnimationConfigClient.normalizeAngles(value.anglePoses, fallback.anglePoses, sourceSchema);
        if (migrateSupineOrientation) {
            ProneTacticalAnimationConfigClient.migrateSupineBodyYaw(value.anglePoses);
        }
        if (migrateRearStraightening) {
            ProneTacticalAnimationConfigClient.migrateRearSupineStraightening(value.anglePoses);
        }
        if (migrateRollOnlyBodyLine) {
            ProneTacticalAnimationConfigClient.migrateRollOnlyBodyLine(value.anglePoses);
        }
        if (migratePostProneRollAxis) {
            ProneTacticalAnimationConfigClient.migratePostProneRollAxis(value.anglePoses);
        }
        if (migrateOppositeCameraRoll) {
            ProneTacticalAnimationConfigClient.migrateOppositeCameraRoll(value.anglePoses);
        }
        ProneTacticalAnimationConfigClient.enforcePostProneRollAxis(value.anglePoses);
        if (sourceSchema < 9) {
            value.anglePoses = fallback.copy().anglePoses;
            value.proneEnter.duration = Math.max(0.1, value.proneEnter.duration * 0.5);
            value.proneExit.duration = Math.max(0.1, value.proneExit.duration * 0.5);
        }
        if (sourceSchema < 11) {
            value.anglePoses = fallback.copy().anglePoses;
        }
        value.schemaVersion = 11;
        value.aimLeft = null;
        value.aimRight = null;
        value.backLeft = null;
        value.backRight = null;
        return value;
    }

    private static void migrateSupineBodyYaw(AnglePose[] poses) {
        if (poses == null) {
            return;
        }
        for (AnglePose entry : poses) {
            double abs;
            if (entry == null || entry.bodyYaw == null || !Double.isFinite(entry.bodyYaw) || (abs = Math.abs(entry.angle)) <= 92.0) continue;
            double progress = ProneTacticalAnimationConfigClient.clamp01((abs - 92.0) / 88.0);
            double keep = 1.0 - ProneTacticalAnimationConfigClient.smooth(progress);
            entry.bodyYaw = ProneTacticalAnimationConfigClient.clampFinite(entry.bodyYaw * keep, 0.0, -180.0, 180.0);
        }
    }

    private static void migrateRearSupineStraightening(AnglePose[] poses) {
        if (poses == null) {
            return;
        }
        for (AnglePose entry : poses) {
            double abs;
            if (entry == null || entry.pose == null || (abs = Math.abs(entry.angle)) < 140.0) continue;
            double p = ProneTacticalAnimationConfigClient.smooth(ProneTacticalAnimationConfigClient.clamp01((abs - 140.0) / 40.0));
            double keep = 1.0 - p;
            entry.pose.body.rotY *= keep;
            entry.pose.body.rotZ *= keep;
            entry.pose.body.rotX *= keep;
            entry.pose.root.rotX *= keep;
            if (!(abs >= 179.5)) continue;
            entry.bodyYaw = 0.0;
            entry.pose.body.rotX = 0.0;
            entry.pose.body.rotY = 0.0;
            entry.pose.body.rotZ = 0.0;
            entry.pose.root.rotX = 0.0;
            entry.pose.root.rotZ = entry.angle < 0.0 ? -180.0 : 180.0;
        }
    }

    private static void migrateRollOnlyBodyLine(AnglePose[] poses) {
        if (poses == null) {
            return;
        }
        for (AnglePose entry : poses) {
            if (entry == null || entry.pose == null) continue;
            double yaw = entry.bodyYaw == null || !Double.isFinite(entry.bodyYaw) ? 0.0 : entry.bodyYaw;
            double roll = entry.pose.root.rotZ;
            if (!Double.isFinite(roll)) {
                roll = 0.0;
            }
            entry.pose.root.rotZ = ProneTacticalAnimationConfigClient.clampFinite(roll + yaw, entry.angle, -180.0, 180.0);
            entry.bodyYaw = 0.0;
            entry.pose.root.rotX = 0.0;
            entry.pose.root.rotY = 0.0;
            double abs = Math.abs(entry.angle);
            if (abs >= 140.0) {
                double keep = 1.0 - ProneTacticalAnimationConfigClient.smooth(ProneTacticalAnimationConfigClient.clamp01((abs - 140.0) / 40.0));
                entry.pose.body.rotX *= keep;
                entry.pose.body.rotY *= keep;
                entry.pose.body.rotZ *= keep;
            }
            if (abs < 0.5) {
                entry.pose.root.rotZ = 0.0;
            }
            if (!(abs >= 179.5)) continue;
            entry.pose.root.rotZ = entry.angle < 0.0 ? -180.0 : 180.0;
            entry.pose.body.rotX = 0.0;
            entry.pose.body.rotY = 0.0;
            entry.pose.body.rotZ = 0.0;
        }
    }

    private static void migratePostProneRollAxis(AnglePose[] poses) {
        if (poses == null) {
            return;
        }
        for (AnglePose entry : poses) {
            if (entry == null || entry.pose == null) continue;
            double oldRoll = Double.isFinite(entry.pose.root.rotZ) ? entry.pose.root.rotZ : entry.angle;
            entry.bodyYaw = 0.0;
            entry.pose.root.rotX = 0.0;
            entry.pose.root.rotY = ProneTacticalAnimationConfigClient.clampFinite(oldRoll, entry.angle, -180.0, 180.0);
            entry.pose.root.rotZ = 0.0;
            double abs = Math.abs(entry.angle);
            if (abs < 0.5) {
                entry.pose.root.rotY = 0.0;
            }
            if (!(abs >= 179.5)) continue;
            entry.pose.root.rotY = entry.angle < 0.0 ? 180.0 : -180.0;
            entry.pose.body.rotX = 0.0;
            entry.pose.body.rotY = 0.0;
            entry.pose.body.rotZ = 0.0;
        }
    }

    private static void migrateOppositeCameraRoll(AnglePose[] poses) {
        if (poses == null) {
            return;
        }
        for (AnglePose entry : poses) {
            if (entry == null || entry.pose == null) continue;
            entry.bodyYaw = 0.0;
            entry.pose.root.rotX = 0.0;
            entry.pose.root.rotY = ProneTacticalAnimationConfigClient.clampFinite(-entry.pose.root.rotY, -entry.angle, -180.0, 180.0);
            entry.pose.root.rotZ = 0.0;
            if (Math.abs(entry.angle) < 0.5) {
                entry.pose.root.rotY = 0.0;
            }
            if (!(Math.abs(entry.angle) >= 179.5)) continue;
            entry.pose.root.rotY = entry.angle < 0.0 ? 180.0 : -180.0;
        }
    }

    private static void enforcePostProneRollAxis(AnglePose[] poses) {
        if (poses == null) {
            return;
        }
        for (AnglePose entry : poses) {
            if (entry == null || entry.pose == null) continue;
            entry.bodyYaw = 0.0;
            entry.pose.root.rotX = 0.0;
            entry.pose.root.rotZ = 0.0;
            double abs = Math.abs(entry.angle);
            if (!(abs < 0.5)) continue;
            entry.pose.root.rotY = 0.0;
        }
    }

    private static Animation normalizeAnimation(Animation value, Animation fallback, double fallbackDuration) {
        if (value == null) {
            value = fallback.copy();
        }
        value.duration = ProneTacticalAnimationConfigClient.clampFinite(value.duration, fallbackDuration, 0.1, 5.0);
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
        return value;
    }

    private static AnglePose[] normalizeAngles(AnglePose[] values, AnglePose[] fallback, int sourceSchema) {
        AnglePose[] result = new AnglePose[37];
        if (values != null) {
            for (AnglePose entry : values) {
                double migratedYaw;
                int rounded;
                if (entry == null || entry.pose == null || !Double.isFinite(entry.angle) || (rounded = (int)Math.round(entry.angle / 10.0) * 10) < -180 || rounded > 180) continue;
                int index = ProneTacticalAnimationConfigClient.indexForAngle(rounded);
                Frame migratedPose = entry.pose.copy();
                double d = migratedYaw = entry.bodyYaw != null && Double.isFinite(entry.bodyYaw) ? entry.bodyYaw : 0.0;
                if (!(sourceSchema >= 2 || entry.bodyYaw != null && Double.isFinite(entry.bodyYaw))) {
                    migratedYaw = migratedPose.root.rotY;
                    migratedPose.root.rotY = 0.0;
                }
                result[index] = new AnglePose(ProneTacticalAnimationConfigClient.angleAtIndex(index), migratedYaw, migratedPose);
            }
        }
        for (int i = 0; i < 37; ++i) {
            double fallbackYaw;
            Frame fallbackFrame = fallback[i].pose;
            double d = fallbackYaw = fallback[i].bodyYaw == null ? 0.0 : fallback[i].bodyYaw;
            if (result[i] == null) {
                result[i] = new AnglePose(ProneTacticalAnimationConfigClient.angleAtIndex(i), fallbackYaw, fallbackFrame.copy());
            }
            result[i].angle = ProneTacticalAnimationConfigClient.angleAtIndex(i);
            result[i].bodyYaw = ProneTacticalAnimationConfigClient.clampFinite(result[i].bodyYaw == null ? fallbackYaw : result[i].bodyYaw, fallbackYaw, -180.0, 180.0);
            result[i].pose.normalize(fallbackFrame);
        }
        return result;
    }

    private static Data fallbackDefaults() {
        int i;
        Data result = new Data();
        result.schemaVersion = 11;
        result.proneEnter = ProneTacticalAnimationConfigClient.blank(0.8);
        result.proneExit = ProneTacticalAnimationConfigClient.blank(0.7);
        for (i = 0; i < 7; ++i) {
            double p = (double)i / 6.0;
            double s = ProneTacticalAnimationConfigClient.smooth(p);
            result.proneEnter.frames[i] = ProneTacticalAnimationConfigClient.frame(ProneTacticalAnimationConfigClient.transform(0.1 * (1.0 - s), -0.35 * (1.0 - s), 0.0, -18.0 * (1.0 - s), -8.0, -5.0), ProneTacticalAnimationConfigClient.transform(-0.1 * (1.0 - s), -0.15 * (1.0 - s), 0.0, 12.0 * (1.0 - s), 6.0, 5.0), ProneTacticalAnimationConfigClient.transform(0.0, -0.2 * (1.0 - s), 0.0, -8.0 * (1.0 - s), 0.0, 0.0), new Transform(), ProneTacticalAnimationConfigClient.transform(0.0, 0.25 * Math.sin(Math.PI * p), 0.0, -90.0 * (1.0 - s), 0.0, -3.0 * Math.sin(Math.PI * p)));
            result.proneExit.frames[i] = ProneTacticalAnimationConfigClient.frame(ProneTacticalAnimationConfigClient.transform(0.18 * (1.0 - s), -0.3 * (1.0 - s), 0.0, -22.0 * (1.0 - s), -6.0, -4.0), ProneTacticalAnimationConfigClient.transform(-0.18 * (1.0 - s), -0.15 * (1.0 - s), 0.0, 18.0 * (1.0 - s), 5.0, 4.0), ProneTacticalAnimationConfigClient.transform(0.0, -0.15 * (1.0 - s), 0.0, -6.0 * (1.0 - s), 0.0, 0.0), new Transform(), ProneTacticalAnimationConfigClient.transform(0.0, 0.18 * Math.sin(Math.PI * p), 0.0, 90.0 * (1.0 - s), 0.0, 2.0 * Math.sin(Math.PI * p)));
        }
        result.anglePoses = new AnglePose[37];
        for (i = 0; i < 37; ++i) {
            int angle = ProneTacticalAnimationConfigClient.angleAtIndex(i);
            Frame pose = ProneTacticalAnimationConfigClient.proceduralDefaultAngle(angle);
            pose.root.rotX = 0.0;
            pose.root.rotY = -angle;
            pose.root.rotZ = 0.0;
            result.anglePoses[i] = new AnglePose(angle, 0.0, pose);
        }
        return result;
    }

    private static Frame proceduralDefaultAngle(int angle) {
        Frame pose;
        int side;
        double abs = Math.abs(angle);
        int n = side = angle < 0 ? -1 : 1;
        if (abs <= 92.0) {
            double p = ProneTacticalAnimationConfigClient.clamp01((abs - 24.0) / 68.0);
            double s = ProneTacticalAnimationConfigClient.smooth(p);
            Frame left = ProneTacticalAnimationConfigClient.frame(ProneTacticalAnimationConfigClient.transform(0.25 * s, -0.25 * s, -0.3 * s, -20.0 * s, -15.0 * s, -12.0 * s), ProneTacticalAnimationConfigClient.transform(-0.1 * s, -0.05 * s, 0.35 * s, 24.0 * s, -8.0 * s, 8.0 * s), ProneTacticalAnimationConfigClient.transform(0.0, -0.18 * s, 0.0, -2.0 * s, -10.0 * s, -8.0 * s), new Transform(), ProneTacticalAnimationConfigClient.transform(0.0, -0.08 * s, 0.0, 0.0, 0.0, -20.0 * s));
            pose = side < 0 ? left : left.mirrored();
            double bodyTurn = angle == 0 ? 0.0 : (double)side * 58.0 * s;
        } else {
            double p = ProneTacticalAnimationConfigClient.clamp01((abs - 92.0) / 88.0);
            double s = ProneTacticalAnimationConfigClient.smooth(p);
            Frame sideStart = ProneTacticalAnimationConfigClient.proceduralDefaultAngle(side * 92);
            Frame leftBack = ProneTacticalAnimationConfigClient.frame(ProneTacticalAnimationConfigClient.transform(0.5, -0.35, -0.65, -38.0, -24.0, -25.0), ProneTacticalAnimationConfigClient.transform(-0.35, -0.1, 0.6, 42.0, -14.0, 18.0), ProneTacticalAnimationConfigClient.transform(0.0, -0.25, 0.0, 0.0, 0.0, 0.0), new Transform(), ProneTacticalAnimationConfigClient.transform(0.0, -0.14, 0.05, 0.0, 0.0, -180.0));
            Frame target = side < 0 ? leftBack : leftBack.mirrored();
            pose = Frame.lerp(sideStart, target, s);
            double bodyTurn = (double)side * ProneTacticalAnimationConfigClient.lerp(58.0, 0.0, s);
        }
        pose.root.rotX = 0.0;
        pose.root.rotY = -angle;
        pose.root.rotZ = 0.0;
        return pose;
    }

    private static AnglePose[] convertLegacyAngles(Data legacy) {
        AnglePose[] result = new AnglePose[37];
        for (int i = 0; i < 37; ++i) {
            Frame pose;
            double p, bodyTurn;
            Animation source;
            int side;
            int angle = ProneTacticalAnimationConfigClient.angleAtIndex(i);
            double abs = Math.abs(angle);
            int n = side = angle < 0 ? -1 : 1;
            if (abs <= 92.0) {
                p = ProneTacticalAnimationConfigClient.clamp01((abs - 24.0) / 68.0);
                source = side < 0 ? legacy.aimLeft : legacy.aimRight;
                pose = ProneTacticalAnimationConfigClient.sampleAnimationFrame(source, p);
                bodyTurn = angle == 0 ? 0.0 : (double)side * 58.0 * ProneTacticalAnimationConfigClient.smooth(p);
            } else {
                p = ProneTacticalAnimationConfigClient.clamp01((abs - 92.0) / 88.0);
                source = side < 0 ? legacy.backLeft : legacy.backRight;
                pose = ProneTacticalAnimationConfigClient.sampleAnimationFrame(source, p);
                bodyTurn = (double)side * ProneTacticalAnimationConfigClient.lerp(58.0, 180.0, ProneTacticalAnimationConfigClient.smooth(p));
            }
            pose.root.rotX = 0.0;
            pose.root.rotY = -angle;
            pose.root.rotZ = 0.0;
            result[i] = new AnglePose(angle, 0.0, pose);
        }
        return result;
    }

    private static Frame sampleAnimationFrame(Animation animation, double progress) {
        if (animation == null || animation.frames == null || animation.frames.length == 0) {
            return new Frame();
        }
        double clamped = ProneTacticalAnimationConfigClient.clamp01(progress);
        double scaled = clamped * (double)(animation.frames.length - 1);
        int i1 = Math.max(0, Math.min(animation.frames.length - 1, (int)Math.floor(scaled)));
        int i2 = Math.max(0, Math.min(animation.frames.length - 1, i1 + 1));
        Frame a = animation.frames[i1] == null ? new Frame() : animation.frames[i1];
        Frame b = animation.frames[i2] == null ? a : animation.frames[i2];
        return Frame.lerp(a, b, ProneTacticalAnimationConfigClient.smooth(scaled - (double)i1));
    }

    private static Animation blank(double duration) {
        Animation value = new Animation();
        value.duration = duration;
        value.frames = new Frame[7];
        for (int i = 0; i < 7; ++i) {
            value.frames[i] = new Frame();
        }
        return value;
    }

    private static Frame frame(Transform left, Transform right, Transform body, Transform head, Transform root) {
        return new Frame(left, right, body, head, root);
    }

    private static Transform transform(double x, double y, double z, double rx, double ry, double rz) {
        return new Transform(x, y, z, rx, ry, rz);
    }

    private static double smooth(double t) {
        t = ProneTacticalAnimationConfigClient.clamp01(t);
        return t * t * (3.0 - 2.0 * t);
    }

    private static double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double monotoneTangent(double a, double b) {
        if (Math.abs(a) < 1.0E-9 || Math.abs(b) < 1.0E-9 || a * b <= 0.0) {
            return 0.0;
        }
        return 2.0 * a * b / (a + b);
    }

    private static double hermite(double p0, double p1, double p2, double p3, double t) {
        double d0 = p1 - p0;
        double d1 = p2 - p1;
        double d2 = p3 - p2;
        double m1 = ProneTacticalAnimationConfigClient.monotoneTangent(d0, d1);
        double m2 = ProneTacticalAnimationConfigClient.monotoneTangent(d1, d2);
        double t2 = t * t;
        double t3 = t2 * t;
        double h00 = 2.0 * t3 - 3.0 * t2 + 1.0;
        double h10 = t3 - 2.0 * t2 + t;
        double h01 = -2.0 * t3 + 3.0 * t2;
        double h11 = t3 - t2;
        return h00 * p1 + h10 * m1 + h01 * p2 + h11 * m2;
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

    private static double lerpAngle(double a, double b, double t) {
        return ProneTacticalAnimationConfigClient.wrapDegrees(a + ProneTacticalAnimationConfigClient.wrapDegrees(b - a) * t);
    }

    private static double hermiteAngle(double p0, double p1, double p2, double p3, double t) {
        double q1 = p1;
        double q0 = q1 + ProneTacticalAnimationConfigClient.wrapDegrees(p0 - q1);
        double q2 = q1 + ProneTacticalAnimationConfigClient.wrapDegrees(p2 - q1);
        double q3 = q2 + ProneTacticalAnimationConfigClient.wrapDegrees(p3 - q2);
        return ProneTacticalAnimationConfigClient.wrapDegrees(ProneTacticalAnimationConfigClient.hermite(q0, q1, q2, q3, t));
    }

    private static double clampFinite(double value, double fallback, double min, double max) {
        if (!Double.isFinite(value)) {
            value = fallback;
        }
        return Math.max(min, Math.min(max, value));
    }

    public static final class Data {
        public int schemaVersion;
        public Animation proneEnter;
        public Animation proneExit;
        public AnglePose[] anglePoses;
        public Animation aimLeft;
        public Animation aimRight;
        public Animation backLeft;
        public Animation backRight;

        private boolean hasLegacyAimAnimations() {
            return this.aimLeft != null && this.aimRight != null && this.backLeft != null && this.backRight != null;
        }

        public Data copy() {
            Data d = new Data();
            d.schemaVersion = this.schemaVersion;
            d.proneEnter = this.proneEnter == null ? null : this.proneEnter.copy();
            Animation animation = d.proneExit = this.proneExit == null ? null : this.proneExit.copy();
            if (this.anglePoses != null) {
                d.anglePoses = new AnglePose[this.anglePoses.length];
                for (int i = 0; i < this.anglePoses.length; ++i) {
                    d.anglePoses[i] = this.anglePoses[i] == null ? null : this.anglePoses[i].copy();
                }
            }
            return d;
        }
    }

    public static enum Motion {
        NONE("\u65e0"),
        PRONE_ENTER("\u8db4\u4e0b\u8fc7\u6e21"),
        PRONE_EXIT("\u8d77\u8eab\u8fc7\u6e21");

        private final String displayName;

        private Motion(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }

    public static final class Animation {
        public double duration = 0.5;
        public Frame[] frames = new Frame[7];

        public Animation copy() {
            Animation a = new Animation();
            a.duration = this.duration;
            a.frames = new Frame[7];
            for (int i = 0; i < 7; ++i) {
                a.frames[i] = this.frames[i] == null ? new Frame() : this.frames[i].copy();
            }
            return a;
        }
    }

    public static final class Frame {
        public Transform leftLeg = new Transform();
        public Transform rightLeg = new Transform();
        public Transform body = new Transform();
        public Transform head = new Transform();
        public Transform leftArm = new Transform();
        public Transform rightArm = new Transform();
        public Transform root = new Transform();

        public Frame() {
        }

        private Frame(Transform leftLeg, Transform rightLeg, Transform body, Transform head, Transform leftArm, Transform rightArm, Transform root) {
            this.leftLeg = leftLeg;
            this.rightLeg = rightLeg;
            this.body = body;
            this.head = head;
            this.leftArm = leftArm;
            this.rightArm = rightArm;
            this.root = root;
        }

        private Frame(Transform leftLeg, Transform rightLeg, Transform body, Transform head, Transform root) {
            this(leftLeg, rightLeg, body, head, new Transform(), new Transform(), root);
        }

        public Transform transform(Bone bone) {
            return switch (bone) {
                default -> throw new IncompatibleClassChangeError();
                case Bone.LEFT_LEG -> this.leftLeg;
                case Bone.RIGHT_LEG -> this.rightLeg;
                case Bone.BODY -> this.body;
                case Bone.HEAD -> this.head;
                case Bone.LEFT_ARM -> this.leftArm;
                case Bone.RIGHT_ARM -> this.rightArm;
                case Bone.ROOT -> this.root;
            };
        }

        public Frame copy() {
            return new Frame(this.leftLeg == null ? new Transform() : this.leftLeg.copy(), this.rightLeg == null ? new Transform() : this.rightLeg.copy(), this.body == null ? new Transform() : this.body.copy(), this.head == null ? new Transform() : this.head.copy(), this.leftArm == null ? new Transform() : this.leftArm.copy(), this.rightArm == null ? new Transform() : this.rightArm.copy(), this.root == null ? new Transform() : this.root.copy());
        }

        public Frame mirrored() {
            Transform lLeg = this.leftLeg == null ? new Transform() : this.leftLeg;
            Transform rLeg = this.rightLeg == null ? new Transform() : this.rightLeg;
            Transform h = this.head == null ? new Transform() : this.head;
            Transform b = this.body == null ? new Transform() : this.body;
            Transform lArm = this.leftArm == null ? new Transform() : this.leftArm;
            Transform rArm = this.rightArm == null ? new Transform() : this.rightArm;
            Transform r = this.root == null ? new Transform() : this.root;
            return new Frame(rLeg.mirrored(), lLeg.mirrored(), b.mirrored(), h.mirrored(), rArm.mirrored(), lArm.mirrored(), r.mirrored());
        }

        public static Frame lerp(Frame a, Frame b, double t) {
            return new Frame(Transform.lerp(a.leftLeg, b.leftLeg, t), Transform.lerp(a.rightLeg, b.rightLeg, t), Transform.lerp(a.body, b.body, t), Transform.lerp(a.head, b.head, t), Transform.lerp(a.leftArm, b.leftArm, t), Transform.lerp(a.rightArm, b.rightArm, t), Transform.lerp(a.root, b.root, t));
        }

        public static Frame circularLerp(Frame a, Frame b, double t) {
            return new Frame(Transform.circularLerp(a.leftLeg, b.leftLeg, t), Transform.circularLerp(a.rightLeg, b.rightLeg, t), Transform.circularLerp(a.body, b.body, t), Transform.circularLerp(a.head, b.head, t), Transform.circularLerp(a.leftArm, b.leftArm, t), Transform.circularLerp(a.rightArm, b.rightArm, t), Transform.circularLerp(a.root, b.root, t));
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
            if (this.head == null) {
                this.head = fallback.head.copy();
            }
            if (this.leftArm == null) {
                this.leftArm = fallback.leftArm.copy();
            }
            if (this.rightArm == null) {
                this.rightArm = fallback.rightArm.copy();
            }
            if (this.root == null) {
                this.root = fallback.root.copy();
            }
            this.leftLeg.sanitizeLeg(fallback.leftLeg);
            this.rightLeg.sanitizeLeg(fallback.rightLeg);
            this.body.sanitizeBody(fallback.body);
            this.head.sanitizeHead(fallback.head);
            this.leftArm.sanitizeArm(fallback.leftArm);
            this.rightArm.sanitizeArm(fallback.rightArm);
            this.root.sanitizeRoot(fallback.root);
        }
    }

    public record Sample(Transform leftLeg, Transform rightLeg, Transform body, Transform head, Transform leftArm, Transform rightArm, Transform root) {
        public static final Sample ZERO = new Sample(new Transform(), new Transform(), new Transform(), new Transform(), new Transform(), new Transform(), new Transform());

        static Sample fromFrame(Frame frame) {
            return new Sample(frame.leftLeg.copy(), frame.rightLeg.copy(), frame.body.copy(), frame.head.copy(), frame.leftArm.copy(), frame.rightArm.copy(), frame.root.copy());
        }

        static Sample hermite(Frame p0, Frame p1, Frame p2, Frame p3, double t) {
            return new Sample(Transform.hermite(p0.leftLeg, p1.leftLeg, p2.leftLeg, p3.leftLeg, t), Transform.hermite(p0.rightLeg, p1.rightLeg, p2.rightLeg, p3.rightLeg, t), Transform.hermite(p0.body, p1.body, p2.body, p3.body, t), Transform.hermite(p0.head, p1.head, p2.head, p3.head, t), Transform.hermite(p0.leftArm, p1.leftArm, p2.leftArm, p3.leftArm, t), Transform.hermite(p0.rightArm, p1.rightArm, p2.rightArm, p3.rightArm, t), Transform.hermite(p0.root, p1.root, p2.root, p3.root, t));
        }

        static Sample lerp(Frame a, Frame b, double t) {
            return new Sample(Transform.lerp(a.leftLeg, b.leftLeg, t), Transform.lerp(a.rightLeg, b.rightLeg, t), Transform.lerp(a.body, b.body, t), Transform.lerp(a.head, b.head, t), Transform.lerp(a.leftArm, b.leftArm, t), Transform.lerp(a.rightArm, b.rightArm, t), Transform.lerp(a.root, b.root, t));
        }

        public static Sample lerp(Sample a, Sample b, double t) {
            return new Sample(Transform.lerp(a.leftLeg, b.leftLeg, t), Transform.lerp(a.rightLeg, b.rightLeg, t), Transform.lerp(a.body, b.body, t), Transform.lerp(a.head, b.head, t), Transform.lerp(a.leftArm, b.leftArm, t), Transform.lerp(a.rightArm, b.rightArm, t), Transform.lerp(a.root, b.root, t));
        }

        public Sample withoutRootYaw() {
            Transform r = this.root.copy();
            r.rotY = 0.0;
            return new Sample(this.leftLeg.copy(), this.rightLeg.copy(), this.body.copy(), this.head.copy(), this.leftArm.copy(), this.rightArm.copy(), r);
        }
    }

    public static final class AnglePose {
        public double angle;
        public Double bodyYaw;
        public Frame pose;

        public AnglePose() {
        }

        private AnglePose(double angle, double bodyYaw, Frame pose) {
            this.angle = angle;
            this.bodyYaw = bodyYaw;
            this.pose = pose;
        }

        public AnglePose copy() {
            return new AnglePose(this.angle, this.bodyYaw == null ? 0.0 : this.bodyYaw, this.pose == null ? new Frame() : this.pose.copy());
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

        public static Transform lerp(Transform a, Transform b, double t) {
            return new Transform(ProneTacticalAnimationConfigClient.lerp(a.x, b.x, t), ProneTacticalAnimationConfigClient.lerp(a.y, b.y, t), ProneTacticalAnimationConfigClient.lerp(a.z, b.z, t), ProneTacticalAnimationConfigClient.lerp(a.rotX, b.rotX, t), ProneTacticalAnimationConfigClient.lerp(a.rotY, b.rotY, t), ProneTacticalAnimationConfigClient.lerp(a.rotZ, b.rotZ, t));
        }

        public static Transform circularLerp(Transform a, Transform b, double t) {
            return new Transform(ProneTacticalAnimationConfigClient.lerp(a.x, b.x, t), ProneTacticalAnimationConfigClient.lerp(a.y, b.y, t), ProneTacticalAnimationConfigClient.lerp(a.z, b.z, t), ProneTacticalAnimationConfigClient.lerpAngle(a.rotX, b.rotX, t), ProneTacticalAnimationConfigClient.lerpAngle(a.rotY, b.rotY, t), ProneTacticalAnimationConfigClient.lerpAngle(a.rotZ, b.rotZ, t));
        }

        public static Transform hermite(Transform p0, Transform p1, Transform p2, Transform p3, double t) {
            return new Transform(ProneTacticalAnimationConfigClient.hermite(p0.x, p1.x, p2.x, p3.x, t), ProneTacticalAnimationConfigClient.hermite(p0.y, p1.y, p2.y, p3.y, t), ProneTacticalAnimationConfigClient.hermite(p0.z, p1.z, p2.z, p3.z, t), ProneTacticalAnimationConfigClient.hermiteAngle(p0.rotX, p1.rotX, p2.rotX, p3.rotX, t), ProneTacticalAnimationConfigClient.hermiteAngle(p0.rotY, p1.rotY, p2.rotY, p3.rotY, t), ProneTacticalAnimationConfigClient.hermiteAngle(p0.rotZ, p1.rotZ, p2.rotZ, p3.rotZ, t));
        }

        private void sanitizeLeg(Transform f) {
            this.x = ProneTacticalAnimationConfigClient.clampFinite(this.x, f.x, -4.0, 4.0);
            this.y = ProneTacticalAnimationConfigClient.clampFinite(this.y, f.y, -4.0, 4.0);
            this.z = ProneTacticalAnimationConfigClient.clampFinite(this.z, f.z, -4.0, 4.0);
            this.rotX = ProneTacticalAnimationConfigClient.clampFinite(this.rotX, f.rotX, -140.0, 140.0);
            this.rotY = ProneTacticalAnimationConfigClient.clampFinite(this.rotY, f.rotY, -120.0, 120.0);
            this.rotZ = ProneTacticalAnimationConfigClient.clampFinite(this.rotZ, f.rotZ, -120.0, 120.0);
        }

        private void sanitizeBody(Transform f) {
            this.x = ProneTacticalAnimationConfigClient.clampFinite(this.x, f.x, -3.0, 3.0);
            this.y = ProneTacticalAnimationConfigClient.clampFinite(this.y, f.y, -3.0, 3.0);
            this.z = ProneTacticalAnimationConfigClient.clampFinite(this.z, f.z, -3.0, 3.0);
            this.rotX = ProneTacticalAnimationConfigClient.clampFinite(this.rotX, f.rotX, -90.0, 90.0);
            this.rotY = ProneTacticalAnimationConfigClient.clampFinite(this.rotY, f.rotY, -120.0, 120.0);
            this.rotZ = ProneTacticalAnimationConfigClient.clampFinite(this.rotZ, f.rotZ, -120.0, 120.0);
        }

        private void sanitizeHead(Transform f) {
            this.x = ProneTacticalAnimationConfigClient.clampFinite(this.x, f.x, -3.0, 3.0);
            this.y = ProneTacticalAnimationConfigClient.clampFinite(this.y, f.y, -3.0, 3.0);
            this.z = ProneTacticalAnimationConfigClient.clampFinite(this.z, f.z, -3.0, 3.0);
            this.rotX = ProneTacticalAnimationConfigClient.clampFinite(this.rotX, f.rotX, -120.0, 120.0);
            this.rotY = ProneTacticalAnimationConfigClient.clampFinite(this.rotY, f.rotY, -180.0, 180.0);
            this.rotZ = ProneTacticalAnimationConfigClient.clampFinite(this.rotZ, f.rotZ, -120.0, 120.0);
        }

        private void sanitizeArm(Transform f) {
            this.x = ProneTacticalAnimationConfigClient.clampFinite(this.x, f.x, -4.0, 4.0);
            this.y = ProneTacticalAnimationConfigClient.clampFinite(this.y, f.y, -4.0, 4.0);
            this.z = ProneTacticalAnimationConfigClient.clampFinite(this.z, f.z, -4.0, 4.0);
            this.rotX = ProneTacticalAnimationConfigClient.clampFinite(this.rotX, f.rotX, -180.0, 180.0);
            this.rotY = ProneTacticalAnimationConfigClient.clampFinite(this.rotY, f.rotY, -180.0, 180.0);
            this.rotZ = ProneTacticalAnimationConfigClient.clampFinite(this.rotZ, f.rotZ, -180.0, 180.0);
        }

        private void sanitizeRoot(Transform f) {
            this.x = ProneTacticalAnimationConfigClient.clampFinite(this.x, f.x, -5.0, 5.0);
            this.y = ProneTacticalAnimationConfigClient.clampFinite(this.y, f.y, -5.0, 5.0);
            this.z = ProneTacticalAnimationConfigClient.clampFinite(this.z, f.z, -5.0, 5.0);
            this.rotX = ProneTacticalAnimationConfigClient.clampFinite(this.rotX, f.rotX, -180.0, 180.0);
            this.rotY = ProneTacticalAnimationConfigClient.clampFinite(this.rotY, f.rotY, -180.0, 180.0);
            this.rotZ = ProneTacticalAnimationConfigClient.clampFinite(this.rotZ, f.rotZ, -180.0, 180.0);
        }
    }

    public static enum Bone {
        LEFT_LEG("\u5de6\u817f"),
        RIGHT_LEG("\u53f3\u817f"),
        BODY("\u8eaf\u5e72"),
        HEAD("\u5934\u90e8"),
        LEFT_ARM("\u5de6\u81c2"),
        RIGHT_ARM("\u53f3\u81c2"),
        ROOT("\u6574\u4f53");

        private final String displayName;

        private Bone(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }
}
