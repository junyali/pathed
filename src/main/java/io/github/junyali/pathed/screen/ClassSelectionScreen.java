package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.classsystem.PathedClass;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ClassSelectionScreen extends PathedScreens {
	// GUI Selection Screen inspired by UltrusBot/AltOriginGui, code ported to NeoForge
	// SOURCE: https://github.com/UltrusBot/AltOriginGui

	private static final ResourceLocation PATH_CHOICES = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/path_choices.png");
	private static final int CHOICES_WIDTH = 219;
	private static final int CHOICES_HEIGHT = 182;
	private static final int PATH_ICON_SIZE = 26;

	private final PathedClass[] selectableClasses;
	private int currentClassIndex = 0;
	private int calculatedTop;
	private int calculatedLeft;

	public ClassSelectionScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.choose_path.title"), showDirtBackground);
		this.selectableClasses = PathedClass.selectableValues();
		if (this.selectableClasses.length > 0) {
			this.showClass(this.selectableClasses[0]);
		}
	}

	@Override
	protected void init() {
		super.init();
		this.calculatedTop = (this.height - CHOICES_HEIGHT) / 2;
		this.calculatedLeft = (this.width - 405) / 2;
		this.guiTop = (this.height - CHOICES_HEIGHT) / 2;
		this.guiLeft = this.calculatedLeft + CHOICES_WIDTH + 10;

		int x = 0;
		int y = 0;

		for (int i = 0; i < this.selectableClasses.length; i++) {
			if (x > 6) {
				x = 0;
				y++;
			}

			int actualX = 12 + x * 28 + this.calculatedLeft;
			int actualY = 10 + y * 30 + this.calculatedTop;
			int finalI = i;

			this.addRenderableWidget(Button.builder(Component.empty(), btn -> {
				this.currentClassIndex = finalI;
				this.showClass(this.selectableClasses[finalI]);
			}).pos(actualX, actualY).size(PATH_ICON_SIZE, PATH_ICON_SIZE).build());

			x++;
		}

		if (this.selectableClasses.length > 0) {
			this.addRenderableWidget(Button.builder(
					Component.translatable("pathed.gui.select"),
					btn -> {
						PathedClass selected = this.getCurrentClass();
						// packet choose class
						this.onClose();
					}
			).bounds(this.guiLeft + 88 - 50, this.guiTop + CHOICES_HEIGHT + 5, 100, 200).build());
		}
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		super.render(guiGraphics, mouseX, mouseY, delta);
		this.renderClassChoicesBox(guiGraphics, mouseX, mouseY);
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

	private void renderClassChoicesBox(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.blit(PATH_CHOICES, this.calculatedLeft, this.calculatedTop, 0, 0, CHOICES_WIDTH, CHOICES_HEIGHT);

		int x = 0;
		int y = 0;

		for (int i = 0; i < this.selectableClasses.length; i++) {
			if (x > 6) {
				x = 0;
				y++;
			}

			int actualX = 12 + x * 28 + this.calculatedLeft;
			int actualY = 10 + y * 30 + this.calculatedTop;

			PathedClass pathedClass = this.selectableClasses[i];
			boolean selected = (i == this.currentClassIndex);

			this.renderClassWidget(guiGraphics, mouseX, mouseY, actualX, actualY, selected, pathedClass);
			guiGraphics.renderItem(pathedClass.getStartingTool(), actualX + 5, actualY);

			x++;
		}
	}

	private void renderClassWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, boolean selected, PathedClass pathedClass) {
		boolean mouseHovering = mouseX >= x && mouseY >= y && mouseX < x + PATH_ICON_SIZE && mouseY < y + PATH_ICON_SIZE;

		GuiEventListener focused = this.getFocused();
		boolean guiSelected = focused instanceof Button btn && btn.getX() == x && (btn.getY() == y || mouseHovering);

		int u = (selected ? PATH_ICON_SIZE : 0) + (guiSelected ? 52 : 0);
		guiGraphics.blit(PATH_CHOICES, x, y, 230, u, PATH_ICON_SIZE, PATH_ICON_SIZE);

		if (mouseHovering) {
			Component text = Component.translatable(pathedClass.getTranslatableName());
			guiGraphics.renderTooltip(this.font, text, mouseX, mouseY);
		}
	}
}
