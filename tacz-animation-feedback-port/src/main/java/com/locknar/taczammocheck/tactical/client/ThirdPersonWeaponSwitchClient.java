package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.ActionAnimationSampler;
import com.locknar.taczammocheck.tactical.client.BackGunClientCache;
import com.locknar.taczammocheck.tactical.client.TaczThirdPersonPoseCompat;
import com.locknar.taczammocheck.tactical.client.WeaponVisualConfigClient;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class ThirdPersonWeaponSwitchClient {
    private static final long PHASE_NANOS = 300000000L;
    private static final long FINISHED_STATE_GRACE_NANOS = 750000000L;
    private static final Map<Integer, State> STATES = new ConcurrentHashMap<Integer, State>();
    private static final Map<Integer, Deque<RenderOverride>> RENDER_OVERRIDES = new ConcurrentHashMap<Integer, Deque<RenderOverride>>();
    private static LocalSnapshot localSnapshot;

    private ThirdPersonWeaponSwitchClient() {
    }

    public static void handleSwitch(int entityId, int sequence, int type, int oldSlot, int newSlot, ItemStack outgoing, ItemStack incoming) {
        ItemStack incomingCopy;
        boolean localPlayer;
        long now = System.nanoTime();
        State existing = STATES.get(entityId);
        Minecraft mc = Minecraft.getInstance();
        boolean bl = localPlayer = mc.player != null && mc.player.getId() == entityId;
        if (type == 0) {
            if (existing != null) {
                if (sequence < existing.sequence) {
                    return;
                }
                if (localPlayer && existing.predicted && (existing.oldSlot != oldSlot || existing.newSlot != newSlot) && now - existing.startNanos < 1350000000L) {
                    return;
                }
            }
            STATES.remove(entityId);
            return;
        }
        ItemStack outgoingCopy = outgoing == null ? ItemStack.EMPTY : outgoing.copy();
        ItemStack itemStack = incomingCopy = incoming == null ? ItemStack.EMPTY : incoming.copy();
        if (existing != null && localPlayer && existing.predicted) {
            if (existing.matches(type, oldSlot, newSlot, outgoingCopy, incomingCopy)) {
                STATES.put(entityId, existing.confirm(sequence));
                return;
            }
            if (now - existing.startNanos < 1350000000L) {
                return;
            }
        }
        if (existing != null && sequence < existing.sequence) {
            return;
        }
        STATES.put(entityId, new State(sequence, type, oldSlot, newSlot, outgoingCopy, incomingCopy, now, false));
    }

    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        AbstractClientPlayer player;
        block5: {
            block4: {
                Player player2 = event.getEntity();
                if (!(player2 instanceof AbstractClientPlayer)) break block4;
                player = (AbstractClientPlayer)player2;
                if (((Boolean)LocknarConfig.THIRD_PERSON_WEAPON_SWITCH_ENABLED.get()).booleanValue()) break block5;
            }
            return;
        }
        ItemStack outgoing = ThirdPersonWeaponSwitchClient.getHolsterOutgoingGun((LivingEntity)player);
        if (outgoing.isEmpty()) {
            return;
        }
        int selected = player.getInventory().selected;
        ItemStack original = player.getInventory().getItem(selected);
        RENDER_OVERRIDES.computeIfAbsent(player.getId(), ignored -> new ArrayDeque()).push(new RenderOverride(selected, original));
        player.getInventory().setItem(selected, outgoing);
    }

    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player2 = (AbstractClientPlayer)player;
        Deque<RenderOverride> overrides = RENDER_OVERRIDES.get(player2.getId());
        if (overrides == null || overrides.isEmpty()) {
            return;
        }
        RenderOverride override = overrides.pop();
        player2.getInventory().setItem(override.slot, override.original);
        if (overrides.isEmpty()) {
            RENDER_OVERRIDES.remove(player2.getId());
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (!RENDER_OVERRIDES.isEmpty()) {
            ThirdPersonWeaponSwitchClient.restoreAllRenderOverrides();
        }
        if (mc.level == null || mc.player == null) {
            STATES.clear();
            localSnapshot = null;
            return;
        }
        ThirdPersonWeaponSwitchClient.ensureLocalTransition((AbstractClientPlayer)mc.player);
        if (mc.player.tickCount % 100 == 0) {
            BackGunClientCache.prune(mc.level);
        }
        long now = System.nanoTime();
        STATES.entrySet().removeIf(entry -> ((State)entry.getValue()).isFinished(now) || mc.level.getEntity(((Integer)entry.getKey()).intValue()) == null);
    }

    public static ItemStack getHolsterOutgoingGun(LivingEntity entity) {
        AbstractClientPlayer player;
        block5: {
            block4: {
                if (!(entity instanceof AbstractClientPlayer)) break block4;
                player = (AbstractClientPlayer)entity;
                if (((Boolean)LocknarConfig.THIRD_PERSON_WEAPON_SWITCH_ENABLED.get()).booleanValue()) break block5;
            }
            return ItemStack.EMPTY;
        }
        ThirdPersonWeaponSwitchClient.ensureLocalTransition(player);
        PhaseView view = ThirdPersonWeaponSwitchClient.phaseView(player.getId(), System.nanoTime());
        if (view == null || view.phase != Phase.HOLSTER || view.state.outgoing.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return view.state.outgoing;
    }

    public static ItemStack getRenderedMainHand(LivingEntity entity) {
        if (!(entity instanceof AbstractClientPlayer)) {
            return entity.getMainHandItem();
        }
        AbstractClientPlayer player = (AbstractClientPlayer)entity;
        ThirdPersonWeaponSwitchClient.ensureLocalTransition(player);
        if (!((Boolean)LocknarConfig.THIRD_PERSON_WEAPON_SWITCH_ENABLED.get()).booleanValue()) {
            return entity.getMainHandItem();
        }
        PhaseView view = ThirdPersonWeaponSwitchClient.phaseView(player.getId(), System.nanoTime());
        if (view == null) {
            return entity.getMainHandItem();
        }
        if (view.phase == Phase.HOLSTER && !view.state.outgoing.isEmpty()) {
            return view.state.outgoing;
        }
        if (view.phase == Phase.DRAW && !view.state.incoming.isEmpty()) {
            return view.state.incoming;
        }
        return entity.getMainHandItem();
    }

    public static void applyPoseAfterSetup(AbstractClientPlayer player, PlayerModel model) {
        TaczGunCompat.GunInfo incomingInfo;
        if (player == null || model == null || player.isSpectator()) {
            return;
        }
        ThirdPersonWeaponSwitchClient.ensureLocalTransition(player);
        WeaponVisualConfigClient.Animation animation = null;
        float progress = -1.0f;
        PhaseView activeView = null;
        if (((Boolean)LocknarConfig.THIRD_PERSON_WEAPON_SWITCH_ENABLED.get()).booleanValue() && (activeView = ThirdPersonWeaponSwitchClient.phaseView(player.getId(), System.nanoTime())) != null) {
            animation = activeView.phase == Phase.HOLSTER ? WeaponVisualConfigClient.get().holster : WeaponVisualConfigClient.get().draw;
            progress = activeView.progress;
        }
        if (animation == null || progress < 0.0f) {
            return;
        }
        TaczGunCompat.GunInfo gunInfo = incomingInfo = activeView == null ? null : TaczGunCompat.getGunInfo(activeView.state.incoming);
        if (activeView != null && activeView.phase == Phase.DRAW && incomingInfo != null && incomingInfo.kind() == TaczGunCompat.GunKind.PISTOL && player.isCrouching() && player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-5) {
            TaczThirdPersonPoseCompat.applyGunHoldPose(player, model, activeView.state.incoming);
        }
        ActionAnimationSampler.applyModelPart(model.body, WeaponVisualConfigClientAdapter.toLegacy(WeaponVisualConfigClient.sample(animation, WeaponVisualConfigClient.Bone.BODY, progress)));
        ActionAnimationSampler.applyModelPart(model.rightArm, WeaponVisualConfigClientAdapter.toLegacy(WeaponVisualConfigClient.sample(animation, WeaponVisualConfigClient.Bone.RIGHT_ARM, progress)));
        ActionAnimationSampler.applyModelPart(model.leftArm, WeaponVisualConfigClientAdapter.toLegacy(WeaponVisualConfigClient.sample(animation, WeaponVisualConfigClient.Bone.LEFT_ARM, progress)));
        model.jacket.copyFrom(model.body);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftSleeve.copyFrom(model.leftArm);
    }

    public static ItemStack getFrontSlingStack(AbstractClientPlayer player, ItemStack normalFirstSlot, int selectedSlot) {
        if (player == null || normalFirstSlot == null) {
            return ItemStack.EMPTY;
        }
        ThirdPersonWeaponSwitchClient.ensureLocalTransition(player);
        if (!((Boolean)LocknarConfig.THIRD_PERSON_WEAPON_SWITCH_ENABLED.get()).booleanValue()) {
            return selectedSlot == 0 ? ItemStack.EMPTY : normalFirstSlot;
        }
        PhaseView view = ThirdPersonWeaponSwitchClient.phaseView(player.getId(), System.nanoTime());
        if (selectedSlot == 0) {
            if (view != null && view.phase == Phase.HOLSTER && view.state.newSlot == 0 && view.state.oldSlot != 0 && !view.state.incoming.isEmpty()) {
                return view.state.incoming;
            }
            return ItemStack.EMPTY;
        }
        if (view != null && view.phase == Phase.HOLSTER && view.state.oldSlot == 0) {
            return ItemStack.EMPTY;
        }
        return normalFirstSlot;
    }

    public static void clear() {
        ThirdPersonWeaponSwitchClient.restoreAllRenderOverrides();
        STATES.clear();
        localSnapshot = null;
    }

    private static void ensureLocalTransition(AbstractClientPlayer player) {
        if (player == null || !player.isLocalPlayer() || ThirdPersonWeaponSwitchClient.isRenderOverrideActive(player.getId())) {
            return;
        }
        LocalSnapshot current = LocalSnapshot.capture(player);
        LocalSnapshot previous = localSnapshot;
        localSnapshot = current;
        if (previous == null || previous.selectedSlot == current.selectedSlot || !((Boolean)LocknarConfig.THIRD_PERSON_WEAPON_SWITCH_ENABLED.get()).booleanValue()) {
            return;
        }
        boolean oldGun = TaczGunCompat.isTaczGun(previous.mainHand);
        boolean newGun = TaczGunCompat.isTaczGun(current.mainHand);
        if (oldGun && !newGun) {
            STATES.remove(player.getId());
            return;
        }
        if (!newGun) {
            return;
        }
        int type = oldGun ? 2 : 1;
        State existing = STATES.get(player.getId());
        if (existing != null && existing.matches(type, previous.selectedSlot, current.selectedSlot, previous.mainHand, current.mainHand)) {
            return;
        }
        int sequenceFloor = existing == null ? Integer.MIN_VALUE : existing.sequence;
        STATES.put(player.getId(), new State(sequenceFloor, type, previous.selectedSlot, current.selectedSlot, previous.mainHand.copy(), current.mainHand.copy(), System.nanoTime(), true));
    }

    private static boolean isRenderOverrideActive(int entityId) {
        Deque<RenderOverride> overrides = RENDER_OVERRIDES.get(entityId);
        return overrides != null && !overrides.isEmpty();
    }

    private static void restoreAllRenderOverrides() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            RENDER_OVERRIDES.forEach((entityId, overrides) -> {
                if (overrides == null || overrides.isEmpty()) {
                    return;
                }
                Entity patt14082$temp = mc.level.getEntity(entityId.intValue());
                if (patt14082$temp instanceof AbstractClientPlayer) {
                    AbstractClientPlayer player = (AbstractClientPlayer)patt14082$temp;
                    RenderOverride original = (RenderOverride)overrides.peekLast();
                    if (original != null) {
                        player.getInventory().setItem(original.slot, original.original);
                    }
                }
            });
        }
        RENDER_OVERRIDES.clear();
    }

    private static PhaseView phaseView(int entityId, long now) {
        State state = STATES.get(entityId);
        if (state == null) {
            return null;
        }
        long elapsed = Math.max(0L, now - state.startNanos);
        return switch (state.type) {
            case 0 -> {
                if (elapsed >= 300000000L) {
                    yield null;
                }
                yield new PhaseView(state, Phase.HOLSTER, (float)elapsed / 3.0E8f);
            }
            case 1 -> {
                if (elapsed >= 300000000L) {
                    yield null;
                }
                yield new PhaseView(state, Phase.DRAW, (float)elapsed / 3.0E8f);
            }
            case 2 -> {
                if (elapsed < 300000000L) {
                    yield new PhaseView(state, Phase.HOLSTER, (float)elapsed / 3.0E8f);
                }
                long drawElapsed = elapsed - 300000000L;
                if (drawElapsed >= 300000000L) {
                    yield null;
                }
                yield new PhaseView(state, Phase.DRAW, (float)drawElapsed / 3.0E8f);
            }
            default -> null;
        };
    }

    private static boolean sameStack(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.isEmpty() || b.isEmpty()) {
            return a.isEmpty() && b.isEmpty();
        }
        return a.getCount() == b.getCount() && ItemStack.isSameItemSameComponents((ItemStack)a, (ItemStack)b);
    }

    private static final class State {
        private final int sequence;
        private final int type;
        private final int oldSlot;
        private final int newSlot;
        private final ItemStack outgoing;
        private final ItemStack incoming;
        private final long startNanos;
        private final boolean predicted;

        private State(int sequence, int type, int oldSlot, int newSlot, ItemStack outgoing, ItemStack incoming, long startNanos, boolean predicted) {
            this.sequence = sequence;
            this.type = type;
            this.oldSlot = oldSlot;
            this.newSlot = newSlot;
            this.outgoing = outgoing;
            this.incoming = incoming;
            this.startNanos = startNanos;
            this.predicted = predicted;
        }

        private State confirm(int serverSequence) {
            return new State(serverSequence, this.type, this.oldSlot, this.newSlot, this.outgoing, this.incoming, this.startNanos, false);
        }

        private boolean matches(int otherType, int otherOldSlot, int otherNewSlot, ItemStack otherOutgoing, ItemStack otherIncoming) {
            return this.type == otherType && this.oldSlot == otherOldSlot && this.newSlot == otherNewSlot && ThirdPersonWeaponSwitchClient.sameStack(this.outgoing, otherOutgoing) && ThirdPersonWeaponSwitchClient.sameStack(this.incoming, otherIncoming);
        }

        private boolean isFinished(long now) {
            long duration = this.type == 2 ? 600000000L : 300000000L;
            return now - this.startNanos >= duration + 750000000L;
        }
    }

    private record RenderOverride(int slot, ItemStack original) {
    }

    private record LocalSnapshot(int selectedSlot, ItemStack mainHand) {
        private static LocalSnapshot capture(AbstractClientPlayer player) {
            return new LocalSnapshot(player.getInventory().selected, player.getMainHandItem().copy());
        }
    }

    private record PhaseView(State state, Phase phase, float progress) {
    }

    private static enum Phase {
        HOLSTER,
        DRAW;

    }

    private static final class WeaponVisualConfigClientAdapter {
        private WeaponVisualConfigClientAdapter() {
        }

        private static ActionAnimationSampler.Transform toLegacy(WeaponVisualConfigClient.Transform transform) {
            return new ActionAnimationSampler.Transform(transform.x, transform.y, transform.z, transform.rotX, transform.rotY, transform.rotZ, 1.0);
        }
    }
}
