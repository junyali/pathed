package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import io.github.junyali.pathed.screen.progression.ProgressionRenderer;
import io.github.junyali.pathed.screen.progression.ProgressionScreen;
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
	private static final int CARD_WIDTH = 150;
	private static final int CARD_HEIGHT = 220;

	private static final int PANEL_WIDTH = 160;
	private static final int PANEL_HEIGHT = 220;
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
	private int optionsPanelLeft;

	private int layoutLeft;
	private int layoutTop;

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
		int layoutLeft = 20;

		this.panelLeft = layoutLeft;
		this.optionsPanelLeft = layoutLeft + PANEL_WIDTH + PANEL_GAP;
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
					if (this.minecraft != null) {
						this.minecraft.setScreen(new ProgressionScreen(this.showDirtBackground));
					}
				}
		).bounds(x, y, buttonWidth, buttonHeight).build());
		y += buttonSpacing;

		this.addRenderableWidget(Button.builder(
				Component.translatable("pathed.gui.path_menu.button.stats"),
				btn -> {

				}
		).bounds(x, y, buttonWidth, buttonHeight).build());
		y += buttonSpacing;

		this.addRenderableWidget(Button.builder(
				Component.translatable("pathed.gui.path_menu.button.reward_stash"),
				btn -> {
					// for collecting rewards
					if (this.minecraft != null) {
						this.minecraft.setScreen(new RewardStashScreen(this.showDirtBackground));
					}
				}
		).bounds(x, y, buttonWidth, buttonHeight).build());
		y += buttonSpacing;

		this.addRenderableWidget(Button.builder(
				Component.translatable("pathed.gui.path_menu.button.attributes"),
				btn -> {
					if (this.minecraft != null) {
						this.minecraft.setScreen(new AttributeScreen(this.showDirtBackground));
					}
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
		this.renderCard(guiGraphics, mouseX, mouseY);
	}

	private void renderCard(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int x = layoutLeft;
		int y = layoutTop;
		int w = CARD_WIDTH;
		int h = CARD_HEIGHT;

		guiGraphics.fill(
				x,
				y,
				x + w,
				y + h,
				0xC0100010
		);

		int headerH = 140;
		guiGraphics.fill(
				x + 1,
				y + 1,
				x + w - 1,
				y + headerH,
				0xB0000000
		);

		guiGraphics.fill(
				x + 1,
				y + headerH,
				x + w - 1,
				y + headerH + 1,
				0xFF373737
		);

		ProgressionRenderer.renderBorder(guiGraphics, x, y, w, h);

		int centreX = x + w / 2;
		int cY = y + 10;

		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			guiGraphics.drawCenteredString(
					this.font,
					mc.player.getDisplayName(),
					centreX,
					cY,
					COLOUR_SUBTEXT
			);
		}
		cY += 12;

		if (mc.player != null) {
			int modelFeetY = y + headerH - 10;
			renderPlayerModel(guiGraphics, mc.player, centreX, modelFeetY, mouseX, mouseY);
		}

		cY = y + headerH + 10;

		if (this.path != null) {
			int iconSize = 16;
			int nameWidth = font.width(this.path.getName());
			int rowWidth = iconSize + 4 + nameWidth;
			int rowX = centreX - rowWidth / 2;

			renderPathIcon(guiGraphics, this.path.getIcon(), rowX, cY);
			guiGraphics.drawString(this.font, this.path.getName(), rowX + iconSize, cY + 4, COLOUR_TEXT);
		} else {
			guiGraphics.drawCenteredString(
					this.font,
					Component.translatable("pathed.gui.path_menu.no_path"),
					centreX,
					cY + 4,
					COLOUR_SUBTEXT
			);
			cY += 22;
		}

		renderLevel(guiGraphics, centreX, cY);
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

	private void renderLevel(GuiGraphics guiGraphics, int centreX, int y) {
		String levelLabel = Component.translatable("pathed.gui.path_menu.level", "").getString().trim();
		guiGraphics.drawCenteredString(font, Component.literal(levelLabel), centreX, y, 0xFF808080);
		y += font.lineHeight + 1;

		String levelStr = String.valueOf(this.level);
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(centreX, y, 0);
		guiGraphics.pose().scale(1.5f, 1.5f, 1f);
		guiGraphics.drawString(font, levelStr, -font.width(levelStr) / 2, 0, 0xFFFFFF55, false);
		guiGraphics.pose().popPose();
		y += (int) (font.lineHeight * 1.5f) + 4;

		int barLeft = centreX - XP_BAR_WIDTH / 2;

		guiGraphics.fill(barLeft, y, barLeft + XP_BAR_WIDTH, y + XP_BAR_HEIGHT, COLOUR_XP_BG);

		if (this.expForNextLevel > 0) {
			int fillWidth = (int) (XP_BAR_WIDTH * Math.min(levelProgress, 1.0f));
			if (fillWidth > 0) {
				guiGraphics.fill(barLeft, y, barLeft + fillWidth, y + XP_BAR_HEIGHT, COLOUR_XP_FILL);
			}
		}

		y += XP_BAR_HEIGHT + 4;

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
