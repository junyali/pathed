package io.github.junyali.pathed.screen.progression;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.data.skill.ClientSkillData;
import io.github.junyali.pathed.data.skill.SkillCategory;
import io.github.junyali.pathed.registry.PathedAttachments;
import io.github.junyali.pathed.screen.common.ScrollBar;
import io.github.junyali.pathed.screen.progression.components.CategoryButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryPanel {
	static final int PANEL_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_SPACING = 0;
	private static final int PANEL_PADDING = 8;
	private static final int NAME_PLATE_WIDTH = 100;
	private static final int NAME_PLATE_HEIGHT = 26;
	private static final int NAME_PLATE_ICON_SIZE = 26;

	private static final int COLOUR_CATEGORY_BG = 0XFF555555;

	private final ProgressionScreen screen;
	private final int left;
	private final int top;
	private final int panelHeight;
	private final List<CategoryButton> buttons = new ArrayList<>();

	private final ScrollBar scrollBar = new ScrollBar();

	public CategoryPanel(ProgressionScreen screen, int left, int top, int panelHeight) {
		this.screen = screen;
		this.left = left;
		this.top = top;
		this.panelHeight = panelHeight;
	}

	public List<CategoryButton> init() {
		this.buttons.clear();

		Map<ResourceLocation, SkillCategory> categories = ClientSkillData.getCategories();
		ResourceLocation playerPath = null;
		if (this.screen.getMinecraft().player != null) {
			PathAttachment pathAttachment = this.screen.getMinecraft().player.getData(PathedAttachments.PATH_ATTACHMENT);
			if (pathAttachment != null && pathAttachment.getPath() != null) {
				playerPath = pathAttachment.getPath().getId();
			}
		}

		List<Map.Entry<ResourceLocation, SkillCategory>> filteredCategories = new ArrayList<>();
		for (Map.Entry<ResourceLocation, SkillCategory> entry : categories.entrySet()) {
			SkillCategory cat = entry.getValue();

			if (cat.getPathLocked().isEmpty() || (playerPath != null && cat.getPathLocked().get().equals(playerPath))) {
				filteredCategories.add(entry);
			}
		}

		int totalContentHeight = filteredCategories.size() * BUTTON_HEIGHT;
		int innerHeight = this.panelHeight - ProgressionScreen.FRAME_BORDER * 2;
		this.scrollBar.setMaxScroll(Math.max(0, totalContentHeight - innerHeight));
		this.scrollBar.setBounds(
				this.left + PANEL_WIDTH - ProgressionScreen.FRAME_BORDER - this.scrollBar.getWidth(),
				this.top + ProgressionScreen.FRAME_BORDER,
				innerHeight
		);

		int buttonWidth = PANEL_WIDTH - ProgressionScreen.FRAME_BORDER * 2 - (this.scrollBar.isVisible() ? this.scrollBar.getWidth() : 0);

		int buttonLeft = this.left + ProgressionScreen.FRAME_BORDER;
		int y = this.top + ProgressionScreen.FRAME_BORDER;

		boolean isFirst = true;
		for (Map.Entry<ResourceLocation, SkillCategory> entry : filteredCategories) {
			ResourceLocation catId = entry.getKey();
			SkillCategory cat = entry.getValue();

			String idStr = catId.toString();

			if (isFirst) {
				this.screen.selectedCategory = idStr;
				isFirst = false;
			}

			CategoryButton button = new CategoryButton(
					buttonLeft,
					y,
					buttonWidth,
					BUTTON_HEIGHT,
					Component.translatable(cat.getNameKey()),
					idStr,
					cat.getIconItem(),
					this.screen
			);
			this.buttons.add(button);
			y += BUTTON_HEIGHT + BUTTON_SPACING;
		}
		return this.buttons;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int b = ProgressionScreen.FRAME_BORDER;
		int innerLeft = this.left + b;
		int innerTop = this.top + b;
		int innerWidth = PANEL_WIDTH - b * 2;
		int innerHeight = panelHeight - b * 2;
		guiGraphics.fill(innerLeft, innerTop, innerLeft + innerWidth, innerTop + innerHeight, COLOUR_CATEGORY_BG);
		ProgressionRenderer.renderBorder(guiGraphics, this.left, this.top, PANEL_WIDTH, this.panelHeight);
	}

	public void renderTitlebar(GuiGraphics guiGraphics) {
		int plateX = this.left + (PANEL_WIDTH - NAME_PLATE_WIDTH) / 2;
		int plateY = this.top - NAME_PLATE_HEIGHT / 2 + ProgressionScreen.FRAME_BORDER / 2;
		guiGraphics.blit(ProgressionScreen.NAME_PLATE, plateX, plateY, 0, 0, NAME_PLATE_WIDTH, NAME_PLATE_HEIGHT, NAME_PLATE_WIDTH, NAME_PLATE_HEIGHT);
		guiGraphics.renderItem(new ItemStack(Items.KNOWLEDGE_BOOK), plateX + 4, plateY + 5);
		Component title = Component.translatable("pathed.gui.progression.categories");
		guiGraphics.drawString(
				this.screen.getMinecraft().font,
				title,
				plateX + NAME_PLATE_ICON_SIZE + 4,
				plateY + 5 + (16 - this.screen.getMinecraft().font.lineHeight) / 2,
				ProgressionScreen.COLOUR_TEXT
		);
	}

	public void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		this.scrollBar.render(guiGraphics, mouseX, mouseY);
	}

	public void enableScissor(GuiGraphics guiGraphics) {
		int b = ProgressionScreen.FRAME_BORDER;
		int innerLeft = this.left + b;
		int innerTop = this.top + b;
		int innerWidth = PANEL_WIDTH - b * 2;
		int innerHeight = this.panelHeight - b * 2;
		guiGraphics.enableScissor(innerLeft, innerTop, innerLeft + innerWidth, innerTop + innerHeight);
	}

	public boolean handleScrollbarClick(double mouseX, double mouseY) {
		return this.scrollBar.mouseClicked(mouseX, mouseY);
	}

	public boolean handleDrag(double mouseY, double _ignored) {
		return this.scrollBar.mouseDragged(mouseY);
	}

	public void stopDragScrolling() {
		this.scrollBar.release();
	}

	public boolean handleScroll(double mouseX, double mouseY, double vertical) {
		if (!isInPanel(mouseX, mouseY)) return false;
		return this.scrollBar.mouseScrolled(vertical, 4);
	}

	public void repositionButtons() {
		int baseY = this.top + ProgressionScreen.FRAME_BORDER;
		int scroll = this.scrollBar.getScroll();
		for (int i = 0; i < this.buttons.size(); i++) {
			this.buttons.get(i).setY(baseY + i * (BUTTON_HEIGHT + BUTTON_SPACING) - scroll);
		}
	}

	private boolean isInPanel(double mouseX, double mouseY) {
		int b = ProgressionScreen.FRAME_BORDER;
		int innerLeft = this.left + b;
		int innerTop = this.top + b;
		int innerWidth = PANEL_WIDTH - b * 2;
		int innerHeight = this.panelHeight - b * 2;
		return mouseX >= innerLeft && mouseX < innerLeft + innerWidth && mouseY >= innerTop && mouseY < innerTop + innerHeight;
	}
}
