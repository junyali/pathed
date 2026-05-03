package io.github.junyali.pathed.screen.attribute;

import io.github.junyali.pathed.data.attribute.Attribute;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeScreen extends Screen {
	private static final int SCREEN_W = 340;
	private static final int SCREEN_H = 220;

	private static final int TOPBAR_H = 24;
	private static final int BOTTOMBAR_H = 24;
	private static final int LEFT_PANEL_W = 120;

	private static final int COLOUR_BACKGROUND_PRIMARY = 0xFF1E1E2A;
	private static final int COLOUR_BACKGROUND_SECONDARY = 0xFF16161F;
	private static final int COLOUR_BORDER = 0xFF3A3A50;

	private final Map<String, Integer> pendingLevels = new HashMap<>();
	private final Map<String, Integer> pendingActive = new HashMap<>();

	private List<Attribute> displayedAttributes = new ArrayList<>();

	private boolean showAllAttributes = false;

	@Nullable
	private Attribute selectedAttributes = null;

	private final boolean showDirtBackground;

	private int originX;
	private int originY;

	public AttributeScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.attributes.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		originX = (width - SCREEN_W) / 2;
		originY = (height - SCREEN_H) / 2;

		// populate pending maps

		refreshDisplayedList();
		addNativeButtons();
	}

	private void addNativeButtons() {
		int bY = originY + TOPBAR_H + (SCREEN_H - TOPBAR_H - BOTTOMBAR_H) + (BOTTOMBAR_H - 16) / 2;
		int applyX = originX + SCREEN_W - 4 - 50;
		int backX = applyX - 4 - 50;

		addRenderableWidget(Button.builder(
				Component.translatable("gui.done"),
				btn -> {
					// on apply
				}
		).bounds(applyX, bY, 50, 16).build());
		addRenderableWidget(Button.builder(
				Component.translatable("gui.back"),
				btn -> {
					// go back
				}
		).bounds(backX, bY, 50, 16).build());
	}

	private void refreshDisplayedList() {
		displayedAttributes.clear();
		// do refresh here
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		renderBackground(guiGraphics, mouseX, mouseY, delta);

		super.render(guiGraphics, mouseX, mouseY, delta);
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		if (this.showDirtBackground) {
			super.renderBackground(guiGraphics, mouseX, mouseY, delta);
		} else {
			this.renderTransparentBackground(guiGraphics);
		}
	}

	@Override
	public void renderTransparentBackground(@NotNull GuiGraphics guiGraphics) {
		guiGraphics.fillGradient(0, 0, this.width, this.height, -5, 1678774288, -2112876528);
	}

	private void renderPanel(GuiGraphics guiGraphics) {
		guiGraphics.fill(originX - 1, originY, originX + SCREEN_W + 1, originY + SCREEN_H + 1, COLOUR_BORDER);
		guiGraphics.fill(originX, originY, originX + SCREEN_W, originY + SCREEN_H, COLOUR_BACKGROUND_SECONDARY);
	}
}
