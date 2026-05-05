package io.github.junyali.pathed.screen.attribute;

import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.data.attribute.AttributeRegistry;
import io.github.junyali.pathed.screen.attribute.components.LevelPipBar;
import io.github.junyali.pathed.screen.attribute.components.ToggleSwitch;
import io.github.junyali.pathed.screen.progression.ProgressionRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class AttributeDetailPanel {
	private static final int ICON_BOX = 24;
	private static final int PADDING = 8;

	private final AttributeScreen screen;

	private final int left;
	private final int top;
	private final int width;
	private final int height;

	private int pipRowX = -1;
	private int pipRowY = -1;

	private int toggleX = -1;
	private int toggleY = -1;

	public AttributeDetailPanel(AttributeScreen screen, int left, int top, int width, int height) {
		this.screen = screen;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		ProgressionRenderer.renderBorder(guiGraphics, left, top, width, height);

		int innerL = left + AttributeScreen.FRAME_BORDER;
		int innerT = top + AttributeScreen.FRAME_BORDER;
		int innerW = width - 2 * AttributeScreen.FRAME_BORDER;
		int innerH = height - 2 * AttributeScreen.FRAME_BORDER;
		guiGraphics.fill(innerL, innerT, innerL + innerW, innerT + innerH, AttributeScreen.COLOUR_PANEL_BG);

		Font font = screen.getMinecraft().font;
		Attribute attr = screen.getSelected();
		if (attr == null) {
			String hint = Component.translatable("pathed.gui.attributes.detail.no_selection").getString();
			guiGraphics.drawString(font, hint, innerL + (innerW - font.width(hint)) / 2, innerT + (innerH - font.lineHeight) / 2, AttributeScreen.COLOUR_TEXT_DIM, false);
			pipRowY = -1;
			toggleY = -1;
			return;
		}

		boolean obtained = screen.isObtained(attr);
		boolean active = screen.getPendingActive(attr);
		boolean conflicts = screen.conflictsWithPendingActive(attr);

		int currentLevel = screen.getPendingLevel(attr);
		int obtainedLevel = screen.getObtainedLevel(attr);

		int cX = innerL + PADDING;
		int cY = innerT + PADDING;
		int contentW = innerW - PADDING * 2;

		guiGraphics.fill(cX, cY, cX + ICON_BOX, cY + ICON_BOX, 0xFF333333);
		outline(guiGraphics, cX, cY, ICON_BOX, ICON_BOX, AttributeScreen.COLOUR_BORDER);
		guiGraphics.renderItem(attr.getIcon(), cX + 4, cY + 4);

		int textX = cX + ICON_BOX + 6;
		guiGraphics.drawString(font, Component.translatable(attr.getNameKey()).copy().withStyle(style -> style.withBold(true)), textX, cY + 2, AttributeScreen.COLOUR_TEXT_HIGHLIGHT, false);
		guiGraphics.drawString(font, attr.getId().toString(), textX, cY + 2 + font.lineHeight + 1, AttributeScreen.COLOUR_TEXT_DIM, false);

		cY += ICON_BOX + PADDING;

		List<FormattedCharSequence> lines = font.split(Component.translatable(attr.getDescriptionKey()), contentW);
		for (FormattedCharSequence line : lines) {
			guiGraphics.drawString(font, line, cX, cY, AttributeScreen.COLOUR_TEXT, false);
			cY += font.lineHeight + 1;
		}

		cY += 4;
		guiGraphics.fill(cX, cY, cX + contentW, cY + 1, AttributeScreen.COLOUR_BORDER);
		cY += 6;

		String levelLabel = Component.translatable("pathed.gui.attributes.detail.level").getString();
		guiGraphics.drawString(font, levelLabel, cX, cY + 1, AttributeScreen.COLOUR_TEXT_DIM, false);
		pipRowX = cX + font.width(levelLabel) + 8;
		pipRowY = cY;

		String levelInfo = obtained
				? currentLevel + "/" + obtainedLevel
				: Component.translatable("pathed.gui.attributes.detail.locked").getString();
		guiGraphics.drawString(font, levelInfo, pipRowX + LevelPipBar.totalWidth(attr.getMaxLevel()) + 6, cY + 1, obtained ? AttributeScreen.COLOUR_TEXT_DIM : AttributeScreen.COLOUR_TEXT_MUTED, false);
		cY += LevelPipBar.HEIGHT + PADDING;

		if (obtained) {
			String activeLabel = Component.translatable("pathed.gui.attributes.detail.active").getString();
			guiGraphics.drawString(font, activeLabel, cX, cY + 2, AttributeScreen.COLOUR_TEXT_DIM, false);
			toggleX = cX + font.width(activeLabel) + 8;
			toggleY = cY;

			ToggleSwitch.render(guiGraphics, toggleX, cY, active, mouseX, mouseY);

			Component statusText = active
					? Component.translatable("pathed.gui.attributes.detail.active.on")
					: Component.translatable("pathed.gui.attributes.detail.active.off");

			int statusColour = active ? AttributeScreen.COLOUR_TEXT_GOOD : AttributeScreen.COLOUR_TEXT_MUTED;

			guiGraphics.drawString(font, statusText, toggleX + ToggleSwitch.WIDTH + 6, cY + 2, statusColour, false);
			cY += ToggleSwitch.HEIGHT + PADDING;

			if (conflicts && !active) {
				MutableComponent warning = buildConflictWarning(attr);
				List<FormattedCharSequence> warnLines = font.split(warning, contentW);
				for (FormattedCharSequence line : warnLines) {
					guiGraphics.drawString(font, line, cX, cY, AttributeScreen.COLOUR_TEXT_BAD, false);
					cY += font.lineHeight + 1;
				}
			}
		} else {
			toggleY = -1;
			guiGraphics.drawString(font, Component.translatable("pathed.gui.attributes.detail.locked.hint"), cX, cY + 2, AttributeScreen.COLOUR_TEXT_MUTED, false);
		}
	}

	public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		Attribute attr = screen.getSelected();
		if (attr == null || pipRowY < 0) return;
		if (!screen.isObtained(attr)) return;
		int hovered = LevelPipBar.hoveredIndex(pipRowX, pipRowY, attr.getMaxLevel(), mouseX, mouseY);
		if (hovered <= 0) return;
		Component line = (hovered <= screen.getObtainedLevel(attr))
				? Component.translatable("pathed.gui.attributes.detail.pip.set", hovered)
				: Component.translatable("pathed.gui.attributes.detail.pip.locked");
		guiGraphics.renderTooltip(screen.getMinecraft().font, line, mouseX, mouseY);
	}

	public boolean mouseClicked(double mouseX, double mouseY) {
		Attribute attr = screen.getSelected();
		if (attr == null) return false;

		if (pipRowY >= 0 && screen.isObtained(attr)) {
			int index = LevelPipBar.hoveredIndex(pipRowX, pipRowY, attr.getMaxLevel(), (int) mouseX, (int) mouseY);
			if (index > 0 && index <= screen.getObtainedLevel(attr)) {
				screen.setPendingLevel(attr, index);
				return true;
			}
		}

		if (toggleY >= 0 && ToggleSwitch.contains(toggleX, toggleY, mouseX, mouseY)) {
			screen.setPendingActive(attr, !screen.getPendingActive(attr));
			return true;
		}

		return false;
	}

	private MutableComponent buildConflictWarning(Attribute attr) {
		List<String> names = new ArrayList<>();
		for (ResourceLocation rl : attr.getIncompatibleWith()) {
			Attribute other = AttributeRegistry.get(rl);
			if (other != null && screen.getPendingActive(other)) {
				names.add(Component.translatable(other.getNameKey()).getString());
			}
		}
		return Component.translatable("pathed.gui.attributes.detail.conflict", String.join(", ", names));
	}

	private static void outline(GuiGraphics guiGraphics, int x, int y, int w, int h, int colour) {
		guiGraphics.fill(x, y, x + w, y + 1, colour);
		guiGraphics.fill(x, y + h - 1, x + w, y + h, colour);
		guiGraphics.fill(x, y, x + 1, y + h, colour);
		guiGraphics.fill(x + w - 1, y, x + w, y + h, colour);
	}
}
