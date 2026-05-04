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
	private static final int COLOUR_BACKGROUND_TOPBAR = 0xFF252535;
	private static final int COLOUR_BACKGROUND_CHIP = 0xFF2AA3C;
	private static final int COLOUR_BACKGROUND_CHIP_SELECTED = 0xFF2E2B50;
	private static final int COLOUR_BACKGROUNF_CHIP_LOCK = 0xFF1C1C28;

	private static final int COLOUR_BORDER = 0xFF3A3A50;
	private static final int COLOUR_BORDER_SELECTED = 0xFF7F77DD;
	private static final int COLOUR_BORDER_WARN = 0xFFF0997B;

	private static final int COLOUR_TEXT_PRIMARY = 0xFFE8E8F0;
	private static final int COLOUR_TEXT_SECONDARY = 0xFF9090A8;
	private static final int COLOUR_EXT_TERTIARY = 0xFF5A5A70;
	private static final int COLOUR_TEXT_WARN = 0xFFF0997B;

	private static final int COLOUR_TAB_BACKGROUND = 0xFF1E1E2A;
	private static final int COLOUR_TAB_BACKGROUND_ACTIVE = 0XFF2E2B50;
	private static final int COLOUR_TAB_BORDER = 0XFF3A3A50;

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

		renderPanel(guiGraphics);
		renderTopBar(guiGraphics, mouseX, mouseY);
		renderLeftPanel(guiGraphics, mouseX, mouseY);
		renderDetailPanel(guiGraphics, mouseX, mouseY);
		renderBottomBar(guiGraphics);

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

	private void renderTopBar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int x = originX;
		int y = originY;

		guiGraphics.fill(x, y, x + SCREEN_W, y + TOPBAR_H, COLOUR_BACKGROUND_TOPBAR);
		guiGraphics.fill(x, y + TOPBAR_H - 1, x + SCREEN_W, y + TOPBAR_H, COLOUR_BORDER);

		guiGraphics.drawString(
				font,
				Component.translatable("pathed.gui.attributes.title"),
				x + 8,
				y + (TOPBAR_H - font.lineHeight) / 2,
				COLOUR_TEXT_PRIMARY,
				false
		);

		renderTabButton(
				guiGraphics,
				x + SCREEN_W - 4 - 82 - 4 - 72,
				y + 4,
				82,
				TOPBAR_H - 8,
				"pathed.gui.attributes.tab.mine",
				!showAllAttributes,
				mouseX,
				mouseY
		);

		renderTabButton(guiGraphics,
				x + SCREEN_W - 4 - 82,
				y + 4,
				82,
				TOPBAR_H - 8,
				"pathed.gui.attributes.tab.all",
				showAllAttributes,
				mouseX,
				mouseY
		);
	}

	private void renderBottomBar(GuiGraphics guiGraphics) {
		int x = originX;
		int y = originY + SCREEN_H - BOTTOMBAR_H;

		guiGraphics.fill(x, y, x + SCREEN_W, y + BOTTOMBAR_H, COLOUR_BACKGROUND_TOPBAR);
		guiGraphics.fill(x, y, x + SCREEN_W, y + 1, COLOUR_BORDER);
	}

	private void renderLeftPanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int x = originX;
		int y = originY + TOPBAR_H;
		int listH = SCREEN_H - TOPBAR_H - BOTTOMBAR_H;

		guiGraphics.fill(x, y, x + LEFT_PANEL_W, y + listH, COLOUR_BACKGROUND_PRIMARY);
		guiGraphics.fill(x + LEFT_PANEL_W - 1, y, x + LEFT_PANEL_W, y + listH, COLOUR_BORDER);
	}

	private void renderDetailPanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int x = originX + LEFT_PANEL_W;
		int y = originY + TOPBAR_H;
		int w = SCREEN_W - LEFT_PANEL_W;
		int h = SCREEN_H - TOPBAR_H - BOTTOMBAR_H;

		guiGraphics.fill(x, y, x + w, y + h, COLOUR_BACKGROUND_PRIMARY);
	}

	private void renderTabButton(GuiGraphics guiGraphics, int x, int y, int w, int h, String translationKey, boolean active, int mouseX, int mouseY) {
		boolean hovered = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
		int bg = active ? COLOUR_TAB_BACKGROUND_ACTIVE : (hovered ? COLOUR_BACKGROUND_CHIP : COLOUR_TAB_BACKGROUND);
		int border = active ? COLOUR_BORDER_SELECTED : COLOUR_TAB_BORDER;
		int text = active ? COLOUR_TEXT_PRIMARY : COLOUR_TEXT_SECONDARY;

		guiGraphics.fill(x, y, x + w, y + h, bg);
		guiGraphics.fill(x, y, x + w, y + 1, border);
		guiGraphics.fill(x, y + h - 1, x + w, y + h, border);
		guiGraphics.fill(x, y, x + 1, y + h, border);
		guiGraphics.fill(x + w - 1, y, x + w, y + h, border);

		String label = Component.translatable(translationKey).getString();
		guiGraphics.drawString(
				font,
				label,
				x + (w - font.width(label)) / 2,
				y + (h - font.lineHeight) / 2,
				text,
				false
		);
	}
}
