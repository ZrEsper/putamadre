package dev.zomboid.survival;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;

/** An industrial generator control panel; no chest textures or external assets. */
public final class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {
    public GeneratorScreen(GeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 342;
        imageHeight = 222;
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
    @Override protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        boolean running = menu.burn() > 0 && menu.energy() <= 63920;
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xff101719);
        g.fill(x + 2, y + 2, x + imageWidth - 2, y + imageHeight - 2, 0xff2d373b);
        g.fill(x + 5, y + 5, x + 167, y + 126, 0xff182225);
        g.fill(x + 170, y + 5, x + 337, y + 126, 0xff202a2e);
        // Generator chassis, exhaust, cooling vents, feet and operating light.
        g.fill(x + 36, y + 34, x + 132, y + 87, 0xff0a1012);
        g.fill(x + 39, y + 37, x + 129, y + 84, 0xff59676d);
        g.fill(x + 48, y + 27, x + 56, y + 37, 0xff77848a);
        g.fill(x + 48, y + 24, x + 68, y + 29, 0xff77848a);
        g.fill(x + 43, y + 87, x + 57, y + 91, 0xff080c0d);
        g.fill(x + 111, y + 87, x + 125, y + 91, 0xff080c0d);
        for (int i = 0; i < 4; i++) g.fill(x + 46, y + 44 + i * 8, x + 63, y + 47 + i * 8, 0xff1b262a);
        g.fill(x + 112, y + 44, x + 120, y + 52, running ? 0xff70e39a : 0xffa7a27e);
        for (Slot slot : menu.slots) {
            g.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xff071013);
            g.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xff35454c);
        }
        gauge(g, x + 12, y + 104, 143, menu.energy(), 64000, 0xff53d2cf);
        gauge(g, x + 12, y + 118, 143, menu.burn(), menu.fuelTotal(), 0xffe3b35a);
        // Hazard stripes distinguish the machinery panel from an inventory chest.
        for (int i = 0; i < 20; i++) g.fill(x + 176 + i * 8, y + 131, x + 180 + i * 8, y + 134, 0xffb89a43);
    }
    private void gauge(GuiGraphics g, int x, int y, int width, int value, int max, int color) {
        g.fill(x, y, x + width, y + 4, 0xff070e10);
        int fill = (int) Math.min(width, (long) width * Math.max(0, value) / Math.max(1, max));
        g.fill(x, y, x + fill, y + 4, color);
    }
    @Override protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, "GENERADOR", 12, 10, 0xfff0d68b, false);
        g.drawString(font, "RESERVA DE COMBUSTIBLE", 175, 15, 0xffc2d0d4, false);
        g.drawString(font, "Combustible", 74, 52, 0xfff2e4c6, false);
        g.drawString(font, "Energia: " + menu.energy() + " / 64000 FE", 12, 94, 0xff84d9d5, false);
        g.drawString(font, "Carga en combustion", 12, 110, 0xffdfc18c, false);
        g.drawString(font, playerInventoryTitle, 8, 129, 0xffc2d0d4, false);
        String status = menu.energy() > 63920 ? "EN ESPERA: deposito lleno" : menu.burn() > 0 ? "EN MARCHA" : "SIN COMBUSTIBLE";
        g.drawString(font, status, 179, 142, menu.burn() > 0 ? 0xff78e4a2 : 0xffe5b572, false);
        g.drawString(font, "Salida: 80 FE/t", 179, 156, 0xffb7ccd0, false);
        g.drawString(font, "Alcance: 25 bloques", 179, 170, 0xffb7ccd0, false);
        long seconds = menu.remainingTicks() / 20;
        g.drawString(font, "Autonomia: " + seconds / 60 + "m " + seconds % 60 + "s", 179, 184, 0xffdfc18c, false);
        g.drawString(font, "64 carbon = 30 min activos", 179, 198, 0xffb7ccd0, false);
    }
}
