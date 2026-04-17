package io.github.junyali.pathed.screen.progression.components;

import io.github.junyali.pathed.screen.progression.ProgressionScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CategoryButton extends Button {
	private static final int COLOUR_BUTTON = 0xFF2A2A2A;
	private static final int COLOUR_BUTTON_HOVER = 0xFFA3A3A3;
	private static final int COLOUR_BUTTON_SELECTED = 0xFF4A4A4A;

	private final String categoryId;
	private final ItemStack iconStack;
	private final ProgressionScreen screen;

	public CategoryButton(int x, int y, int width, int height, Component message, String categoryId, String iconItem, ProgressionScreen screen) {
		super(x, y, width, height, message, btn -> screen.selectCategory(categoryId), DEFAULT_NARRATION);
		this.categoryId = categoryId;
		this.iconStack = BuiltInRegistries.ITEM.get(ResourceLocation.parse(iconItem)).getDefaultInstance();
		this.screen = screen;
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		boolean isSelected = this.categoryId.equals(this.screen.selectedCategory);
		boolean isHovered = this.isHovered();

		int colour = isSelected ? COLOUR_BUTTON_SELECTED : (isHovered ? COLOUR_BUTTON_HOVER : COLOUR_BUTTON);
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, colour);

		int borderColour = isSelected ? 0xFF888888 : 0xFF444444;
		guiGraphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, borderColour);

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, -150);
		guiGraphics.renderItem(this.iconStack, this.getX() + 2, this.getY() + 2);
		guiGraphics.pose().popPose();

		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(0.8f, 0.8f, 1.0f);
		guiGraphics.drawString(
				this.screen.getMinecraft().font,
				this.getMessage(),
				(int) ((this.getX() + 20) / 0.8f),
				(int) ((this.getY() + (this.height - 8) / 2) / 0.8f),
				ProgressionScreen.COLOUR_TEXT
		);
		guiGraphics.pose().popPose();
	}
}
