package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.client.ClientCleanup;
import com.locknar.taczammocheck.tactical.client.ClientRenderLayers;
import com.locknar.taczammocheck.tactical.client.DirectionalLegAnimationClient;
import com.locknar.taczammocheck.tactical.client.DoorOpenHandAnimationClient;
import com.locknar.taczammocheck.tactical.client.GunRunAnimationClient;
import com.locknar.taczammocheck.tactical.client.HudRenderer;
import com.locknar.taczammocheck.tactical.client.KickAnimationClient;
import com.locknar.taczammocheck.tactical.client.MirrorViewClient;
import com.locknar.taczammocheck.tactical.client.PlayerAnimatorBridge;
import com.locknar.taczammocheck.tactical.client.PlayerStatusHudRenderer;
import com.locknar.taczammocheck.tactical.client.ProneTacticalAnimationClient;
import com.locknar.taczammocheck.tactical.client.TaczHighReadyClient;
import com.locknar.taczammocheck.tactical.client.TaczWallObstructionClient;
import com.locknar.taczammocheck.tactical.client.TaczWallObstructionThirdPersonClient;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import com.locknar.taczammocheck.tactical.client.VanillaHudHider;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;

public final class ClientInit {
    private ClientInit() {
    }

    public static void initModBus(IEventBus modEventBus) {
        modEventBus.addListener(TaczHighReadyClient::onRegisterKeyMappings);
        modEventBus.addListener(ClientRenderLayers::onAddLayers);
    }

    public static void init() {
        PlayerAnimatorBridge.init();
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, PlayerStatusHudRenderer::onRenderGui);
        NeoForge.EVENT_BUS.addListener(VanillaHudHider::onRenderGuiOverlayPre);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, HudRenderer::onRenderGui);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, MirrorViewClient::onRenderGui);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, MirrorViewClient::onRenderHand);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, true, TaczWallObstructionClient::onRenderHand);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, KickAnimationClient::captureFirstPersonKickLegPose);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, KickAnimationClient::onRenderHand);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, KickAnimationClient::onRenderGui);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, DoorOpenHandAnimationClient::onRenderHand);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DoorOpenHandAnimationClient::onRenderGui);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, TaczWallObstructionClient::onRenderGui);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, MirrorViewClient::onCameraAngles);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, KickAnimationClient::onCameraAngles);
        NeoForge.EVENT_BUS.addListener(MirrorViewClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(TaczWallObstructionClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(KickAnimationClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(TaczHighReadyClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(TaczWallObstructionThirdPersonClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ThirdPersonWeaponSwitchClient::onRenderPlayerPre);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ThirdPersonWeaponSwitchClient::onRenderPlayerPost);
        NeoForge.EVENT_BUS.addListener(ThirdPersonWeaponSwitchClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(GunRunAnimationClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(DirectionalLegAnimationClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(ProneTacticalAnimationClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClientCleanup::onClientLoggingOut);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, MirrorViewClient::onMovementInputUpdate);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, MirrorViewClient::onRightClickBlockClient);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, KickAnimationClient::onRenderPlayerPre);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, KickAnimationClient::onRenderPlayerPost);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, GunRunAnimationClient::onRenderPlayerPre);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, GunRunAnimationClient::onRenderPlayerPost);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, ProneTacticalAnimationClient::onRenderPlayerPre);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ProneTacticalAnimationClient::onRenderPlayerPost);
    }
}
