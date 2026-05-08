package io.github.junyali.pathed.screen.stat.stats;

import io.github.junyali.pathed.screen.stat.AbstractStatPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlocksBrokenPanel extends AbstractStatPanel {
	private static final int SEARCH_H = 16;
	private static final int SEARCH_PAD = 6;
	private static final int HEADER_H = 28;

	private static final int CELL_SIZE = 48;
	private static final int CELL_GAP = 4;
	private static final int GRID_PADDING = 6;

	private static final int ICON_SIZE = 20;

	private List<Map.Entry<ResourceLocation, Integer>> displayList = new ArrayList<>();

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {

	}

	private void renderGrid(GuiGraphics guiGraphics, int mouseX, int mouseY, int startY) {
		int cols = Math.max(1, (panelWidth - GRID_PADDING * 2 + CELL_GAP) / (CELL_SIZE + CELL_GAP));
		int cellX0 = panelX + GRID_PADDING;

		int row = 0;
		int col = 0;
		for (Map.Entry<ResourceLocation, Integer> entry : displayList) {
			int cX = cellX0 + col * (CELL_SIZE + CELL_GAP);
			int cY = startY + row * (CELL_SIZE + CELL_GAP);

			if (cY + CELL_SIZE < startY || cY > panelY + panelHeight) {
				col++;
				if (col >= cols) {
					col = 0;
					row++;
				}
				continue;
			}

			renderCell(guiGraphics, entry.getKey(), entry.getValue(), cX, cY, mouseX, mouseY);

			col++;
			if (col >= cols) {
				col = 0;
				row++;
			}
		}
	}

	private void renderCell(GuiGraphics guiGraphics, ResourceLocation block, int count, int x, int y, int mouseX, int mouseY) {
		guiGraphics.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, 0xFF2A2A3C);

		int iconX = x + (CELL_SIZE - ICON_SIZE) / 2;
		int iconY = y + 5;
		ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(block));
		guiGraphics.renderItem(stack, x, y);

		String countStr = String.valueOf(count);
		guiGraphics.drawString(
				font,
				countStr,
				x + (CELL_SIZE - font.width(countStr)) / 2,
				iconY + ICON_SIZE + 2,
				0xFFFFFFFF,
				false
		);
	}
}
