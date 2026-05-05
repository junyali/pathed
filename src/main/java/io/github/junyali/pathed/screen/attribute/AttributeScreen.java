package io.github.junyali.pathed.screen.attribute;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.data.attribute.AttributeRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AttributeScreen extends Screen {
	public static final int FRAME_BORDER = 9;
	public static final int LIST_PANEL_W = 124;
	public static final int FOOTER_H = 28;

	public static final int COLOUR_TEXT = 0xFFFFFFFF;
	public static final int COLOUR_TEXT_DIM = 0xFFAAAAAA;
	public static final int COLOUR_TEXT_MUTED = 0xFF555555;
	public static final int COLOUR_TEXT_HIGHLIGHT = 0xFFFFFF55;
	public static final int COLOUR_TEXT_GOOD = 0xFF55FF55;
	public static final int COLOUR_TEXT_BAD = 0xFFFF5555;

	public static final int COLOUR_PANEL_BG = 0xFF555555;
	public static final int COLOUR_CHIP_BG = 0xC0202020;
	public static final int COLOUR_CHIP_HOVER = 0xC0404040;
	public static final int COLOUR_CHIP_SELECTED = 0xC0606060;
	public static final int COLOUR_BORDER = 0xFF000000;
	public static final int COLOUR_BORDER_HIGHLIGHT = 0xFFFFFF55;

	public static final int COLOUR_LOCKED = 0xC0202020;
	public static final int COLOUR_ACTIVE = 0xC0142D14;
	public static final int COLOUR_CONFLICT = 0xC02D1414;

	public static final int COLOUR_TAB_BG = 0xFF333333;
	public static final int COLOUR_TAB_HOVERED = 0xFF444444;

	private AttributeListPanel listPanel;
	private AttributeDetailPanel detailPanel;

	private final Map<String, Integer> pendingLevels = new HashMap<>();
	private final Map<String, Boolean> pendingActive = new HashMap<>();

	@Nullable
	private Attribute selected;
	private boolean showAll;

	private final boolean showDirtBackground;

	public AttributeScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.attributes.title"));
		this.showDirtBackground = showDirtBackground;
	}

	@Override
	protected void init() {
		super.init();

		Player player = this.getMinecraft().player;
		if (player != null) {
			ProgressionAttachment p = ProgressionAttachment.get(player);
			for (Attribute attr : AttributeRegistry.all()) {
				String key = attr.getId().getPath();
				int level = p.getUpgradeData().getAttributeLevel(attr.getId());
				pendingLevels.put(key, level);
				pendingActive.put(key, level > 0);
				// pendingActive.put();
				// to be used later
			}
		}

		int panelTop = 10;
		int panelBottom = this.height - FOOTER_H - 4;
		int panelHeight = panelBottom - panelTop;

		this.listPanel = new AttributeListPanel(this, 10, panelTop, LIST_PANEL_W, panelHeight);

		int detailLeft = 10 + LIST_PANEL_W + 8;
		int detailWidth = this.width - detailLeft - 10;
		this.detailPanel = new AttributeDetailPanel(this, detailLeft, panelTop, detailWidth, panelHeight);

		int btnY = this.height - FOOTER_H + (FOOTER_H - 20) / 2;
		int btnW = 100;
		int doneX = this.width / 2 + 4;
		int cancelX = this.width / 2 - 4 - btnW;

		this.addRenderableWidget(
				Button.builder(
						Component.translatable("gui.cancel"),
						btn -> this.onCancel()
				).bounds(cancelX, btnY, btnW, 20).build()
		);

		this.addRenderableWidget(
				Button.builder(
						Component.translatable("gui.done"),
						btn -> this.onDone()
				).bounds(doneX, btnY, btnW, 20).build()
		);
	}

	private void onDone() {
		if (this.minecraft != null) {
			this.minecraft.setScreen(null);
		}
	}

	private void onCancel() {
		if (this.minecraft != null) {
			this.minecraft.setScreen(null);
		}
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		this.listPanel.render(guiGraphics, mouseX, mouseY);
		this.detailPanel.render(guiGraphics, mouseX, mouseY);
		super.render(guiGraphics, mouseX, mouseY, delta);
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			if (this.listPanel.mouseClicked(mouseX, mouseY)) return true;
			if (this.detailPanel.mouseClicked(mouseX, mouseY)) return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (this.listPanel.mouseScrolled(mouseX, mouseY, scrollY)) return true;
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (this.listPanel.mouseDragged(mouseX, mouseY)) return true;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.listPanel.mouseReleased();
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Nullable
	public Attribute getSelected() {
		return selected;
	}

	public void setSelected(@Nullable Attribute a) {
		this.selected = a;
	}

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean v) {
		if (this.showAll == v) return;
		this.showAll = v;
	}

	public int getPendingLevel(Attribute attr) {
		return pendingLevels.getOrDefault(attr.getId().getPath(), 0);
	}

	public void setPendingLevel(Attribute attr, int level) {
		pendingLevels.put(attr.getId().getPath(), Math.clamp(level, 0, attr.getMaxLevel()));
	}

	public boolean getPendingActive(Attribute attr) {
		return pendingActive.getOrDefault(attr.getId().getPath(), false);
	}

	public void setPendingActive(Attribute attr, boolean active) {
		if (active && conflictsWithPendingActive(attr)) return;;
		pendingActive.put(attr.getId().getPath(), active);
	}

	public boolean conflictsWithPendingActive(Attribute attr) {
		for (ResourceLocation other : attr.getIncompatibleWith()) {
			Attribute o = AttributeRegistry.get(other);
			if (o != null && getPendingActive(o)) return true;
		}
		return false;
	}

	public boolean isObtained(Attribute attr) {
		Player p = this.getMinecraft().player;
		if (p == null) return false;
		return ProgressionAttachment.get(p).getUpgradeData().hasAttribute(attr.getId());
	}

	public int getObtainedLevel(Attribute attr) {
		Player p = this.getMinecraft().player;
		if (p == null) return 0;
		return ProgressionAttachment.get(p).getUpgradeData().getAttributeLevel(attr.getId());
	}
}
