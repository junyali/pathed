package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.classsystem.PathedClass;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class PathedScreens extends Screen {
	// Code taken from Origins NeoForge port
	// SOURCE: https://github.com/UltrusBot/AltOriginGui

	private static final ResourceLocation WINDOW_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/sprites/background.png");
	private static final ResourceLocation WINDOW_BORDER = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/sprites/border.png");
	private static final ResourceLocation WINDOW_NAME_PLATE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "textures/gui/sprites/name_plate.png");

	protected static final int WINDOW_WIDTH = 176;
	protected static final int WINDOW_HEIGHT = 182;

	protected final boolean showDirtBackground;

	private PathedClass currentClass;
	private int currentMaxScroll = 0;

	protected int guiTop, guiLeft;
	protected int scrollPos = 0;

	public PathedScreens(Component title, boolean showDirtBackground) {
		super(title);
		this.showDirtBackground = showDirtBackground;
	}

	public void showClass(PathedClass pathedClass) {
		this.currentClass = pathedClass;
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
		this.renderClassWindow(guiGraphics, mouseX, mouseY, delta);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
		int newScrollPos = this.scrollPos - (int) vertical * 4;
		this.scrollPos = Mth.clamp(newScrollPos, 0, this.currentMaxScroll);
		return super.mouseScrolled(x, y, horizontal, vertical);
	}

	public PathedClass getCurrentClass() {
		return this.currentClass;
	}

	protected void renderClassWindow(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		guiGraphics.blit(WINDOW_BACKGROUND, this.guiLeft, this.guiTop, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		if (this.currentClass != null) {
			guiGraphics.enableScissor(this.guiLeft, this.guiTop, this.guiLeft + WINDOW_WIDTH, this.guiTop + WINDOW_HEIGHT);
			this.renderClassContent(guiGraphics);
			guiGraphics.disableScissor();
		}

		guiGraphics.blit(WINDOW_BORDER, this.guiLeft, this.guiTop, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		guiGraphics.blit(WINDOW_NAME_PLATE, this.guiLeft + 10, this.guiTop + 10, 0, 0, 150, 26);
		if (this.currentClass != null) {
			this.renderClassName(guiGraphics);
			guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, this.guiTop - 15, 0xFFFFFF);
		}
	}

	protected void renderClassName(GuiGraphics guiGraphics) {
		Component name = Component.translatable(this.currentClass.getTranslatableName());
		guiGraphics.drawString(this.font, name, this.guiLeft + 38, this.guiTop + 18, 0xFFFFFF);
		guiGraphics.renderItem(this.currentClass.getStartingTool(), this.guiLeft + 15, this.guiTop + 15);
	}

	protected void renderClassContent(GuiGraphics guiGraphics) {
		int textWidthLimit = WINDOW_WIDTH - 48;
		int x = this.guiLeft + 18;
		int y = this.guiTop + 45 - this.scrollPos;

		for (FormattedCharSequence line : this.font.split(Component.literal(this.currentClass.getDescription()), textWidthLimit)) {
			guiGraphics.drawString(this.font, line, x + 2, y, 0xCCCCCC);
			y += 12;
		}

		y += 12;

		guiGraphics.drawString(this.font, Component.literal("Starting Tool").withStyle(s -> s.withUnderlined(true)), x, y, 0xFFFFFF);
		y += 14;
		guiGraphics.renderItem(this.currentClass.getStartingTool(), x + 2, y);
		y += this.scrollPos + 30;
		this.currentMaxScroll = Math.max(0, y - 14 - (this.guiTop + 158));
	}
}
