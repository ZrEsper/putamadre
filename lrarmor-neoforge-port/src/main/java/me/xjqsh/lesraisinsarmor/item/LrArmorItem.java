package me.xjqsh.lesraisinsarmor.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import me.xjqsh.lesraisinsarmor.client.renderer.BedrockArmorRenderer;
import me.xjqsh.lesraisinsarmor.config.CommonConfig;
import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorData;
import me.xjqsh.lesraisinsarmor.resource.data.ArmorPartData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LrArmorItem
extends ArmorItem
implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);
    private final String suitIdf;
    @Nullable
    private final Supplier<MobEffect> suitEffect;
    private ArmorData armorData;



    public LrArmorItem(String suitIdf, ArmorItem.Type slot, Item.Properties properties, @Nullable Supplier<MobEffect> suitEffect) {
        super(ArmorMaterials.LEATHER, slot, properties.durability(128));
        this.suitIdf = suitIdf;
        this.suitEffect = suitEffect;
    }

    public String getSuitIdf() {
        return this.suitIdf;
    }

    public void setArmorData(ArmorDataManager.ArmorDataSupplier supplier) {
        this.armorData = supplier.get();

    }


    public int getDefense() {
        return ArmorData.getByType(this.armorData, this.type, 0, ArmorPartData::getDefense);
    }

    public float getToughness() {
        return ArmorData.getByType(this.armorData, this.type, Float.valueOf(0.0f), ArmorPartData::getToughness).floatValue();
    }

    public int getMaxDamage(ItemStack stack) {
        return this.getMaxDurability();
    }

    public int getMaxDurability() {
        return ArmorData.getByType(this.armorData, this.type, 0, ArmorPartData::getMaxDurability);
    }

    public float getKnockbackResistance() {
        return ArmorData.getByType(this.armorData, this.type, Float.valueOf(0.0f), ArmorPartData::getKnockbackResistance).floatValue();
    }

    public int getEnchantmentValue(ItemStack stack) {
        return ArmorData.getByType(this.armorData, this.type, 5, ArmorPartData::getEnchantmentValue);
    }

    public boolean isDamageable(ItemStack stack) {
        return this.getMaxDamage(stack) > 0;
    }

    public boolean canBeDepleted() {
        return this.getMaxDurability() > 0;
    }

    @NotNull
    public Holder<SoundEvent> getEquipSound() {
        return Holder.direct(ArmorData.getByType(this.armorData, this.type, SoundEvents.ARMOR_EQUIP_LEATHER.value(), ArmorPartData::getEquipSound));
    }

    public boolean isValidRepairItem(@NotNull ItemStack pToRepair, @NotNull ItemStack pRepair) {
        Ingredient ingredient = this.getIngredient();
        if (ingredient != null) {
            return ingredient.test(pRepair);
        }
        return false;
    }

    @Nullable
    public Ingredient getIngredient() {
        return ArmorData.getByType(this.armorData, this.type, null, ArmorPartData::getRepairIngredient);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        if(!CommonConfig.enableArmorAttribute.get()||armorData==null)return ItemAttributeModifiers.EMPTY;
        var part=ArmorData.getByType(armorData,type,(ArmorPartData)null,p->p);
        if(part==null)return ItemAttributeModifiers.EMPTY;
        var builder=ItemAttributeModifiers.builder();
        for(var entry:part.getAttributes().entries())builder.add(entry.getKey(),entry.getValue(),EquipmentSlotGroup.bySlot(type.getSlot()));
        return builder.build();
    }
    @Override
    public void createGeoRenderer(Consumer<software.bernie.geckolib.animatable.client.GeoRenderProvider> consumer) {
        consumer.accept(new software.bernie.geckolib.animatable.client.GeoRenderProvider(){
            private GeoArmorRenderer<?> renderer;
            @Override public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T entity,ItemStack stack,EquipmentSlot slot,HumanoidModel<T> original){
                if(renderer==null)renderer=new BedrockArmorRenderer(getSuitIdf());
                renderer.prepForRender(entity,stack,slot,original);return renderer;
            }
        });
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController[]{new AnimationController((GeoAnimatable)this, 20, state -> {
            state.setAnimation(DefaultAnimations.IDLE);
            return PlayState.CONTINUE;
        })});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, Item.TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag tooltipFlag) {
        if (Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            MutableComponent title = Component.translatable((String)"tooltip.lramrmor.suit", (Object[])new Object[]{Component.translatable((String)("suit.lrarmor." + this.suitIdf)), Component.literal((String)String.format("(%d/4)", this.getSuitCount((Player)player, stack)))}).withStyle(ChatFormatting.GRAY);
            list.add((Component)title);
            boolean flag = true;
            LrArmorItem item = (LrArmorItem)stack.getItem();
            ItemStack equipItem = player.getItemBySlot(item.getEquipmentSlot());
            if (!stack.equals(equipItem)) {
                flag = false;
            }
            for (ArmorItem.Type slot : ArmorItem.Type.values()) {
            if(slot==ArmorItem.Type.BODY)continue;
                MutableComponent part = Component.translatable((String)("item.lrarmor." + this.suitIdf + "_" + slot.getName()));
                if (flag && this.isPartEquipped((Player)player, slot)) {
                    part.withStyle(ChatFormatting.GREEN);
                } else {
                    part.withStyle(ChatFormatting.GRAY);
                }
                list.add((Component)part);
            }
        }
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "lrarmor:textures/item/armor/" + this.suitIdf + ".png";
    }

    public void applyEffect(Player player) {
        if (this.suitEffect == null) {
            return;
        }
        MobEffect effect = this.suitEffect.get();
        if (effect != null) {
            player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), 250));
        }
    }

    public boolean isPartEquipped(Player player, ArmorItem.Type slot) {
        ItemStack equipItem = player.getItemBySlot(slot.getSlot());
        Item item = equipItem.getItem();
        if (item instanceof LrArmorItem) {
            LrArmorItem item2 = (LrArmorItem)item;
            return item2.getSuitIdf().equals(this.suitIdf);
        }
        return false;
    }

    public int getSuitCount(Player player, @Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof LrArmorItem)) {
            return 0;
        }
        LrArmorItem item2 = (LrArmorItem)item;
        ItemStack equipItem = player.getItemBySlot(item2.getEquipmentSlot());
        if (!stack.equals(equipItem)) {
            return 0;
        }
        return this.getSuitCount(player);
    }

    public int getSuitCount(Player player) {
        return LrArmorItem.getSuitCount(player, this.suitIdf);
    }

    public static int getSuitCount(Player player, String suitIdf) {
        int cnt = 0;
        for (ArmorItem.Type slot : ArmorItem.Type.values()) {
            if(slot==ArmorItem.Type.BODY)continue;
            LrArmorItem item1;
            ItemStack stack1 = player.getItemBySlot(slot.getSlot());
            Item item = stack1.getItem();
            if (!(item instanceof LrArmorItem) || !(item1 = (LrArmorItem)item).getSuitIdf().equals(suitIdf)) continue;
            ++cnt;
        }
        return cnt;
    }
}
