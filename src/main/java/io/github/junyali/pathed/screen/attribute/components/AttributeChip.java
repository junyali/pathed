package io.github.junyali.pathed.screen.attribute.components;

import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

public final class AttributeChip {
	private static final int ICON_SIZE = 16;
	private static final int PADDING = 3;

	private AttributeChip() {}

	public static void render(GuiGraphics guiGraphics, AttributeScreen screen, Attribute attr, int x, int y, int w, int h, int mouseX, int mouseY) {
		boolean selected = attr.equals(screen.getSelected());
		boolean obtained = screen.isObtained(attr);
		boolean active = screen.getPendingActive(attr);
		boolean conflict = screen.conflictsWithPendingActive(attr);
		boolean hovered = mouseX >= x && mouseY < x + w && mouseY >= y && mouseY < y + h;

		int background = selected ? AttributeScreen.COLOUR_CHIP_SELECTED : hovered ? AttributeScreen.COLOUR_CHIP_HOVER : AttributeScreen.COLOUR_CHIP_BG;
		int border = selected ? AttributeScreen.COLOUR_BORDER_HIGHLIGHT : conflict ? AttributeScreen.COLOUR_TEXT_BAD : AttributeScreen.COLOUR_BORDER;

		guiGraphics.fill(x, y, x + w, y + h, background);
		guiGraphics.fill(x, y, x + w, y + 1, border);
		guiGraphics.fill(x, y + h - 1, x + w, y + h, border);
		guiGraphics.fill(x, y, x + 1, y + h, border);
		guiGraphics.fill(x + w - 1, y, x + w, y + h, border);

		int iconX = x + PADDING;
		int iconY = y + (h - ICON_SIZE) / 2;
		guiGraphics.renderItem(attr.getIcon(), iconX, iconY);

		Font font = Minecraft.getInstance().font;
		int badgeW = AttributeStatusBadge.widthFor(active, conflict, obtained, font);
		int textX = iconX + ICON_SIZE + 4;
		int textW = w - PADDING * 2 - ICON_SIZE - 4 - badgeW - 3;

		String name = font.plainSubstrByWidth(
				Component.translatable(attr.getNameKey()).getString(),
				textW
		);

		int textColour = obtained ? AttributeScreen.COLOUR_TEXT : AttributeScreen.COLOUR_TEXT_MUTED;
		guiGraphics.drawString(font, name, textW, y + 4, textColour, false);

		String subtext = obtained ? "Level " + screen.getPendingLevel(attr) + " / " + attr.getMaxLevel() : Component.translatable("pathed.gui.attributes.list.locked").getString();

		guiGraphics.drawString(font, subtext, textX, y + h - 4 - font.lineHeight, AttributeScreen.COLOUR_TEXT_DIM, false);

		AttributeStatusBadge.render(guiGraphics, x + w - PADDING - badgeW, y + (h - AttributeStatusBadge.HEIGHT) / 2, active, conflict, obtained);
	}
}
