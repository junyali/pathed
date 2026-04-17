package io.github.junyali.pathed.screen.progression;

import io.github.junyali.pathed.data.skill.ClientSkillData;
import io.github.junyali.pathed.data.skill.SkillCategory;
import io.github.junyali.pathed.data.skill.SkillNode;
import io.github.junyali.pathed.screen.progression.components.ConnectionRenderer;
import io.github.junyali.pathed.screen.progression.components.NodeRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public class SkillTreePanel {
	private final ProgressionScreen screen;
	private final int left;
	private final int top;
	private final int width;
	private final int height;
	private final int contentLeft;
	private final int contentTop;
	private final int contentWidth;
	private final int contentHeight;

	private boolean isDragging = false;
	private double scrollX = 0;
	private double scrollY = 0;

	public SkillTreePanel(ProgressionScreen screen, int left, int top, int width, int height) {
		this.screen = screen;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.contentLeft = left + ProgressionScreen.FRAME_BORDER;
		this.contentTop = top + ProgressionScreen.FRAME_BORDER;
		this.contentWidth = width - ProgressionScreen.FRAME_BORDER * 2;
		this.contentHeight = height - ProgressionScreen.FRAME_BORDER * 2;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.contentLeft, this.contentTop, 0);

		guiGraphics.enableScissor(
				this.contentLeft,
				this.contentTop,
				this.contentLeft + this.contentWidth,
				this.contentTop + this.contentHeight
		);

		this.renderBackground(guiGraphics);

		guiGraphics.pose().translate(scrollX, scrollY, 0);
		this.renderNodes(guiGraphics);

		guiGraphics.disableScissor();
		guiGraphics.pose().popPose();

		ProgressionRenderer.renderBorder(guiGraphics, this.left, this.top, this.width, this.height);
	}

	private void renderBackground(GuiGraphics guiGraphics) {
		ResourceLocation texture = this.getCurrentCategoryBackground();
		int tileSize = 32;
		for (int x = 0; x < this.contentWidth; x += tileSize) {
			for (int y = 0; y < this.contentHeight; y += tileSize) {
				guiGraphics.blit(texture, x, y, 0, 0, tileSize, tileSize, tileSize, tileSize);
			}
		}
	}

	private ResourceLocation getCurrentCategoryBackground() {
		if (this.screen.selectedCategory.isEmpty()) {
			return ResourceLocation.withDefaultNamespace("textures/block/dirt.png");
		}

		SkillCategory category = ClientSkillData.getCategories().get(ResourceLocation.parse(this.screen.selectedCategory));
		if (category != null && category.getBackground() != null) {
			return category.getBackground();
		}

		return ResourceLocation.withDefaultNamespace("textures/block/dirt.png");
	}

	private void renderNodes(GuiGraphics guiGraphics) {
		if (this.screen.selectedCategory.isEmpty()) return;

		SkillCategory category = ClientSkillData.getCategories().get(ResourceLocation.parse(this.screen.selectedCategory));
		if (category == null) return;

		int centreX = this.contentWidth / 2;
		int centreY = this.contentHeight / 2;

		Map<ResourceLocation, SkillNode> nodeMap = new LinkedHashMap<>();
		for (SkillNode node : category.getNodes()) {
			nodeMap.put(node.id(), node);
		}

		for (SkillNode node : category.getNodes()) {
			ConnectionRenderer.render(guiGraphics, node, nodeMap, centreX, centreY);
		}

		for (SkillNode node : category.getNodes()) {
			NodeRenderer.render(guiGraphics, node, centreX, centreY, false);
		}
	}

	public boolean isInArea(double mouseX, double mouseY) {
		return mouseX >= this.contentLeft && mouseX <= this.contentLeft + this.contentWidth && mouseY >= this.contentTop && mouseY <= this.contentTop + this.contentHeight;
	}

	public void startDragging() {
		this.isDragging = true;
	}

	public void stopDragging() {
		this.isDragging = false;
	}

	public boolean handleDrag(double deltaX, double deltaY) {
		if (!this.isDragging) return false;
		this.scrollX += deltaX;
		this.scrollY += deltaY;
		return true;
	}

	public void resetScroll() {
		this.scrollX = 0;
		this.scrollY = 0;
	}
}
