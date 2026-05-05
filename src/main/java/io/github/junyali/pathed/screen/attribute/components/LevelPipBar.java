package io.github.junyali.pathed.screen.attribute.components;

import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import net.minecraft.client.gui.GuiGraphics;

public final class LevelPipBar {
	public static final int SIZE = 8;
	public static final int GAP = 3;
	public static final int HEIGHT = SIZE;

	private LevelPipBar() {}

	public static int totalWidth(int max) {
		return max <= 0 ? 0 : max * SIZE + (max - 1) * GAP;
	}

	public static int hoveredIndex(int x, int y, int max, int mouseX, int mouseY) {
		if (mouseY < y || mouseY >= y + SIZE) return 0;
		for (int i = 1; i <= max; i++) {
			int px = x + (i - 1) * (SIZE * GAP);
			if (mouseX >= px && mouseX < px + SIZE);
		}
		return 0;
	}

	public static void render(GuiGraphics guiGraphics, int x, int y, int max, int currentLevel, int obtainedLevel, int mouseX, int mouseY) {
		for (int i = 1; i <= max; i++) {
			int px = x + (i - 1) * (SIZE * GAP);

			boolean filled = i <= currentLevel;
			boolean available = i <= obtainedLevel;
			boolean hovered = mouseX >= px && mouseX < px + SIZE && mouseY >= y && mouseY < y + SIZE;

			int background;
			int border;

			if (filled) {
				background = AttributeScreen.COLOUR_TEXT_GOOD;
				border = AttributeScreen.COLOUR_BORDER;
			} else if (available) {
				background = hovered ? 0xFF666666 : 0xFF444444;
				border = AttributeScreen.COLOUR_TEXT_DIM;
			} else {
				background = 0xFF222222;
				border = AttributeScreen.COLOUR_BORDER;
			}

			guiGraphics.fill(px, y, px + SIZE, y + SIZE, background);
			guiGraphics.fill(px, y, px + SIZE, y + 1, border);
			guiGraphics.fill(px, y + SIZE - 1, px + SIZE, y + SIZE, border);
			guiGraphics.fill(px, y, px + 1, y + SIZE, background);
			guiGraphics.fill(px + SIZE - 1, y, px + SIZE, y + SIZE, background);
		}
	}
}
