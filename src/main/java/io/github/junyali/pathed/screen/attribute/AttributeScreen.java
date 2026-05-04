package io.github.junyali.pathed.screen.attribute;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.data.attribute.AttributeRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AttributeScreen extends Screen {
	private static final int FRAME_BORDER = 9;
	private static final int LIST_PANEL_W = 124;
	private static final int FOOTER_H = 28;

	private static final int COLOUR_TEXT = 0xFFFFFFFF;
	private static final int COLOUR_TEXT_DIM = 0xFFAAAAAA;
	private static final int COLOUR_TEXT_MUTED = 0xFF555555;
	private static final int COLOUR_TEXT_HIGHLIGHT = 0xFFFFFF55;
	private static final int COLOUR_TEXT_GOOD = 0xFF55FF55;
	private static final int COLOUR_TEXT_BAD = 0xFFFF5555;

	private static final int COLOUR_PANEL_BG = 0xFF555555;
	private static final int COLOUR_CHIP_BG = 0xC0202020;
	private static final int COLOUR_CHIP_HOVER = 0xC0404040;
	private static final int COLOUR_CHIP_SELECTED = 0xC0606060;
	private static final int COLOUR_BORDER = 0xFF000000;
	private static final int COLOUR_BORDER_HIGHLIGHT = 0xFFFFFF55;

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

		// this.listPanel

		// this.detailPanel

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
	public boolean isPauseScreen() {
		return false;
	}
}
