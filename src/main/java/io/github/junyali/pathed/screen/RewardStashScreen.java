package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.Pathed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardStashScreen extends Screen {
	private static final ResourceLocation CONTAINER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

	private static final int CHEST_WIDTH = 176;
	private static final int CHEST_HEIGHT = 222;
	private static final int SLOTS_PER_ROW = 9;
	private static final int VISIBLE_ROWS = 6;
	private static final int SLOT_SIZE = 18;
	private static final int SLOTS_START_X = 8;
	private static final int SLOTS_START_Y = 18;

	private final boolean showDirtBackground;
	// TODO: replace this w/ actual rewards idk
	private final List<ItemStack> rewards;

	private int guiLeft;
	private int guiTop;

	public RewardStashScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.reward_stash.title"));
		this.showDirtBackground = showDirtBackground;
		this.rewards = new ArrayList<>();
	}

	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - CHEST_WIDTH) / 2;
		this.guiTop = (this.height - CHEST_HEIGHT) / 2;
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		if (this.showDirtBackground) {
			super.render(guiGraphics, mouseX, mouseY, delta);
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

		renderChestBackground(guiGraphics);
		renderTitle(guiGraphics);
		renderRewardSlots(guiGraphics, mouseX, mouseY);
	}

	private void renderChestBackground(GuiGraphics guiGraphics) {
		guiGraphics.blit(
				CONTAINER_TEXTURE,
				this.guiLeft,
				this.guiTop,
				0,
				0,
				CHEST_WIDTH,
				CHEST_HEIGHT,
				256,
				256
		);
	}

	private void renderTitle(GuiGraphics guiGraphics) {
		guiGraphics.drawString(
				this.font,
				this.title,
				this.guiLeft + 8,
				this.guiTop + 6,
				0x404040,
				false
		);
	}

	private void renderRewardSlots(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int startIndex = 0;
		int endIndex = Math.min(startIndex + (VISIBLE_ROWS * SLOTS_PER_ROW), this.rewards.size());

		for (int i = startIndex; i < endIndex; i++) {
			int relativeIndex = i - startIndex;
			int row = relativeIndex / SLOTS_PER_ROW;
			int col = relativeIndex % SLOT_SIZE;

			int slotX = this.guiLeft + SLOTS_START_X + col * SLOT_SIZE;
			int slotY = this.guiTop + SLOTS_START_Y + row * SLOT_SIZE;

			ItemStack reward = this.rewards.get(i);
			renderRewardItem(guiGraphics, reward, slotX, slotY, mouseX, mouseY);
		}
	}

	private void renderRewardItem(GuiGraphics guiGraphics, ItemStack reward, int x, int y, int mouseX, int mouseY) {
		if (isMouseOverSlot(mouseX, mouseY, x, y)) {
			guiGraphics.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
		}
	}

	private boolean isMouseOverSlot(int mouseX, int mouseY, int slotX, int slotY) {
		return mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16;
	}
}
