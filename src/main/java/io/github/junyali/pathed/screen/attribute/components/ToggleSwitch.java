package io.github.junyali.pathed.screen.attribute.components;

import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import net.minecraft.client.gui.GuiGraphics;

public final class ToggleSwitch {
	public static final int WIDTH = 22;
	public static final int HEIGHT = 11;

	private ToggleSwitch() {}

	public static boolean contains(int x, int y, double mouseX, double mouseY) {
		return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
	}

	public static void render(GuiGraphics guiGraphics, int x, int y, boolean active, int mouseX, int mouseY) {
		boolean hovered = contains(x, y, mouseX, mouseY);

		int trackBackground = active ? 0xFF2C7A2C : 0xFF2A2A2A;
		int trackBorder = hovered ? AttributeScreen.COLOUR_TEXT_HIGHLIGHT : AttributeScreen.COLOUR_BORDER;
		int thumbColour = active ? AttributeScreen.COLOUR_TEXT_GOOD : AttributeScreen.COLOUR_TEXT_DIM;
		int thumbSize = HEIGHT - 4;
		int thumbX = active ? x + WIDTH - 2 - thumbSize : x + 2;

		guiGraphics.fill(x, y, x + WIDTH, y + HEIGHT, trackBackground);
		guiGraphics.fill(x, y, x + WIDTH, y + 1, trackBorder);
		guiGraphics.fill(x, y + HEIGHT - 1, x + WIDTH, y + HEIGHT, trackBorder);
		guiGraphics.fill(x, y, x + 1, y + HEIGHT, trackBorder);
		guiGraphics.fill(x + WIDTH - 1, y, x + WIDTH, y + HEIGHT, trackBorder);

		guiGraphics.fill(thumbX, y + 2, thumbX + thumbSize, y + 2 + thumbSize, thumbColour);
	}
}
