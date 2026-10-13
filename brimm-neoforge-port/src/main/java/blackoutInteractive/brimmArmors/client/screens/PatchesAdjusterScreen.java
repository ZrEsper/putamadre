package blackoutInteractive.brimmArmors.client.screens;

import blackoutInteractive.brimmArmors.common.items.ArmorPatch;
import blackoutInteractive.brimmArmors.common.items.BrimmArmor;
import blackoutInteractive.brimmArmors.common.registries.ItemRegistry;
import blackoutInteractive.ema_08_.rendering.geom.MatrixRTS;
import blackoutInteractive.ema_08_.rendering.overlay.OverlayPos;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PatchesAdjusterScreen
extends Screen {
    private static final Vector3f lightDir = new Vector3f(0.0f, 0.0f, 1.0f);
    private static final float TRANSLATE_BOUND = 500.0f;
    private final String armorItemName;
    private final OverlayPos op;
    private LivingEntity dummyEntity;
    private ItemStack wornArmor;
    private final Minecraft mc = Minecraft.getInstance();
    private float rotationX = 0.0f;
    private float rotationY = 0.0f;
    private float scale = 1.0f;
    private boolean dragging = false;
    private int draggingButton = -1;
    private float translateX = 0.0f;
    private float translateY = 0.0f;
    private final float[] matrix = new float[9];
    private final EditBox[] matrixFields = new EditBox[9];
    private boolean isMatrixUpToDate = true;
    private ColoredButton updateButton;

    public PatchesAdjusterScreen(String armorItemName, OverlayPos op) {
        super((Component)Component.literal((String)"Patches Adjuster"));
        this.armorItemName = armorItemName;
        this.op = op;
    }

    protected void init() {
        super.init();
        ClientLevel clientWorld = this.mc.level;
        if (clientWorld == null) {
            return;
        }
        this.dummyEntity = new ArmorStand((Level)clientWorld, 0.0, 0.0, 0.0);
        this.dummyEntity.setInvisible(false);
        this.dummyEntity.setNoGravity(true);
        Item item = ItemRegistry.getOrThrow(this.armorItemName);
        if (!(item instanceof BrimmArmor)) {
            return;
        }
        BrimmArmor armorItem = (BrimmArmor)item;
        this.wornArmor = new ItemStack((ItemLike)item);
        armorItem.setPatch(this.wornArmor, (ArmorPatch)ItemRegistry.DEBUG_PATCH.get());
        this.dummyEntity.setItemSlot(armorItem.getEquipmentSlot(), this.wornArmor);
        this.matrix[0] = 1.0f;
        this.matrix[1] = 1.0f;
        this.matrix[2] = 1.0f;
        this.matrix[3] = 0.0f;
        this.matrix[4] = 0.0f;
        this.matrix[5] = 0.0f;
        this.matrix[6] = 0.0f;
        this.matrix[7] = 0.0f;
        this.matrix[8] = 0.0f;
        this.updatePatchMatrix();
        String[] LABELS = new String[]{"ScX:", "ScY:", "ScZ:", "RtX:", "RtY:", "RtZ:", "TrX:", "TrY:", "TrZ:"};
        int leftX = 10;
        int startY = 50;
        int spacing = 25;
        int labelWidth = 30;
        int padding = 5;
        for (int i = 0; i < 9; ++i) {
            int boxY = startY + i * spacing;
            this.addRenderableOnly(new RenderableLabel(LABELS[i], leftX, boxY + 4, 0xFFFFFF));
            EditBox box = new EditBox(this.font, leftX + labelWidth + padding, boxY, 80, 18, (Component)Component.literal((String)LABELS[i]));
            box.setValue(Float.toString(this.matrix[i]));
            int index = i;
            box.setResponder(s -> this.onMatrixFieldChanged(index, (String)s));
            this.matrixFields[i] = box;
            this.addRenderableWidget(box);
        }
        int buttonX = 10;
        int buttonY = startY + 9 * spacing + 10;
        int buttonWidth = 80;
        int buttonHeight = 20;
        this.updateButton = new ColoredButton(buttonX, buttonY, buttonWidth, buttonHeight, (Component)Component.literal((String)"UPDATE"), btn -> {
            this.updatePatchMatrix();
            this.isMatrixUpToDate = true;
        });
        this.addRenderableWidget(this.updateButton);
    }

    private void onMatrixFieldChanged(int index, String value) {
        block2: {
            try {
                this.matrix[index] = Float.parseFloat(value);
                this.isMatrixUpToDate = false;
            }
            catch (NumberFormatException ignored) {
                if (value == null || value.isEmpty() || value.equals(":")) break block2;
                this.mc.player.sendSystemMessage((Component)Component.literal((String)("Invalid value '" + value + "': not a number.")));
            }
        }
    }

    private void updatePatchMatrix() {
        Item item = this.wornArmor.getItem();
        if (!(item instanceof BrimmArmor)) {
            throw new IllegalStateException("Expected BasicArmor, got " + this.wornArmor.getItem().getClass().getSimpleName());
        }
        BrimmArmor armor = (BrimmArmor)item;
        MatrixRTS mat = MatrixRTS.getMatrix(this.matrix[6], this.matrix[7], this.matrix[8], this.matrix[3], this.matrix[4], this.matrix[5], this.matrix[0], this.matrix[1], this.matrix[2], false);
        armor.setPatchesDebugOverride(this.wornArmor, mat, this.op);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0 || button == 1) {
            this.dragging = true;
            this.draggingButton = button;
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (button == this.draggingButton) {
            this.dragging = false;
            this.draggingButton = -1;
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.dragging) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        if (button == 0) {
            this.rotationY += (float)(dragX * 0.125);
            this.rotationX += (float)(dragY * 0.125);
            this.rotationX = Mth.clamp((float)this.rotationX, (float)-90.0f, (float)90.0f);
            return true;
        }
        if (button == 1) {
            this.translateX += (float)dragX;
            this.translateY += (float)dragY;
            double bound = this.translBound();
            this.translateX = (float)Mth.clamp((double)this.translateX, (double)(-bound), (double)bound);
            this.translateY = (float)Mth.clamp((double)this.translateY, (double)(-bound), (double)bound);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalDelta, double delta) {
        float scaleFactor = 0.1f;
        float newScale = this.scale + (float)(delta * (double)scaleFactor);
        this.scale = Mth.clamp((float)newScale, (float)0.5f, (float)20.0f);
        return true;
    }

    public void render(@NotNull GuiGraphics guig, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guig, mouseX, mouseY, partialTicks);
        guig.fill(0, 0, this.width, this.height, -1072689136);
        guig.drawString(this.font, "Armor: " + this.armorItemName + "; Overlay Pos: " + this.op.name(), 10, 15, 0xFFFFFF);
        guig.pose().pushPose();
        guig.pose().translate(this.translateX, this.translateY, 0.0f);
        int centerX = this.width / 2;
        int centerY = this.height / 2 + 30;
        PatchesAdjusterScreen.renderEntityOnScreen(guig.pose(), centerX, centerY, this.scale, this.rotationX, this.rotationY, this.dummyEntity);
        guig.pose().popPose();
        super.render(guig, mouseX, mouseY, partialTicks);
    }

    private double translBound() {
        return (double)(500.0f * this.scale) * 0.5;
    }

    private static void renderEntityOnScreen(PoseStack poseStack, int posX, int posY, float scale, float rotX, float rotY, LivingEntity entity) {
        poseStack.pushPose();
        poseStack.translate((float)posX, (float)posY, 1050.0f);
        poseStack.scale(50.0f, -50.0f, 50.0f);
        poseStack.scale(scale, scale, scale);
        Quaternionf rotation = Axis.YP.rotationDegrees(rotY);
        poseStack.mulPose(rotation);
        rotation = Axis.XP.rotationDegrees(rotX);
        poseStack.mulPose(rotation);
        RenderSystem.setShaderLights((Vector3f)lightDir, (Vector3f)lightDir);
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        EntityRenderDispatcher renderer = Minecraft.getInstance().getEntityRenderDispatcher();
        renderer.render((Entity)entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, poseStack, (MultiBufferSource)buffer, 0xF000F0);
        buffer.endBatch();
        poseStack.popPose();
    }

    private static class RenderableLabel
    implements Renderable {
        private final Minecraft mc = Minecraft.getInstance();
        private final String text;
        private final int x;
        private final int y;
        private final int color;

        public RenderableLabel(String text, int x, int y, int color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public void render(GuiGraphics g, int mouseX, int mouseY, float partialTicks) {
            g.drawString(this.mc.font, this.text, this.x, this.y, this.color);
        }
    }

    private class ColoredButton
    extends Button {
        private final int x;
        private final int y;

        public ColoredButton(int x, int y, int width, int height, Component title, Button.OnPress onPress) {
            super(x, y, width, height, title, onPress, Button.DEFAULT_NARRATION);
            this.x = x;
            this.y = y;
        }

        public void renderWidget(GuiGraphics guig, int mouseX, int mouseY, float partialTicks) {
            super.renderWidget(guig, mouseX, mouseY, partialTicks);
            int color = PatchesAdjusterScreen.this.isMatrixUpToDate ? -16711936 : -65536;
            guig.drawCenteredString(PatchesAdjusterScreen.this.font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }
    }
}

