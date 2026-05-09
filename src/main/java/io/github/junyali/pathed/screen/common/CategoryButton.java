package io.github.junyali.pathed.screen.common;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CategoryButton extends Button {
	private static final int COLOUR_BUTTON = 0xFF2A2A2A;
	private static final int COLOUR_BUTTON_HOVER = 0xFFA3A3A3;
	private static final int COLOUR_BUTTON_SELECTED = 0xFF4A4A4A;
	private static final int COLOUR_BORDER = 0xFF444444;
	private static final int COLOUR_BORDER_SELECTED = 0xFF888888;
	private static final int COLOUR_TEXT = 0xFFFFFFFF;

	private final String categoryId;
	private final ItemStack iconStack;
	private final CategoryHost host;

	public CategoryButton(int x, int y, int width, int height, Component message, String categoryId, ItemStack iconItem, CategoryHost host) {
		super(x, y, width, height, message, btn -> host.selectCategory(categoryId), DEFAULT_NARRATION);
		this.categoryId = categoryId;
		this.iconStack = iconItem;
		this.host = host;
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		boolean isSelected = this.categoryId.equals(this.host.getSelectedCategory());
		boolean isHovered = this.isHovered();

		int colour = isSelected ? COLOUR_BUTTON_SELECTED : (isHovered ? COLOUR_BUTTON_HOVER : COLOUR_BUTTON);
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, colour);

		int borderColour = isSelected ? COLOUR_BORDER_SELECTED : COLOUR_BORDER;
		guiGraphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, borderColour);

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, -150);
		guiGraphics.renderItem(this.iconStack, this.getX() + 2, this.getY() + 2);
		guiGraphics.pose().popPose();

		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(0.8f, 0.8f, 1.0f);
		guiGraphics.drawString(
				this.host.getMinecraft().font,
				this.getMessage(),
				(int) ((this.getX() + 20) / 0.8f),
				(int) ((this.getY() + (float) (this.height - 8) / 2) / 0.8f),
				COLOUR_TEXT
		);
		guiGraphics.pose().popPose();
	}
}
