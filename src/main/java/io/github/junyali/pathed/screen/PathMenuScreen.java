package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;

public class PathMenuScreen extends Screen {
	private static final int PANEL_WIDTH = 160;
	private static final int PANEL_HEIGHT = 220;
	private static final int ICON_PANEL_WIDTH = 28;
	private static final int ICON_PANEL_PADDING = 6;
	private static final int OPTIONS_PANEL_WIDTH = 120;
	private static final int PANEL_GAP = 6;

	private static final int PLAYER_MODEL_SIZE = 60;
	private static final int XP_BAR_WIDTH = 100;
	private static final int XP_BAR_HEIGHT = 6;

	private final boolean showDirtBackground;

	private static final int COLOUR_TEXT = 0xFFFFFFFF;
	private static final int COLOUR_SUBTEXT = 0xFFAAAAAA;
	private static final int COLOUR_XP_BG = 0xFF333333;
	private static final int COLOUR_XP_FILL = 0xFF55FF55;
	private static final int COLOUR_XP_BORDER = 0xFF888888;

	private int panelLeft;
	private int panelTop;
	private int iconPanelLeft;
	private int optionsPanelLeft;

	private Path path;
	private int level;
	private long currentExp;
	private long expForNextLevel;
	private float levelProgress;

	public PathMenuScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.path_menu.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		super.init();
		int totalWidth = PANEL_WIDTH + PANEL_GAP + ICON_PANEL_WIDTH + PANEL_GAP + OPTIONS_PANEL_WIDTH;
		int layoutLeft = 20;

		this.panelLeft = layoutLeft;
		this.iconPanelLeft = layoutLeft + PANEL_WIDTH + PANEL_GAP;
		this.optionsPanelLeft = layoutLeft + PANEL_WIDTH + PANEL_GAP + ICON_PANEL_WIDTH + PANEL_GAP;
		this.panelTop = (this.height - PANEL_HEIGHT) / 2;

