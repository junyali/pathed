package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
	private static final int PLAYER_MODEL_SIZE = 60;

	private final boolean showDirtBackground;

	private static final int COLOUR_TEXT = 0xFFFFFFFF;
	private static final int COLOUR_SUBTEXT = 0xFFAAAAAA;

	private int panelLeft;
	private int panelTop;

	private Path path;
	private int level;
	private long currentExp;
	private long expForNextLevel;

	public PathMenuScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.path_menu.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		super.init();
		this.panelLeft = (this.width / 2) - PANEL_WIDTH - 40;
		this.panelTop = (this.height - PANEL_HEIGHT) / 2;
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
		// this.expForNextLevel =
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
		// this.renderRightPanel(blah blah);
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

		if (this.path != null) {
			int iconX = centreX - 8;
			renderPathIcon(guiGraphics, this.path.getIcon(), iconX, y);
			y += 20;
			guiGraphics.drawCenteredString(this.font, this.path.getName(), centreX, y, COLOUR_TEXT);
			y += 14;
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

		Component levelText = Component.translatable("pathed.gui.path_menu.level", this.level);
		guiGraphics.drawCenteredString(this.font, levelText, centreX, y, COLOUR_TEXT);
		y += 14;

		// render xp bar here
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
