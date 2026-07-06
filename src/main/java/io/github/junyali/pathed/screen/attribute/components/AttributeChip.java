package io.github.junyali.pathed.screen.attribute.components;

import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class AttributeChip {
	private static final int ICON_SIZE = 16;
	private static final int PADDING = 3;
	private static final float STATUS_SCALE = 0.85f;

	private static final int[] ROWAN_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	private static final String[] ROWAN_NUMERALS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

	private AttributeChip() {}

	public static void render(GuiGraphics guiGraphics, AttributeScreen screen, Attribute attr, int x, int y, int w, int h, int mouseX, int mouseY) {
		boolean selected = attr.equals(screen.getSelected());
		boolean obtained = screen.isObtained(attr);
		boolean active = screen.getPendingActive(attr);
		boolean conflict = screen.conflictsWithPendingActive(attr);
		boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;

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
		int textX = iconX + ICON_SIZE + 4;
		int contentRight = x + w - PADDING;

		int currentLevel = screen.getPendingLevel(attr);
		boolean showLevelBox = attr.getMaxLevel() > 1 && currentLevel >= 1;
		String roman = showLevelBox ? toRoman(currentLevel) : "";
		int boxH = font.lineHeight + 1;
		int boxW = showLevelBox ? Math.max(boxH, font.width(roman) + 4) : 0;
		int gapBeforeBox = showLevelBox ? 4 : 0;

		int nameMaxW = contentRight - textX - gapBeforeBox - boxW;
		String name = font.plainSubstrByWidth(Component.translatable(attr.getNameKey()).getString(), nameMaxW);

		int textColour = obtained ? AttributeScreen.COLOUR_TEXT : AttributeScreen.COLOUR_TEXT_MUTED;
		guiGraphics.drawString(font, name, textX, y + 3, textColour, false);

		if (showLevelBox) {
			int boxX = textX + font.width(name) + gapBeforeBox;
			int boxY = y + 2;
			guiGraphics.fill(boxX, boxY, boxX + boxW, boxY + boxH, AttributeScreen.COLOUR_SLOT_BG);
			guiGraphics.fill(boxX, boxY, boxX + boxW, boxY + 1, AttributeScreen.COLOUR_BORDER);
			guiGraphics.fill(boxX, boxY + boxH - 1, boxX + boxW, boxY + boxH, AttributeScreen.COLOUR_BORDER);
			guiGraphics.fill(boxX, boxY, boxX + 1, boxY + boxH, AttributeScreen.COLOUR_BORDER);
			guiGraphics.fill(boxX + boxW - 1, boxY, boxX + boxW, boxY + boxH, AttributeScreen.COLOUR_BORDER);
			guiGraphics.drawString(font, roman, boxX + (boxW - font.width(roman)) / 2, boxY + 1, AttributeScreen.COLOUR_TEXT_HIGHLIGHT, false);
		}

		String status;
		int statusColour;
		if (!obtained) {
			status = Component.translatable("pathed.gui.attributes.list.locked").getString();
			statusColour = AttributeScreen.COLOUR_TEXT_DIM;
		} else if (active) {
			status = Component.translatable("pathed.gui.attributes.list.active").getString();
			statusColour = AttributeScreen.COLOUR_TEXT_GOOD;
		} else {
			status = Component.translatable("pathed.gui.attributes.list.inactive").getString();
			statusColour = AttributeScreen.COLOUR_TEXT_BAD;
		}

		int statusY = y + 3 + font.lineHeight + 1;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(textX, statusY, 0);
		guiGraphics.pose().scale(STATUS_SCALE, STATUS_SCALE, 1);
		guiGraphics.drawString(font, status, 0, 0, statusColour, false);
		guiGraphics.pose().popPose();
	}

	private static String toRoman(int n) {
		if (n <= 0) return "0";
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < ROWAN_VALUES.length; i++) {
			while (n >= ROWAN_VALUES[i]) {
				stringBuilder.append(ROWAN_NUMERALS[i]);
				n -= ROWAN_VALUES[i];
			}
		}
		return stringBuilder.toString();
	}
}