		this.addOptionsPanelButtons();
		this.refreshData();
	}

	private void refreshData() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;

		PathAttachment pathAttachment = PathAttachment.get(mc.player);
		this.path = pathAttachment.getPath();

		ProgressionAttachment progressionAttachment = ProgressionAttachment.get(mc.player);
		this.level = progressionAttachment.getLevel();
		this.currentExp = progressionAttachment.getExperience();
		this.expForNextLevel = progressionAttachment.getExperienceForNextLevel();
		this.levelProgress = progressionAttachment.getLevelProgress();
	}

	private void addOptionsPanelButtons() {
		int x = this.optionsPanelLeft;
		int y = this.panelTop + 10;
		int buttonWidth = OPTIONS_PANEL_WIDTH;
		int buttonHeight = 20;
		int buttonSpacing = 26;

		this.addRenderableWidget(Button.builder(
				Component.translatable("pathed.gui.path_menu.button.skill_tree"),
				btn -> {
					// do later
				}
		).bounds(x, y, buttonWidth, buttonHeight).build());
		y += buttonSpacing;

		this.addRenderableWidget(Button.builder(
				Component.translatable("pathed.gui.path_menu.button.stats"),
				btn -> {

				}
		).bounds(x, y, buttonWidth, buttonHeight).build());
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
		this.refreshData();
		this.renderLeftPanel(guiGraphics, mouseX, mouseY);
		this.renderOptionsPanel(guiGraphics);
	}

	private void renderLeftPanel(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.fill(
				panelLeft,
				panelTop,
				panelLeft + PANEL_WIDTH,
				panelTop + PANEL_HEIGHT,
				0xAA000000
		);

		int centreX = panelLeft + PANEL_WIDTH / 2;
		int y = panelTop + 12;

		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			guiGraphics.drawCenteredString(
					this.font,
					mc.player.getDisplayName(),
					centreX,
					y,
					COLOUR_SUBTEXT
			);
		}
		y+= 20;

		if (mc.player != null) {
			int modelCentreX = centreX;
			int modelFeetY = y + PLAYER_MODEL_SIZE + 50;

			renderPlayerModel(guiGraphics, mc.player, modelCentreX, modelFeetY, mouseX, mouseY);
			y = modelFeetY + 10;
		}

		if (this.path != null) {
			int textWidth = this.font.width(this.path.getName());
			int totalWidth = 16 + 4 + textWidth;
			int startX = centreX - totalWidth / 2;
			renderPathIcon(guiGraphics, this.path.getIcon(), startX, y);
			guiGraphics.drawString(this.font, this.path.getName(), startX + 20, y + 4, COLOUR_TEXT);
			y += 20;
		} else {
			guiGraphics.drawCenteredString(
					this.font,
					Component.translatable("pathed.gui.path_menu.no_path"),
					centreX,
					y,
					COLOUR_SUBTEXT
			);
			y += 14;
		}

		Component levelText = Component.translatable("pathed.gui.path_menu.level", this.level);
		guiGraphics.drawCenteredString(this.font, levelText, centreX, y, COLOUR_TEXT);
		y += 14;

		renderXpBar(guiGraphics, centreX, y);
	}

	private void renderOptionsPanel(GuiGraphics guiGraphics) {
		guiGraphics.fill(
				iconPanelLeft,
				panelTop,
				iconPanelLeft + ICON_PANEL_WIDTH,
				panelTop + PANEL_HEIGHT,
				0xAA000000
		);

		int iconX = iconPanelLeft + ICON_PANEL_PADDING;
		int y = panelTop + ICON_PANEL_PADDING;
	}

	private void renderPlayerModel(GuiGraphics guiGraphics, LivingEntity entity, int x, int y, int mouseX, int mouseY) {
		InventoryScreen.renderEntityInInventoryFollowsMouse(
				guiGraphics,
				x - PLAYER_MODEL_SIZE,
				y - PLAYER_MODEL_SIZE * 2,
				x + PLAYER_MODEL_SIZE,
				y,
				PLAYER_MODEL_SIZE,
				0.0625F,
				mouseX,
				mouseY,
				entity
		);
	}

	private void renderXpBar(GuiGraphics guiGraphics, int centreX, int y) {
		int barLeft = centreX - XP_BAR_WIDTH / 2;

		guiGraphics.fill(barLeft - 1, y - 1, barLeft + XP_BAR_WIDTH + 1, y + XP_BAR_HEIGHT + 1, COLOUR_XP_BORDER);
		guiGraphics.fill(barLeft, y, barLeft + XP_BAR_WIDTH, y + XP_BAR_HEIGHT, COLOUR_XP_BG);

		if (this.expForNextLevel > 0) {
			float progress = this.levelProgress;
			int fillWidth = (int) (XP_BAR_WIDTH * Math.min(progress, 1.0f));
			if (fillWidth > 0) {
				guiGraphics.fill(barLeft, y, barLeft + fillWidth, y + XP_BAR_HEIGHT, COLOUR_XP_FILL);
			}
		}

		Component expLabel = Component.literal(this.currentExp + " / " + this.expForNextLevel);
		guiGraphics.drawCenteredString(this.font, expLabel, centreX, y + XP_BAR_HEIGHT + 4, COLOUR_SUBTEXT);
	}

	private void renderPathIcon(GuiGraphics guiGraphics, PathIcon icon, int x, int y) {
		if (icon.isPlayerHead()) {
			ItemStack head = new ItemStack(Items.PLAYER_HEAD);
			Minecraft mc = Minecraft.getInstance();
			if (mc.player != null) {
				head.set(DataComponents.PROFILE, new ResolvableProfile(mc.player.getGameProfile()));
			}
			guiGraphics.renderItem(head, x, y);
		} else if (icon.isItem()) {
			guiGraphics.renderItem(icon.getItem(), x, y);
		} else {
			icon.getTexture().ifPresent(texture -> guiGraphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16));
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
