package io.github.junyali.pathed.screen.stat;

import io.github.junyali.pathed.screen.common.PanelRenderer;
import io.github.junyali.pathed.screen.common.ScrollBar;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
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
	private static final int COLOUR_TOOLBAR_BACKGROUND = 0xFF252525;

	private static final int TOOLBAR_H = 20;
	private static final int SEARCH_H = 16;
	private static final int SORT_BUTTON = 16;
	private static final int CELL_GAP = 4;

	private final int targetCellW;
	private final int cellSizeY;

	private final ScrollBar scrollBar = new ScrollBar();
	private final List<AbstractWidget> ownedWidgets = new ArrayList<>();

	private EditBox searchBox;
	private Button sortButton;

	private SortMode sortMode = SortMode.COUNT_DESC;
	private String filter = "";
	private List<Entry<K>> visible = new ArrayList<>();

	protected record Entry<K>(K key, int count, ItemStack icon, Component name) {}

	protected IconCountStatPanel(int x, int y, int w, int h, int targetCellW, int cellSizeY) {
		super(x, y, w, h);
		this.targetCellW = targetCellW;
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
		int toolbarY = panelY + b + HEADER_HEIGHT + 1 + (TOOLBAR_H - SEARCH_H) / 2;

		int sortX = panelX + panelWidth - b - PADDING - SORT_BUTTON;
		int searchX = panelX + b + PADDING;
		int searchW = sortX - 4 - searchX;

		searchBox = new EditBox(font, searchX, toolbarY, searchW, SEARCH_H, Component.translatable("pathed.gui.stats.search"));
		searchBox.setHint(Component.translatable("pathed.gui.stats.search").withStyle(ChatFormatting.DARK_GRAY));
		searchBox.setResponder(s -> { filter = s.toLowerCase(Locale.ROOT); rebuild(); });

		sortButton = Button.builder(currentSortGlyph(), button -> toggleSort())
				.bounds(sortX, toolbarY, SORT_BUTTON, SORT_BUTTON)
				.tooltip(Tooltip.create(currentSortLabel()))
				.build();

		Consumer<AbstractWidget> tracking = w -> { ownedWidgets.add(w); register.accept(w); };
		tracking.accept(searchBox);
		tracking.accept(sortButton);
		rebuild();
	}

	private Component currentSortGlyph() {
		return Component.literal(sortMode == SortMode.COUNT_DESC ? "#" : "A");
	}

	private Component currentSortLabel() {
		return Component.translatable(sortMode == SortMode.COUNT_DESC
				? "pathed.gui.stats.sort.count"
				: "pathed.gui.stats.sort.alpha");
	}

	private void toggleSort() {
		sortMode = (sortMode == SortMode.COUNT_DESC) ? SortMode.ALPHA_ASC : SortMode.COUNT_DESC;
		sortButton.setMessage(currentSortGlyph());
		sortButton.setTooltip(Tooltip.create(currentSortLabel()));
		rebuild();
	}

	public void removeFrom(Consumer<AbstractWidget> remover) {
		for (AbstractWidget w : ownedWidgets) {
			remover.accept(w);
		}
		ownedWidgets.clear();
	}

	private int gridTop() {
		return panelY + PanelRenderer.FRAME_BORDER + HEADER_HEIGHT + 1 + TOOLBAR_H;
	}

	private int gridLeft() {
		return panelX + PanelRenderer.FRAME_BORDER;
	}

	private int gridRightEdge() {
		return panelX + panelWidth - PanelRenderer.FRAME_BORDER;
	}

	private int gridBottom() {
		return panelY + panelHeight - PanelRenderer.FRAME_BORDER;
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

		int gridH = gridBottom() - gridTop();
		int usableW = gridRightEdge() - gridLeft() - scrollBar.getWidth();
		int cols = Math.max(1, (usableW + CELL_GAP) / (targetCellW + CELL_GAP));
		int rows = (int) Math.ceil(visible.size() / (double) cols);
		int rowH = cellSizeY + CELL_GAP;
		int contentH = Math.max(0, rows * rowH - CELL_GAP);

		scrollBar.setMaxScroll(Math.max(0, contentH - gridH));
		scrollBar.setBounds(gridRightEdge() - scrollBar.getWidth(), gridTop(), gridH);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		renderBase(guiGraphics);

		int b = PanelRenderer.FRAME_BORDER;
		int tX1 = panelX + b;
		int tY1 = panelY + b + HEADER_HEIGHT + 1;
		int tX2 = panelX + panelWidth - b;
		int tY2 = tY1 + TOOLBAR_H;
		guiGraphics.fill(tX1, tY1, tX2, tY2, COLOUR_TOOLBAR_BACKGROUND);
		guiGraphics.fill(tX1, tY2 - 1, tX2, tY2, 0xFF000000);

		int gridLeft = gridLeft();
		int gridTop = gridTop();
		int gridRight = gridRightEdge() - scrollBar.getWidth();
		int gridH = gridBottom() - gridTop;
		int usableW = gridRight - gridLeft;

		int cols = Math.max(1, (usableW + CELL_GAP) / (targetCellW + CELL_GAP));
		int totalGaps = (cols - 1) * CELL_GAP;
		int baseCellW = (usableW - totalGaps) / cols;
		int leftover = (usableW - totalGaps) - baseCellW * cols;

		int scroll = scrollBar.getScroll();
		guiGraphics.enableScissor(gridLeft, gridTop, gridRight, gridTop + gridH);

		Entry<K> hovered = null;
		for (int i = 0; i < visible.size(); i++) {
			int row = i / cols;
			int col = i % cols;
			int cW = baseCellW + (col < leftover ? 1 : 0);
			int cX = gridLeft;
			for (int c = 0; c < col; c++) {
				cX += (baseCellW + (c < leftover ? 1 : 0)) + CELL_GAP;
			}
			int cY = gridTop + row * (cellSizeY + CELL_GAP) - scroll;
			if (cY + cellSizeY < gridTop || cY > gridTop + gridH) continue;

			Entry<K> e = visible.get(i);
			boolean isHov = isHovered(mouseX, mouseY, cX, cY, cW, cellSizeY) && mouseY >= gridTop && mouseY < gridTop + gridH;
			renderCell(guiGraphics, e, cX, cY, cW, isHov);
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

	private void renderCell(GuiGraphics guiGraphics, Entry<K> e, int x, int y, int w, boolean hovered) {
		guiGraphics.fill(x, y, x + w, y + cellSizeY, hovered ? COLOUR_CELL_HOVER : COLOUR_CELL_BACKGROUND);
		guiGraphics.fill(x, y, x + w, y + 1, COLOUR_CELL_BORDER_LOW);
		guiGraphics.fill(x, y, x + 1, y + cellSizeY, COLOUR_CELL_BORDER_LOW);
		guiGraphics.fill(x, y + cellSizeY - 1, x + w, y + cellSizeY, COLOUR_CELL_BORDER_HIGH);
		guiGraphics.fill(x + w - 1, y, x + w, y + cellSizeY, COLOUR_CELL_BORDER_HIGH);

		renderIcon(guiGraphics, e, x, y, w);
		String countStr = compact(e.count());
		int textY = y + cellSizeY - font.lineHeight - 3;
		guiGraphics.drawString(
				font,
				countStr,
				x + w - font.width(countStr) - 3,
				textY,
				COLOUR_TEXT,
				true
		);
	}

	protected void renderIcon(GuiGraphics guiGraphics, Entry<K> e, int cellX, int cellY, int cellW) {
		int iconX = cellX + (cellW - 16) / 2;
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
