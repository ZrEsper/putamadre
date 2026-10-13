package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.TaczWallObstructionCommon;
import com.locknar.taczammocheck.tactical.client.PlayerAnimatorBridge;
import com.locknar.taczammocheck.tactical.client.TaczHighReadyClient;
import com.locknar.taczammocheck.tactical.client.TaczKickRaiseClient;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class TaczWallObstructionThirdPersonClient {
    private static final Map<Integer, Boolean> ACTIVE_PLAYER_ANIM = new HashMap<Integer, Boolean>();
    private static final Map<Integer, Long> LAST_SEEN = new HashMap<Integer, Long>();
    private static final Map<Integer, SavedArmPose> SAVED_POSES = new HashMap<Integer, SavedArmPose>();
    private static final Map<Integer, ObstructionSample> OBSTRUCTION_CACHE = new HashMap<Integer, ObstructionSample>();
    private static final int OBSTRUCTION_CACHE_TICKS = 5;

    private TaczWallObstructionThirdPersonClient() {
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        if (!ACTIVE_PLAYER_ANIM.isEmpty()) {
            TaczWallObstructionThirdPersonClient.stopAllPlayerAnimator();
        }
        TaczWallObstructionThirdPersonClient.cleanupObstructionCache();
    }

    public static void applyConfiguredPoseAfterSetup(AbstractClientPlayer player, PlayerModel<?> rawModel) {
        boolean pistol;
        if (player == null || rawModel == null) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        TaczGunCompat.GunInfo info = TaczWallObstructionThirdPersonClient.getMainHandGunInfo((Player)player);
        if (info == null) {
            return;
        }
        float strength = TaczWallObstructionThirdPersonClient.getPoseStrength(player, info);
        if (strength <= 0.01f) {
            return;
        }
        PlayerModel<?> model = rawModel;
        boolean bl = pistol = info.kind() == TaczGunCompat.GunKind.PISTOL;
        if (pistol) {
            TaczWallObstructionThirdPersonClient.applyPistolPose(model, strength);
        } else {
            TaczWallObstructionThirdPersonClient.applyLongGunPose(model, strength);
        }
        TaczWallObstructionThirdPersonClient.syncSleeves(model);
    }

    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        boolean pistol;
        AbstractClientPlayer player;
        Player entity = event.getEntity();
        if (!(entity instanceof AbstractClientPlayer) || (player = (AbstractClientPlayer)entity).isSpectator()) {
            return;
        }
        TaczGunCompat.GunInfo info = TaczWallObstructionThirdPersonClient.getMainHandGunInfo((Player)player);
        if (info == null) {
            return;
        }
        float strength = TaczWallObstructionThirdPersonClient.getPoseStrength(player, info);
        if (strength <= 0.01f) {
            return;
        }
        PlayerModel model = (PlayerModel)event.getRenderer().getModel();
        if (model == null) {
            return;
        }
        SAVED_POSES.put(player.getId(), SavedArmPose.capture((PlayerModel<?>)model));
        boolean bl = pistol = info.kind() == TaczGunCompat.GunKind.PISTOL;
        if (pistol) {
            TaczWallObstructionThirdPersonClient.applyPistolPose((PlayerModel<?>)model, strength);
        } else {
            TaczWallObstructionThirdPersonClient.applyLongGunPose((PlayerModel<?>)model, strength);
        }
        TaczWallObstructionThirdPersonClient.syncSleeves((PlayerModel<?>)model);
    }

    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player entity = event.getEntity();
        if (!(entity instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)entity;
        SavedArmPose pose = SAVED_POSES.remove(player.getId());
        if (pose == null) {
            return;
        }
        PlayerModel model = (PlayerModel)event.getRenderer().getModel();
        if (model != null) {
            pose.restore((PlayerModel<?>)model);
        }
    }

    private static void applyLongGunPose(PlayerModel<?> model, float strength) {
        model.rightArm.xRot += TaczWallObstructionThirdPersonClient.deg(-47.0f) * strength;
        model.rightArm.yRot += TaczWallObstructionThirdPersonClient.deg(4.5f) * strength;
        model.rightArm.zRot += TaczWallObstructionThirdPersonClient.deg(1.7f) * strength;
        model.leftArm.xRot += TaczWallObstructionThirdPersonClient.deg(-41.0f) * strength;
        model.leftArm.yRot += TaczWallObstructionThirdPersonClient.deg(9.0f) * strength;
        model.leftArm.zRot += TaczWallObstructionThirdPersonClient.deg(-4.5f) * strength;
    }

    private static void applyPistolPose(PlayerModel<?> model, float strength) {
        model.rightArm.xRot += TaczWallObstructionThirdPersonClient.deg(-35.5f) * strength;
        model.rightArm.yRot += TaczWallObstructionThirdPersonClient.deg(2.8f) * strength;
        model.rightArm.zRot += TaczWallObstructionThirdPersonClient.deg(-1.1f) * strength;
        model.leftArm.xRot += TaczWallObstructionThirdPersonClient.deg(-14.0f) * strength;
        model.leftArm.yRot += TaczWallObstructionThirdPersonClient.deg(2.8f) * strength;
        model.leftArm.zRot += TaczWallObstructionThirdPersonClient.deg(-1.7f) * strength;
    }

    private static float deg(float degrees) {
        return (float)Math.toRadians(degrees);
    }

    private static void syncSleeves(PlayerModel<?> model) {
        model.rightSleeve.xRot = model.rightArm.xRot;
        model.rightSleeve.yRot = model.rightArm.yRot;
        model.rightSleeve.zRot = model.rightArm.zRot;
        model.rightSleeve.x = model.rightArm.x;
        model.rightSleeve.y = model.rightArm.y;
        model.rightSleeve.z = model.rightArm.z;
        model.leftSleeve.xRot = model.leftArm.xRot;
        model.leftSleeve.yRot = model.leftArm.yRot;
        model.leftSleeve.zRot = model.leftArm.zRot;
        model.leftSleeve.x = model.leftArm.x;
        model.leftSleeve.y = model.leftArm.y;
        model.leftSleeve.z = model.leftArm.z;
    }

    private static boolean isWallOrKickRaiseActive(AbstractClientPlayer player, TaczGunCompat.GunInfo info) {
        return TaczWallObstructionThirdPersonClient.getPoseStrength(player, info) > 0.01f;
    }

    private static float getPoseStrength(AbstractClientPlayer player, TaczGunCompat.GunInfo info) {
        float wallStrength = (Boolean)LocknarConfig.TACZ_WALL_OBSTRUCTION_ENABLED.get() != false && TaczWallObstructionThirdPersonClient.isMuzzleObstructedCached(player, info.length()) ? 1.0f : 0.0f;
        float kickStrength = TaczKickRaiseClient.getKickRaiseProgress(player.getId());
        float highReadyStrength = TaczHighReadyClient.getHighReadyProgressForEntity(player.getId());
        return Math.max(wallStrength, Math.max(kickStrength, highReadyStrength));
    }

    private static boolean isMuzzleObstructedCached(AbstractClientPlayer player, double length) {
        if (player == null) {
            return false;
        }
        int entityId = player.getId();
        long tick = player.tickCount;
        long nowMs = System.currentTimeMillis();
        ObstructionSample cached = OBSTRUCTION_CACHE.get(entityId);
        if (cached != null && tick - cached.tick() >= 0L && tick - cached.tick() <= 5L && Math.abs(cached.length() - length) < 0.001) {
            return cached.obstructed();
        }
        boolean obstructed = TaczWallObstructionCommon.isMuzzleObstructed((LivingEntity)player, length);
        OBSTRUCTION_CACHE.put(entityId, new ObstructionSample(tick, nowMs, length, obstructed));
        return obstructed;
    }

    private static void cleanupObstructionCache() {
        if (OBSTRUCTION_CACHE.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Integer, ObstructionSample>> iterator = OBSTRUCTION_CACHE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, ObstructionSample> entry = iterator.next();
            if (now - entry.getValue().lastAccessMs() <= 3000L) continue;
            iterator.remove();
        }
    }

    private static TaczGunCompat.GunInfo getMainHandGunInfo(Player player) {
        if (player == null) {
            return null;
        }
        ItemStack mainHand = player.getMainHandItem();
        return TaczGunCompat.getGunInfo(mainHand);
    }

    public static void clearClientState() {
        TaczWallObstructionThirdPersonClient.stopAllPlayerAnimator();
        SAVED_POSES.clear();
        OBSTRUCTION_CACHE.clear();
    }

    private static void stopAllPlayerAnimator() {
        if (!PlayerAnimatorBridge.isAvailable()) {
            ACTIVE_PLAYER_ANIM.clear();
            LAST_SEEN.clear();
            return;
        }
        for (Integer entityId : ACTIVE_PLAYER_ANIM.keySet()) {
            PlayerAnimatorBridge.stopTaczWallForEntity(entityId);
        }
        ACTIVE_PLAYER_ANIM.clear();
        LAST_SEEN.clear();
    }

    private record SavedArmPose(float rightArmX, float rightArmY, float rightArmZ, float leftArmX, float leftArmY, float leftArmZ, float rightArmPosX, float rightArmPosY, float rightArmPosZ, float leftArmPosX, float leftArmPosY, float leftArmPosZ, float rightSleeveX, float rightSleeveY, float rightSleeveZ, float leftSleeveX, float leftSleeveY, float leftSleeveZ, float rightSleevePosX, float rightSleevePosY, float rightSleevePosZ, float leftSleevePosX, float leftSleevePosY, float leftSleevePosZ) {
        static SavedArmPose capture(PlayerModel<?> model) {
            return new SavedArmPose(model.rightArm.xRot, model.rightArm.yRot, model.rightArm.zRot, model.leftArm.xRot, model.leftArm.yRot, model.leftArm.zRot, model.rightArm.x, model.rightArm.y, model.rightArm.z, model.leftArm.x, model.leftArm.y, model.leftArm.z, model.rightSleeve.xRot, model.rightSleeve.yRot, model.rightSleeve.zRot, model.leftSleeve.xRot, model.leftSleeve.yRot, model.leftSleeve.zRot, model.rightSleeve.x, model.rightSleeve.y, model.rightSleeve.z, model.leftSleeve.x, model.leftSleeve.y, model.leftSleeve.z);
        }

        void restore(PlayerModel<?> model) {
            model.rightArm.xRot = this.rightArmX;
            model.rightArm.yRot = this.rightArmY;
            model.rightArm.zRot = this.rightArmZ;
            model.leftArm.xRot = this.leftArmX;
            model.leftArm.yRot = this.leftArmY;
            model.leftArm.zRot = this.leftArmZ;
            model.rightArm.x = this.rightArmPosX;
            model.rightArm.y = this.rightArmPosY;
            model.rightArm.z = this.rightArmPosZ;
            model.leftArm.x = this.leftArmPosX;
            model.leftArm.y = this.leftArmPosY;
            model.leftArm.z = this.leftArmPosZ;
            model.rightSleeve.xRot = this.rightSleeveX;
            model.rightSleeve.yRot = this.rightSleeveY;
            model.rightSleeve.zRot = this.rightSleeveZ;
            model.leftSleeve.xRot = this.leftSleeveX;
            model.leftSleeve.yRot = this.leftSleeveY;
            model.leftSleeve.zRot = this.leftSleeveZ;
            model.rightSleeve.x = this.rightSleevePosX;
            model.rightSleeve.y = this.rightSleevePosY;
            model.rightSleeve.z = this.rightSleevePosZ;
            model.leftSleeve.x = this.leftSleevePosX;
            model.leftSleeve.y = this.leftSleevePosY;
            model.leftSleeve.z = this.leftSleevePosZ;
        }
    }

    private record ObstructionSample(long tick, long lastAccessMs, double length, boolean obstructed) {
    }
}
