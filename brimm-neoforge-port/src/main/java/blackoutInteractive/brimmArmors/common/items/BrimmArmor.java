package blackoutInteractive.brimmArmors.common.items;

import blackoutInteractive.brimmArmors.BrimmArmors;
import blackoutInteractive.brimmArmors.client.render.BrimmArmorRender;
import blackoutInteractive.brimmArmors.common.items.ArmorPatch;
import blackoutInteractive.brimmArmors.common.items.BrimmRarity;
import blackoutInteractive.brimmArmors.common.registries.ItemRegistry;
import blackoutInteractive.ema_08_.items.SimpleArmorMaterial;
import blackoutInteractive.ema_08_.items.effectsProvidingArmors.v2.ArmorEffectsSetup;
import blackoutInteractive.ema_08_.items.effectsProvidingArmors.v2.IEffectsProvider;
import blackoutInteractive.ema_08_.rendering.geom.MatrixRTS;
import blackoutInteractive.ema_08_.rendering.obj.providers.IMultiObjModelsProvider;
import blackoutInteractive.ema_08_.rendering.overlay.OverlayLocation;
import blackoutInteractive.ema_08_.rendering.overlay.OverlayPos;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

public class BrimmArmor
extends ArmorItem
implements IMultiObjModelsProvider,
IEffectsProvider {
    private static final ConcurrentHashMap<String, ArmorPatch> patch_cache = new ConcurrentHashMap();
    private static final Function<String, ArmorPatch> cache_computator = key -> {
        Optional<Item> result = ItemRegistry.get(key);
        if (result.isEmpty()) {
            return null;
        }
        Item item = result.get();
        if (!(item instanceof ArmorPatch)) {
            throw new IllegalArgumentException(key + " is the name of a " + item.getClass().getName() + ", not of an armor patch");
        }
        return (ArmorPatch)item;
    };
    public final String unlocName;
    private final BrimmRarity rarity;
    private final int[] models;
    private final Collection<OverlayLocation> patchesPositions;
    private final ArmorEffectsSetup effects;

    public BrimmArmor(String unlocName, ArmorItem.Type type, BrimmRarity rarity, SimpleArmorMaterial material, int[] models, Collection<OverlayLocation> patchesPositions, ArmorEffectsSetup effects) {
        super(material.holder(), type, new Item.Properties().durability(material.durabilityValue()));
        this.rarity = rarity;
        this.unlocName = unlocName;
        this.models = models;
        this.patchesPositions = Collections.unmodifiableCollection(patchesPositions);
        this.effects = effects == null ? ArmorEffectsSetup.EMPTY : effects;
    }

    @NotNull
    public Component getName(@NotNull ItemStack stack) {
        return Component.literal((String)this.rarity.applyFormatting(super.getName(stack).getString()));
    }

    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext world, @NotNull List<Component> tooltipList, @NotNull TooltipFlag flag) {
        String tooltipRaw = I18n.get((String)("tooltip.brimm." + this.unlocName), (Object[])new Object[0]);
        if (!tooltipRaw.isBlank()) {
            tooltipList.add((Component)Component.literal((String)this.rarity.applyFormatting("\u00a7o" + tooltipRaw)));
        }
        if (this.patchesPositions.isEmpty()) {
            tooltipList.add((Component)Component.translatable((String)"tooltip.brimm.armors.no_patch_supported").withStyle(ChatFormatting.RED));
        } else {
            ArmorPatch patch = this.getPatch(stack);
            if (patch != null) {
                tooltipList.add((Component)Component.translatable((String)"tooltip.brimm.armors.current_patch").append(": ").append((Component)Component.translatable((String)("item.brimm." + patch.getPatchName()))));
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void registerClientExtension(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions(){

            @NotNull
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                ModelPart root = new ModelPart(new ArrayList(), Map.of("head", defaultModel.head, "hat", defaultModel.hat, "body", defaultModel.body, "right_arm", defaultModel.rightArm, "left_arm", defaultModel.leftArm, "right_leg", defaultModel.rightLeg, "left_leg", defaultModel.leftLeg));
                return new BrimmArmorRender(root, stack);
            }
        });
    }

    @Override
    public int[] getModelIds() {
        return this.models;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    public Collection<OverlayLocation> patchesPositions(ItemStack is) {
        if (is.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA) && PortTags.read(is).contains("debug-patchPosOverride") && PortTags.read(is).contains("debug-overlayPosOverride")) {
            int[] matrixSer = PortTags.read(is).getIntArray("debug-patchPosOverride");
            OverlayPos pos = OverlayPos.values()[PortTags.read(is).getInt("debug-overlayPosOverride")];
            return List.of(new OverlayLocation(pos, MatrixRTS.getMatrix(Float.intBitsToFloat(matrixSer[0]), Float.intBitsToFloat(matrixSer[1]), Float.intBitsToFloat(matrixSer[2]), Float.intBitsToFloat(matrixSer[3]), Float.intBitsToFloat(matrixSer[4]), Float.intBitsToFloat(matrixSer[5]), Float.intBitsToFloat(matrixSer[6]), Float.intBitsToFloat(matrixSer[7]), Float.intBitsToFloat(matrixSer[8]), false)));
        }
        return this.patchesPositions;
    }

    public void setPatch(ItemStack is, ArmorPatch patch) {
        blackoutInteractive.brimmArmors.common.items.PortTags.update(is, tag -> tag.putString("patch", patch.getPatchName()));
    }

    public void removePatch(ItemStack is) {
        blackoutInteractive.brimmArmors.common.items.PortTags.update(is, tag -> tag.remove("patch"));
    }

    public ArmorPatch getPatch(ItemStack is) {
        CompoundTag tag = PortTags.read(is);
        if (!tag.contains("patch")) {
            return null;
        }
        ArmorPatch patch = patch_cache.computeIfAbsent(tag.getString("patch"), cache_computator);
        if (patch == null) {
            BrimmArmors.LOGGER.warn("Could not retrieve currently set patch named {} for {}, resetting patch tag.", (Object)tag.getString("patch"), (Object)BuiltInRegistries.ITEM.getKey(is.getItem()));
            this.removePatch(is);
            return null;
        }
        return patch;
    }

    public void setPatchesDebugOverride(ItemStack is, MatrixRTS matrix, OverlayPos pos) {
        int[] matrixSer = new int[]{Float.floatToIntBits(matrix.trX), Float.floatToIntBits(matrix.trY), Float.floatToIntBits(matrix.trZ), Float.floatToIntBits(matrix.rtX), Float.floatToIntBits(matrix.rtY), Float.floatToIntBits(matrix.rtZ), Float.floatToIntBits(matrix.scX), Float.floatToIntBits(matrix.scY), Float.floatToIntBits(matrix.scZ)};
        blackoutInteractive.brimmArmors.common.items.PortTags.update(is, tag -> tag.putIntArray("debug-patchPosOverride", matrixSer));
        blackoutInteractive.brimmArmors.common.items.PortTags.update(is, tag -> tag.putInt("debug-overlayPosOverride", pos.ordinal()));
    }

    public void removePatchesDebugOverride(ItemStack is) {
        blackoutInteractive.brimmArmors.common.items.PortTags.update(is, tag -> tag.remove("debug-patchPosOverride"));
        blackoutInteractive.brimmArmors.common.items.PortTags.update(is, tag -> tag.remove("debug-overlayPosOverride"));
    }

    @Override
    public ArmorEffectsSetup getEffectsSetup() {
        return this.effects;
    }
}

