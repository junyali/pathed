package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class PathedScreens extends Screen {
	// Code taken from Origins NeoForge port
	// SOURCE: https://github.com/UltrusBot/AltOriginGui

	private static final ResourceLocation WINDOW_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "choose_path/background");
	private static final ResourceLocation WINDOW_BORDER = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "choose_path/border");
	private static final ResourceLocation WINDOW_NAME_PLATE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "choose_path/name_plate");
	private static final ResourceLocation WINDOW_SCROLL_BAR = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "choose_path/scroll_bar");
	private static final ResourceLocation WINDOW_SCROLL_BAR_PRESSED = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "choose_path/scroll_bar/pressed");
	private static final ResourceLocation WINDOW_SCROLL_BAR_SLOT = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "choose_path/scroll_bar/slot");

	protected static final int WINDOW_WIDTH = 176;
	protected static final int WINDOW_HEIGHT = 182;

	protected final boolean showDirtBackground;

	private boolean dragScrolling = false;

	private double mouseDragStart = 0;

	private Path currentPath;
	private int currentMaxScroll = 0;
	private int scrollDragStart = 0;

	protected int guiTop, guiLeft;
	protected int scrollPos = 0;

	public PathedScreens(Component title, boolean showDirtBackground) {
		super(title);
		this.showDirtBackground = showDirtBackground;
	}

	public void showPath(Path path) {
		this.currentPath = path;
		this.scrollPos = 0;
	}

	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - WINDOW_WIDTH) / 2;
		this.guiTop = (this.height - WINDOW_HEIGHT) / 2;
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
		this.renderPathWindow(guiGraphics, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.dragScrolling = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean mouseClicked = super.mouseClicked(mouseX, mouseY, button);
		if (this.cannotScroll()) return mouseClicked;
		this.dragScrolling = false;
		int scrollBarY = 36;
		int maxScrollBarOffset = 141;
		scrollBarY += (int) ((maxScrollBarOffset - scrollBarY) * (this.scrollPos / (float) this.currentMaxScroll));
		if (!this.canDragScroll(mouseX, mouseY, scrollBarY)) return mouseClicked;
		this.dragScrolling = true;
		this.scrollDragStart = scrollBarY;
		this.mouseDragStart = mouseY;
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean mouseDragged = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		if (!this.dragScrolling) return mouseDragged;
		int delta = (int) (mouseY - this.mouseDragStart);
		int newScrollPos = Math.max(36, Math.min(141, this.scrollDragStart + delta));
		float part = (newScrollPos - 36) / (float) (141 - 36);
		this.scrollPos = (int) (part * this.currentMaxScroll);
		return mouseDragged;
	}

	@Override
	public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
		int newScrollPos = this.scrollPos - (int) vertical * 4;
		this.scrollPos = Mth.clamp(newScrollPos, 0, this.currentMaxScroll);
		return super.mouseScrolled(x, y, horizontal, vertical);
	}

	public Path getCurrentPath() {
		return this.currentPath;
	}

	protected void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (this.cannotScroll()) return;
		guiGraphics.blitSprite(WINDOW_SCROLL_BAR_SLOT, this.guiLeft + 155, this.guiTop + 36, 8, 134);
		int scrollBarY = 36;
		int maxScrollbarOffset = 141;
		scrollBarY += (int) ((maxScrollbarOffset - scrollBarY) * (this.scrollPos / (float) this.currentMaxScroll));
		ResourceLocation scrollBarTexture = this.dragScrolling || this.canDragScroll(mouseX, mouseY, scrollBarY) ? WINDOW_SCROLL_BAR_PRESSED : WINDOW_SCROLL_BAR;
		guiGraphics.blitSprite(scrollBarTexture, this.guiLeft + 156, this.guiTop + scrollBarY, 6, 27);
	}

	protected boolean cannotScroll() {
		return this.currentPath == null || this.currentMaxScroll <= 0;
	}

	protected boolean canDragScroll(double mouseX, double mouseY, int scrollBarY) {
		return (mouseX >= this.guiLeft + 156 && mouseX < this.guiLeft + 156 + 6) && (mouseY >= this.guiTop + scrollBarY && mouseY < this.guiTop + scrollBarY + 27);
	}

	protected boolean isWithinWindowBoundaries(int mouseX, int mouseY) {
		return (mouseX >= this.guiLeft && mouseX < this.guiLeft + WINDOW_WIDTH) && (mouseY >= this.guiTop && mouseY < this.guiTop + WINDOW_HEIGHT);
	}

	protected void renderPathWindow(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		guiGraphics.blitSprite(WINDOW_BACKGROUND, this.guiLeft, this.guiTop, -4, WINDOW_WIDTH, WINDOW_HEIGHT);
		if (this.currentPath != null) {
			guiGraphics.enableScissor(this.guiLeft, this.guiTop, this.guiLeft + WINDOW_WIDTH, this.guiTop + WINDOW_HEIGHT);
			this.renderPathContent(guiGraphics);
			guiGraphics.disableScissor();
		}

		guiGraphics.blitSprite(WINDOW_BORDER, this.guiLeft, this.guiTop, 2, WINDOW_WIDTH, WINDOW_HEIGHT);
		guiGraphics.blitSprite(WINDOW_NAME_PLATE, this.guiLeft + 10, this.guiTop + 10, 2, 150, 26);
		if (this.currentPath != null) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 5);
			this.renderPathName(guiGraphics);

			guiGraphics.pose().pushPose();
			guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, this.guiTop - 15, 0xFFFFFF);

			this.renderScrollbar(guiGraphics, mouseX, mouseY);
		}
	}

	protected void renderPathName(GuiGraphics guiGraphics) {
		Component name = this.currentPath.getName();
		guiGraphics.drawString(this.font, name, this.guiLeft + 38, this.guiTop + 18, 0xFFFFFF);
		this.renderPathIcon(guiGraphics, this.currentPath.getIcon(), this.guiLeft + 15, this.guiTop + 15);
	}

	protected void renderPathIcon(GuiGraphics guiGraphics, PathIcon icon, int x, int y) {
		if (icon.isPlayerHead()) {
			ItemStack head = new ItemStack(Items.PLAYER_HEAD);
			if (minecraft != null && minecraft.player != null) {
				head.set(DataComponents.PROFILE, new ResolvableProfile(minecraft.player.getGameProfile()));
			}
			guiGraphics.renderItem(head, x, y);
		} else {
			if (icon.isItem()) {
				guiGraphics.renderItem(icon.getItem(), x, y);
			} else {
				icon.getTexture().ifPresent(texture -> guiGraphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16));
			}
		}
	}

	protected void renderPathContent(GuiGraphics guiGraphics) {
		int textWidthLimit = WINDOW_WIDTH - 48;
		int x = this.guiLeft + 18;
		int y = this.guiTop + 45 - this.scrollPos;

		for (FormattedCharSequence line : this.font.split(this.currentPath.getDescription(), textWidthLimit)) {
			guiGraphics.drawString(this.font, line, x + 2, y, 0xCCCCCC);
			y += 12;
		}

		y += 12;

		var startingItems = this.currentPath.getStartingItems().getItems();
		if (!startingItems.isEmpty()) {
			guiGraphics.drawString(this.font, Component.translatable("pathed.gui.choose_path.starting_kit").withStyle(s -> s.withUnderlined(true)), x, y, 0xFFFFFF);
			y += 14;

			int itemX = x + 2;
			for (ItemStack stack : startingItems) {
				guiGraphics.renderItem(stack, itemX, y);
				itemX += 18;
				if (itemX > x + textWidthLimit - 16) {
					itemX = x + 2;
					y += 18;
				}
			}
			y += 18;
		}
		y += this.scrollPos + 12;
		this.currentMaxScroll = Math.max(0, y - 14 - (this.guiTop + 158));
	}
}
