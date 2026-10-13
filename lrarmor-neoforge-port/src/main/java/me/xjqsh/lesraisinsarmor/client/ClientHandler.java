package me.xjqsh.lesraisinsarmor.client;

import com.google.common.collect.ImmutableList;
import me.xjqsh.lesraisinsarmor.client.resource.ArmorRenderConfigManager;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorPartRenderConfig;
import me.xjqsh.lesraisinsarmor.client.resource.data.ArmorRenderConfig;
import me.xjqsh.lesraisinsarmor.client.resource.data.PlayerModelPart;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@OnlyIn(value=Dist.CLIENT)
@EventBusSubscriber(modid="lrarmor", value={Dist.CLIENT})
public class ClientHandler {
    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity living = event.getEntity();
        EntityModel model = event.getRenderer().getModel();
        if (model instanceof PlayerModel) {
            PlayerModel playerModel = (PlayerModel)model;
            ClientHandler.resolveVisibleParts(living, EquipmentSlot.HEAD, playerModel);
            ClientHandler.resolveVisibleParts(living, EquipmentSlot.CHEST, playerModel);
            ClientHandler.resolveVisibleParts(living, EquipmentSlot.LEGS, playerModel);
            ClientHandler.resolveVisibleParts(living, EquipmentSlot.FEET, playerModel);
        } else if (model instanceof HumanoidModel) {
            HumanoidModel humanoidModel = (HumanoidModel)model;
            ClientHandler.resolveVisibleParts(living, EquipmentSlot.HEAD, humanoidModel);
            ClientHandler.resolveVisibleParts(living, EquipmentSlot.CHEST, humanoidModel);
            ClientHandler.resolveVisibleParts(living, EquipmentSlot.LEGS, humanoidModel);
            ClientHandler.resolveVisibleParts(living, EquipmentSlot.FEET, humanoidModel);
        }
    }

    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Post<?, ?> event) {
        EntityModel model = event.getRenderer().getModel();
        if (model instanceof PlayerModel) {
            PlayerModel playerModel = (PlayerModel)model;
            ClientHandler.playerBodyParts(playerModel).forEach(part -> {
                if (part != null) {
                    part.visible = true;
                }
            });
        } else if (model instanceof HumanoidModel) {
            HumanoidModel humanoidModel = (HumanoidModel)model;
            ClientHandler.bodyParts(humanoidModel).forEach(part -> {
                if (part != null) {
                    part.visible = true;
                }
            });
        }
    }

    private static Iterable<ModelPart> playerBodyParts(PlayerModel<?> playerModel) {
        return ImmutableList.of(playerModel.body, playerModel.rightArm, playerModel.leftArm, playerModel.rightLeg, playerModel.leftLeg, playerModel.head, playerModel.hat, playerModel.jacket, playerModel.rightSleeve, playerModel.leftSleeve, playerModel.rightPants, playerModel.leftPants);
    }

    private static Iterable<ModelPart> bodyParts(HumanoidModel<?> humanoidModel) {
        return ImmutableList.of(humanoidModel.body, humanoidModel.rightArm, humanoidModel.leftArm, humanoidModel.rightLeg, humanoidModel.leftLeg, humanoidModel.head, humanoidModel.hat);
    }

    public static void resolveVisibleParts(LivingEntity living, EquipmentSlot slot, HumanoidModel<?> model) {
        Item item = living.getItemBySlot(slot).getItem();
        if (item instanceof LrArmorItem) {
            LrArmorItem lrArmor = (LrArmorItem)item;
            ArmorRenderConfig config = ClientHandler.getConfig(lrArmor);
            if (config == null) {
                return;
            }
            switch (slot) {
                case HEAD: {
                    ClientHandler.setVisibility(config.helmet, model);
                    break;
                }
                case CHEST: {
                    ClientHandler.setVisibility(config.chestplate, model);
                    break;
                }
                case LEGS: {
                    ClientHandler.setVisibility(config.leggings, model);
                    break;
                }
                case FEET: {
                    ClientHandler.setVisibility(config.boots, model);
                }
            }
        }
    }

    private static void setVisibility(ArmorPartRenderConfig config, HumanoidModel<?> model) {
        if (config == null) {
            return;
        }
        for (PlayerModelPart part : config.hideParts) {
            switch (part) {
                case HEAD: {
                    model.head.visible = false;
                    break;
                }
                case HAT: {
                    model.hat.visible = false;
                    break;
                }
                case BODY: {
                    model.body.visible = false;
                    break;
                }
                case RIGHT_ARM: {
                    model.rightArm.visible = false;
                    break;
                }
                case LEFT_ARM: {
                    model.leftArm.visible = false;
                    break;
                }
                case RIGHT_LEG: {
                    model.rightLeg.visible = false;
                    break;
                }
                case LEFT_LEG: {
                    model.leftLeg.visible = false;
                }
            }
        }
    }

    public static void resolveVisibleParts(LivingEntity player, EquipmentSlot slot, PlayerModel<?> model) {
        Item item = player.getItemBySlot(slot).getItem();
        if (item instanceof LrArmorItem) {
            LrArmorItem lrArmor = (LrArmorItem)item;
            ArmorRenderConfig config = ClientHandler.getConfig(lrArmor);
            if (config == null) {
                return;
            }
            switch (slot) {
                case HEAD: {
                    ClientHandler.setVisibility(config.helmet, model);
                    break;
                }
                case CHEST: {
                    ClientHandler.setVisibility(config.chestplate, model);
                    break;
                }
                case LEGS: {
                    ClientHandler.setVisibility(config.leggings, model);
                    break;
                }
                case FEET: {
                    ClientHandler.setVisibility(config.boots, model);
                }
            }
        }
    }

    private static void setVisibility(ArmorPartRenderConfig config, PlayerModel<?> model) {
        if (config == null) {
            return;
        }
        for (PlayerModelPart part : config.hideParts) {
            switch (part) {
                case HEAD: {
                    model.head.visible = false;
                    break;
                }
                case HAT: {
                    model.hat.visible = false;
                    break;
                }
                case BODY: {
                    model.body.visible = false;
                    break;
                }
                case JACKET: {
                    model.jacket.visible = false;
                    break;
                }
                case RIGHT_ARM: {
                    model.rightArm.visible = false;
                    break;
                }
                case RIGHT_SLEEVE: {
                    model.rightSleeve.visible = false;
                    break;
                }
                case LEFT_ARM: {
                    model.leftArm.visible = false;
                    break;
                }
                case LEFT_SLEEVE: {
                    model.leftSleeve.visible = false;
                    break;
                }
                case RIGHT_LEG: {
                    model.rightLeg.visible = false;
                    break;
                }
                case RIGHT_PANTS: {
                    model.rightPants.visible = false;
                    break;
                }
                case LEFT_LEG: {
                    model.leftLeg.visible = false;
                    break;
                }
                case LEFT_PANTS: {
                    model.leftPants.visible = false;
                }
            }
        }
    }

    private static ArmorRenderConfig getConfig(LrArmorItem lrArmor) {
        return ArmorRenderConfigManager.getInstance().getConfig(ResourceLocation.tryBuild((String)"lrarmor", (String)lrArmor.getSuitIdf()));
    }
}
