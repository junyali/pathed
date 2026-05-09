package io.github.junyali.pathed.screen.common;

import io.github.junyali.pathed.Pathed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScrollBar {
	private static final ResourceLocation SCROLL_BAR = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar");
	private static final ResourceLocation SCROLL_BAR_PRESSED = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar/pressed");
	private static final ResourceLocation SCROLL_BAR_SLOT = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar/slot");

	private static final int WIDTH = 8;
	private static final int THUMB_HEIGHT = 27;

	private int x;
	private int y;

	private int height;
	private int scrollPos = 0;
	private int maxScroll = 0;
	private boolean dragging = false;
	private double dragMouseStart = 0;
	private int dragThumbStart = 0;

	public void setBounds(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.height = height;
	}

	public void setMaxScroll(int max) {
		this.maxScroll = Math.max(0, max);
		this.scrollPos = Mth.clamp(this.scrollPos, 0, this.maxScroll);
	}

	public int getScroll() {
		return scrollPos;
	}

	public int getMaxScroll() {
		return maxScroll;
	}

	public boolean isVisible() {
		return maxScroll > 0;
	}

	public int getWidth() {
		return WIDTH;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (!isVisible()) return;
		guiGraphics.blitSprite(SCROLL_BAR_SLOT, x, y, WIDTH, height);
		int thumbY = thumbY();
		boolean hovered = mouseX >= x && mouseX < x + WIDTH && mouseY >= thumbY && mouseY < thumbY + THUMB_HEIGHT;
		guiGraphics.blitSprite((dragging || hovered) ? SCROLL_BAR_PRESSED : SCROLL_BAR, x + 1, thumbY, WIDTH - 2, THUMB_HEIGHT);
	}

	public boolean mouseClicked(double mouseX, double mouseY) {
		if (!isVisible()) return false;
		int thumbY = thumbY();
		if (mouseX >= x && mouseX < x + WIDTH && mouseY >= thumbY && mouseY < thumbY + THUMB_HEIGHT) {
			dragging = true;
			dragThumbStart = thumbY;
			dragMouseStart = mouseY;
			return true;
		}
		return false;
	}

	public boolean mouseDragged(double mouseY) {
		if (!dragging) return false;
		int range = height - THUMB_HEIGHT;
		int newThumb = Mth.clamp(dragThumbStart + (int) (mouseY - dragMouseStart), y, y + range);
		scrollPos = (int) (((newThumb - y) / (float) range) * maxScroll);
		return true;
	}

	public boolean mouseScrolled(double vertical, int step) {
		if (!isVisible()) return false;
		scrollPos = Mth.clamp(scrollPos - (int) vertical * step, 0, maxScroll);
		return true;
	}

	public void release() {
		dragging = false;
	}

	private int thumbY() {
		int range = height - THUMB_HEIGHT;
		if (maxScroll <= 0) return y;
		return y + Math.round(range * (scrollPos / (float) maxScroll));
	}
}
