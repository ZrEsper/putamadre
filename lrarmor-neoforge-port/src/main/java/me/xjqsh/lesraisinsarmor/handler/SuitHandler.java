package me.xjqsh.lesraisinsarmor.handler;

import me.xjqsh.lesraisinsarmor.config.CommonConfig;
import me.xjqsh.lesraisinsarmor.init.ModEffects;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid="lrarmor")
public class SuitHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        LrArmorItem armorItem;
        if (!((Boolean)CommonConfig.enableArmorSetEffect.get()).booleanValue()) {
            return;
        }
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getEntity().tickCount % 10 != 0) {
            return;
        }
        Item item = event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem();
        if (item instanceof LrArmorItem && (armorItem = (LrArmorItem)item).getSuitCount(event.getEntity()) == 4) {
            if ("medical".equals(armorItem.getSuitIdf())) {
                if (event.getEntity().getEffect(ModEffects.RESCUE_COOLDOWN) == null) {
                    armorItem.applyEffect(event.getEntity());
                }
            } else {
                armorItem.applyEffect(event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getEntity().hasEffect(ModEffects.HEAVY_ARMOR)) {
            event.setNewDamage(event.getNewDamage() * 0.85f);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        LivingEntity livingEntity;
        if (event.getEntity().level().isClientSide() || !((livingEntity = event.getEntity()) instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        if ((double)(player.getHealth() - event.getAmount()) < (double)player.getMaxHealth() * 0.3 && player.hasEffect(ModEffects.RESCUE)) {
            player.removeEffect(ModEffects.RESCUE);
            player.addEffect(new MobEffectInstance(ModEffects.RESCUE_COOLDOWN, 600));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3));
        }
    }
}
