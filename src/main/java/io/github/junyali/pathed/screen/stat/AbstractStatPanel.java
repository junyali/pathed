package io.github.junyali.pathed.screen.stat;

import io.github.junyali.pathed.screen.common.PanelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractStatPanel {
	public static final int PADDING = 6;
	public static final int COLOUR_PANEL_BACKGROUND = 0xFF8B8B8B;
	public static final int COLOUR_ACCENT = 0xFFFFD267;
	public static final int COLOUR_TEXT = 0xFFFFFFFF;
	public static final int COLOUR_TEXT_DIM = 0xFFB0B0B0;

	protected int panelX;
	protected int panelY;
	protected int panelWidth;
	protected int panelHeight;
	protected Font font;
	protected final StatHeader header;

	protected AbstractStatPanel(int x, int y, int width, int height) {
		this.panelX = x;
		this.panelY = y;
		this.panelWidth = width;
		this.panelHeight = height;
		this.font = Minecraft.getInstance().font;
		this.header = new StatHeader(font);
	}

	public abstract Component getTitle();

	protected ItemStack headerIcon() {
		return ItemStack.EMPTY;
	}

	public void init() {}

	public void resize(int x, int y, int width, int height) {
		this.panelX = x;
		this.panelY = y;
		this.panelWidth = width;
		this.panelHeight = height;
	}

	public abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta);

	protected void renderFrame(GuiGraphics guiGraphics) {
		int b = PanelRenderer.FRAME_BORDER;
		guiGraphics.fill(panelX + b, panelY + b, panelX + panelWidth - b, panelY + panelHeight - b, COLOUR_PANEL_BACKGROUND);
		PanelRenderer.renderBorder(guiGraphics, panelX, panelY, panelWidth, panelHeight);
		header.render(guiGraphics, panelX, panelY, panelWidth, getTitle(), headerIcon());
	}

	protected int contentTop() {
		return StatHeader.bottomY(panelY);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return false;
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return false;
	}

	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		return false;
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double vertical) {
		return false;
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	public boolean charTyped(char c, int modifiers) {
		return false;
	}

	protected static boolean isHovered(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
	}

	protected static boolean isHovered(double mouseX, double mouseY, int x, int y, int w, int h) {
		return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
	}
}
