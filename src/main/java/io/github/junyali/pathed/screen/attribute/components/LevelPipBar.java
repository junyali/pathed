package io.github.junyali.pathed.screen.attribute.components;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class LevelPipBar {
	public static final int SIZE = 8;
	public static final int GAP = 3;
	public static final int HEIGHT = SIZE;

	private static final ResourceLocation PIPS = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/sprites/attributes/pips_sprite.png");
	private static final int SHEET_W = 32;
	private static final int SHEET_H = 8;
	private static final int U_BACKGROUND = 0;
	private static final int U_HOVER_OUTLINE = 8;
	private static final int U_RED = 16;
	private static final int U_GREEN = 24;

	private LevelPipBar() {}

	public static int totalWidth(int max) {
		return max <= 0 ? 0 : max * SIZE + (max - 1) * GAP;
	}

	public static int hoveredIndex(int x, int y, int max, int mouseX, int mouseY) {
		if (mouseY < y || mouseY >= y + SIZE) return 0;
		for (int i = 1; i <= max; i++) {
			int px = x + (i - 1) * (SIZE + GAP);
			if (mouseX >= px && mouseX < px + SIZE) return i;
		}
		return 0;
	}

	public static void render(GuiGraphics guiGraphics, int x, int y, int max, int currentLevel, int obtainedLevel, int mouseX, int mouseY) {
		for (int i = 1; i <= max; i++) {
			int px = x + (i - 1) * (SIZE + GAP);

			boolean filled = i <= currentLevel;
			boolean available = i <= obtainedLevel;
			boolean hovered = mouseX >= px && mouseX < px + SIZE && mouseY >= y && mouseY < y + SIZE;

			guiGraphics.blit(PIPS, px, y, U_BACKGROUND, 0, SIZE, SIZE, SHEET_W, SHEET_H);

			if (filled) {
				guiGraphics.blit(PIPS, px, y, U_GREEN, 0, SIZE, SIZE, SHEET_W, SHEET_H);
			} else if (available) {
				guiGraphics.blit(PIPS, px, y, U_RED, 0, SIZE, SIZE, SHEET_W, SHEET_H);
			}

			if (hovered && available) {
				guiGraphics.blit(PIPS, px, y, U_HOVER_OUTLINE, SIZE, SIZE, SIZE, SHEET_W, SHEET_H);
			}
		}
	}
}
