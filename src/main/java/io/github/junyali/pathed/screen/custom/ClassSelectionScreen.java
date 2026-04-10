package io.github.junyali.pathed.screen.custom;

import io.github.junyali.pathed.classsystem.PathedClass;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ClassSelectionScreen extends Screen {

	private static final int PANEL_WIDTH = 160;
	private static final int PANEL_HEIGHT = 180;
	private static final int ENTRY_HEIGHT = 28;
	private static final int ICON_SIZE = 16;
	private static final int PADDING = 8;

	private PathedClass selectedClass = PathedClass.selectableValues()[0];
	private Button confirmButton;

	public ClassSelectionScreen() {
		super(Component.literal("meow?"));
	}

	@Override
	protected void init() {
		int leftPanelX = (this.width / 2) - PANEL_WIDTH - 10;
		int panelY = (this.height - PANEL_HEIGHT) / 2;

		for (int i = 0; i < PathedClass.selectableValues().length; i++) {
			PathedClass info = PathedClass.selectableValues()[i];
			int entryY = panelY + PADDING + (i * ENTRY_HEIGHT);

			this.addRenderableWidget(Button.builder(Component.translatable(info.getTranslatableName()),
				btn -> {
					selectedClass = info;
					rebuildWidgets();
				})
				.pos(leftPanelX + PADDING, entryY)
				.size(PANEL_WIDTH - (PADDING * 2), ENTRY_HEIGHT - 4)
				.build()
			);
		}

		int confirmY = (this.height / 2) + (PANEL_HEIGHT / 2) + 5;
		this.confirmButton = this.addRenderableWidget(Button.builder(
				Component.translatable("meow"),
				btn -> {
					// send confirm packet
					if (this.minecraft != null) {
						this.minecraft.setScreen(null);
					}
				})
				.pos((this.width / 2) - 50, confirmY)
				.size(100, 20)
				.build()
		);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(guiGraphics, mouseX, mouseY, delta);

		int leftPanelX = (this.width / 2) - PANEL_WIDTH - 10;
		int rightPanelX = (this.width / 2) + 10;
		int panelY = (this.height - PANEL_HEIGHT) / 2;

		guiGraphics.fill(leftPanelX, panelY, leftPanelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA000000);
		guiGraphics.fill(rightPanelX, panelY, rightPanelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xAA000000);

		int selectedIndex = Arrays.asList(PathedClass.selectableValues()).indexOf(selectedClass);
		int highlightY = panelY + PADDING + (selectedIndex * ENTRY_HEIGHT);
		guiGraphics.fill(leftPanelX + PADDING - 1, highlightY - 1, leftPanelX + PANEL_WIDTH - PADDING + 1, highlightY + ENTRY_HEIGHT - 5, 0x44FFFFFF);

		for (int i = 0; i < PathedClass.selectableValues().length; i++) {
			PathedClass info = PathedClass.selectableValues()[i];
			int entryY = panelY + PADDING + (i * ENTRY_HEIGHT);
			guiGraphics.renderItem(info.getStartingTool(), leftPanelX + PADDING - 18, entryY + 2);
		}

		guiGraphics.drawCenteredString(this.font, Component.translatable(selectedClass.getTranslatableName()), rightPanelX + PANEL_WIDTH / 2, panelY + PADDING, 0xFFD700);

		// i have no idea what i'm doing here D:
		List<FormattedCharSequence> lines = this.font.split(
				Component.literal(selectedClass.getDescription()), PANEL_WIDTH - (PADDING * 2)
		);

		int lineY = panelY + PADDING + 16;
		for (FormattedCharSequence line : lines) {
			guiGraphics.drawString(this.font, line, rightPanelX + PADDING, lineY, 0xDDDDDD);
			lineY += this.font.lineHeight + 2;
		}

		guiGraphics.drawCenteredString(this.font, Component.literal("Starting Tool"), rightPanelX + PANEL_WIDTH / 2, panelY + PANEL_HEIGHT - 36, 0xAAAAAA);
		guiGraphics.renderItem(selectedClass.getStartingTool(), rightPanelX + (PANEL_WIDTH / 2) - 8, panelY + PANEL_HEIGHT - 24);
		guiGraphics.drawCenteredString(this.font, Component.literal("Choose Your Path"), this.width / 2, panelY - 16, 0xFFFFFF);

		super.render(guiGraphics, mouseX, mouseY, delta);
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
}
