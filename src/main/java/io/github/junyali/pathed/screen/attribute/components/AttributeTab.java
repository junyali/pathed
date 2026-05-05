package io.github.junyali.pathed.screen.attribute.components;

import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class AttributeTab {
	private AttributeTab() {}

	public static void render(GuiGraphics guiGraphics, int x, int y, int w, int h, Component label, boolean active, int mouseX, int mouseY) {
		boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
		int background = active ? AttributeScreen.COLOUR_PANEL_BG : hovered ? AttributeScreen.COLOUR_TAB_HOVERED : AttributeScreen.COLOUR_TAB_BG;
		int border = active ? AttributeScreen.COLOUR_TEXT_HIGHLIGHT : AttributeScreen.COLOUR_BORDER;
		int text = active ? AttributeScreen.COLOUR_TEXT_HIGHLIGHT : AttributeScreen.COLOUR_TEXT;

		guiGraphics.fill(x, y, x + w, y + h, background);
		guiGraphics.fill(x, y, x+ w, y + 1, border);
		guiGraphics.fill(x, y, x + 1, y +h, border);
		guiGraphics.fill(x + w - 1, y, x + w, y + h, border);
		if (!active) guiGraphics.fill(x, y + h - 1, x + w, y + h, border);

		Font font = Minecraft.getInstance().font;
		guiGraphics.drawString(font, label, x + (w - font.width(label)) / 2, y + (h - font.lineHeight) / 2, text, false);
	}
}
