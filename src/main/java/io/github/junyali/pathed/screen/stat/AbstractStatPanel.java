package io.github.junyali.pathed.screen.stat;

import io.github.junyali.pathed.screen.common.PanelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractStatPanel {
	public static final int PADDING = 6;
	public static final int HEADER_HEIGHT = 6;

	public static final int COLOUR_PANEL_BACKGROUND = 0xFF8B8B8B;
	public static final int COLOUR_HEADER_BACKGROUND_TOP = 0xFF2B2B2B;
	public static final int COLOUR_HEADER_BACKGROUND_BOTTOM = 0xFF1C1C1C;
	public static final int COLOUR_HEADER_HIGH = 0xFF6E6E6E;
	public static final int COLOUR_HEADER_LOW = 0xFF000000;
	public static final int COLOUR_ACCENT = 0xFFFFD267;
	public static final int COLOUR_TEXT = 0xFFFFFFFF;
	public static final int COLOUR_TEXT_DIM = 0xFFB0B0B0;

	protected int panelX;
	protected int panelY;
	protected int panelWidth;
	protected int panelHeight;

	protected Font font;

	protected AbstractStatPanel(int x, int y, int width, int height) {
		this.panelX = x;
		this.panelY = y;
		this.panelWidth = width;
		this.panelHeight = height;
		this.font = Minecraft.getInstance().font;
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

	protected void renderBase(GuiGraphics guiGraphics) {
		int b = PanelRenderer.FRAME_BORDER;
		guiGraphics.fill(panelX + b, panelY + b, panelX + panelWidth - b, panelY + panelHeight - b, COLOUR_PANEL_BACKGROUND);
		PanelRenderer.renderBorder(guiGraphics, panelX, panelY, panelWidth, panelHeight);

		int hX = panelX + b;
		int hY = panelY + b;
		int hW = panelWidth - b * 2;
		int hH = HEADER_HEIGHT;

		guiGraphics.fillGradient(hX, hY, hX + hW, hY + hH, COLOUR_HEADER_BACKGROUND_TOP, COLOUR_HEADER_BACKGROUND_BOTTOM);
		guiGraphics.fill(hX, hY, hX + hW, hY + 1, COLOUR_HEADER_HIGH);
		guiGraphics.fill(hX, hY + hH - 1, hX + hW, hY + hH, COLOUR_HEADER_LOW);
		guiGraphics.fill(hX, hY + hH, hX + hW, hY + hH + 1, COLOUR_ACCENT);

		int textY = hY + (hH - font.lineHeight) / 2;
		int textX = hX + PADDING;
		ItemStack icon = headerIcon();
		if (!icon.isEmpty()) {
			guiGraphics.renderItem(icon, hX + PADDING, hY + (hH - 16) / 2);
			textX += 18 + 4;
		}
		guiGraphics.drawString(font, getTitle(), textX, textY, COLOUR_TEXT, true);
	}

	protected int contentTop() {
		return panelY + PanelRenderer.FRAME_BORDER + HEADER_HEIGHT + 1;
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
