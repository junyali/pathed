package io.github.junyali.pathed.screen.stat.stats;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.screen.common.PanelRenderer;
import io.github.junyali.pathed.screen.stat.AbstractStatPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class DistanceTravelledPanel extends AbstractStatPanel {
	public static final int COLOUR_PANEL_BACKGROUND = 0xFF8B8B8B;

	public DistanceTravelledPanel(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public Component getTitle() {
		return Component.translatable("pathed.gui.stats.distance_travelled.title");
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, COLOUR_PANEL_BACKGROUND);
		PanelRenderer.renderBorder(guiGraphics, panelX, panelY, panelWidth, panelHeight);

		Player player = Minecraft.getInstance().player;
		if (player == null) return;

		ProgressionAttachment progressionAttachment = ProgressionAttachment.get(player);
		int distanceFixedPoint = progressionAttachment.getDistanceTravelled();
		double distanceBlocks = distanceFixedPoint / 100.0;

		Component text = Component.literal(String.format("%.2f blocks", distanceBlocks));

		int textX = panelX + (panelWidth - font.width(text)) / 2;
		int textY = panelY + (panelHeight - font.lineHeight) / 2;
		guiGraphics.drawString(font, text, textX, textY, 0xFFFFFFFF);
	}
}
