package io.github.junyali.pathed.screen.progression;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.screen.progression.components.CategoryButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ProgressionScreen extends Screen {
	public static final int FRAME_BORDER = 9;
	public static final int FRAME_TEX_WIDTH = 176;
	public static final int FRAME_TEX_HEIGHT = 182;
	public static final int COLOUR_TEXT = 0xFFFFFFFF;

	public static final ResourceLocation FRAME_TEXTURE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/border.png");
	public static final ResourceLocation NAME_PLATE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/title_bar.png");

	private final boolean showDirtBackground;

	private CategoryPanel categoryPanel;
	private SkillTreePanel skillTreePanel;

	public String selectedCategory = "";

	public ProgressionScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.progression.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		super.init();

		int panelHeight = this.height - 20;

		this.categoryPanel = new CategoryPanel(this, 10, 10, panelHeight);
		for (CategoryButton button : this.categoryPanel.init()) {
			this.addRenderableWidget(button);
		}

		int skillTreeLeft = 10 + CategoryPanel.PANEL_WIDTH + 10;
		this.skillTreePanel = new SkillTreePanel(this, skillTreeLeft, 10, this.width - skillTreeLeft - 10, this.height - 20);
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
		this.categoryPanel.repositionButtons();
		this.skillTreePanel.render(guiGraphics, mouseX, mouseY, delta);
		this.categoryPanel.render(guiGraphics, mouseX, mouseY);

		this.categoryPanel.enableScissor(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, delta);
		guiGraphics.flush();
		guiGraphics.disableScissor();

		this.categoryPanel.renderScrollbar(guiGraphics, mouseX, mouseY);
		this.categoryPanel.renderTitlebar(guiGraphics);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && this.categoryPanel.handleScrollbarClick(mouseX, mouseY)) {
			return true;
		}
		if (button == 0 && this.skillTreePanel.isInArea(mouseX, mouseY)) {
			this.skillTreePanel.startDragging();
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			this.skillTreePanel.stopDragging();
			this.categoryPanel.stopDragScrolling();
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.categoryPanel.handleDrag(mouseY, mouseX)) {
			return true;
		}
		if (this.skillTreePanel.handleDrag(deltaX, deltaY)) {
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
		if (this.categoryPanel.handleScroll(mouseX, mouseY, vertical)) {
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public void selectCategory(String categoryId) {
		this.selectedCategory = categoryId;
		this.skillTreePanel.resetScroll();
	}
}
