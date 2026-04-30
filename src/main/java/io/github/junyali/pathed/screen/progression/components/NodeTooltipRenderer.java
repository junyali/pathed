package io.github.junyali.pathed.screen.progression.components;

import io.github.junyali.pathed.data.skill.SkillNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class NodeTooltipRenderer {
	private static final int TOOLTIP_PADDING = 8;
	private static final int LINE_HEIGHT = 10;
	private static final int BACKGROUND_COLOUR = 0xF0100010;
	private static final int BORDER_COLOUR_TOP = 0x505000FF;
	private static final int BORDER_COLOUR_BOTTOM = 0x5028007F;

	public static void render(GuiGraphics guiGraphics, SkillNode node, Font font, int mouseX, int mouseY, int screenWidth, int screenHeight) {
		List<Component> lines = buildTooltipLines(node);
		if (lines.isEmpty()) {
			return;
		}

		int tooltipWidth = calculateTooltipWidth(lines, font);
		int tooltipHeight = calculateTooltipHeight(lines);

		int x = mouseX + 12;
		int y = mouseY - 12;

		if (x + tooltipWidth > screenWidth) {
			x = mouseX - tooltipWidth - 12;
		}
		if (y + tooltipHeight > screenHeight) {
			y = screenHeight - tooltipHeight;
		}
		if (y < 0 ) {
			y = 0;
		}

		renderTooltipBackground(guiGraphics, x, y, tooltipWidth, tooltipHeight);
		renderTooltipText(guiGraphics, lines, font, x, y);
	}

	private static List<Component> buildTooltipLines(SkillNode node) {
		List<Component> lines = new ArrayList<>();

		String categoryId = node.category().getPath();
		String nodeId = node.id().getPath().replace(categoryId + "/", "");

		lines.add(Component.translatable("pathed.skill." + categoryId + "." + nodeId + ".name")
				.withStyle(style -> style.withColor(0xFFFFFF).withBold(true)));

		Component description = Component.translatable("pathed.skill." + categoryId + "." + nodeId + ".desc")
				.withStyle(style -> style.withColor(0xAAAAAA));
		lines.add(Component.empty());
		lines.add(description);

		if (node.prerequisites() != null && !node.prerequisites().isEmpty()) {
			lines.add(Component.empty());
			lines.add(Component.translatable("pathed.skill.tooltip.prerequisites")
					.withStyle(style -> style.withColor(0xFFAA00).withBold(true)));

			for (ResourceLocation prereq : node.prerequisites()) {
				String prereqCategory = prereq.getPath().substring(0, prereq.getPath().lastIndexOf("/"));
				String prereqNode = prereq.getPath().substring(prereq.getPath().lastIndexOf("/") + 1);

				lines.add(Component.literal(" ->")
						.append(Component.translatable("pathed.skill." + prereqCategory + "." + prereqNode + ".name"))
						.withStyle(style -> style.withColor(0xFFAA00)));
			}
		}

		return lines;
	}

	private static int calculateTooltipWidth(List<Component> lines, Font font) {
		int maxWidth = 0;
		for (Component line : lines) {
			int lineWidth = font.width(line);
			if (lineWidth > maxWidth) {
				maxWidth = lineWidth;
			}
		}
		return maxWidth + TOOLTIP_PADDING * 2;
	}

	private static int calculateTooltipHeight(List<Component> lines) {
		return lines.size() * LINE_HEIGHT + TOOLTIP_PADDING * 2;
	}

	private static void renderTooltipBackground(GuiGraphics guiGraphics, int x, int y, int width, int height) {
		guiGraphics.fill(x, y, x + width, y + height, BACKGROUND_COLOUR);

		guiGraphics.fill(x - 1, y - 1, x + width + 1, y, BORDER_COLOUR_TOP);
		guiGraphics.fill(x - 1, y + height, x + width + 1, y + height + 1, BORDER_COLOUR_BOTTOM);
		guiGraphics.fill(x - 1, y, x, y + height, BORDER_COLOUR_TOP);
		guiGraphics.fill(x + width, y, x + width + 1, y + height, BORDER_COLOUR_BOTTOM);
	}

	private static void renderTooltipText(GuiGraphics guiGraphics, List<Component> lines, Font font, int x, int y) {
		int currentY = y + TOOLTIP_PADDING;
		for (Component line : lines) {
			guiGraphics.drawString(font, line, x + TOOLTIP_PADDING, currentY, 0xFFFFFFF);
			currentY += LINE_HEIGHT;
		}
	}
}
