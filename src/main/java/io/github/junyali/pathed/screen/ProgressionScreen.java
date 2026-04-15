package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.Pathed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProgressionScreen extends Screen {
	private static final int CATEGORY_PANEL_WIDTH = 100;
	private static final int CATEGORY_BUTTON_HEIGHT = 20;
	private static final int CATEGORY_BUTTON_SPACING = 0;
	private static final int CATEGORY_PANEL_PADDING = 8;

	private static final int FRAME_BORDER = 9;
	private static final int FRAME_TEX_WIDTH = 176;
	private static final int FRAME_TEX_HEIGHT = 182;

	private static final ResourceLocation FRAME_TEXTURE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/border.png");
	private static final ResourceLocation PANEL_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/background");
	private static final ResourceLocation PANEL_BORDER = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/border");
	private static final ResourceLocation SCROLL_BAR = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar");
	private static final ResourceLocation SCROLL_BAR_PRESSED = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar/pressed");
	private static final ResourceLocation SCROLL_BAR_SLOT = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar/slot");

	private static final int COLOUR_CATEGORY_BG = 0xCC000000;
	private static final int COLOUR_CATEGORY_BUTTON = 0xFF2A2A2A;
	private static final int COLOUR_CATEGORY_BUTTON_HOVER = 0xFFA3A3A3;
	private static final int COLOUR_CATEGORY_BUTTON_SELECTED = 0xFF4A4A4A;
	private static final int COLOUR_TEXT = 0xFFFFFFFF;

	private final boolean showDirtBackground;
	private final List<CategoryButton> categoryButtons = new ArrayList<>();

	private int panelHeight;
	private int categoryPanelLeft;
	private int categoryPanelTop;
	private int skillTreeLeft;
	private int skillTreeTop;
	private int skillTreeWidth;
	private int skillTreeHeight;
	private int contentLeft;
	private int contentTop;
	private int contentWidth;
	private int contentHeight;

	private boolean isDragging = false;
	private double scrollX = 0;
	private double scrollY = 0;
	private int categoryScrollPos = 0;
	private int categoryMaxScroll = 0;
	private boolean categoryDragScrolling = false;
	private double categoryMouseDragStart = 0;
	private int categoryScrollDragStart = 0;

	private String selectedCategory = "";

	public ProgressionScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.progression.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		super.init();

		this.panelHeight = this.height - 20;

		this.categoryPanelLeft = 10;
		this.categoryPanelTop = 10;

		this.skillTreeLeft = this.categoryPanelLeft + CATEGORY_PANEL_WIDTH + 10;
		this.skillTreeTop = 10;
		this.skillTreeWidth = this.width - this.skillTreeLeft - 10;
		this.skillTreeHeight = this.height - 20;

		this.contentLeft = this.skillTreeLeft + FRAME_BORDER;
		this.contentTop = this.skillTreeTop + FRAME_BORDER;
		this.contentWidth = this.skillTreeWidth - FRAME_BORDER * 2;
		this.contentHeight = this.skillTreeHeight - FRAME_BORDER * 2;

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

		int totalContentHeight = categories.length * (CATEGORY_BUTTON_HEIGHT + CATEGORY_BUTTON_SPACING) - CATEGORY_BUTTON_SPACING;
		int innerHeight = panelHeight - FRAME_BORDER * 2 - CATEGORY_PANEL_PADDING * 2 - this.font.lineHeight - CATEGORY_BUTTON_SPACING;
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
		this.repositionCategoryButtons();
		this.renderSkillTreeArea(guiGraphics, mouseX, mouseY, delta);
		this.renderCategoryPanel(guiGraphics, mouseX, mouseY);
		super.render(guiGraphics, mouseX, mouseY, delta);
	}

	private void renderCategoryPanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int panelHeight = this.height - 20;
		int innerLeft = this.categoryPanelLeft + FRAME_BORDER;
		int innerTop = this.categoryPanelTop + FRAME_BORDER;
		int innerWidth = CATEGORY_PANEL_WIDTH - FRAME_BORDER * 2;
		int innerHeight = panelHeight - FRAME_BORDER * 2;
		guiGraphics.blitSprite(PANEL_BACKGROUND, innerLeft, innerTop, -4, innerWidth, innerHeight);
		guiGraphics.enableScissor(innerLeft, innerTop, innerLeft + innerWidth, innerTop + innerHeight);
		guiGraphics.disableScissor();
		guiGraphics.blitSprite(PANEL_BORDER, this.categoryPanelLeft, this.categoryPanelTop, 2, CATEGORY_PANEL_WIDTH, panelHeight);

		Component title = Component.translatable("pathed.gui.progression.categories");
		guiGraphics.drawCenteredString(
				this.font,
				title,
				this.categoryPanelLeft + CATEGORY_PANEL_WIDTH / 2,
				this.categoryPanelTop + FRAME_BORDER + 4,
				COLOUR_TEXT
		);

		this.renderCategoryScrollbar(guiGraphics, mouseX, mouseY);
	}

	private void renderCategoryScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (this.categoryMaxScroll <= 0) return;

		int slotX = this.categoryPanelLeft + CATEGORY_PANEL_WIDTH - FRAME_BORDER - 10;
		int slotY = this.categoryPanelTop + FRAME_BORDER + this.font.lineHeight + CATEGORY_BUTTON_SPACING;
		int slotHeight = (this.height - 20) - FRAME_BORDER * 2 - this.font.lineHeight - CATEGORY_BUTTON_SPACING;

		guiGraphics.blitSprite(SCROLL_BAR_SLOT, slotX, slotY, 8, slotHeight);

		int thumbHeight = 27;
		int scrollRange = slotHeight - thumbHeight;
		int thumbY = slotY + (int) (scrollRange * (this.categoryScrollPos / (float) this.categoryMaxScroll));

		boolean hovered = mouseX >= slotX && mouseX < slotX + 8 && mouseY >= thumbY && mouseY < thumbY + thumbHeight;
		ResourceLocation texture = (this.categoryDragScrolling || hovered) ? SCROLL_BAR_PRESSED : SCROLL_BAR;
		guiGraphics.blitSprite(texture, slotX + 1, thumbY, 6, thumbHeight);
	}

	private void renderSkillTreeArea(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.contentLeft, this.contentTop, 0);

		guiGraphics.enableScissor(
				this.contentLeft,
				this.contentTop,
				this.contentLeft + this.contentWidth,
				this.contentTop + this.contentHeight
		);

		this.renderDirtBackground(guiGraphics);

		guiGraphics.pose().translate(scrollX, scrollY, 0);
		this.renderElement(guiGraphics);

		guiGraphics.disableScissor();
		guiGraphics.pose().popPose();

		this.renderFrame(guiGraphics);
	}

	private void renderDirtBackground(GuiGraphics guiGraphics) {
		ResourceLocation dirtTexture = ResourceLocation.withDefaultNamespace("textures/block/dirt.png");
		int tileSize = 32;
		for (int x = 0; x < this.contentWidth; x += tileSize) {
			for (int y = 0; y < this.contentHeight; y += tileSize) {
				guiGraphics.blit(dirtTexture, x, y, 0, 0, tileSize, tileSize, tileSize, tileSize);
			}
		}
	}

	private void renderFrame(GuiGraphics guiGraphics) {
		// warning: some very complex maths here D:
		int x = this.skillTreeLeft;
		int y = this.skillTreeTop;
		int w = this.skillTreeWidth;
		int h = this.skillTreeHeight;
		int b = FRAME_BORDER;
		int tw = FRAME_TEX_WIDTH;
		int th = FRAME_TEX_HEIGHT;
		int iw = tw - b * 2;
		int ih = th - b * 2;

		guiGraphics.blit(FRAME_TEXTURE, x, y, b, b, 0, 0, b, b, tw, th);
		guiGraphics.blit(FRAME_TEXTURE, x + w - b, y, b, b, tw - b, 0, b, b, tw, th);
		guiGraphics.blit(FRAME_TEXTURE, x, y + h - b, b, b, 0, th - b, b, b, tw, th);
		guiGraphics.blit(FRAME_TEXTURE, x + w - b, y + h - b, b, b, tw - b, th - b, b, b, tw, th);

		guiGraphics.blit(FRAME_TEXTURE, x + b, y, w - b * 2, b, b, 0, iw, b, tw, th);
		guiGraphics.blit(FRAME_TEXTURE, x + b, y + h - b, w - b * 2, b, b, th - b, iw, b, tw, th);
		guiGraphics.blit(FRAME_TEXTURE, x, y + b, b, h - b * 2, 0, b, b, ih, tw, th);
		guiGraphics.blit(FRAME_TEXTURE, x + w - b, y + b, b, h - b * 2, tw - b, b, b, ih, tw, th);
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
		if (button == 0 && this.categoryMaxScroll > 0) {
			int slotX = this.categoryPanelLeft + CATEGORY_PANEL_WIDTH - FRAME_BORDER - 10;
			int slotY = this.categoryPanelTop + FRAME_BORDER + this.font.lineHeight + CATEGORY_BUTTON_SPACING;
			int slotHeight = this.panelHeight - FRAME_BORDER * 2 - this.font.lineHeight - CATEGORY_BUTTON_SPACING;
			int thumbHeight = 27;
			int scrollRange = slotHeight - thumbHeight;
			int thumbY = slotY + (int) (scrollRange * (this.categoryScrollPos / (float) this.categoryMaxScroll));

			if (mouseX >= slotX && mouseX < slotX + 8 && mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
				this.categoryDragScrolling = true;
				this.categoryScrollDragStart = thumbY;
				this.categoryMouseDragStart = mouseY;
				return true;
			}
		}
		if (button == 0 && isInSkillTreeArea(mouseX, mouseY)) {
			this.isDragging = true;
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.isDragging = false;
			this.categoryDragScrolling = false;
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.categoryDragScrolling) {
			int slotY = this.categoryPanelTop + FRAME_BORDER + this.font.lineHeight + CATEGORY_BUTTON_SPACING;
			int slotHeight = this.panelHeight - FRAME_BORDER * 2 - this.font.lineHeight - CATEGORY_BUTTON_SPACING;
			int thumbHeight = 27;
			int scrollRange = slotHeight - thumbHeight;
			int delta = (int) (mouseY - this.categoryMouseDragStart);
			int newThumbY = Mth.clamp(this.categoryScrollDragStart + delta, slotY, slotY + scrollRange);
			float part = (newThumbY - slotY) / (float) scrollRange;
			this.categoryScrollPos = (int) (part * this.categoryMaxScroll);
			return true;
		}
		if (this.isDragging) {
			this.scrollX += deltaX;
			this.scrollY += deltaY;
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	private boolean isInSkillTreeArea(double mouseX, double mouseY) {
		return mouseX >= this.contentLeft && mouseX <= this.contentLeft + this.contentWidth && mouseY >= this.contentTop && mouseY <= this.contentTop + this.contentHeight;
	}

	private boolean isInCategoryPanel(double mouseX, double mouseY) {
		int innerLeft = this.categoryPanelLeft + FRAME_BORDER;
		int innerTop = this.categoryPanelTop + FRAME_BORDER;
		int innerWidth = CATEGORY_PANEL_WIDTH - FRAME_BORDER * 2;
		int innerHeight = this.panelHeight - FRAME_BORDER * 2;
		return mouseX >= innerLeft && mouseX < innerLeft + innerWidth && mouseY >= innerTop && mouseY < innerTop + innerHeight;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
		if (isInCategoryPanel(mouseX, mouseY) && this.categoryMaxScroll > 0) {
			this.categoryScrollPos = Mth.clamp(this.categoryScrollPos - (int) vertical * 4, 0, this.categoryMaxScroll);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
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
			guiGraphics.pose().pushPose();
			guiGraphics.pose().scale(0.8f, 0.8f, 1.0f);
			guiGraphics.drawCenteredString(
					ProgressionScreen.this.font,
					this.getMessage(),
					(int) ((this.getX() + this.width / 2) / 0.8f),
					(int) ((this.getY() + (this.height - 8) / 2) / 0.8f),
					COLOUR_TEXT
			);
			guiGraphics.pose().popPose();
		}
	}

	private void repositionCategoryButtons() {
		int baseY = this.categoryPanelTop + FRAME_BORDER + CATEGORY_PANEL_PADDING + this.font.lineHeight + CATEGORY_BUTTON_SPACING;
		for (int i = 0; i < this.categoryButtons.size(); i++) {
			int y = baseY + i * (CATEGORY_BUTTON_HEIGHT + CATEGORY_BUTTON_SPACING) - this.categoryScrollPos;
			this.categoryButtons.get(i).setY(y);
		}
	}
}
