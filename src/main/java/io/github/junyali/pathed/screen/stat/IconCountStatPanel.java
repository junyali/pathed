package io.github.junyali.pathed.screen.stat;

import io.github.junyali.pathed.screen.common.PanelRenderer;
import io.github.junyali.pathed.screen.common.ScrollBar;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public abstract class IconCountStatPanel<K> extends AbstractStatPanel {
	public enum SortMode { COUNT_DESC, ALPHA_ASC }

	private static final int COLOUR_CELL_BACKGROUND = 0xFF373737;
	private static final int COLOUR_CELL_HOVER = 0xFF505050;
	private static final int COLOUR_CELL_BORDER_HIGH = 0xFFFFFFFF;
	private static final int COLOUR_CELL_BORDER_LOW = 0xFF555555;

	private static final int SEARCH_H = 16;
	private static final int BUTTON_H = 16;
	private static final int BUTTON_W = 70;
	private static final int BUTTON_GAP = 2;
	private static final int TITLE_H = 12;
	private static final int CELL_GAP = 4;
	private static final int PADDING = 6;

	private final int cellSizeX;
	private final int cellSizeY;

	private final ScrollBar scrollBar = new ScrollBar();
	private final List<AbstractWidget> ownedWidgets = new ArrayList<>();

	private EditBox searchBox;
	private Button sortCountButton;
	private Button sortAlphaButton;

	private SortMode sortMode = SortMode.COUNT_DESC;
	private String filter = "";
	private List<Entry<K>> visible = new ArrayList<>();

	protected record Entry<K>(K key, int count, ItemStack icon, Component name) {}

	protected IconCountStatPanel(int x, int y, int w, int h, int cellSizeX, int cellSizeY) {
		super(x, y, w, h);
		this.cellSizeX = cellSizeX;
		this.cellSizeY = cellSizeY;
	}

	@Override
	public abstract Component getTitle();

	protected abstract Map<K, Integer> data();

	protected abstract ItemStack iconFor(K key);

	protected abstract Component nameFor(K key);

	protected List<Component> extraTooltip(K key, int count) {
		return List.of();
	}

	public void initWidgets(Consumer<AbstractWidget> register) {
		ownedWidgets.clear();
		int b = PanelRenderer.FRAME_BORDER;

		int searchAndButtonY = panelY + b + PADDING + TITLE_H;
		int totalButtonWidth = BUTTON_W * 2 + BUTTON_GAP;
		int searchWidth = panelWidth - (b + PADDING) * 2 - totalButtonWidth - (BUTTON_GAP * 2);

		searchBox = new EditBox(font, panelX + b + PADDING, searchAndButtonY, searchWidth, SEARCH_H, Component.translatable("pathed.gui.stats.search"));
		searchBox.setHint(Component.translatable("pathed.gui.stats.search").withStyle(ChatFormatting.DARK_GRAY));
		searchBox.setResponder(s -> { filter = s.toLowerCase(Locale.ROOT); rebuild(); });

		int firstButtonX = panelX + b + PADDING + searchWidth + (BUTTON_GAP * 2);
		sortCountButton = Button.builder(Component.translatable("pathed.gui.stats.sort.count"), btn -> { sortMode = SortMode.COUNT_DESC; rebuild(); }).bounds(firstButtonX, searchAndButtonY, BUTTON_W, BUTTON_H).build();
		sortAlphaButton = Button.builder(Component.translatable("pathed.gui.stats.sort.alpha"), btn -> { sortMode = SortMode.ALPHA_ASC; rebuild(); }).bounds(firstButtonX + BUTTON_W + BUTTON_GAP, searchAndButtonY, BUTTON_W, BUTTON_H).build();

		Consumer<AbstractWidget> tracking = w -> { ownedWidgets.add(w); register.accept(w); };
		tracking.accept(searchBox);
		tracking.accept(sortCountButton);
		tracking.accept(sortAlphaButton);
		rebuild();
	}

	public void removeFrom(Consumer<AbstractWidget> remover) {
		for (AbstractWidget w : ownedWidgets) {
			remover.accept(w);
		}
		ownedWidgets.clear();
	}

	protected void rebuild() {
		visible.clear();
		for (Map.Entry<K, Integer> e : data().entrySet()) {
			Component name = nameFor(e.getKey());
			if (!filter.isEmpty() && !name.getString().toLowerCase(Locale.ROOT).contains(filter)) continue;
			visible.add(new Entry<>(e.getKey(), e.getValue(), iconFor(e.getKey()), name));
		}
		Comparator<Entry<K>> comparator = switch (sortMode) {
			case COUNT_DESC -> Comparator.<Entry<K>>comparingInt(Entry::count).reversed();
			case ALPHA_ASC  -> Comparator.comparing(e -> e.name().getString(), String.CASE_INSENSITIVE_ORDER);
		};
		visible.sort(comparator);

		int b = PanelRenderer.FRAME_BORDER;
		int gridTop = gridTop();
		int gridH = panelY + panelHeight - gridTop - PADDING - b;
		int usableW = panelWidth - (b + PADDING) * 2 - scrollBar.getWidth() - 2;
		int cols = Math.max(1, (usableW + CELL_GAP) / (cellSizeX + CELL_GAP));
		int rows = (int) Math.ceil(visible.size() / (double) cols);
		int contentH = Math.max(0, rows * (cellSizeY + CELL_GAP) - CELL_GAP);
		scrollBar.setMaxScroll(Math.max(0, contentH - gridH));
		scrollBar.setBounds(panelX + panelWidth - PADDING - b - scrollBar.getWidth(), gridTop, gridH);
	}

	private int gridTop() {
		int b = PanelRenderer.FRAME_BORDER;
		return panelY + b + PADDING + TITLE_H + SEARCH_H + 2 + BUTTON_H + 4;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		int b = PanelRenderer.FRAME_BORDER;
		guiGraphics.fill(panelX + b, panelY + b, panelX + panelWidth - b, panelY + panelHeight - b, COLOUR_PANEL_BACKGROUND);
		PanelRenderer.renderBorder(guiGraphics, panelX, panelY, panelWidth, panelHeight);

		guiGraphics.drawString(
				font,
				getTitle(),
				panelX + b + PADDING,
				panelY + b + PADDING,
				COLOUR_TEXT,
				false
		);

		int gridTop = gridTop();
		int gridH = panelY + panelHeight - gridTop - PADDING - b;
		int gridLeft = panelX + PADDING + b;
		int gridRight = panelX + panelWidth - PADDING - b - (scrollBar.isVisible() ? scrollBar.getWidth() + 2 : 0);

		guiGraphics.enableScissor(gridLeft, gridTop, gridRight, gridTop + gridH);
		int cols = Math.max(1, (gridRight - gridLeft + CELL_GAP) / (cellSizeX + CELL_GAP));
		int scroll = scrollBar.getScroll();

		Entry<K> hovered = null;
		for (int i = 0; i < visible.size(); i++) {
			int row = i / cols;
			int col = i % cols;
			int cX = gridLeft + col * (cellSizeX + CELL_GAP);
			int cY = gridTop + row * (cellSizeY + CELL_GAP) - scroll;
			if (cY + cellSizeY < gridTop || cY > gridTop + gridH) continue;
			Entry<K> e = visible.get(i);
			boolean isHov = isHovered(mouseX, mouseY, cX, cY, cellSizeX, cellSizeY) && mouseY >= gridTop && mouseY < gridTop + gridH;
			renderCell(guiGraphics, e, cX, cY, isHov);
			if (isHov) {
				hovered = e;
			}
		}
		guiGraphics.disableScissor();

		scrollBar.render(guiGraphics, mouseX, mouseY);

		if (hovered != null) {
			List<Component> lines = new ArrayList<>();
			lines.add(hovered.name());
			lines.add(Component.translatable("pathed.gui.stats.tooltip.count", formatNumber(hovered.count())).withStyle(ChatFormatting.GRAY));
			lines.addAll(extraTooltip(hovered.key(), hovered.count()));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}
	}

	private void renderCell(GuiGraphics guiGraphics, Entry<K> e, int x, int y, boolean hovered) {
		guiGraphics.fill(x, y, x + cellSizeX, y + cellSizeY, hovered ? COLOUR_CELL_HOVER : COLOUR_CELL_BACKGROUND);
		guiGraphics.fill(x, y, x + cellSizeX, y + 1, COLOUR_CELL_BORDER_LOW);
		guiGraphics.fill(x, y, x + 1, y + cellSizeY, COLOUR_CELL_BORDER_LOW);
		guiGraphics.fill(x, y + cellSizeY - 1, x + cellSizeX, y + cellSizeY, COLOUR_CELL_BORDER_HIGH);
		guiGraphics.fill(x + cellSizeX - 1, y, x + cellSizeX, y + cellSizeY, COLOUR_CELL_BORDER_HIGH);
		renderIcon(guiGraphics, e, x, y);
		String countStr = compact(e.count());
		int textY = y + cellSizeY - font.lineHeight - 3;
		guiGraphics.drawString(
				font,
				countStr,
				x + cellSizeX - font.width(countStr) - 3,
				textY,
				COLOUR_TEXT,
				true
		);
	}

	protected void renderIcon(GuiGraphics guiGraphics, Entry<K> e, int cellX, int cellY) {
		int iconX = cellX + (cellSizeX - 16) / 2;
		int iconY = cellY + 2;
		guiGraphics.renderItem(e.icon(), iconX, iconY);
	}

	private static String compact(int n) {
		// TODO: find a better way to do this, or at the very least, expand it
		if (n >= 1_000_000) return String.format(Locale.ROOT, "%.1fM", n / 1_000_000.0);
		if (n >= 10_000) return String.format(Locale.ROOT, "%.1fk", n / 1_000.0);
		return String.format(Locale.ROOT, "%,d", n);
	}

	private static String formatNumber(int n) {
		return String.format(Locale.ROOT, "%,d", n);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return scrollBar.mouseClicked(mouseX, mouseY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		scrollBar.release();
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		return scrollBar.mouseDragged(mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double vertical) {
		if (!isHovered(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight)) return false;
		return scrollBar.mouseScrolled(vertical, cellSizeY / 8);
	}
}
