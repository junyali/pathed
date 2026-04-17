package io.github.junyali.pathed.screen.progression.components;

import io.github.junyali.pathed.data.skill.SkillNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ConnectionRenderer {
	private static final int LINE_COLOUR = 0xFFFFFFFF;
	private static final int BORDER_COLOUR = 0xFF000000;
	private static final int LINE_WIDTH = 1;
	private static final int BORDER_WIDTH = 3;

	public static void render(GuiGraphics guiGraphics, SkillNode node, Map<ResourceLocation, SkillNode> nodeMap, int centreX, int centreY) {
		int toX = centreX + node.position().x();
		int toY = centreY + node.position().y();

		for (ResourceLocation prevId : node.previousNodes()) {
			SkillNode prev = nodeMap.get(prevId);
			if (prev == null) continue;

			int fromX = centreX + prev.position().x();
			int fromY = centreY + prev.position().y();

			drawVerticalLine(guiGraphics, fromX, fromY, toY, BORDER_COLOUR, BORDER_WIDTH);
			drawHorizontalLine(guiGraphics, fromX, toX, toY, BORDER_COLOUR, BORDER_WIDTH);

			drawVerticalLine(guiGraphics, fromX, fromY, toY, LINE_COLOUR, LINE_WIDTH);
			drawHorizontalLine(guiGraphics, fromX, toX, toY, LINE_COLOUR, LINE_WIDTH);
		}
	}

	// some not-so-complex maths
	private static void drawVerticalLine(GuiGraphics guiGraphics, int x, int y1, int y2, int colour, int width) {
		int minY = Math.min(y1, y2);
		int maxY = Math.max(y1, y2);
		int half = width / 2;
		guiGraphics.fill(x - half, minY, x - half + width, maxY, colour);
	}

	private static void drawHorizontalLine(GuiGraphics guiGraphics, int x1, int x2, int y, int colour, int width) {
		int minX = Math.min(x1, x2);
		int maxX = Math.max(x1, x2);
		int half = width / 2;
		guiGraphics.fill(minX, y - half, maxX, y - half + width, colour);
	}
}
