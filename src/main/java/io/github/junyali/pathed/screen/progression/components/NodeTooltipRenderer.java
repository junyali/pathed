package io.github.junyali.pathed.screen.progression.components;

import io.github.junyali.pathed.data.skill.SkillNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class NodeTooltipRenderer {
	public static void render(GuiGraphics guiGraphics, SkillNode node, Font font, int mouseX, int mouseY) {
		List<Component> lines = buildTooltipLines(node);
		if (lines.isEmpty()) {
			return;
		}

		List<FormattedCharSequence> visual = new ArrayList<>(lines.size());
		for (Component line : lines) {
			visual.add(line.getVisualOrderText());
		}

		guiGraphics.renderTooltip(font, visual, mouseX, mouseY);
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
}
