package io.github.junyali.pathed.screen.attribute;

import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.data.attribute.AttributeRegistry;
import io.github.junyali.pathed.screen.attribute.components.AttributeChip;
import io.github.junyali.pathed.screen.attribute.components.AttributeTab;
import io.github.junyali.pathed.screen.common.ScrollBar;
import io.github.junyali.pathed.screen.common.PanelRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AttributeListPanel {
	private static final int TAB_BAR_H = 18;
	private static final int CHIP_H = 22;
	private static final int CHIP_GAP = 2;

	private final AttributeScreen screen;
	private final List<Attribute> visible = new ArrayList<>();

	private final int left;
	private final int top;
	private final int width;
	private final int height;

	private final ScrollBar scrollBar = new ScrollBar();

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
		if (screen.getSelected() != null && !visible.contains(screen.getSelected())) {
			screen.setSelected(null);
		}
		recalcScroll();
	}

	private void recalcScroll() {
		int contentH = visible.size() * (CHIP_H + CHIP_GAP);
		int innerH = innerHeight();
		int innerL = left + AttributeScreen.FRAME_BORDER;
		int innerW = width - 2 * AttributeScreen.FRAME_BORDER;
		int bodyTop = top + AttributeScreen.FRAME_BORDER + TAB_BAR_H;

		scrollBar.setMaxScroll(Math.max(0, contentH - innerH));
		scrollBar.setBounds(innerL + innerW - scrollBar.getWidth(), bodyTop, innerH);
	}

	private int innerHeight() {
		return height - 2 * AttributeScreen.FRAME_BORDER - TAB_BAR_H;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		PanelRenderer.renderBorder(guiGraphics, left, top, width, height);

		int innerL = left + AttributeScreen.FRAME_BORDER;
		int innerT = top + AttributeScreen.FRAME_BORDER;
		int innerW = width - 2 * AttributeScreen.FRAME_BORDER;
		int innerH = innerHeight();
		int sbInset = scrollBar.isVisible() ? scrollBar.getWidth() : 0;

		guiGraphics.fill(
				innerL,
				innerT,
				innerL + innerW,
				innerT + TAB_BAR_H + innerH,
				AttributeScreen.COLOUR_PANEL_BG
		);

		int tabW = innerW / 2;
		AttributeTab.render(guiGraphics, innerL, innerT, tabW, TAB_BAR_H, Component.translatable("pathed.gui.attributes.tab.mine"), !screen.isShowAll(), mouseX, mouseY);
		AttributeTab.render(guiGraphics, innerL + tabW, innerT, innerW - tabW, TAB_BAR_H, Component.translatable("pathed.gui.attributes.tab.all"), screen.isShowAll(), mouseX, mouseY);

		int bodyTop = innerT + TAB_BAR_H;
		int bodyBottom = bodyTop + innerH;
		guiGraphics.fill(
				innerL,
				bodyTop,
				innerL + innerW,
				bodyTop + 1,
				AttributeScreen.COLOUR_BORDER
		);

		guiGraphics.enableScissor(
				innerL,
				bodyTop + 1,
				innerL + innerW - sbInset,
				bodyBottom
		);

		int chipX = innerL + 3;
		int chipY = bodyTop + 4 - scrollBar.getScroll();
		int chipW = innerW - 6 - sbInset;

		for (Attribute a : visible) {
			AttributeChip.render(guiGraphics, screen, a, chipX, chipY, chipW, CHIP_H, mouseX, mouseY);
			chipY += CHIP_H + CHIP_GAP;
		}

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

		scrollBar.render(guiGraphics, mouseX, mouseY);
	}

	public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int innerL = left + AttributeScreen.FRAME_BORDER;
		int innerW = width - 2 * AttributeScreen.FRAME_BORDER;
		int chipX = innerL + 3;
		int chipW = innerW - 6 - (scrollBar.isVisible() ? scrollBar.getWidth() : 0);
		int bodyTop = top + AttributeScreen.FRAME_BORDER + TAB_BAR_H;
		int chipY = bodyTop + 4 - scrollBar.getScroll();

		for (Attribute a : visible) {
			boolean inBody = mouseY >= bodyTop && mouseY < bodyTop + innerHeight();
			boolean onChip = mouseX >= chipX && mouseX < chipX + chipW && mouseY >= chipY && mouseY < chipY + CHIP_H;
			if (inBody && onChip) {
				guiGraphics.renderComponentTooltip(
						screen.getMinecraft().font,
						List.of(
								Component.translatable(a.getNameKey()),
								Component.literal(a.getId().toString()).withStyle(style -> style.withColor(AttributeScreen.COLOUR_TEXT_DIM))
						),
						mouseX,
						mouseY
				);
				return;
			}
			chipY += CHIP_H + CHIP_GAP;
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY) {
		int innerL = left + AttributeScreen.FRAME_BORDER;
		int innerT = top + AttributeScreen.FRAME_BORDER;
		int innerW = width - 2 * AttributeScreen.FRAME_BORDER;

		int tabW = innerW / 2;
		if (within(mouseX, mouseY, innerL, innerT, tabW, TAB_BAR_H)) {
			screen.setShowAll(false);
			return true;
		}
		if (within(mouseX, mouseY, innerL + tabW, innerT, innerW - tabW, TAB_BAR_H)) {
			screen.setShowAll(true);
			return true;
		}

		if (scrollBar.mouseClicked(mouseX, mouseY)) return true;

		int bodyTop = innerT + TAB_BAR_H;
		int chipX = innerL + 3;
		int chipY = bodyTop + 4 - scrollBar.getScroll();
		int chipW = innerW - 6 - (scrollBar.isVisible() ? scrollBar.getWidth() : 0);
		if (within(mouseX, mouseY, innerL, bodyTop, innerW, innerHeight())) {
			for (Attribute a : visible) {
				if (within(mouseX, mouseY, chipX, chipY, chipW, CHIP_H)) {
					screen.setSelected(a);
					return true;
				}
				chipY += CHIP_H + CHIP_GAP;
			}
		}
		return false;
	}

	private static boolean within(double mouseX, double mouseY, int x, int y, int w, int h) {
		return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
		int innerL = left + AttributeScreen.FRAME_BORDER;
		int innerW = width - 2 * AttributeScreen.FRAME_BORDER;
		int bodyTop = top + AttributeScreen.FRAME_BORDER + TAB_BAR_H;
		if (!within(mouseX, mouseY, innerL, bodyTop, innerW, innerHeight())) return false;
		return scrollBar.mouseScrolled(scrollY, 8);
	}

	public boolean mouseDragged(double mouseX, double mouseY) {
		return scrollBar.mouseDragged(mouseY);
	}

	public void mouseReleased() {
		scrollBar.release();
	}
}
