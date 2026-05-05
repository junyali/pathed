package io.github.junyali.pathed.screen.attribute.components;

import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class AttributeStatusBadge {
	public static final int HEIGHT = 9;
	private static final int PADDING_X = 3;

	private AttributeStatusBadge() {}

	private static String labelFor(boolean active, boolean conflict, boolean obtained) {
		if (!obtained) return "?";
		if (active) return "ON";
		if (conflict) return "!";
		return "OFF";
	}

	public static int widthFor(boolean active, boolean conflict, boolean obtained, Font font) {
		return font.width(labelFor(active, conflict, obtained)) + PADDING_X * 2;
	}

	public static void render(GuiGraphics guiGraphics, int x, int y, boolean active, boolean conflict, boolean obtained) {
		Font font = Minecraft.getInstance().font;
		String label = labelFor(active, conflict, obtained);
		int w = font.width(label) + PADDING_X * 2;

		int textColour;
		int borderColour;
		int backgroundColour;

		if (!obtained) {
			textColour = AttributeScreen.COLOUR_TEXT_MUTED;
			borderColour = AttributeScreen.COLOUR_BORDER;
			backgroundColour = AttributeScreen.COLOUR_LOCKED;
		} else if (active) {
			textColour = AttributeScreen.COLOUR_TEXT_GOOD;
			borderColour = AttributeScreen.COLOUR_TEXT_GOOD;
			backgroundColour = AttributeScreen.COLOUR_ACTIVE;
		} else if (conflict) {
			textColour = AttributeScreen.COLOUR_TEXT_BAD;
			borderColour = AttributeScreen.COLOUR_TEXT_BAD;
			backgroundColour = AttributeScreen.COLOUR_CONFLICT;
		} else {
			textColour = AttributeScreen.COLOUR_TEXT_DIM;
			borderColour = AttributeScreen.COLOUR_BORDER;
			backgroundColour = AttributeScreen.COLOUR_LOCKED;
		}

		guiGraphics.fill(x, y, x + w, y + HEIGHT, backgroundColour);
		guiGraphics.fill(x, y, x + w, y + 1, borderColour);
		guiGraphics.fill(x, y + HEIGHT - 1, x + w, y + HEIGHT, borderColour);
		guiGraphics.fill(x, y, x + 1, y + HEIGHT, borderColour);
		guiGraphics.fill(x + w - 1, y, x + w, y + HEIGHT, borderColour);
		guiGraphics.drawString(font, label, x + PADDING_X, y + 1, textColour, false);
	}
}
