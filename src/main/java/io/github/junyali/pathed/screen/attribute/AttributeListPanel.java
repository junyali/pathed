package io.github.junyali.pathed.screen.attribute;

import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.data.attribute.AttributeRegistry;
import io.github.junyali.pathed.screen.progression.ProgressionRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AttributeListPanel {
	private static final int TAB_BAR_H = 18;
	private static final int CHIP_H = 22;
	private static final int CHIP_GAP = 2;
	private static final int SCROLLBAR_W = 5;

	private final AttributeScreen screen;
	private final List<Attribute> visible = new ArrayList<>();

	private final int left;
	private final int top;
	private final int width;
	private final int height;

	private int scrollPos = 0;
	private int maxScroll = 0;
	private boolean draggingScroll = false;
	private double dragStartMouseY;
	private int dragStartScroll;

	public AttributeListPanel(AttributeScreen screen, int left, int top, int width, int height) {
		this.screen = screen;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public void refresh() {
		visible.clear();
		if (screen.isShowAll()) {
			visible.addAll(AttributeRegistry.all());
		} else {
			for (Attribute a : AttributeRegistry.all()) {
				if (screen.isObtained(a)) visible.add(a);
			}
		}
		if (screen.getSelected() != null && !visible.contains(screen.getSelected()));
	}

	private int innerHeight() {
		return height - 2 * AttributeScreen.FRAME_BORDER - TAB_BAR_H;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		ProgressionRenderer.renderBorder(guiGraphics, left, top, width, height);

		int innerL = left + AttributeScreen.FRAME_BORDER;
		int innerT = top + AttributeScreen.FRAME_BORDER;
		int innerW = width - 2 * AttributeScreen.FRAME_BORDER;
		int innerH = innerHeight();

		guiGraphics.fill(
				innerL,
				innerT,
				innerL + innerW,
				innerT + TAB_BAR_H + innerH,
				AttributeScreen.COLOUR_PANEL_BG
		);

		// render tabs here

		int bodyTop = innerT + TAB_BAR_H;
		int bodyBottom = bodyTop + innerH;
		guiGraphics.fill(
				innerL,
				bodyTop,
				innerL + innerW,
				bodyTop + 1,
				AttributeScreen.COLOUR_BORDER
		);

		int contentRight = innerL + innerW;
		guiGraphics.enableScissor(
				innerL,
				bodyTop + 1,
				contentRight,
				bodyBottom
		);

		// render chips here

		if (visible.isEmpty()) {
			Font font = screen.getMinecraft().font;
			String hint = Component.translatable("pathed.gui.attributes.list.empty").getString();
			guiGraphics.drawString(font,
					hint,
					innerL + (innerW - font.width(hint)) / 2,
					bodyTop + (innerH - font.lineHeight) / 2,
					AttributeScreen.COLOUR_TEXT_MUTED,
					false
			);
		}
		guiGraphics.disableScissor();
	}
}
