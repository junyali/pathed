package io.github.junyali.pathed.screen.custom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ClassSelectionScreen extends Screen {
	public ClassSelectionScreen() {
		super(Component.literal("meow?"));
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	public void onClose() {
		if (this.minecraft != null) {
			this.minecraft.setScreen(new PauseScreen(true));
		}
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredString(
				this.font,
				"meow? do i work?",
				this.width / 2,
				this.height / 2,
				0xFFFFFF
		);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}
}
