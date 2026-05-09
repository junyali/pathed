package io.github.junyali.pathed.screen.stat;

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

public class IconCountStatPanel<K> extends AbstractStatPanel {
	public enum SortMode { COUNT_DESC, ALPHA_ASC }

	private static final int SEARCH_H = 16;
	private static final int SEARCH_PAD = 6;
	private static final int HEADER_H = 28;
	private static final int CELL_SIZE = 48;
	private static final int CELL_GAP = 4;
	private static final int PADDING = 6;

	private final ScrollBar scrollBar = new ScrollBar();
	private EditBox searchBox;
	private Button sortCountButton;
	private Button sortAlphaButton;

	private SortMode sortMode = SortMode.COUNT_DESC;
	private String filter = "";
	private List<Entry<K>> visible = new ArrayList<>();

	protected record Entry<K>(K key, int count, ItemStack icon, Component name) {}

	protected IconCountStatPanel(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	public Component getTitle() {
		return null;
	}

	protected Map<K, Integer> data() {
		return null;
	}

	protected ItemStack iconFor(K key) {
		return null;
	}

	protected Component nameFor(K key) {
		return null;
	}

	protected List<Component> extraTooltip(K key, int count) {
		return List.of();
	}

	public void initWidgets(Consumer<AbstractWidget> register) {
		searchBox = new EditBox(font, panelX + PADDING, panelY + PADDING, panelWidth - PADDING * 2, SEARCH_H, Component.translatable("pathed.gui.stats.search"));
		searchBox.setHint(Component.translatable("pathed.gui.stats.search").withStyle(ChatFormatting.DARK_GRAY));
		searchBox.setResponder(s -> { filter = s.toLowerCase(Locale.ROOT); rebuild(); });

		int buttonY = panelY + PADDING + SEARCH_H + 2;
		sortCountButton = Button.builder(Component.translatable("pathed.gui.stats.sort.count"), btn -> { sortMode = SortMode.COUNT_DESC; rebuild(); }).bounds(panelX + PADDING, buttonY, 70, 14).build();
		sortAlphaButton = Button.builder(Component.translatable("pathed.gui.stats.sort.alpha"), btn -> { sortMode = SortMode.ALPHA_ASC; rebuild(); }).bounds(panelX + PADDING + 72, buttonY, 70, 14).build();

		register.accept(searchBox);
		register.accept(sortCountButton);
		register.accept(sortAlphaButton);
		rebuild();
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

		int gridTop = gridTop();
		int gridH = panelY + panelHeight - gridTop - PADDING;
		int cols = Math.max(1, (panelWidth - PADDING * 2 - scrollBar.getWidth() - 2 * CELL_GAP) / (CELL_SIZE + CELL_GAP));
		int rows = (int) Math.ceil(visible.size() / (double) cols);
		int contentH = rows * (CELL_SIZE + CELL_GAP) - CELL_GAP;
		scrollBar.setMaxScroll(Math.max(0, contentH - gridH));
		scrollBar.setBounds(panelX + panelWidth - PADDING - scrollBar.getWidth(), gridTop, gridH);
	}

	private int gridTop() {
		return panelY + PADDING + SEARCH_H + 2 + 14 + 4;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		int gridTop = gridTop();
		int gridH = panelY + panelHeight - gridTop - PADDING;
		int gridLeft = panelX + PADDING;
		int gridRight = panelX + panelWidth - PADDING - (scrollBar.isVisible() ? scrollBar.getWidth() + 2 : 0);

		guiGraphics.enableScissor(gridLeft, gridTop, gridRight, gridTop + gridH);
		int cols = Math.max(1, (gridRight - gridLeft + CELL_GAP) / (CELL_SIZE + CELL_GAP));
		int scroll = scrollBar.getScroll();

		Entry<K> hovered = null;
		int hX = 0;
		int hY = 0;
		for (int i = 0; i < visible.size(); i++) {
			int row = i / cols;
			int col = i % cols;
			int cX = gridLeft + col * (CELL_SIZE + CELL_GAP);
			int cY = gridTop + row * (CELL_SIZE + CELL_GAP) - scroll;
			if (cY + CELL_SIZE < gridTop || cY > gridTop + gridH) continue;
			Entry<K> e = visible.get(i);
			renderCell(guiGraphics, e, cX, cY);
			if (isHovered(mouseX, mouseY, cX, cY, CELL_SIZE, CELL_SIZE)) {
				hovered = e;
				hX = cX;
				hY = cY;
			}
		}
		guiGraphics.disableScissor();

		scrollBar.render(guiGraphics, mouseX, mouseY);

		if (hovered != null) {
			List<Component> lines = new ArrayList<>();
			lines.add(hovered.name());
			lines.add(Component.literal("* " + hovered.count()).withStyle(ChatFormatting.GRAY));
			lines.addAll(extraTooltip(hovered.key(), hovered.count()));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}
	}

	private void renderCell(GuiGraphics guiGraphics, Entry<K> e, int x, int y) {
		guiGraphics.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, 0xFF2A2A3C);

		int iconX = x + (CELL_SIZE - 16) / 2;
		int iconY = y + 2;
		guiGraphics.renderItem(e.icon(), iconX, iconY);
		String countStr = compact(e.count());
		guiGraphics.drawString(
				font,
				countStr,
				x + CELL_SIZE - font.width(countStr) / 2,
				y + CELL_SIZE - font.lineHeight - 2,
				0xFFFFFFFF,
				false
		);
	}

	private static String compact(int n) {
		// TODO: find a better way to do this, or at the very least, expand it
		if (n >= 1_000_000) return (n / 1000_000) + "M";
		if (n >= 10_000) return (n / 100) + "k";
		return Integer.toString(n);
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
		if (!isHovered((int) mouseX, (int) mouseY, panelX, panelY, panelWidth, panelHeight)) return false;
		return scrollBar.mouseScrolled(vertical, CELL_SIZE / 2);
	}
}
