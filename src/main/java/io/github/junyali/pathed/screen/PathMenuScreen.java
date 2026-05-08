package io.github.junyali.pathed.screen;

import com.mojang.math.Axis;
import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import io.github.junyali.pathed.screen.attribute.AttributeScreen;
import io.github.junyali.pathed.screen.progression.ProgressionRenderer;
import io.github.junyali.pathed.screen.progression.ProgressionScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathMenuScreen extends Screen {
	private static final ResourceLocation BUTTON_LOCKED_TEXTURE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "progression/button_locked");
	private static final ResourceLocation BUTTON_LOCKED_ICON = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/button_locked_icon.png");

	private static final int CARD_WIDTH = 150;
	private static final int CARD_HEIGHT = 220;

	private static final int CLUSTER_GAP = 24;

	private static final int PLAYER_MODEL_SIZE = 48;

	private static final int XP_BAR_WIDTH = 100;
	private static final int XP_BAR_HEIGHT = 6;

	private static final int BUTTON_SIZE = 36;
	private static final int BUTTON_RADIUS = 40;

	private static final int CENTRE_NODE_SIZE = 10;

	private static final int COLOUR_CARD_BACKGROUND = 0XC0101010;
	private static final int COLOUR_BUTTON_BACKGROUND = 0xC0202020;
	private static final int COLOUR_BUTTON_BACKGROUND_HOVER = 0xD0404040;
	private static final int COLOUR_BORDER = 0xFF555555;
	private static final int COLOUR_BORDER_HOVER = 0xFFFFFFFF;
	private static final int COLOUR_CONNECTOR = 0xFF555555;
	private static final int COLOUR_CENTRE_GLOW = 0x40FFFF55;
	private static final int COLOUR_CENTRE_BACKGROUND = 0xFF373737;
	private static final int COLOUR_CENTRE_DOT = 0xFFFFFF55;
	private static final int COLOUR_XP_BG = 0xFF1B1B1B;
	private static final int COLOUR_XP_FILL = 0xFF80FF20;
	private static final int COLOUR_TEXT = 0xFFFFFFFF;
	private static final int COLOUR_SUBTEXT = 0xFFAAAAAA;
	private static final int COLOUR_TERTIARY = 0xFF000000;
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
				new ItemStack(Items.KNOWLEDGE_BOOK),
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
				boolean isLocked = this.path == null || this.path.getId().equals(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "human"));
				Component tooltip = isLocked ? Component.translatable("pathed.gui.path_menu.tooltip.locked") : button.tooltip;
				guiGraphics.renderTooltip(font, tooltip, mouseX, mouseY);
				break;
			}
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean isLocked = this.path == null || this.path.getId().equals(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "human"));
		if (button == 0 && !isLocked) {
			for (DiamondButton btn : buttons) {
				if (btn.isHovered((int) mouseX, (int) mouseY)) {
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
					btn.action.run();
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	// TODO: register pathcolour as one of the proporties of path rather than keeping it client-sided
	private static final Map<ResourceLocation, ChatFormatting> PATH_COLOURS = Map.of(
			ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "human"), ChatFormatting.GRAY,
			ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "blademaster"), ChatFormatting.RED,
			ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "spelunker"), ChatFormatting.GOLD,
			ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "lumberjack"), ChatFormatting.DARK_GREEN,
			ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "excavator"), ChatFormatting.AQUA,
			ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "cultivator"), ChatFormatting.GREEN
	);

	private static ChatFormatting getPathColour(Path path) {
		if (path == null) ;
		return PATH_COLOURS.getOrDefault(path.getId(), ChatFormatting.GRAY);
	}

	private void renderCard(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int x = layoutLeft;
		int y = layoutTop;
		int w = CARD_WIDTH;
		int h = CARD_HEIGHT;
		int b = ProgressionScreen.FRAME_BORDER;

		guiGraphics.fill(
				x + b,
				y + b,
				x + w - b,
				y + h - b,
				COLOUR_CARD_BACKGROUND
		);

		int headerH = 132;

		ProgressionRenderer.renderBorder(guiGraphics, x, y, w, h);

		int centreX = x + w / 2;
		int nameY = y + b + 6;

		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			ChatFormatting nameColour = getPathColour(this.path);
			MutableComponent styledName = mc.player.getDisplayName().copy()
							.withStyle(Style.EMPTY
									.withBold(true)
									.withColor(nameColour));
			guiGraphics.drawCenteredString(
					this.font,
					styledName,
					centreX,
					nameY,
					COLOUR_SUBTEXT
			);
		}

		if (mc.player != null) {
			int nameBottom = nameY + font.lineHeight;
			int modelTop = nameBottom + 10;
			int modelFeetY = y + headerH;
			int allowedHeight = modelFeetY - modelTop;
			int modelScale = Math.min(PLAYER_MODEL_SIZE, allowedHeight / 2);
			renderPlayerModel(guiGraphics, mc.player, centreX, modelFeetY, modelScale, mouseX, mouseY);
		}

		int cY = y + headerH + 8;

		if (this.path != null) {
			int iconSize = 16;
			int nameWidth = font.width(this.path.getName());
			int rowWidth = iconSize + 4 + nameWidth;
			int rowX = centreX - rowWidth / 2;

			renderPathIcon(guiGraphics, this.path.getIcon(), rowX, cY);
			guiGraphics.drawString(this.font, this.path.getName(), rowX + iconSize + 4, cY + 4, COLOUR_TEXT);
		} else {
			guiGraphics.drawCenteredString(
					this.font,
					Component.translatable("pathed.gui.path_menu.no_path"),
					centreX,
					cY + 4,
					COLOUR_SUBTEXT
			);
		}
		cY += 22;

		boolean isLocked = this.path == null || this.path.getId().equals(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "human"));

		if (isLocked) {
			ChatFormatting colour = ChatFormatting.GRAY;
			if (colour.getColor() != null) {
				Component greyLabel = Component.translatable("pathed.gui.path_menu.default").withStyle(getPathColour(this.path));
				guiGraphics.drawCenteredString(font, greyLabel, centreX, cY, -1);
			}

		} else {
			renderLevel(guiGraphics, centreX, cY);
		}
	}

	private void renderPlayerModel(GuiGraphics guiGraphics, LivingEntity entity, int x, int y, int scale, int mouseX, int mouseY) {
		InventoryScreen.renderEntityInInventoryFollowsMouse(
				guiGraphics,
				x - scale,
				y - scale * 2,
				x + scale,
				y,
				scale,
				0.0625F,
				mouseX,
				mouseY,
				entity
		);
	}

	private void renderLevel(GuiGraphics guiGraphics, int centreX, int y) {
		Component levelLabel = Component.translatable("pathed.gui.path_menu.level", this.level);
		guiGraphics.drawCenteredString(font, levelLabel, centreX, y, COLOUR_TITLE);
		y += font.lineHeight + 6;

		int barLeft = centreX - XP_BAR_WIDTH / 2;

		guiGraphics.fill(barLeft, y, barLeft + XP_BAR_WIDTH, y + XP_BAR_HEIGHT, COLOUR_XP_BG);

		if (this.expForNextLevel > 0) {
			int fillWidth = (int) (XP_BAR_WIDTH * Math.min(levelProgress, 1.0f));
			if (fillWidth > 0) {
				guiGraphics.fill(barLeft, y, barLeft + fillWidth, y + XP_BAR_HEIGHT, COLOUR_XP_FILL);
			}
		}

		drawBorder(guiGraphics, barLeft - 1, y - 1, XP_BAR_WIDTH + 2, XP_BAR_HEIGHT + 2, COLOUR_BORDER);
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

	private void drawConnectorLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
		if (x1 == x2) {
			int top = Math.min(y1, y2);
			int bottom = Math.max(y1, y2);

			guiGraphics.fill(x1 - 1, top, x1 + 2, bottom + 1, COLOUR_TERTIARY);
			guiGraphics.fill(x1, top, x1 + 1, bottom + 1, COLOUR_CONNECTOR);
			return;
		}
		if (y1 == y2) {
			int left = Math.min(x1, x2);
			int right = Math.max(x1, x2);

			guiGraphics.fill(left, y1 - 1, right + 1, y1 + 2, COLOUR_TERTIARY);
			guiGraphics.fill(left, y1, right + 1, y1 + 1, COLOUR_CONNECTOR);
			return;
		}

		int dX = Math.abs(x2 - x1);
		int dY = Math.abs(y2 - y1);
		int sX = x1 < x2 ? 1 : -1;
		int sY = y1 < y2 ? 1 : -1;
		int err = dX - dY;
		int cX = x1;
		int cY = y1;

		// ahhhh scawwy maths D:

		while (true) {
			guiGraphics.fill(cX, cY, cX + 1, cY + 1, COLOUR_CONNECTOR);
			if (cX == x2 && cY == y2) break;
			int e2 = 2 * err;
			if (e2 > -dY) {
				err -= dY;
				cX += sX;
			}
			if (e2 < dX) {
				err += dX;
				cY += sY;
			}
		}
	}

	private static void drawBorder(GuiGraphics guiGraphics, int x, int y, int w, int h, int colour) {
		guiGraphics.fill(x, y, x + w, y + 1, colour);
		guiGraphics.fill(x, y + h - 1, x + w, y + h, colour);
		guiGraphics.fill(x, y, x + 1, y + h, colour);
		guiGraphics.fill(x + w - 1, y, x + w, y + h, colour);
	}

	private void renderCluster(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int margin = 4;
		for (DiamondButton button : buttons) {
			int cX = button.x + BUTTON_SIZE / 2;
			int cY = button.y + BUTTON_SIZE / 2;
			double dX = cX - clusterCX;
			double dY = cY - clusterCY;
			double len = Math.hypot(dX, dY);
			int endX = (int) (cX - dX / len * margin);
			int endY = (int) (cY - dY / len * margin);
			drawConnectorLine(guiGraphics, clusterCX, clusterCY, endX, endY);
		}

		int nodeHalf = CENTRE_NODE_SIZE / 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(clusterCX, clusterCY, 0);
		guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(45));
		guiGraphics.fill(
				-nodeHalf - 2,
				-nodeHalf - 2,
				nodeHalf + 2,
				nodeHalf + 2,
				COLOUR_CENTRE_GLOW
		);
		guiGraphics.fill(
				-nodeHalf,
				-nodeHalf,
				nodeHalf,
				nodeHalf,
				COLOUR_CENTRE_BACKGROUND
		);
		drawBorder(
				guiGraphics,
				-nodeHalf,
				-nodeHalf,
				CENTRE_NODE_SIZE,
				CENTRE_NODE_SIZE,
				COLOUR_TERTIARY
		);

		int dotHalf = 2;
		guiGraphics.fill(
				-dotHalf,
				-dotHalf,
				dotHalf,
				dotHalf,
				COLOUR_CENTRE_DOT
		);
		guiGraphics.pose().popPose();

		for (DiamondButton button : buttons) {
			renderButton(guiGraphics, button, mouseX, mouseY);
		}
	}

	private void renderButton(GuiGraphics guiGraphics, DiamondButton button, int mouseX, int mouseY) {
		boolean hovered = button.isHovered(mouseX, mouseY);
		boolean isLocked = this.path == null || this.path.getId().equals(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "human"));

		int centreX = button.x + button.size / 2;
		int centreY = button.y + button.size / 2;
		int halfSize = button.size / 2;

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(centreX, centreY, 0);
		guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(45));

		if (isLocked) {
			guiGraphics.blitSprite(BUTTON_LOCKED_TEXTURE, -halfSize, -halfSize, button.size, button.size);
		} else {
			int background = hovered ? COLOUR_BUTTON_BACKGROUND_HOVER : COLOUR_BUTTON_BACKGROUND;
			int border = hovered ? COLOUR_BORDER_HOVER : COLOUR_BORDER;

			guiGraphics.fill(-halfSize, -halfSize, halfSize, halfSize, background);
			drawBorder(guiGraphics, -halfSize, -halfSize, button.size, button.size, COLOUR_TERTIARY);
			drawBorder(guiGraphics, -halfSize + 1, -halfSize + 1, button.size - 2, button.size - 2, border);
		}

		guiGraphics.pose().popPose();


		if (isLocked) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 101);
			int iconOffset = (button.size - 20) / 2;
			guiGraphics.blit(BUTTON_LOCKED_ICON, button.x + iconOffset, button.y + iconOffset, 0, 0, 20, 20, 20, 20);
			guiGraphics.pose().popPose();
		} else {
			int iconOffset = (button.size - 16) / 2;
			guiGraphics.renderItem(button.icon, button.x + iconOffset, button.y + iconOffset);
		}
	}

	private static class DiamondButton {
		final int x;
		final int y;
		final int size;
		final ItemStack icon;
		final Component tooltip;
		final Runnable action;
		final int halfDiagonal;

		DiamondButton(int x, int y, int size, ItemStack icon, Component tooltip, Runnable action) {
			this.x = x;
			this.y = y;
			this.size = size;
			this.icon = icon;
			this.tooltip = tooltip;
			this.action = action;
			this.halfDiagonal = (int) Math.round((double) size / 2 * Math.sqrt(2));
		}

		boolean isHovered(int mouseX, int mouseY) {
			int cX = x + size / 2;
			int cY = y + size / 2;
			return Math.abs(mouseX - cX) + Math.abs(mouseY - cY) <= halfDiagonal;
		}
	}
}
