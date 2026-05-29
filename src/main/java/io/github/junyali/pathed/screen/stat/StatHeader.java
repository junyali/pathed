package io.github.junyali.pathed.screen.stat;

import io.github.junyali.pathed.screen.common.PanelRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class StatHeader {
	public static final int STRIP_HEIGHT = 22;
	public static final int MARGIN = 4;
	public static final int TOTAL_HEIGHT = MARGIN + STRIP_HEIGHT + MARGIN;

	private static final int BACKGROUND_TOP = 0xFF2B2B2B;
	private static final int BACKGROUND_BOTTOM = 0xFF1C1C1C;
	private static final int BEVEL_HIGH = 0xFF6E6E6E;
	private static final int BEVEL_LOW = 0xFF000000;
	private static final int ACCENT = 0xFFFFD267;
	private static final int TEXT = 0xFFFFFFFF;

	private final Font font;

	public StatHeader(Font font) {
		this.font = font;
	}

	public void render(GuiGraphics guiGraphics, int panelX, int panelY, int panelWidth, Component title, ItemStack icon) {
		int b = PanelRenderer.FRAME_BORDER;
		int hX = panelX + b + MARGIN;
		int hY = panelY + b + MARGIN;
		int hW = panelWidth - 2 * (b + MARGIN) - 1;
		int hH = STRIP_HEIGHT;

		guiGraphics.fillGradient(hX, hY, hX + hW, hY + hH, BACKGROUND_TOP, BACKGROUND_BOTTOM);
		guiGraphics.fill(hX, hY, hX + hW, hY + 1, BEVEL_HIGH);
		guiGraphics.fill(hX, hY + hH - 1, hX + hW, hY + hH, BEVEL_LOW);
		guiGraphics.fill(hX, hY, hX + 2, hY + hH, ACCENT);

		int textX = hX + 6;
		if (icon != null && !icon.isEmpty()) {
			guiGraphics.renderItem(icon, hX + 6, hY + (hH - 16) / 2);
			textX += 18;
		}
		int textY = hY + (hH - font.lineHeight) / 2;
		guiGraphics.drawString(font, title, textX, textY, TEXT, true);
	}

	public static int bottomY(int panelY) {
		return panelY + PanelRenderer.FRAME_BORDER + TOTAL_HEIGHT;
	}
}
