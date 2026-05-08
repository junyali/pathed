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

import java.util.ArrayList;
import java.util.List;

public class PathMenuScreen extends Screen {
	private static final int CARD_WIDTH = 150;
	private static final int CARD_HEIGHT = 220;

	private static final int CLUSTER_GAP = 24;

	private static final int PLAYER_MODEL_SIZE = 60;

	private static final int XP_BAR_WIDTH = 100;
	private static final int XP_BAR_HEIGHT = 6;

	private static final int BUTTON_SIZE = 36;
	private static final int BUTTON_RADIUS = 40;

	private static final int COLOUR_CARD_BACKGROUND = 0XC000010;
	private static final int COLOUR_CARD_TOP_BACKGROUND = 0xB0000000;
	private static final int COLOUR_BUTTON_BACKGROUND = 0xC0202020;
	private static final int COLOUR_BUTTON_BACKGROUND_HOVER = 0xD0404040;
	private static final int COLOUR_BORDER = 0xFF555555;
	private static final int COLOUR_BORDER_HOVER = 0xFFFFFFFF;
	private static final int COLOUR_DIVIDER = 0xFF373737;
	private static final int COLOUR_XP_BG = 0xFF1B1B1B;
	private static final int COLOUR_XP_FILL = 0xFF80FF20;
	private static final int COLOUR_TEXT = 0xFFFFFFFF;
	private static final int COLOUR_SUBTEXT = 0xFFAAAAAA;
	private static final int COLOUR_TERTIARY = 0xFF808080;
	private static final int COLOUR_TITLE = 0xFFFFFF55;

	private final boolean showDirtBackground;

	private Path path;
	private int level;
	private long currentExp;
	private long expForNextLevel;
	private float levelProgress;

	private int layoutLeft;
	private int layoutTop;

	private int clusterCX;
	private int clusterCY;

	private final List<DiamondButton> buttons = new ArrayList<>();

	public PathMenuScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.path_menu.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		super.init();

		int clusterDiameter = (BUTTON_RADIUS + BUTTON_SIZE) * 2;
		int totalWidth = CARD_WIDTH + CLUSTER_GAP + clusterDiameter;
		int totalHeight = CARD_HEIGHT;

		this.layoutLeft = (this.width - totalWidth) / 2;
		this.layoutTop = (this.height - totalHeight) / 2;

		this.clusterCX = this.layoutLeft + CARD_WIDTH + CLUSTER_GAP + clusterDiameter / 2;
		this.clusterCY = this.layoutTop + CARD_HEIGHT / 2;

		this.buildButtons();
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

	private void buildButtons() {
		buttons.clear();

		double[] angles = {
				Math.toRadians(270),
				Math.toRadians(0),
				Math.toRadians(90),
				Math.toRadians(180)
		};

		ItemStack[] icons = {
				new ItemStack(Items.BOOK),
				new ItemStack(Items.NETHER_STAR),
				new ItemStack(Items.CHEST),
				new ItemStack(Items.COMPASS)
		};

		Component[] tooltips = {
				Component.translatable("pathed.gui.path_menu.button.skill_tree"),
				Component.translatable("pathed.gui.path_menu.button.attributes"),
				Component.translatable("pathed.gui.path_menu.button.reward_stash"),
				Component.translatable("pathed.gui.path_menu.button.stats")
		};

		Runnable[] actions = {
				() -> {
					if (minecraft != null) {
						minecraft.setScreen(new ProgressionScreen(showDirtBackground));
					}
				},
				() -> {
					if (minecraft != null) {
						minecraft.setScreen(new AttributeScreen(showDirtBackground));
					}
				},
				() -> {
					if (minecraft != null) {
						minecraft.setScreen(new RewardStashScreen(showDirtBackground));
					}
				},
				() -> {
					if (minecraft != null) {
						// set stats screen;
					}
				}
		};

		for (int i = 0; i < angles.length; i++) {
			int cX = clusterCX + (int) Math.round(Math.cos(angles[i]) * BUTTON_RADIUS);
			int cY = clusterCY + (int) Math.round(Math.sin(angles[i]) * BUTTON_RADIUS);
			int bX = cX - BUTTON_SIZE / 2;
			int bY = cY - BUTTON_SIZE / 2;
			buttons.add(new DiamondButton(bX, bY, BUTTON_SIZE, icons[i], tooltips[i], actions[i]));
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
		super.render(guiGraphics, mouseX, mouseY, delta);
		this.refreshData();
		this.renderCard(guiGraphics, mouseX, mouseY);
		this.renderCluster(guiGraphics, mouseX, mouseY);

		for (DiamondButton button : buttons) {
			if (button.isHovered(mouseX, mouseY)) {
				guiGraphics.renderTooltip(font, button.tooltip, mouseX, mouseY);
				break;
			}
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
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
				COLOUR_CARD_BACKGROUND
		);

		int headerH = 140;
		guiGraphics.fill(
				x + 1,
				y + 1,
				x + w - 1,
				y + headerH,
				COLOUR_CARD_TOP_BACKGROUND
		);

		guiGraphics.fill(
				x + 1,
				y + headerH,
				x + w - 1,
				y + headerH + 1,
				COLOUR_DIVIDER
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
		guiGraphics.drawCenteredString(font, Component.literal(levelLabel), centreX, y, COLOUR_TERTIARY);
		y += font.lineHeight + 1;

		String levelStr = String.valueOf(this.level);
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(centreX, y, 0);
		guiGraphics.pose().scale(1.5f, 1.5f, 1f);
		guiGraphics.drawString(font, levelStr, -font.width(levelStr) / 2, 0, COLOUR_TITLE, false);
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

	private void renderCluster(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		for (DiamondButton button : buttons) {
			renderButton(guiGraphics, button, mouseX, mouseY);
		}
	}

	private void renderButton(GuiGraphics guiGraphics, DiamondButton button, int mouseX, int mouseY) {
		boolean hovered = button.isHovered(mouseX, mouseY);
		int background = hovered ? COLOUR_BUTTON_BACKGROUND_HOVER : COLOUR_BUTTON_BACKGROUND;

		guiGraphics.fill(button.x, button.y, button.x + button.size, button.y + button.size, background);

		int iconOffset = (button.size - 16) / 2;
		guiGraphics.renderItem(button.icon, button.x + iconOffset, button.y + iconOffset);
	}

	private static class DiamondButton {
		final int x;
		final int y;
		final int size;
		final ItemStack icon;
		final Component tooltip;
		final Runnable action;

		DiamondButton(int x, int y, int size, ItemStack icon, Component tooltip, Runnable action) {
			this.x = x;
			this.y = y;
			this.size = size;
			this.icon = icon;
			this.tooltip = tooltip;
			this.action = action;
		}

		boolean isHovered(int mouseX, int mouseY) {
			return mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size;
		}
	}
}
