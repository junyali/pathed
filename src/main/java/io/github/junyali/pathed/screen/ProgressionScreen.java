package io.github.junyali.pathed.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProgressionScreen extends Screen {
	private static final int CATEGORY_PANEL_WIDTH = 120;
	private static final int CATEGORY_BUTTON_HEIGHT = 30;
	private static final int CATEGORY_BUTTON_SPACING = 4;
	private static final int CATEGORY_PANEL_PADDING = 8;

	private static final int COLOUR_CATEGORY_BG = 0xCC000000;
	private static final int COLOUR_CATEGORY_BUTTON = 0xFF2A2A2A;
	private static final int COLOUR_CATEGORY_BUTTON_HOVER = 0xFFA3A3A3;
	private static final int COLOUR_CATEGORY_BUTTON_SELECTED = 0xFF4A4A4A;
	private static final int COLOUR_TEXT = 0xFFFFFFFF;

	private final boolean showDirtBackground;
	private final List<CategoryButton> categoryButtons = new ArrayList<>();

	private int categoryPanelLeft;
	private int categoryPanelTop;
	private int skillTreeLeft;
	private int skillTreeTop;
	private int skillTreeWidth;
	private int skillTreeHeight;

	private boolean isDragging = false;
	private double dragStartX;
	private double dragStartY;
	private double scrollX = 0;
	private double scrollY = 0;

	private String selectedCategory = "";

	public ProgressionScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.progression.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		super.init();

		this.categoryPanelLeft = 10;
		this.categoryPanelTop = 10;

		this.skillTreeLeft = this.categoryPanelLeft + CATEGORY_PANEL_WIDTH + 10;
		this.skillTreeTop = 10;
		this.skillTreeWidth = this.width - this.skillTreeLeft - 10;
		this.skillTreeHeight = this.height - 20;

		this.setupCategoryButtons();
	}

	private void setupCategoryButtons() {
		this.categoryButtons.clear();

		String[] categories = {"Meow", "Mrow", "Mrrp"};
		String[] categoryIds = {"meow", "mrow", "mrrp"};

		int y = this.categoryPanelTop + CATEGORY_PANEL_PADDING;

		for (int i = 0; i < categories.length; i++) {
			final String categoryId = categoryIds[i];
			CategoryButton button = new CategoryButton(
					this.categoryPanelLeft + CATEGORY_PANEL_PADDING,
					y,
					CATEGORY_PANEL_WIDTH - CATEGORY_PANEL_PADDING * 2,
					CATEGORY_BUTTON_HEIGHT,
					Component.literal(categories[i]),
					categoryId,
					btn -> this.selectedCategory = categoryId
			);
			this.categoryButtons.add(button);
			this.addRenderableWidget(button);
			y += CATEGORY_BUTTON_HEIGHT + CATEGORY_BUTTON_SPACING;
		}
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

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		super.render(guiGraphics, mouseX, mouseY, delta);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private class CategoryButton extends Button {
		private final String categoryId;

		public CategoryButton(int x, int y, int width, int height, Component message, String categoryId, OnPress onPress) {
			super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
			this.categoryId = categoryId;
		}

		@Override
		public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
			boolean isSelected = this.categoryId.equals(selectedCategory);
			boolean isHovered = this.isHovered();

			int colour = isSelected ? COLOUR_CATEGORY_BUTTON_SELECTED : (isHovered ? COLOUR_CATEGORY_BUTTON_HOVER : COLOUR_CATEGORY_BUTTON);

			guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, colour);
			int borderColour = isSelected ? 0xFF888888 : 0xFF444444;
			guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, borderColour);
			guiGraphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, borderColour);
			guiGraphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, borderColour);
			guiGraphics.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, borderColour);
			guiGraphics.drawCenteredString(
					ProgressionScreen.this.font,
					this.getMessage(),
					this.getX() + this.width / 2,
					this.getY() + (this.height - 8) / 2,
					COLOUR_TEXT
			);
		}
	}
}
