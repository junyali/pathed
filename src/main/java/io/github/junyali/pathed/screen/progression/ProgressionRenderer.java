package io.github.junyali.pathed.screen.progression;

import net.minecraft.client.gui.GuiGraphics;

public class ProgressionRenderer {
	public static void renderBorder(GuiGraphics guiGraphics, int x, int y, int w, int h) {
		// warning: some very complex maths here D:
		int b = ProgressionScreen.FRAME_BORDER;
		int tw = ProgressionScreen.FRAME_TEX_WIDTH;
		int th = ProgressionScreen.FRAME_TEX_HEIGHT;
		int iw = tw - b * 2;
		int ih = th - b * 2;

		var tex = ProgressionScreen.FRAME_TEXTURE;

		guiGraphics.blit(tex, x, y, b, b, 0, 0, b, b, tw, th);
		guiGraphics.blit(tex, x + w - b, y, b, b, tw - b, 0, b, b, tw, th);
		guiGraphics.blit(tex, x, y + h - b, b, b, 0, th - b, b, b, tw, th);
		guiGraphics.blit(tex, x + w - b, y + h - b, b, b, tw - b, th - b, b, b, tw, th);

		guiGraphics.blit(tex, x + b, y, w - b * 2, b, b, 0, iw, b, tw, th);
		guiGraphics.blit(tex, x + b, y + h - b, w - b * 2, b, b, th - b, iw, b, tw, th);
		guiGraphics.blit(tex, x, y + b, b, h - b * 2, 0, b, b, ih, tw, th);
		guiGraphics.blit(tex, x + w - b, y + b, b, h - b * 2, tw - b, b, b, ih, tw, th);
	}
}
