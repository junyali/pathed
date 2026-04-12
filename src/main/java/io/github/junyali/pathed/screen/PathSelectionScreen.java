package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathRegistry;
import io.github.junyali.pathed.network.payload.c2s.ChoosePathPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PathSelectionScreen extends PathedScreens {
	// GUI Selection Screen inspired by UltrusBot/AltOriginGui, code ported to NeoForge
	// SOURCE: https://github.com/UltrusBot/AltOriginGui

	private static final ResourceLocation PATH_CHOICES = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/path_choices.png");
	private static final int CHOICES_WIDTH = 219;
	private static final int CHOICES_HEIGHT = 182;
	private static final int PATH_ICON_SIZE = 26;

	private final List<Path> selectablePaths;
	private int currentPathIndex = 0;
	private int calculatedTop;
	private int calculatedLeft;

	public PathSelectionScreen(boolean showDirtBackground) {
		super(Component.translatable("pathed.gui.choose_path.title"), showDirtBackground);
		this.selectablePaths = PathRegistry.selectable();
		if (!this.selectablePaths.isEmpty()) {
			this.showPath(this.selectablePaths.getFirst());
		}
	}

	@Override
	protected void init() {
		super.init();
		this.calculatedTop = (this.height - CHOICES_HEIGHT) / 2;
		this.calculatedLeft = (this.width - 405) / 2;
		this.guiTop = (this.height - WINDOW_HEIGHT) / 2;
		this.guiLeft = this.calculatedLeft + CHOICES_WIDTH + 10;

		int x = 0;
		int y = 0;

		for (int i = 0; i < this.selectablePaths.size(); i++) {
			if (x > 6) {
				x = 0;
				y++;
			}

			int actualX = 12 + x * 28 + this.calculatedLeft;
			int actualY = 10 + y * 30 + this.calculatedTop;
			int finalI = i;

			this.addRenderableWidget(Button.builder(Component.empty(), btn -> {
				this.currentPathIndex = finalI;
				this.showPath(this.selectablePaths.get(finalI));
			}).pos(actualX, actualY).size(PATH_ICON_SIZE, PATH_ICON_SIZE).build());

			x++;
		}

		if (!this.selectablePaths.isEmpty()) {
			this.addRenderableWidget(Button.builder(
					Component.translatable("pathed.gui.choose_path.select"),
					btn -> {
						Path selected = this.getCurrentPath();
						PacketDistributor.sendToServer(new ChoosePathPacket(selected.getId()));
						if (this.minecraft != null) {
							this.minecraft.setScreen(null);
						}
					}
			).bounds(this.guiLeft + 88 - 50, this.guiTop + CHOICES_HEIGHT + 5, 100, 20).build());
		}
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		super.render(guiGraphics, mouseX, mouseY, delta);
		this.renderPathChoicesBox(guiGraphics, mouseX, mouseY);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {

	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void renderPathChoicesBox(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.blit(PATH_CHOICES, this.calculatedLeft, this.calculatedTop, 0, 0, CHOICES_WIDTH, CHOICES_HEIGHT);

		int x = 0;
		int y = 0;

		for (int i = 0; i < this.selectablePaths.size(); i++) {
			if (x > 6) {
				x = 0;
				y++;
			}

			int actualX = 12 + x * 28 + this.calculatedLeft;
			int actualY = 10 + y * 30 + this.calculatedTop;

			Path path = this.selectablePaths.get(i);
			boolean selected = (i == this.currentPathIndex);

			this.renderClassWidget(guiGraphics, mouseX, mouseY, actualX, actualY, selected, path);
			this.renderPathIcon(guiGraphics, path.getIcon(), actualX + 5, actualY + 5);

			x++;
		}
	}

	private void renderClassWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, boolean selected, Path path) {
		boolean mouseHovering = mouseX >= x && mouseY >= y && mouseX < x + PATH_ICON_SIZE && mouseY < y + PATH_ICON_SIZE;

		GuiEventListener focused = this.getFocused();
		boolean guiSelected = focused instanceof Button btn && btn.getX() == x && (btn.getY() == y || mouseHovering);

		int u = (selected ? PATH_ICON_SIZE : 0) + (guiSelected ? 52 : 0);
		guiGraphics.blit(PATH_CHOICES, x, y, 230, u, PATH_ICON_SIZE, PATH_ICON_SIZE);

		if (mouseHovering) {
			guiGraphics.renderTooltip(this.font, path.getName(), mouseX, mouseY);
		}
	}
}
