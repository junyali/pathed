package io.github.junyali.pathed.screen.stat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class AbstractStatPanel {
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

	public void init() {}

	public void resize(int x, int y, int width, int height) {
		this.panelX = x;
		this.panelY = y;
		this.panelWidth = width;
		this.panelHeight = height;
	}

	public abstract void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta);

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
}
