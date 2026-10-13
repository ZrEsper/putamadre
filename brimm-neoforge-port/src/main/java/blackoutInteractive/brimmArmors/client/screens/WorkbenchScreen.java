package blackoutInteractive.brimmArmors.client.screens;

import blackoutInteractive.brimmArmors.BrimmArmors;
import blackoutInteractive.brimmArmors.common.packets.CraftPacket;
import blackoutInteractive.brimmArmors.common.workbench.Craft;
import blackoutInteractive.brimmArmors.common.workbench.CraftsManager;
import blackoutInteractive.brimmArmors.common.workbench.Ingredient;
import blackoutInteractive.ema_08_.rendering.obj.ObjModelsManager;
import blackoutInteractive.ema_08_.rendering.obj.metadata.ArmorModelMetadata;
import blackoutInteractive.ema_08_.rendering.obj.modelsHolders.LoadedModel;
import blackoutInteractive.ema_08_.rendering.obj.providers.IMultiObjModelsProvider;
import blackoutInteractive.ema_08_.rendering.obj.providers.IObjModelProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.joml.Vector3f;

public class WorkbenchScreen
extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath("brimm", "textures/gui/workbench_bg.png");
    private static final int BG_XSIZE = 192;
    private static final int BG_YSIZE = 129;
    private static final float ITEMICONS_SCALE = 4.0f;
    private static final Vector3f lightDir = new Vector3f(0.0f, 0.0f, 1.0f);
    private final Minecraft mc = Minecraft.getInstance();
    private final List<Component> tooltip = List.of(Component.literal((String)I18n.get((String)"screen.brimm.workbench.tooltip", (Object[])new Object[0])));
    private float rotationX = 0.0f;
    private float rotationY = 0.0f;
    private CraftsManager.CraftsSectionAccessor section = CraftsManager.accessor(CraftsManager.firstSection());
    private String localisedSectionName;
    private Craft currentReceipe = this.section.first();
    private boolean dragging = false;

    public WorkbenchScreen() {
        super((Component)Component.literal((String)I18n.get((String)"screen.brimm.workbench.title", (Object[])new Object[0])));
        this.localisedSectionName = "\u00a7l" + this.section.section().localisedName();
    }

    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int bottomY = (this.height + 129) / 2 + 10;
        this.addRenderableWidget(Button.builder((Component)Component.literal((String)(String.valueOf(ChatFormatting.BOLD) + I18n.get((String)"screen.brimm.workbench.craft", (Object[])new Object[0]))), button -> {
            BrimmArmors.network.sendToServer(new CraftPacket(this.currentReceipe.id()));
            this.mc.player.level().playSound(null, this.mc.player.blockPosition(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }).pos(centerX - 30, bottomY).size(60, 20).build());
        this.addRenderableWidget(Button.builder((Component)Component.literal((String)(String.valueOf(ChatFormatting.BOLD) + "<")), button -> {
            this.currentReceipe = Optional.ofNullable(this.section.prev(this.currentReceipe)).orElse(this.section.last());
            this.rotationX = 0.0f;
            this.rotationY = 0.0f;
            this.mc.player.level().playSound(null, this.mc.player.blockPosition(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }).pos(centerX - 70, bottomY).size(20, 20).build());
        this.addRenderableWidget(Button.builder((Component)Component.literal((String)(String.valueOf(ChatFormatting.BOLD) + ">")), button -> {
            this.currentReceipe = Optional.ofNullable(this.section.next(this.currentReceipe)).orElse(this.section.first());
            this.rotationX = 0.0f;
            this.rotationY = 0.0f;
            this.mc.player.level().playSound(null, this.mc.player.blockPosition(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }).pos(centerX + 50, bottomY).size(20, 20).build());
        this.addRenderableWidget(Button.builder((Component)Component.literal((String)(String.valueOf(ChatFormatting.BOLD) + "<<<")), button -> {
            this.section = CraftsManager.accessor(Optional.ofNullable(CraftsManager.prevSection(this.section.section())).orElse(CraftsManager.lastSection()));
            this.currentReceipe = this.section.first();
            this.localisedSectionName = "\u00a7l" + this.section.section().localisedName();
            this.rotationX = 0.0f;
            this.rotationY = 0.0f;
            this.mc.player.level().playSound(null, this.mc.player.blockPosition(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }).pos(centerX - 70 - 20, bottomY + 22).size(20, 20).build());
        this.addRenderableWidget(Button.builder((Component)Component.literal((String)(String.valueOf(ChatFormatting.BOLD) + ">>>")), button -> {
            this.section = CraftsManager.accessor(Optional.ofNullable(CraftsManager.nextSection(this.section.section())).orElse(CraftsManager.firstSection()));
            this.currentReceipe = this.section.first();
            this.localisedSectionName = "\u00a7l" + this.section.section().localisedName();
            this.rotationX = 0.0f;
            this.rotationY = 0.0f;
            this.mc.player.level().playSound(null, this.mc.player.blockPosition(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }).pos(centerX + 50 + 20, bottomY + 22).size(20, 20).build());
    }

    public void render(GuiGraphics guig, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guig, mouseX, mouseY, partialTicks);
        super.render(guig, mouseX, mouseY, partialTicks);
        int guiLeft = (this.width - 192) / 2;
        int guiTop = (this.height - 129) / 2;
        int darkColor = -1072689136;
        guig.fill(0, 0, this.width, this.height, darkColor);
        guig.blit(BACKGROUND_TEXTURE, guiLeft, guiTop, 0.0f, 0.0f, 192, 129, 192, 129);
        this.renderItem(guig, guig.pose(), guiLeft + 96, guiTop + 64);
        int sectionX = guiLeft + 96 - this.mc.font.width(this.localisedSectionName) / 2;
        int sectionY = (this.height + 129) / 2 + 10 + 30;
        guig.drawString(this.mc.font, this.localisedSectionName, sectionX, sectionY, 0x555555, false);
        String itemName = this.currentReceipe.result().getName(new ItemStack((ItemLike)this.currentReceipe.result())).getString();
        int nameX = guiLeft + 96 - this.mc.font.width(itemName) / 2;
        Objects.requireNonNull(this.mc.font);
        int nameY = guiTop - 9 - 2;
        guig.drawString(this.mc.font, itemName, nameX, nameY, 0xFFFFFF, false);
        int ingX = guiLeft + 192 + 10;
        int ingY = guiTop;
        for (Ingredient ing : this.currentReceipe.ingredients()) {
            int count = this.mc.player.getInventory().countItem(ing.type());
            ChatFormatting materialColor = count >= ing.amt() ? ChatFormatting.GREEN : (count > 0 ? ChatFormatting.YELLOW : ChatFormatting.RED);
            MutableComponent ingText = Component.literal((String)"- ").append((Component)Component.literal((String)(ing.amt() + "x ")).withStyle(materialColor)).append(ing.type().getName(new ItemStack((ItemLike)ing.type())));
            guig.drawString(this.mc.font, (Component)ingText, ingX, ingY, 0xAAAAAA, false);
            Objects.requireNonNull(this.mc.font);
            ingY += 9 + 2;
        }
        if (!this.dragging && (this.currentReceipe.result() instanceof IObjModelProvider || this.currentReceipe.result() instanceof IMultiObjModelsProvider) && mouseX >= guiLeft && mouseX <= guiLeft + 192 && mouseY >= guiTop && mouseY <= guiTop + 129) {
            guig.renderTooltip(this.mc.font, this.tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private void setupObjRendering(PoseStack poseStack, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderLights((Vector3f)lightDir, (Vector3f)lightDir);
        poseStack.translate((float)x, (float)y, 150.0f);
        poseStack.mulPose(Axis.YP.rotation(this.rotationY));
        poseStack.mulPose(Axis.XP.rotation(this.rotationX));
    }

    private void renderItem(GuiGraphics guig, PoseStack poseStack, int x, int y) {
        poseStack.pushPose();
        Item current = this.currentReceipe.result();
        if (current instanceof IObjModelProvider) {
            IObjModelProvider modelRefP = (IObjModelProvider)current;
            this.setupObjRendering(poseStack, x, y);
            LoadedModel model = ObjModelsManager.get(modelRefP.getModelId());
            ((ArmorModelMetadata)model.getMetadata()).workbench_transform().apply(poseStack);
            model.render(poseStack, (MultiBufferSource)Minecraft.getInstance().renderBuffers().bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY, this.mc.getTimer().getGameTimeDeltaPartialTick(false));
        } else if (current instanceof IMultiObjModelsProvider) {
            int[] modelIds;
            IMultiObjModelsProvider modelRefsP = (IMultiObjModelsProvider)current;
            this.setupObjRendering(poseStack, x, y);
            for (int modelId : modelIds = modelRefsP.getModelIds()) {
                poseStack.pushPose();
                LoadedModel model = ObjModelsManager.get(modelId);
                ((ArmorModelMetadata)model.getMetadata()).workbench_transform().apply(poseStack);
                model.render(poseStack, (MultiBufferSource)Minecraft.getInstance().renderBuffers().bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY, this.mc.getTimer().getGameTimeDeltaPartialTick(false));
                poseStack.popPose();
            }
        } else {
            ItemStack itemStack = new ItemStack((ItemLike)current);
            poseStack.translate((float)x, (float)y, 100.0f);
            poseStack.scale(4.0f, 4.0f, 1.0f);
            guig.renderItem(itemStack, -8, -8);
        }
        poseStack.popPose();
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (this.dragging) {
            this.rotationY += (float)(dx * (double)0.03f);
            this.rotationX += (float)(dy * (double)0.03f);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        this.dragging = true;
        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        this.dragging = false;
        this.rotationX = 0.0f;
        this.rotationY = 0.0f;
        return true;
    }

    public void tick() {
        super.tick();
        if (!this.dragging) {
            this.rotationY += 0.04f;
        }
    }
}

