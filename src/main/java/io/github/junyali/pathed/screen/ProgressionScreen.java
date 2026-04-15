package io.github.junyali.pathed.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

		int y = this.categoryPanelTop + CATEGORY_PANEL_PADDING + this.font.lineHeight + CATEGORY_BUTTON_SPACING;

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
		this.renderSkillTreeArea(guiGraphics, mouseX, mouseY, delta);
		this.renderCategoryPanel(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, delta);
	}

	private void renderCategoryPanel(GuiGraphics guiGraphics) {
		int panelHeight = this.height - 20;
		guiGraphics.fill(
				this.categoryPanelLeft,
				this.categoryPanelTop,
				this.categoryPanelLeft + CATEGORY_PANEL_WIDTH,
				this.categoryPanelTop + panelHeight,
				COLOUR_CATEGORY_BG
		);

		Component title = Component.translatable("pathed.gui.progression.categories");
		guiGraphics.drawCenteredString(
				this.font,
				title,
				this.categoryPanelLeft + CATEGORY_PANEL_WIDTH / 2,
				this.categoryPanelTop + CATEGORY_PANEL_PADDING,
				COLOUR_TEXT
		);
	}

	private void renderSkillTreeArea(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.skillTreeLeft, this.skillTreeTop, 0);

		guiGraphics.enableScissor(
				this.skillTreeLeft,
				this.skillTreeTop,
				this.skillTreeLeft + this.skillTreeWidth,
				this.skillTreeTop + this.skillTreeHeight
		);

		this.renderDirtBackground(guiGraphics);

		guiGraphics.pose().translate(scrollX, scrollY, 0);
		this.renderElement(guiGraphics);
		guiGraphics.disableScissor();
		guiGraphics.pose().popPose();
		guiGraphics.fill(
				this.skillTreeLeft - 1,
				this.skillTreeTop - 1,
				this.skillTreeLeft + this.skillTreeWidth + 1,
				this.skillTreeTop,
				0xFF888888
		);
		guiGraphics.fill(
				this.skillTreeLeft - 1,
				this.skillTreeTop + this.skillTreeHeight,
				this.skillTreeLeft + this.skillTreeWidth + 1,
				this.skillTreeTop + this.skillTreeHeight + 1,
				0xFF888888
		);guiGraphics.fill(
				this.skillTreeLeft - 1,
				this.skillTreeTop,
				this.skillTreeLeft,
				this.skillTreeTop + this.skillTreeHeight,
				0xFF888888
		);guiGraphics.fill(
				this.skillTreeLeft + this.skillTreeWidth,
				this.skillTreeTop,
				this.skillTreeLeft + this.skillTreeWidth + 1,
				this.skillTreeTop + this.skillTreeHeight,
				0xFF888888
		);
	}

	private void renderDirtBackground(GuiGraphics guiGraphics) {
		ResourceLocation dirtTexture = ResourceLocation.withDefaultNamespace("textures/block/dirt.png");
		int tileSize = 32;
		for (int x = 0; x < this.skillTreeWidth; x += tileSize) {
			for (int y = 0; y < this.skillTreeHeight; y += tileSize) {
				guiGraphics.blit(dirtTexture, x, y, 0, 0, tileSize, tileSize, tileSize, tileSize);
			}
		}
	}

	private void renderElement(GuiGraphics guiGraphics) {
		// drag testing
		int size = 50;
		int x = -size / 2;
		int y = -size / 2;
		guiGraphics.fill(x, y, x + size, y + size, 0xFFFF5555);
		guiGraphics.fill(x + 2, y + 2, x + size - 2, y + size - 2, 0xFF55FF55);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && isInSkillTreeArea(mouseX, mouseY)) {
			this.isDragging = true;
			this.dragStartX = mouseX;
			this.dragStartY = mouseY;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.isDragging = false;
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.isDragging) {
			double dx = mouseX - this.dragStartX;
			double dy = mouseY - this.dragStartY;

			this.scrollX += dx;
			this.scrollY += dy;

			this.dragStartX = mouseX;
			this.dragStartY = mouseY;
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	private boolean isInSkillTreeArea(double mouseX, double mouseY) {
		return mouseX >= this.skillTreeLeft && mouseX <= this.skillTreeLeft + this.skillTreeWidth && mouseY >= this.skillTreeTop && mouseY <= this.skillTreeTop + this.skillTreeHeight;
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
