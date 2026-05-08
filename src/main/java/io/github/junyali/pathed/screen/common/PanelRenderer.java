package io.github.junyali.pathed.screen.common;

import io.github.junyali.pathed.Pathed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class PanelRenderer {
	public static final ResourceLocation FRAME_TEXTURE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/border.png");
	public static final int FRAME_BORDER = 9;
	public static final int FRAME_TEXTURE_WIDTH = 176;
	public static final int FRAME_TEXTURE_HEIGHT = 182;

	private PanelRenderer() {}

	public static void renderBorder(GuiGraphics guiGraphics, int x, int y, int w, int h) {
		renderBorder(guiGraphics, x, y, w, h, FRAME_TEXTURE, FRAME_BORDER, FRAME_TEXTURE_WIDTH, FRAME_TEXTURE_HEIGHT);
	}

	public static void renderBorder(GuiGraphics guiGraphics, int x, int y, int w, int h, ResourceLocation texture, int b, int tw, int th) {
		// warning: some very complex maths here D:
		int iw = tw - b * 2;
		int ih = th - b * 2;

		guiGraphics.blit(texture, x, y, b, b, 0, 0, b, b, tw, th);
		guiGraphics.blit(texture, x + w - b, y, b, b, tw - b, 0, b, b, tw, th);
		guiGraphics.blit(texture, x, y + h - b, b, b, 0, th - b, b, b, tw, th);
		guiGraphics.blit(texture, x + w - b, y + h - b, b, b, tw - b, th - b, b, b, tw, th);

		guiGraphics.blit(texture, x + b, y, w - b * 2, b, b, 0, iw, b, tw, th);
		guiGraphics.blit(texture, x + b, y + h - b, w - b * 2, b, b, th - b, iw, b, tw, th);
		guiGraphics.blit(texture, x, y + b, b, h - b * 2, 0, b, b, ih, tw, th);
		guiGraphics.blit(texture, x + w - b, y + b, b, h - b * 2, tw - b, b, b, ih, tw, th);
	}
}
