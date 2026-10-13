package com.locknar.taczammocheck.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.locknar.taczammocheck.TaczGunCompat;
import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.locknar.taczammocheck.client.PlayerBodyRenderContext;
import com.locknar.taczammocheck.network.AmmoCheckPosePacket;
import com.locknar.taczammocheck.prone.PronePoseMath;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class AmmoCheckThirdPersonClient {
    private static final Map<Integer, AmmoCheckPosePacket> STATES = new HashMap<Integer, AmmoCheckPosePacket>();
    private static Object level;
    private static JsonObject clips;

    private AmmoCheckThirdPersonClient() {
    }

    private static void level() {
        ClientLevel current = Minecraft.getInstance().level;
        if (level != current) {
            STATES.clear();
            level = current;
        }
    }

    public static void accept(AmmoCheckPosePacket packet) {
        AmmoCheckThirdPersonClient.level();
        if (packet.duration() < 250L || packet.duration() > 10000L) {
            return;
        }
        AmmoCheckPosePacket old = STATES.get(packet.entity());
        if (packet.active()) {
            if (old == null || !old.uuid().equals(packet.uuid()) || old.started() <= packet.started()) {
                STATES.put(packet.entity(), packet);
            }
        } else if (old != null && old.uuid().equals(packet.uuid()) && old.token().equals(packet.token())) {
            STATES.remove(packet.entity());
        }
    }

    private static float progress(AbstractClientPlayer p) {
        if (TaczGunCompat.isAmmoCheckDisabled(TaczGunCompat.getGunId(p.getMainHandItem()))) {
            return -1.0f;
        }
        AmmoCheckThirdPersonClient.level();
        Minecraft mc = Minecraft.getInstance();
        if (p == mc.player) {
            return AmmoCheckClient.isActive() ? AmmoCheckClient.getActiveAnimationProgress(mc.getTimer().getGameTimeDeltaPartialTick(false)) : -1.0f;
        }
        AmmoCheckPosePacket s = STATES.get(p.getId());
        if (!(s != null && s.uuid().equals(p.getUUID()) && p.isAlive() && s.gunId().equals(TaczGunCompat.getGunId(p.getMainHandItem())))) {
            return -1.0f;
        }
        double ms = ((float)(p.level().getGameTime() - s.started()) + mc.getTimer().getGameTimeDeltaPartialTick(false)) * 50.0f;
        if (ms > (double)(s.duration() + 300L)) {
            STATES.remove(p.getId());
            return -1.0f;
        }
        return Mth.clamp((float)((float)(ms / (double)s.duration())), (float)0.0f, (float)1.0f);
    }

    public static boolean active(AbstractClientPlayer p) {
        return AmmoCheckThirdPersonClient.progress(p) >= 0.0f;
    }

    public static void apply(AbstractClientPlayer p, PlayerModel<?> model) {
        int high;
        if (!PlayerBodyRenderContext.active(p, model)) {
            return;
        }
        float t = AmmoCheckThirdPersonClient.progress(p);
        if (t < 0.0f) {
            return;
        }
        if (clips == null) {
            try (InputStream stream = AmmoCheckThirdPersonClient.class.getResourceAsStream("/assets/locknar_doorkick/third_person/ammo_check.json");){
                if (stream == null) {
                    return;
                }
                clips = JsonParser.parseReader((Reader)new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            }
            catch (Exception e) {
                return;
            }
        }
        String id = TaczGunCompat.getGunId(p.getMainHandItem());
        TaczGunCompat.GunInfo info = TaczGunCompat.getGunInfo(p.getMainHandItem());
        String kind = info == null ? "" : String.valueOf((Object)info.kind()).toLowerCase(Locale.ROOT);
        boolean pistol = kind.contains("pistol") || id.contains("taurus") || id.contains("deagle");
        JsonArray frames = clips.getAsJsonArray(pistol ? "pistol" : "rifle");
        for (high = 1; high < frames.size() - 1 && frames.get(high).getAsJsonObject().get("t").getAsFloat() < t; ++high) {
        }
        JsonObject a = frames.get(high - 1).getAsJsonObject();
        JsonObject b = frames.get(high).getAsJsonObject();
        float start = a.get("t").getAsFloat();
        float end = b.get("t").getAsFloat();
        float f = AmmoCheckThirdPersonClient.smooth((t - start) / (end - start));
        boolean rightMagazine = id.equals("tacz:mk14");
        AmmoCheckThirdPersonClient.arm(rightMagazine ? model.rightArm : model.leftArm, a.getAsJsonArray("left"), b.getAsJsonArray("left"), f, rightMagazine);
        AmmoCheckThirdPersonClient.arm(rightMagazine ? model.leftArm : model.rightArm, a.getAsJsonArray("right"), b.getAsJsonArray("right"), f, rightMagazine);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
    }

    private static void arm(ModelPart part, JsonArray a, JsonArray b, float f, boolean mirror) {
        float[] v = new float[6];
        for (int i = 0; i < 6; ++i) {
            v[i] = Mth.lerp((float)f, (float)a.get(i).getAsFloat(), (float)b.get(i).getAsFloat());
        }
        if (mirror) {
            v[0] = -v[0];
            v[4] = -v[4];
            v[5] = -v[5];
        }
        part.x += v[0];
        part.y += v[1];
        part.z += v[2];
        Quaternionf pose = new Quaternionf().rotationZYX(part.zRot, part.yRot, part.xRot);
        pose.mul((Quaternionfc)new Quaternionf().rotationZYX(v[5] * ((float)Math.PI / 180), v[4] * ((float)Math.PI / 180), v[3] * ((float)Math.PI / 180)));
        Vector3f e = PronePoseMath.eulerZYX((Quaternionfc)pose);
        part.xRot = e.x;
        part.yRot = e.y;
        part.zRot = e.z;
    }

    private static float smooth(float t) {
        t = Mth.clamp((float)t, (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }
}
