package io.github.junyali.pathed.screen.stat;

import io.github.junyali.pathed.screen.common.CategoryButton;
import io.github.junyali.pathed.screen.common.CategoryHost;
import io.github.junyali.pathed.screen.stat.stats.BlocksBrokenPanel;
import io.github.junyali.pathed.screen.stat.stats.DistanceTravelledPanel;
import io.github.junyali.pathed.screen.stat.stats.EntitiesDamagedPanel;
import io.github.junyali.pathed.screen.stat.stats.EntitiesKilledPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StatsScreen extends Screen implements CategoryHost {
	private final boolean showDirtBackground;

	private StatCategoryPanel categoryPanel;
	private AbstractStatPanel activePanel;
	private List<StatCategory> categories;

	private String selectedCategory = "";

	private int activeLeft;
	private int activeTop;
	private int activeWidth;
	private int activeHeight;

	public StatsScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.stats.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		super.init();

		this.categories = List.of(
				new StatCategory(
						"blocks_broken",
						"pathed.gui.stats.blocks_broken.title",
						new ItemStack(Items.IRON_PICKAXE),
						BlocksBrokenPanel::new
				),
				new StatCategory(
						"entities_killed",
						"pathed.gui.stats.entities_killed.title",
						new ItemStack(Items.IRON_SWORD),
						EntitiesKilledPanel::new
				),
				new StatCategory("entities_damaged",
						"pathed.gui.stats.entities_damaged.title",
						new ItemStack(Items.IRON_SWORD),
						EntitiesDamagedPanel::new
				),
				new StatCategory(
						"distance_travelled",
						"pathed.gui.stats.distance_travelled.title",
						new ItemStack(Items.COMPASS),
						DistanceTravelledPanel::new
				)
		);

		int panelHeight = this.height - 20;
		this.categoryPanel = new StatCategoryPanel(this, 10, 10, panelHeight);
		for (CategoryButton button : this.categoryPanel.init(this.categories)) {
			this.addWidget(button);
		}

		this.activeLeft = 10 + StatCategoryPanel.PANEL_WIDTH + 10;
		this.activeTop = 10;
		this.activeWidth = this.width - this.activeLeft - 10;
		this.activeHeight = this.height - 20;

		rebuildActivePanel();
	}

	private void rebuildActivePanel() {
		if (this.activePanel instanceof IconCountStatPanel<?> old) {
			old.removeFrom(this::removeWidget);
		}
		if (this.activePanel instanceof DamageListPanel old) {
			old.removeFrom(this::removeWidget);
		}

		// meow?
		StatCategory cat = currentCategory();
		if (cat == null) {
			this.activePanel = null;
			return;
		}

		this.activePanel = cat.factory().create(activeLeft, activeTop, activeWidth, activeHeight);
		if (this.activePanel instanceof IconCountStatPanel<?> panel) {
			panel.initWidgets(this::addRenderableWidget);
		}
		if (this.activePanel instanceof DamageListPanel panel) {
			panel.initWidgets(this::addRenderableWidget);
		}
	}

	private StatCategory currentCategory() {
		for (StatCategory cat : categories) {
			if (cat.id().equals(selectedCategory)) return cat;
		}
		return categories.isEmpty() ? null : categories.getFirst();
	}

	public void selectCategory(StatCategory cat) {
		this.selectedCategory = cat.id();
	}

	public void selectCategory(String id) {
		if (id.equals(this.selectedCategory)) return;
		this.selectedCategory = id;
		rebuildActivePanel();
	}

	@Override
	public String getSelectedCategory() {
		return selectedCategory;
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		this.categoryPanel.repositionButtons();
		if (activePanel != null) activePanel.render(guiGraphics, mouseX, mouseY, delta);
		super.render(guiGraphics, mouseX, mouseY, delta);
		this.categoryPanel.render(guiGraphics, mouseX, mouseY);
		this.categoryPanel.enableScissor(guiGraphics);
		this.categoryPanel.renderButtons(guiGraphics, mouseX, mouseY, delta);
		guiGraphics.disableScissor();
		this.categoryPanel.renderScrollbar(guiGraphics, mouseX, mouseY);
		this.categoryPanel.renderTitlebar(guiGraphics);
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		if (showDirtBackground) {
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && categoryPanel.handleScrollbarClick(mouseX, mouseY)) return true;
		if (activePanel != null && activePanel.mouseClicked(mouseX, mouseY, button)) return true;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) categoryPanel.stopDragScrolling();
		if (activePanel != null) activePanel.mouseReleased(mouseX, mouseY, button);
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (categoryPanel.handleDrag(mouseY)) return true;
		if (activePanel != null && activePanel.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (categoryPanel.handleScroll(mouseX, mouseY, scrollY)) return true;
		if (activePanel != null && activePanel.mouseScrolled(mouseX, mouseY, scrollY)) return true;
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
