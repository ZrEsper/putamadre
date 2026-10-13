package me.xjqsh.lesraisinsarmor.init;

import me.xjqsh.lesraisinsarmor.effect.SuitEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEffects {
    public static final MobEffect TOUGH_EFFECT = new SuitEffect(MobEffectCategory.BENEFICIAL, 0, "attacker").addAttributeModifier(Attributes.ARMOR_TOUGHNESS, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lrarmor","effect_62e0c0f3_d94e_3821_bf1c_b5f17cb7f971"), 15.0, AttributeModifier.Operation.ADD_VALUE).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lrarmor","effect_0bce518e_9d05_cb4a_c435_c68beee57650"), 0.25, AttributeModifier.Operation.ADD_VALUE);
    public static final MobEffect LIGHT_LEG_EFFECT = new SuitEffect(MobEffectCategory.BENEFICIAL, 0, "scout").addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lrarmor","effect_fc303d2a_7c7f_ffab_2b4e_a5ef09bb2af1"), 0.025, AttributeModifier.Operation.ADD_VALUE);
    public static final MobEffect HEAVY_EFFECT = new SuitEffect(MobEffectCategory.BENEFICIAL, 0, "defender").addAttributeModifier(Attributes.MOVEMENT_SPEED, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("lrarmor","effect_37dae46a_cb52_2e2c_71fc_4ae7348b0b8d"), -0.02, AttributeModifier.Operation.ADD_VALUE);
    public static final MobEffect RESCUE_EFFECT = new SuitEffect(MobEffectCategory.BENEFICIAL, 0, "medical");
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, "lrarmor");
    public static final DeferredHolder<MobEffect,MobEffect> TOUGH = REGISTER.register("tough", () -> TOUGH_EFFECT);
    public static final DeferredHolder<MobEffect,MobEffect> LIGHT_LEG = REGISTER.register("light_leg", () -> LIGHT_LEG_EFFECT);
    public static final DeferredHolder<MobEffect,MobEffect> HEAVY_ARMOR = REGISTER.register("heavy_armor", () -> HEAVY_EFFECT);
    public static final DeferredHolder<MobEffect,MobEffect> RESCUE = REGISTER.register("rescue", () -> RESCUE_EFFECT);
    public static final DeferredHolder<MobEffect,MobEffect> RESCUE_COOLDOWN = REGISTER.register("rescue_cooldown", () -> new MobEffect(MobEffectCategory.HARMFUL, 0){});
}
