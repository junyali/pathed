package io.github.junyali.pathed.screen.attribute;

import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.screen.progression.ProgressionRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class AttributeDetailPanel {
	private static final int ICON_BOX = 24;
	private static final int PADDING = 8;

	private final AttributeScreen screen;

	private final int left;
	private final int top;
	private final int width;
	private final int height;

	private int pipRowX = -1;
	private int pipRowY = -1;

	private int toggleX = -1;
	private int toggleY = -1;

	public AttributeDetailPanel(AttributeScreen screen, int left, int top, int width, int height) {
		this.screen = screen;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		ProgressionRenderer.renderBorder(guiGraphics, left, top, width, height);

		int innerL = left + AttributeScreen.FRAME_BORDER;
		int innerT = top + AttributeScreen.FRAME_BORDER;
		int innerW = width - 2 * AttributeScreen.FRAME_BORDER;
		int innerH = height - 2 * AttributeScreen.FRAME_BORDER;
		guiGraphics.fill(innerL, innerT, innerL + innerW, innerT + innerH, AttributeScreen.COLOUR_PANEL_BG);

		Font font = screen.getMinecraft().font;
		Attribute attr = screen.getSelected();
		if (attr == null) {
			String hint = Component.translatable("pathed.gui.attributes.detail.no_selection").getString();
			guiGraphics.drawString(font, hint, innerL + (innerW - font.width(hint)) / 2, innerT + (innerH - font.lineHeight) / 2, AttributeScreen.COLOUR_TEXT_DIM, false);
			pipRowX = -1;
			toggleY = -1;
			return;
		}

		boolean obtained = screen.isObtained(attr);
		boolean active = screen.getPendingActive(attr);
		boolean conflicts = screen.conflictsWithPendingActive(attr);

		int cX = innerL + PADDING;
		int cY = innerT + PADDING;
		int contentW = innerW - PADDING * 2;

		guiGraphics.fill(cX, cY, cX + ICON_BOX, cY + ICON_BOX, 0xFF333333);
		outline(guiGraphics, cX, cY, ICON_BOX, ICON_BOX, AttributeScreen.COLOUR_BORDER);
		guiGraphics.renderItem(attr.getIcon(), cX + 4, cY + 4);

		int textX = cX + ICON_BOX + 6;
		guiGraphics.drawString(font, Component.translatable(attr.getNameKey()).copy().withStyle(style -> style.withBold(true)), textX, cY + 2, AttributeScreen.COLOUR_TEXT_HIGHLIGHT, false);
		guiGraphics.drawString(font, attr.getId().toString(), textX, cY + 2 + font.lineHeight + 1, AttributeScreen.COLOUR_TEXT_DIM, false);

		cY += ICON_BOX + PADDING;

		List<FormattedCharSequence> lines = font.split(Component.translatable(attr.getDescriptionKey()), contentW);
		for (FormattedCharSequence line : lines) {
			guiGraphics.drawString(font, line, cX, cY, AttributeScreen.COLOUR_TEXT, false);
			cY += font.lineHeight + 1;
		}

		cY += 4;
		guiGraphics.fill(cX, cY, cX + contentW, cY + 1, AttributeScreen.COLOUR_BORDER);
	}

	public boolean mouseClicked(double mouseX, double mouseY) {
		Attribute attr = screen.getSelected();
		if (attr == null) return false;

		return false;
	}

	private static void outline(GuiGraphics guiGraphics, int x, int y, int w, int h, int colour) {
		guiGraphics.fill(x, y, x + w, y + 1, colour);
		guiGraphics.fill(x, y + h - 1, x + w, y + h, colour);
		guiGraphics.fill(x, y, x + 1, y + h, colour);
		guiGraphics.fill(x + w - 1, y, x + w, y + h, colour);
	}
}
