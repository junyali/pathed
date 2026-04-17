package io.github.junyali.pathed.screen.progression.components;

import io.github.junyali.pathed.data.skill.SkillNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class NodeRenderer {
	public static final int FRAME_SIZE = 26;
	private static final int ICON_OFFSET = 5;

	public static void render(GuiGraphics guiGraphics, SkillNode node, int centreX, int centreY, boolean completed) {
		int x = centreX + node.position().x() - FRAME_SIZE / 2;
		int y = centreY + node.position().y() - FRAME_SIZE / 2;

		ResourceLocation frameTex = NodeTextureMapper.getTextureForNodeType(node.nodeType(), completed);
		guiGraphics.blit(frameTex, x, y, 0, 0, FRAME_SIZE, FRAME_SIZE, FRAME_SIZE, FRAME_SIZE);

		if ("item".equals(node.icon().type())) {
			ItemStack stack = BuiltInRegistries.ITEM.get(ResourceLocation.parse(node.icon().value())).getDefaultInstance();
			guiGraphics.renderItem(stack, x + ICON_OFFSET, y + ICON_OFFSET);
		}
	}
}
