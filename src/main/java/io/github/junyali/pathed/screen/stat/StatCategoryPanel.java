package io.github.junyali.pathed.screen.stat;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.screen.common.PanelRenderer;
import io.github.junyali.pathed.screen.common.ScrollBar;
import io.github.junyali.pathed.screen.common.CategoryButton;
import io.github.junyali.pathed.screen.progression.ProgressionScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class StatCategoryPanel {
	public static final int PANEL_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 20;
	private static final int NAME_PLATE_WIDTH = 100;
	private static final int NAME_PLATE_HEIGHT = 26;
	private static final int NAME_PLATE_ICON_SIZE = 26;

	public static final ResourceLocation NAME_PLATE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/title_bar.png");

	private static final int COLOUR_PANEL_BACKGROUND = 0XFF555555;
	private static final int COLOUR_TEXT = 0xFFFFFFFF;

	private final StatsScreen screen;
	private final int left;
	private final int top;
	private final int panelHeight;
	private final List<CategoryButton> buttons = new ArrayList<>();
	private final ScrollBar scrollBar = new ScrollBar();

	public StatCategoryPanel(StatsScreen screen, int left, int top, int panelHeight) {
		this.screen = screen;
		this.left = left;
		this.top = top;
		this.panelHeight = panelHeight;
	}

	public List<CategoryButton> init(List<StatCategory> categories) {
		buttons.clear();

		int innerHeight = panelHeight - PanelRenderer.FRAME_BORDER * 2;
		int totalContentHeight = categories.size() * BUTTON_HEIGHT;
		scrollBar.setMaxScroll(Math.max(0, totalContentHeight - innerHeight));
		scrollBar.setBounds(
				left + PANEL_WIDTH - PanelRenderer.FRAME_BORDER - scrollBar.getWidth(),
				top + PanelRenderer.FRAME_BORDER,
				innerHeight
		);

		int buttonWidth = PANEL_WIDTH - PanelRenderer.FRAME_BORDER * 2 - (scrollBar.isVisible() ? scrollBar.getWidth() : 0);
		int buttonLeft = left + PanelRenderer.FRAME_BORDER;
		int y = top + PanelRenderer.FRAME_BORDER;

		boolean first = true;
		for (StatCategory cat : categories) {
			if (first) {
				screen.selectCategory(cat.id());
				first = false;
			}

			CategoryButton button = new CategoryButton(
					buttonLeft,
					y,
					buttonWidth,
					BUTTON_HEIGHT,
					Component.translatable(cat.nameKey()),
					cat.id(),
					cat.icon(),
					screen
			);
			buttons.add(button);
			y += BUTTON_HEIGHT;
		}
		return buttons;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int b = PanelRenderer.FRAME_BORDER;
		guiGraphics.fill(
				left + b,
				top + b,
				left + PANEL_WIDTH - b,
				top + panelHeight - b,
				COLOUR_PANEL_BACKGROUND
		);
		PanelRenderer.renderBorder(guiGraphics, left, top, PANEL_WIDTH, panelHeight);
	}

	public void renderTitlebar(GuiGraphics guiGraphics) {
		int plateX = left + (PANEL_WIDTH - NAME_PLATE_WIDTH) / 2;
		int plateY = top - NAME_PLATE_HEIGHT / 2 + PanelRenderer.FRAME_BORDER / 2;
		guiGraphics.blit(NAME_PLATE, plateX, plateY, 0, 0, NAME_PLATE_WIDTH, NAME_PLATE_HEIGHT, NAME_PLATE_WIDTH, NAME_PLATE_HEIGHT);
		guiGraphics.renderItem(new ItemStack(Items.COMPASS), plateX + 4, plateY + 5);
		Component title = Component.translatable("pathed.gui.stats.categories");
		guiGraphics.drawString(screen.getMinecraft().font, title, plateX + NAME_PLATE_ICON_SIZE + 4, plateY + 5 + (16 - screen.getMinecraft().font.lineHeight) / 2, COLOUR_TEXT);
	}

	public void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		scrollBar.render(guiGraphics, mouseX, mouseY);
	}

	public void enableScissor(GuiGraphics guiGraphics) {
		int b = PanelRenderer.FRAME_BORDER;
		guiGraphics.enableScissor(left + b, top + b, left + PANEL_WIDTH - b, top + panelHeight - b);
	}

	public boolean handleScrollbarClick(double mouseX, double mouseY) {
		return scrollBar.mouseClicked(mouseX, mouseY);
	}

	public boolean handleDrag(double mouseY) {
		return scrollBar.mouseDragged(mouseY);
	}

	public void stopDragScrolling() {
		scrollBar.release();
	}

	public boolean handleScroll(double mouseX, double mouseY, double vertical) {
		int b = PanelRenderer.FRAME_BORDER;
		if (mouseX < left + b || mouseX >= left + PANEL_WIDTH - b  || mouseY < top + b || mouseY >= top + panelHeight - b) return false;
		return scrollBar.mouseScrolled(vertical, 4);
	}

	public void repositionButtons() {
		int baseY = top + PanelRenderer.FRAME_BORDER;
		int scroll = scrollBar.getScroll();
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).setY(baseY + i * BUTTON_HEIGHT - scroll);
		}
	}
}
