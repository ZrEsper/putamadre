package com.locknar.taczammocheck;

import com.locknar.taczammocheck.TaczGunCompat;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LocknarConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec.BooleanValue AMMO_CHECK_ENABLED;
    public static final ModConfigSpec.DoubleValue HOLD_TRIGGER_SECONDS;
    public static final ModConfigSpec.DoubleValue ANIMATION_DURATION_SECONDS;
    public static final ModConfigSpec.DoubleValue RELOAD_START_BLOCK_SECONDS;
    public static final ModConfigSpec.DoubleValue LOW_AMMO_THRESHOLD;
    public static final ModConfigSpec.DoubleValue SOUND_VOLUME_MULTIPLIER;
    public static final ModConfigSpec.IntValue UI_OFFSET_X;
    public static final ModConfigSpec.IntValue UI_OFFSET_Y;
    public static final ModConfigSpec.DoubleValue HIGH_READY_LONG_RIGHT_OFFSET;
    public static final ModConfigSpec.DoubleValue HIGH_READY_LONG_VERTICAL_OFFSET;
    public static final ModConfigSpec.DoubleValue HIGH_READY_LONG_BACK_OFFSET;
    public static final ModConfigSpec.DoubleValue HIGH_READY_PISTOL_RIGHT_OFFSET;
    public static final ModConfigSpec.DoubleValue HIGH_READY_PISTOL_VERTICAL_OFFSET;
    public static final ModConfigSpec.DoubleValue HIGH_READY_PISTOL_BACK_OFFSET;
    public static final ModConfigSpec.DoubleValue WALL_LONG_RIGHT_OFFSET;
    public static final ModConfigSpec.DoubleValue WALL_LONG_VERTICAL_OFFSET;
    public static final ModConfigSpec.DoubleValue WALL_LONG_BACK_OFFSET;
    public static final ModConfigSpec.DoubleValue WALL_PISTOL_RIGHT_OFFSET;
    public static final ModConfigSpec.DoubleValue WALL_PISTOL_VERTICAL_OFFSET;
    public static final ModConfigSpec.DoubleValue WALL_PISTOL_BACK_OFFSET;
    public static final ModConfigSpec.DoubleValue PLAYER_WALK_SPEED_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue HIGH_READY_SPEED_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue TACZ_WALL_OBSTRUCTION_ENABLED;
    public static final ModConfigSpec.BooleanValue PLAYER_STATUS_HUD_ENABLED;
    public static final ModConfigSpec.BooleanValue HIDE_VANILLA_HUD;
    public static final ModConfigSpec.BooleanValue FRONT_SLING_GUN_ENABLED;
    public static final ModConfigSpec.BooleanValue THIRD_PERSON_WEAPON_SWITCH_ENABLED;
    public static final ModConfigSpec.BooleanValue DIRECTIONAL_LEG_ANIMATIONS_ENABLED;
    public static final ModConfigSpec.BooleanValue GUN_RUN_ANIMATIONS_ENABLED;
    public static final ModConfigSpec.BooleanValue TACTICAL_PRONE_ANIMATIONS_ENABLED;
    public static final ModConfigSpec.IntValue TACTICAL_PRONE_ROTATION_SMOOTHING_TICKS;
    public static final ModConfigSpec.IntValue TACTICAL_PRONE_ROTATION_FOLLOW_SMOOTHING_MS;
    private static final int MIRROR_USE_TICKS = 30;
    private static final int MIRROR_CANCEL_TICKS = 20;
    private static final int KICK_SUCCESS_CHANCE = 100;
    public static final double SNIPER_LENGTH = 1.0;
    public static final double RIFLE_LENGTH = 0.8;
    public static final double SHOTGUN_LENGTH = 0.8;
    public static final double SMG_LENGTH = 0.6;
    public static final double PISTOL_LENGTH = 0.4;
    public static final String SNIPER_KEYWORDS = "sniper,awm,m24,m700,mosin,kar98,kar98k,m82,m107,svd";
    public static final String RIFLE_KEYWORDS = "rifle,m4,m4a1,ak,ak47,akm,scar,ar15,hk416,g36,aug,fal,groza";
    public static final String SHOTGUN_KEYWORDS = "shotgun,m870,m1014,aa12,ks23,db,saiga";
    public static final String SMG_KEYWORDS = "smg,mp5,ump,uzi,vector,p90,mp7,mac10,pp19,bizon";
    public static final String PISTOL_KEYWORDS = "pistol,glock,glock17,glock_17,m1911,deagle,usp,p226,cz75,revolver,fn57";

    private LocknarConfig() {
    }

    public static long holdTriggerMs() {
        return Math.round((Double)HOLD_TRIGGER_SECONDS.get() * 1000.0);
    }

    public static long animationMs() {
        return Math.max(1L, Math.round((Double)ANIMATION_DURATION_SECONDS.get() * 1000.0));
    }

    public static long reloadStartBlockMs() {
        return Math.max(0L, Math.round((Double)RELOAD_START_BLOCK_SECONDS.get() * 1000.0));
    }

    public static float lowAmmoThreshold() {
        return ((Double)LOW_AMMO_THRESHOLD.get()).floatValue();
    }

    public static float soundVolume() {
        return ((Double)SOUND_VOLUME_MULTIPLIER.get()).floatValue();
    }

    public static int uiOffsetX() {
        return (Integer)UI_OFFSET_X.get();
    }

    public static int uiOffsetY() {
        return (Integer)UI_OFFSET_Y.get();
    }

    public static String keywordsForKind(TaczGunCompat.GunKind kind) {
        return switch (kind) {
            default -> throw new IncompatibleClassChangeError();
            case TaczGunCompat.GunKind.SNIPER -> SNIPER_KEYWORDS;
            case TaczGunCompat.GunKind.SHOTGUN -> SHOTGUN_KEYWORDS;
            case TaczGunCompat.GunKind.SMG -> SMG_KEYWORDS;
            case TaczGunCompat.GunKind.PISTOL -> PISTOL_KEYWORDS;
            case TaczGunCompat.GunKind.RIFLE -> RIFLE_KEYWORDS;
        };
    }

    public static int fallbackCapacityForKind(TaczGunCompat.GunKind kind) {
        return switch (kind) {
            default -> throw new IncompatibleClassChangeError();
            case TaczGunCompat.GunKind.PISTOL -> 17;
            case TaczGunCompat.GunKind.SHOTGUN -> 8;
            case TaczGunCompat.GunKind.SNIPER -> 5;
            case TaczGunCompat.GunKind.SMG, TaczGunCompat.GunKind.RIFLE -> 30;
        };
    }

    private static ModConfigSpec.DoubleValue offset(ModConfigSpec.Builder builder, String name, double def, String zh, double min, double max) {
        return builder.comment(new String[]{zh, "\u5355\u4f4d\uff1a\u7b2c\u4e00\u4eba\u79f0\u6e32\u67d3\u504f\u79fb\u3002\u5efa\u8bae\u6bcf\u6b21\u8c03\u6574 0.02 ~ 0.10\u3002"}).defineInRange(name, def, min, max);
    }

    public static int mirrorUseTicks() {
        return 30;
    }

    public static int mirrorCancelTicks() {
        return 20;
    }

    public static boolean mirrorOldTvEffect() {
        return true;
    }

    public static int kickSuccessChance() {
        return 100;
    }

    public static double lengthForKind(com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind kind) {
        return switch (kind) {
            default -> throw new IncompatibleClassChangeError();
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.SNIPER -> 1.0;
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.SHOTGUN -> 0.8;
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.SMG -> 0.6;
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.PISTOL -> 0.4;
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.RIFLE -> 0.8;
        };
    }

    public static String keywordsForKind(com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind kind) {
        return switch (kind) {
            default -> throw new IncompatibleClassChangeError();
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.SNIPER -> SNIPER_KEYWORDS;
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.SHOTGUN -> SHOTGUN_KEYWORDS;
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.SMG -> SMG_KEYWORDS;
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.PISTOL -> PISTOL_KEYWORDS;
            case com.locknar.taczammocheck.tactical.TaczGunCompat.GunKind.RIFLE -> RIFLE_KEYWORDS;
        };
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("ammo_check");
        AMMO_CHECK_ENABLED = builder.comment(new String[]{"Enable hold-reload ammo check. Short reload taps are forwarded to TaCZ normally.", "\u662f\u5426\u542f\u7528\u957f\u6309\u6362\u5f39\u952e\u67e5\u5f39\u3002\u77ed\u6309\u6362\u5f39\u4f1a\u6b63\u5e38\u4ea4\u7ed9 TaCZ\u3002\u9ed8\u8ba4\u5f00\u542f\u3002"}).define("enableHoldReloadAmmoCheck", true);
        HOLD_TRIGGER_SECONDS = builder.comment(new String[]{"Hold time in seconds before reload becomes ammo check.", "\u957f\u6309\u6362\u5f39\u952e\u89e6\u53d1\u67e5\u5f39\u6240\u9700\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2\u3002"}).defineInRange("holdTriggerSeconds", 0.4, 0.1, 3.0);
        ANIMATION_DURATION_SECONDS = builder.comment(new String[]{"Fallback ammo check animation duration in seconds. Per-gun ammo_check preset length is used when available.", "\u67e5\u5f39\u52a8\u753b\u515c\u5e95\u6301\u7eed\u65f6\u95f4\uff0c\u5355\u4f4d\u79d2\u3002\u5b58\u5728\u4e13\u5c5e ammo_check \u52a8\u753b\u65f6\u4f1a\u4f18\u5148\u4f7f\u7528\u52a8\u753b\u81ea\u8eab\u957f\u5ea6\u3002"}).defineInRange("animationDurationSeconds", 2.55, 0.5, 10.0);
        RELOAD_START_BLOCK_SECONDS = builder.comment(new String[]{"After a normal reload tap is forwarded to TaCZ, ammo check cannot be triggered for this many seconds.", "\u77ed\u6309\u6362\u5f39\u89e6\u53d1 TaCZ \u6362\u5f39\u540e\uff0c\u591a\u5c11\u79d2\u5185\u7981\u6b62\u89e6\u53d1\u67e5\u5f39\u3002\u9ed8\u8ba4 2 \u79d2\u3002"}).defineInRange("reloadStartAmmoCheckBlockSeconds", 2.0, 0.0, 10.0);
        LOW_AMMO_THRESHOLD = builder.comment(new String[]{"Legacy bar setting retained for config compatibility. Numeric ammo colors use fixed 20% / 80% thresholds.", "\u65e7\u7ad6\u6761\u9009\u9879\uff0c\u4ec5\u4e3a\u914d\u7f6e\u517c\u5bb9\u4fdd\u7559\u3002\u6570\u5b57\u5f39\u91cf\u56fa\u5b9a\u4f7f\u7528 20% / 80% \u989c\u8272\u9608\u503c\u3002"}).defineInRange("lowAmmoRedThreshold", 0.15, 0.0, 1.0);
        SOUND_VOLUME_MULTIPLIER = builder.comment(new String[]{"Local ammo-check sound volume multiplier.", "\u67e5\u5f39\u58f0\u97f3\u97f3\u91cf\u500d\u7387\u3002"}).defineInRange("soundVolumeMultiplier", 1.7, 0.0, 4.0);
        UI_OFFSET_X = builder.comment(new String[]{"Horizontal offset for the magazine ammo count in GUI pixels. Positive moves right, negative moves left.", "\u5f39\u5323\u6570\u5b57\u5f39\u91cf\u7684\u6c34\u5e73\u504f\u79fb\uff0c\u5355\u4f4d\u4e3a GUI \u50cf\u7d20\u3002\u6b63\u6570\u5411\u53f3\uff0c\u8d1f\u6570\u5411\u5de6\u3002"}).defineInRange("uiOffsetX", -100, -500, 500);
        UI_OFFSET_Y = builder.comment(new String[]{"Vertical offset for the magazine ammo count in GUI pixels. Positive moves down, negative moves up.", "\u5f39\u5323\u6570\u5b57\u5f39\u91cf\u7684\u5782\u76f4\u504f\u79fb\uff0c\u5355\u4f4d\u4e3a GUI \u50cf\u7d20\u3002\u6b63\u6570\u5411\u4e0b\uff0c\u8d1f\u6570\u5411\u4e0a\u3002"}).defineInRange("uiOffsetY", 0, -500, 500);
        builder.pop();
        builder.push("high_ready_first_person");
        builder.comment(new String[]{"First-person position offsets while holding the High Ready key.", "\u9ad8\u59ff\u6001\u6301\u67aa\u7b2c\u4e00\u4eba\u79f0\u4f4d\u7f6e\u504f\u79fb\u3002\u53ea\u5f71\u54cd\u6309\u4f4f\u9ad8\u59ff\u6001\u6301\u67aa\u952e\u65f6\u7684\u624b\u81c2/\u67aa\u68b0\u4f4d\u7f6e\uff0c\u4e0d\u5f71\u54cd\u666e\u901a\u9876\u5899\u68c0\u6d4b\u957f\u5ea6\u3002", "RightOffset: positive moves weapon/arms to the player's right.", "VerticalOffset: negative moves up, positive moves down.", "BackOffset: positive moves backward toward the player."});
        builder.push("long_gun");
        HIGH_READY_LONG_RIGHT_OFFSET = LocknarConfig.offset(builder, "longRightOffset", 0.4, "\u957f\u67aa\u9ad8\u59ff\u6001\u5411\u53f3\u504f\u79fb\u3002", -1.0, 1.0);
        HIGH_READY_LONG_VERTICAL_OFFSET = LocknarConfig.offset(builder, "longVerticalOffset", -0.4, "\u957f\u67aa\u9ad8\u59ff\u6001\u4e0a\u4e0b\u504f\u79fb\u3002\u8d1f\u6570\u4e0a\u79fb\uff0c\u6b63\u6570\u4e0b\u79fb\u3002", -1.0, 1.0);
        HIGH_READY_LONG_BACK_OFFSET = LocknarConfig.offset(builder, "longBackOffset", 0.0, "\u957f\u67aa\u9ad8\u59ff\u6001\u524d\u540e\u504f\u79fb\u3002\u6b63\u6570\u5411\u540e\u9760\u8fd1\u73a9\u5bb6\u3002", -1.0, 1.0);
        builder.pop();
        builder.push("pistol");
        HIGH_READY_PISTOL_RIGHT_OFFSET = LocknarConfig.offset(builder, "pistolRightOffset", 0.5, "\u624b\u67aa\u9ad8\u59ff\u6001\u5411\u53f3\u504f\u79fb\u3002", -1.0, 1.0);
        HIGH_READY_PISTOL_VERTICAL_OFFSET = LocknarConfig.offset(builder, "pistolVerticalOffset", 0.0, "\u624b\u67aa\u9ad8\u59ff\u6001\u4e0a\u4e0b\u504f\u79fb\u3002\u8d1f\u6570\u4e0a\u79fb\uff0c\u6b63\u6570\u4e0b\u79fb\u3002", -1.0, 1.0);
        HIGH_READY_PISTOL_BACK_OFFSET = LocknarConfig.offset(builder, "pistolBackOffset", 0.0, "\u624b\u67aa\u9ad8\u59ff\u6001\u524d\u540e\u504f\u79fb\u3002\u6b63\u6570\u5411\u540e\u9760\u8fd1\u73a9\u5bb6\u3002", -1.0, 1.0);
        builder.pop();
        builder.pop();
        builder.push("wall_obstruction_first_person");
        builder.comment(new String[]{"First-person position offsets for the muzzle wall-obstruction raise animation.", "\u7b2c\u4e00\u4eba\u79f0\u67aa\u53e3\u9876\u5899\u52a8\u753b\u4f4d\u7f6e\u504f\u79fb\u3002\u53ea\u5f71\u54cd\u67aa\u53e3\u9876\u5899\u65f6\u7684\u624b\u81c2/\u67aa\u68b0\u62ac\u8d77\u4f4d\u7f6e\uff0c\u4e0d\u5f71\u54cd\u9ad8\u59ff\u6001\u6301\u67aa\u548c\u8e22\u95e8\u4e3e\u67aa\u3002", "RightOffset: positive moves weapon/arms to the player's right.", "VerticalOffset: negative moves up, positive moves down.", "BackOffset: positive moves backward toward the player."});
        builder.push("long_gun");
        WALL_LONG_RIGHT_OFFSET = LocknarConfig.offset(builder, "wallLongRightOffset", 0.1, "\u957f\u67aa\u67aa\u53e3\u9876\u5899\u5411\u53f3\u504f\u79fb\u3002", -1.0, 1.0);
        WALL_LONG_VERTICAL_OFFSET = LocknarConfig.offset(builder, "wallLongVerticalOffset", -0.4, "\u957f\u67aa\u67aa\u53e3\u9876\u5899\u4e0a\u4e0b\u504f\u79fb\u3002\u8d1f\u6570\u4e0a\u79fb\uff0c\u6b63\u6570\u4e0b\u79fb\u3002", -1.0, 1.0);
        WALL_LONG_BACK_OFFSET = LocknarConfig.offset(builder, "wallLongBackOffset", 0.1, "\u957f\u67aa\u67aa\u53e3\u9876\u5899\u524d\u540e\u504f\u79fb\u3002\u6b63\u6570\u5411\u540e\u9760\u8fd1\u73a9\u5bb6\u3002", -1.0, 1.0);
        builder.pop();
        builder.push("pistol");
        WALL_PISTOL_RIGHT_OFFSET = LocknarConfig.offset(builder, "wallPistolRightOffset", 0.05, "\u624b\u67aa\u67aa\u53e3\u9876\u5899\u5411\u53f3\u504f\u79fb\u3002", -1.0, 1.0);
        WALL_PISTOL_VERTICAL_OFFSET = LocknarConfig.offset(builder, "wallPistolVerticalOffset", -0.03, "\u624b\u67aa\u67aa\u53e3\u9876\u5899\u4e0a\u4e0b\u504f\u79fb\u3002\u8d1f\u6570\u4e0a\u79fb\uff0c\u6b63\u6570\u4e0b\u79fb\u3002", -1.0, 1.0);
        WALL_PISTOL_BACK_OFFSET = LocknarConfig.offset(builder, "wallPistolBackOffset", 0.05, "\u624b\u67aa\u67aa\u53e3\u9876\u5899\u524d\u540e\u504f\u79fb\u3002\u6b63\u6570\u5411\u540e\u9760\u8fd1\u73a9\u5bb6\u3002", -1.0, 1.0);
        builder.pop();
        builder.pop();
        builder.push("player_movement");
        PLAYER_WALK_SPEED_MULTIPLIER = builder.comment(new String[]{"Base non-sprinting movement speed multiplier while holding a TaCZ gun in Survival/Adventure mode.", "\u666e\u901a\u6301\u67aa\u79fb\u52a8\u901f\u5ea6\u500d\u7387\u3002\u53ea\u5728\u751f\u5b58/\u5192\u9669\u6a21\u5f0f\u3001\u975e\u75be\u8dd1\u3001\u624b\u6301 TaCZ \u67aa\u68b0\u65f6\u751f\u6548\u3002\u9ed8\u8ba4 0.90 = \u7565\u5fae\u964d\u4f4e\u8d70\u8def\u901f\u5ea6\u3002"}).defineInRange("walkSpeedMultiplier", 0.9, 0.05, 2.0);
        HIGH_READY_SPEED_MULTIPLIER = builder.comment(new String[]{"Extra non-sprinting movement speed multiplier while High Ready is active.", "\u9ad8\u59ff\u6001\u6301\u67aa\u79fb\u52a8\u901f\u5ea6\u500d\u7387\u3002\u4f1a\u53e0\u52a0\u5728\u666e\u901a\u6301\u67aa\u79fb\u52a8\u901f\u5ea6\u4e0a\uff1b\u75be\u8dd1\u4f1a\u53d6\u6d88\u9ad8\u59ff\u6001\uff0c\u75be\u8dd1\u901f\u5ea6\u4e0d\u53d7\u5f71\u54cd\u3002"}).defineInRange("highReadySpeedMultiplier", 1.12, 0.05, 2.0);
        builder.pop();
        builder.push("features");
        TACZ_WALL_OBSTRUCTION_ENABLED = builder.comment(new String[]{"Enable TaCZ muzzle wall-obstruction raise mechanic. Shooting is blocked while the muzzle is obstructed.", "\u67aa\u53e3\u9876\u5899\u673a\u5236\u662f\u5426\u5f00\u542f\u3002\u5f00\u542f\u540e\u67aa\u53e3\u8d34\u8fd1\u5899\u4f53\u4f1a\u62ac\u67aa\uff0c\u5e76\u963b\u6b62\u5c04\u51fb\u3002\u9ed8\u8ba4\u5f00\u542f\u3002"}).define("enableTaczWallObstruction", true);
        PLAYER_STATUS_HUD_ENABLED = builder.comment(new String[]{"Enable the lower-left player status portrait HUD.", "\u5de6\u4e0b\u89d2\u4eba\u7269\u72b6\u6001 HUD \u662f\u5426\u663e\u793a\u3002\u5173\u95ed\u540e\u4e0d\u518d\u6e32\u67d3\u5de6\u4e0b\u89d2\u4eba\u7269\u56fe\u7247\u3002\u9ed8\u8ba4\u5173\u95ed\u3002"}).define("enablePlayerStatusHud", false);
        HIDE_VANILLA_HUD = builder.comment("\u9690\u85cf\u539f\u7248\u751f\u5b58 HUD\uff1b\u9ed8\u8ba4\u5173\u95ed\u3002").define("hideVanillaHud", false);
        FRONT_SLING_GUN_ENABLED = builder.comment(new String[]{"Replace TaCZ hotbar gun display with the front lower-abdomen sling display for slot 1 long guns.", "\u63a5\u7ba1 TaCZ \u5feb\u6377\u680f\u67aa\u68b0\u663e\u793a\u3002\u5feb\u6377\u680f\u7b2c\u4e00\u683c\u4e2d\u7684\u975e\u624b\u67aa\u4f1a\u663e\u793a\u5728\u73a9\u5bb6\u524d\u65b9\u4e0b\u8179\u90e8\u3002\u9ed8\u8ba4\u5f00\u542f\u3002"}).define("enableFrontSlingGun", true);
        THIRD_PERSON_WEAPON_SWITCH_ENABLED = builder.comment(new String[]{"Enable synchronized third-person holster/draw animations when switching items.", "\u7b2c\u4e09\u4eba\u79f0\u5207\u67aa\u52a8\u753b\u662f\u5426\u5f00\u542f\u3002\u6536\u67aa\u548c\u4e3e\u67aa\u5404 0.3 \u79d2\u3002\u9ed8\u8ba4\u5f00\u542f\u3002"}).define("enableThirdPersonWeaponSwitchAnimation", true);
        DIRECTIONAL_LEG_ANIMATIONS_ENABLED = builder.comment(new String[]{"Enable direction-aware TaCZ lower-body movement animations for forward, backward and strafing movement.", "\u66f4\u597d\u7684 TaCZ \u6301\u67aa\u817f\u90e8\u52a8\u4f5c\u3002\u6839\u636e\u5b9e\u9645\u79fb\u52a8\u65b9\u5411\u533a\u5206\u516b\u4e2a\u7ad9\u7acb\u79fb\u52a8\u65b9\u5411\uff1b\u4e0b\u8e72\u4fdd\u6301 TaCZ \u539f\u7248\u52a8\u4f5c\u3002\u9ed8\u8ba4\u5f00\u542f\u3002"}).define("enableDirectionalLegAnimations", true);
        GUN_RUN_ANIMATIONS_ENABLED = builder.comment(new String[]{"Enable tactical TaCZ gun-running animations with diagonal running and interruptible braking inertia.", "\u771f\u5b9e TaCZ \u6301\u67aa\u8dd1\u6b65\u52a8\u4f5c\u3002\u5305\u542b\u524d/\u5de6\u524d/\u53f3\u524d\u8dd1\u6b65\u3001\u53ef\u4e2d\u65ad\u5239\u505c\u548c\u7b2c\u4e09\u4eba\u79f0\u8eab\u4f53\u503e\u659c\u3002\u9ed8\u8ba4\u5f00\u542f\u3002"}).define("enableGunRunAnimations", true);
        TACTICAL_PRONE_ANIMATIONS_ENABLED = builder.comment(new String[]{"Enable prone transitions, crawling and stationary turning animations.", "\u542f\u7528\u8db4\u4e0b\u3001\u8d77\u8eab\u3001\u722c\u884c\u548c\u9759\u6b62\u8f6c\u8eab\u52a8\u753b\uff1b\u7a7a\u624b\u4e0e\u6301\u67aa\u5747\u53ef\u4f7f\u7528\u3002"}).define("enableTacticalProneAnimations", true);
        TACTICAL_PRONE_ROTATION_SMOOTHING_TICKS = builder.comment(new String[]{"Blend time, in client ticks, when entering/leaving the stationary prone rotation overlay.", "\u8db4\u4e0b\u9759\u6b62\u65cb\u8f6c\u59ff\u52bf\u7684\u5e73\u6ed1\u8fc7\u6e21 tick \u6570\u3002\u5c24\u5176\u7528\u4e8e\u65cb\u8f6c\u59ff\u52bf\u4e2d\u5f00\u59cb\u79fb\u52a8\u65f6\uff0c\u907f\u514d\u6a21\u578b\u77ac\u95f4\u8df3\u56de TaCZ \u722c\u884c\u52a8\u4f5c\u3002\u9ed8\u8ba4 6 tick\uff08\u7ea6 0.30 \u79d2\uff09\u3002"}).defineInRange("proneRotationSmoothingTicks", 6, 1, 30);
        TACTICAL_PRONE_ROTATION_FOLLOW_SMOOTHING_MS = builder.comment(new String[]{"Frame-rate-independent visual follow smoothing for the third-person prone rotation pose, in milliseconds. 0 disables it.", "\u8db4\u4e0b\u9759\u6b62\u65cb\u8f6c\u59ff\u52bf\u8ddf\u968f\u89c6\u89d2\u65f6\u7684\u7b2c\u4e09\u4eba\u79f0\u89c6\u89c9\u5e73\u6ed1\u65f6\u95f4\uff08\u6beb\u79d2\uff09\u3002\u53ea\u5e73\u6ed1\u6a21\u578b\uff0c\u4e0d\u5f71\u54cd\u73a9\u5bb6\u5b9e\u9645\u89c6\u89d2/\u7784\u51c6\u3002\u9ed8\u8ba4 40 ms\uff1b0 \u4e3a\u5173\u95ed\u3002"}).defineInRange("proneRotationFollowSmoothingMs", 40, 0, 150);
        builder.pop();
        COMMON_SPEC = builder.build();
    }
}
