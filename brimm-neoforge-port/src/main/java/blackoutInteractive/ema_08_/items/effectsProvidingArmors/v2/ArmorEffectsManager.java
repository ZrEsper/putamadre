package blackoutInteractive.ema_08_.items.effectsProvidingArmors.v2;

import blackoutInteractive.ema_08_.items.effectsProvidingArmors.v2.ArmorEffectsSetup;
import blackoutInteractive.ema_08_.items.effectsProvidingArmors.v2.IEffectsProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;

public final class ArmorEffectsManager {
    private ArmorEffectsManager() {
    }

    @SubscribeEvent
    public static void onEffectApplication(MobEffectEvent.Applicable event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)livingEntity;
        for (ItemStack stack : player.getInventory().armor) {
            IEffectsProvider ep;
            Item item = stack.getItem();
            if (!(item instanceof IEffectsProvider) || !(ep = (IEffectsProvider)item).getEffectsSetup().remOnWear().contains(event.getEffectInstance().getEffect().value())) continue;
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
    }

    @SubscribeEvent
    public static void onEffectRemotion(MobEffectEvent.Remove event) {
        int amplifier;
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)livingEntity;
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) {
            effectInstance = player.getEffect(event.getEffect());
        }
        if (effectInstance != null && effectInstance.isInfiniteDuration() && (amplifier = effectInstance.getAmplifier()) >= 0 && amplifier <= 4) {
            MobEffect effect = effectInstance.getEffect().value();
            ArmorEffectsSetup.AmplifiedEffect effectAmplified = new ArmorEffectsSetup.AmplifiedEffect(effect, amplifier);
            boolean toCancel = false;
            for (ItemStack stack : player.getInventory().armor) {
                Item item = stack.getItem();
                if (!(item instanceof IEffectsProvider)) continue;
                IEffectsProvider ep = (IEffectsProvider)item;
                if (ep.getEffectsSetup().remOnWear().contains(effect)) {
                    return;
                }
                if (toCancel || !ep.getEffectsSetup().addOnWear().contains(effectAmplified)) continue;
                toCancel = true;
            }
            if (toCancel) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        Item t;
        ServerPlayer player;
        block11: {
            block10: {
                LivingEntity livingEntity = event.getEntity();
                if (!(livingEntity instanceof ServerPlayer)) break block10;
                player = (ServerPlayer)livingEntity;
                if (event.getSlot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR) break block11;
            }
            return;
        }
        Item f = event.getFrom().getItem();
        if (f == (t = event.getTo().getItem())) {
            return;
        }
        if (f instanceof IEffectsProvider) {
            IEffectsProvider fromp = (IEffectsProvider)f;
            for (ArmorEffectsSetup.AmplifiedEffect old : fromp.getEffectsSetup().addOnWear()) {
                if (!player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(old.effect()))) continue;
                player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(old.effect()));
            }
        }
        for (ItemStack stack : player.getInventory().armor) {
            Item item = stack.getItem();
            if (!(item instanceof IEffectsProvider)) continue;
            IEffectsProvider ep = (IEffectsProvider)item;
            for (ArmorEffectsSetup.AmplifiedEffect e : ep.getEffectsSetup().addOnWear()) {
                player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(e.effect()), -1, e.amplifier(), false, false, true));
            }
        }
        if (t instanceof IEffectsProvider) {
            IEffectsProvider top = (IEffectsProvider)t;
            for (MobEffect e : top.getEffectsSetup().remOnWear()) {
                if (!player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(e))) continue;
                player.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(e));
            }
        }
    }
}

