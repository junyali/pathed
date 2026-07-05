package io.github.junyali.pathed.screen.stat.stats;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.screen.stat.AbstractStatPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Locale;

public class DistanceTravelledPanel extends AbstractStatPanel {
	private static final int CARD_BACKGROUND = 0XFF373737;
	private static final int CARD_BORDER_HIGH = 0xFFFFFFFF;
	private static final int CARD_BORDER_LOW = 0xFF555555;
	private static final int CHIP_BACKGROUND = 0xFF2A2A2A;

	public DistanceTravelledPanel(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public Component getTitle() {
		return Component.translatable("pathed.gui.stats.distance_travelled.title");
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		renderFrame(guiGraphics);

		Player player = Minecraft.getInstance().player;
		if (player == null) return;

		int fixed = ProgressionAttachment.get(player).getDistanceTravelled();
		double blocks = fixed / 100.0;
		int left = panelX + PADDING + 3;
		int right = panelX + panelWidth - PADDING - 3;
		int top = contentTop();
		int bottom = panelY + panelHeight - PADDING - 3;
		int width = right - left;
		int height = bottom - top;

		int cardH = Math.max(60, height / 2);
		drawCard(guiGraphics, left, top, width, cardH);

		String big = String.format(Locale.ROOT, "%,.2f", blocks);
		String unit = "blocks travelled";
		int bigScale = 3;

		int bigW = font.width(big) * bigScale;
		int bigX = left + (width - bigW) / 2;
		int bigY = top + (cardH / 2) - (font.lineHeight * bigScale) / 2 - 4;

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(bigX, bigY, 0);
		guiGraphics.pose().scale(bigScale, bigScale, 1);
		guiGraphics.drawString(font, big, 0, 0, COLOUR_TEXT, true);
		guiGraphics.pose().popPose();

		int unitX = left + (width - font.width(unit)) / 2;
		int unitY = bigY + font.lineHeight * bigScale + 4;
		guiGraphics.drawString(font, unit, unitX, unitY, COLOUR_TEXT_DIM, false);

		double chunks = blocks / 16.0;
		double km = blocks / 1000.0;
		double regions = blocks / 512.0;

		List<String[]> chips = List.of(
				new String[]{"Chunks", String.format(Locale.ROOT, "%,.1f", chunks)},
				new String[]{"Kilometres", String.format(Locale.ROOT, "%,.3f km", km)},
				new String[]{"Regions", String.format(Locale.ROOT, "%,.2f", regions)}
		);

		int chipsTop = top + cardH + 6;
		int chipsH = 28;
		int gap = 6;
		int chipW = (width - gap * (chips.size() - 1)) / chips.size();
		for (int i = 0; i < chips.size(); i++) {
			int cX = left + i * (chipW + gap);
			drawChip(guiGraphics, cX, chipsTop, chipW, chipsH, chips.get(i)[0], chips.get(i)[1]);
		}
	}

	private void drawCard(GuiGraphics guiGraphics, int x, int y, int w, int h) {
		guiGraphics.fill(x, y, x + w, y + h, CARD_BACKGROUND);
		guiGraphics.fill(x, y, x + w, y + 1, CARD_BORDER_LOW);
		guiGraphics.fill(x, y, x + 1, y + h, CARD_BORDER_LOW);
		guiGraphics.fill(x, y + h - 1, x + w, y + h, CARD_BORDER_HIGH);
		guiGraphics.fill(x + w - 1, y, x + w, y + h, CARD_BORDER_HIGH);
	}

	private void drawChip(GuiGraphics guiGraphics, int x, int y, int w, int h, String label, String value) {
		guiGraphics.fill(x, y, x + w, y + h, CHIP_BACKGROUND);
		guiGraphics.fill(x, y, x + w, y + 1, CARD_BORDER_LOW);
		guiGraphics.fill(x, y, x + 1, y + h, CARD_BORDER_LOW);
		guiGraphics.fill(x, y + h - 1, x + w, y + h, CARD_BORDER_HIGH);
		guiGraphics.drawString(font, label, x + 4, y + 3, COLOUR_TEXT_DIM, false);
		guiGraphics.drawString(font, value, x + w - font.width(value) - 4, y + h - font.lineHeight - 3, COLOUR_TEXT, true);
	}
}
