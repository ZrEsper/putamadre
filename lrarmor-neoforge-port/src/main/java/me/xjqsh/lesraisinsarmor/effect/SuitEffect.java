package me.xjqsh.lesraisinsarmor.effect;

import javax.annotation.Nonnull;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SuitEffect
extends MobEffect {
    private final String suitIdf;

    public SuitEffect(MobEffectCategory effectType, int color, String suitIdf) {
        super(effectType, color);
        this.suitIdf = suitIdf;
    }

    @Override
    public boolean applyEffectTick(@Nonnull LivingEntity entity,int amplifier){
        return !(entity instanceof Player player)||LrArmorItem.getSuitCount(player,this.suitIdf)==4;
    }

    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
