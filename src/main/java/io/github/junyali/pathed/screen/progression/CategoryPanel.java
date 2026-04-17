package io.github.junyali.pathed.screen.progression;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.data.skill.ClientSkillData;
import io.github.junyali.pathed.data.skill.SkillCategory;
import io.github.junyali.pathed.registry.PathedAttachments;
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

	private static final ResourceLocation SCROLL_BAR = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar");
	private static final ResourceLocation SCROLL_BAR_PRESSED = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar/pressed");
	private static final ResourceLocation SCROLL_BAR_SLOT = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/scroll_bar/slot");

	private final ProgressionScreen screen;
	private final int left;
	private final int top;
	private final int panelHeight;
	private final List<CategoryButton> buttons = new ArrayList<>();

	private int scrollPos = 0;
	private int maxScroll = 0;
	private boolean dragScrolling = false;
	private double mouseDragStart = 0;
	private int scrollDragStart = 0;

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
		this.maxScroll = Math.max(0, totalContentHeight - innerHeight);

		int buttonWidth = PANEL_WIDTH - ProgressionScreen.FRAME_BORDER * 2;
		if (this.maxScroll > 0) {
			buttonWidth -= 8;
		}

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
		guiGraphics.renderItem(new ItemStack(Items.NETHER_STAR), plateX + 4, plateY + 5);
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
		if (this.maxScroll <= 0) return;

		int slotX = this.left + PANEL_WIDTH - ProgressionScreen.FRAME_BORDER - 8;
		int slotY = this.top + ProgressionScreen.FRAME_BORDER;
		int slotHeight = this.panelHeight - ProgressionScreen.FRAME_BORDER * 2;

		guiGraphics.blitSprite(SCROLL_BAR_SLOT, slotX, slotY, 8, slotHeight);

		int thumbHeight = 27;
		int scrollRange = slotHeight - thumbHeight;
		int thumbY = slotY + (int) (scrollRange * (this.scrollPos / (float) this.maxScroll));

		boolean hovered = mouseX >= slotX && mouseX < slotX + 8 && mouseY >= thumbY && mouseY < thumbY + thumbHeight;
		ResourceLocation texture = (this.dragScrolling || hovered) ? SCROLL_BAR_PRESSED : SCROLL_BAR;
		guiGraphics.blitSprite(texture, slotX + 1, thumbY, 6, thumbHeight);
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
		if (this.maxScroll <= 0) return false;

		int slotX = this.left + PANEL_WIDTH - ProgressionScreen.FRAME_BORDER - 10;
		int slotY = this.top + ProgressionScreen.FRAME_BORDER;
		int slotHeight = this.panelHeight - ProgressionScreen.FRAME_BORDER * 2;
		int thumbHeight = 27;
		int scrollRange = slotHeight - thumbHeight;
		int thumbY = slotY + (int) (scrollRange * (this.scrollPos / (float) this.maxScroll));

		if (mouseX >= slotX && mouseX < slotX + 8 && mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
			this.dragScrolling = true;
			this.scrollDragStart = thumbY;
			this.mouseDragStart = mouseY;
			return true;
		}
		return false;
	}

	public boolean handleDrag(double mouseY, double _ignored) {
		if (!this.dragScrolling) return false;

		int slotY = this.top + ProgressionScreen.FRAME_BORDER;
		int slotHeight = this.panelHeight - ProgressionScreen.FRAME_BORDER * 2;
		int thumbHeight = 27;
		int scrollRange = slotHeight - thumbHeight;
		int delta = (int) (mouseY - this.mouseDragStart);
		int newThumbY = Mth.clamp(this.scrollDragStart + delta, slotY, slotY + scrollRange);
		float part = (newThumbY - slotY) / (float) scrollRange;
		this.scrollPos = (int) (part * this.maxScroll);
		return true;
	}

	public void stopDragScrolling() {
		this.dragScrolling = false;
	}

	public boolean handleScroll(double mouseX, double mouseY, double vertical) {
		if (!isInPanel(mouseX, mouseY) || this.maxScroll <= 0) return false;
		this.scrollPos = Mth.clamp(this.scrollPos - (int) vertical * 4, 0, this.maxScroll);
		return true;
	}

	public void repositionButtons() {
		int baseY = this.top + ProgressionScreen.FRAME_BORDER;
		for (int i = 0; i < this.buttons.size(); i++) {
			int y = baseY + i * (BUTTON_HEIGHT + BUTTON_SPACING) - this.scrollPos;
			this.buttons.get(i).setY(y);
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
