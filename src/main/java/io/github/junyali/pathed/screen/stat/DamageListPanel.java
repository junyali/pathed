package io.github.junyali.pathed.screen.stat;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.screen.common.PanelRenderer;
import io.github.junyali.pathed.screen.common.ScrollBar;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

import java.util.*;
import java.util.function.Consumer;

public abstract class DamageListPanel extends AbstractStatPanel {
	public enum SortMode { VALUE_DESC, ALPHA_DESC }

	private static final int TOOLBAR_H = 20;
	private static final int SEARCH_H = 16;
	private static final int SORT_BUTTON = 16;
	private static final int ROW_H = 40;
	private static final int ROW_GAP = 3;
	private static final int PORTRAIT = ROW_H;
	private static final int HEART_PX = 9;
	private static final int HEARTS = 10;

	private static final int COLOUR_ROW_BACKGROUND = 0xFF373737;
	private static final int COLOUR_ROW_HOVER = 0xFF505050;
	private static final int COLOUR_ROW_BORDER_H = 0xFFFFFFFF;
	private static final int COLOUR_ROW_BORDER_L = 0xFF555555;
	private static final int COLOUR_TOOLBAR_BACKGROUND = 0xFF252525;

	private static final ResourceLocation HEART_FULL = ResourceLocation.withDefaultNamespace("hud/heart/full");
	private static final ResourceLocation HEART_HALF = ResourceLocation.withDefaultNamespace("hud/heart/half");
	private static final ResourceLocation HEART_CONTAINER = ResourceLocation.withDefaultNamespace("hud/heart/container");

	private static final ResourceLocation ENVIRONMENT_SOURCE = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "environment");
	private static final ResourceLocation PLAYER_ID = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PLAYER);

	private final ScrollBar scrollBar = new ScrollBar();
	private final List<AbstractWidget> ownedWidgets = new ArrayList<>();
	private final Map<ResourceLocation, LivingEntity> entityMap = new HashMap<>();

	private EditBox searchBox;
	private Button sortButton;

	private SortMode sortMode = SortMode.VALUE_DESC;
	private String filter = "";
	private final List<Entry> visible = new ArrayList<>();
	private float totalDmaage = 0f;

	protected record Entry(ResourceLocation key, float hp, Component name) {}

	protected DamageListPanel(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	protected abstract Map<ResourceLocation, Integer> rawData();

	public void initWidgets(Consumer<AbstractWidget> register) {
		ownedWidgets.clear();
		int b = PanelRenderer.FRAME_BORDER;
		int toolbarY = panelY + b + (TOOLBAR_H - SEARCH_H) / 2;
		int sortX = panelX + panelWidth - b - PADDING - SORT_BUTTON - 1;
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
		return Component.literal(sortMode == SortMode.VALUE_DESC ? "#" : "A");
	}

	private Component currentSortLabel() {
		return Component.translatable(sortMode == SortMode.VALUE_DESC
				? "pathed.gui.stats.sort.hp"
				: "pathed.gui.stats.sort.alpha");
	}

	private void toggleSort() {
		sortMode = (sortMode == SortMode.VALUE_DESC) ? SortMode.ALPHA_DESC : SortMode.VALUE_DESC;
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

	private int listTop() { return panelY + PanelRenderer.FRAME_BORDER + TOOLBAR_H; }
	private int listLeft() { return panelX + PanelRenderer.FRAME_BORDER; }
	private int listRight() { return panelX + panelWidth - PanelRenderer.FRAME_BORDER - 1; }
	private int listBottom() { return panelY + panelHeight - PanelRenderer.FRAME_BORDER - 1; }

	protected void rebuild() {
		visible.clear();
		totalDmaage = 0f;

		for (Map.Entry<ResourceLocation, Integer> e : rawData().entrySet()) {
			float hp = e.getValue() / 10f;
			Component name = nameFor(e.getKey());
			totalDmaage += hp;
			if (!filter.isEmpty() && !name.getString().toLowerCase(Locale.ROOT).contains(filter)) continue;
			visible.add(new Entry(e.getKey(), hp, name));
		}

		Comparator<Entry> comparator = switch (sortMode) {
			case VALUE_DESC -> Comparator.<Entry>comparingDouble(Entry::hp).reversed();
			case ALPHA_DESC -> Comparator.comparing(e -> e.name().getString(), String.CASE_INSENSITIVE_ORDER);
		};
		visible.sort(comparator);

		int listH = listBottom() - listTop();
		int rowH = ROW_H + ROW_GAP;
		int contentH = Math.max(0, visible.size() * rowH - ROW_GAP);
		scrollBar.setMaxScroll(Math.max(0, contentH - listH));
		scrollBar.setBounds(panelX + panelWidth - PanelRenderer.FRAME_BORDER - scrollBar.getWidth() - 1, listTop(), listH);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		renderFrame(guiGraphics);

		int b = PanelRenderer.FRAME_BORDER;
		int tX1 = panelX + b;
		int tY1 = panelY + b;
		int tX2 = panelX + panelWidth - b - 1;
		int tY2 = tY1 + TOOLBAR_H;
		guiGraphics.fill(tX1, tY1, tX2, tY2, COLOUR_TOOLBAR_BACKGROUND);
		guiGraphics.fill(tX1, tY2 - 1, tX2, tY2, 0xFF000000);

		int left = listLeft();
		int top = listTop();
		int right = listRight() - (scrollBar.isVisible() ? scrollBar.getWidth() + 2 : 0);
		int bottom = listBottom();
		int width = right - left;
		int scroll = scrollBar.getScroll();

		guiGraphics.enableScissor(left, top, right, bottom);

		Entry hovered = null;
		for (int i = 0; i < visible.size(); i++) {
			int rY = top + i * (ROW_H + ROW_GAP) - scroll;
			if (rY + ROW_H < top || rY > bottom) continue;

			Entry e = visible.get(i);
			boolean isHov = isHovered(mouseX, mouseY, left, rY, width, ROW_H) && mouseY >= top && mouseY < bottom;
			renderRow(guiGraphics, e, left, rY, width, isHov);
			if (isHov) hovered = e;
		}
		guiGraphics.disableScissor();

		scrollBar.render(guiGraphics, mouseX, mouseY);

		if (hovered != null) {
			List<Component> lines = new ArrayList<>();
			lines.add(hovered.name());
			lines.add(Component.translatable("pathed.gui.stats.tooltip.hp", String.format(Locale.ROOT, "%.1f", hovered.hp())).withStyle(ChatFormatting.GRAY));
			guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
		}
	}

	private void renderRow(GuiGraphics guiGraphics, Entry e, int x, int y, int w, boolean hovered) {
		guiGraphics.fill(x, y, x + w, y + ROW_H, hovered ? COLOUR_ROW_HOVER : COLOUR_ROW_BACKGROUND);
		guiGraphics.fill(x, y, x + w, y + 1, COLOUR_ROW_BORDER_L);
		guiGraphics.fill(x, y, x + 1, y + ROW_H, COLOUR_ROW_BORDER_L);
		guiGraphics.fill(x, y + ROW_H - 1, x + w, y + ROW_H, COLOUR_ROW_BORDER_H);
		guiGraphics.fill(x + w - 1, y, x + w, y + ROW_H, COLOUR_ROW_BORDER_H);

		int hpAreaWidth = 70;
		int hpAreaRight = x + w - 8;
		int hpAreaLeft = hpAreaRight - hpAreaWidth;
		int heartsWidth = HEARTS * (HEART_PX + 1) - 1;
		int heartsX = hpAreaLeft - 6 - heartsWidth;
		int heartsY = y + (ROW_H - HEART_PX) / 2;

		int pX = x + 1;
		int pY = y + 1;
		int portraitH = ROW_H - 2;
		int portraitW = (heartsX - 4) - pX;
		guiGraphics.enableScissor(pX, pY, pX + portraitW, pY + portraitH);
		renderEntity(guiGraphics, e.key, pX, pY, portraitW, portraitH);
		guiGraphics.disableScissor();

		String hpStr = formatHp(e.hp());
		int hpX = hpAreaRight - font.width(hpStr);
		int hpY = y + (ROW_H - font.lineHeight) / 2;
		guiGraphics.drawString(font, hpStr, hpX, hpY, COLOUR_TEXT, true);

		float pct = totalDmaage > 0 ? Math.min(1f, e.hp() / totalDmaage) : 0f;
		int halfHearts = Math.round(pct * HEARTS * 2f);
		if (halfHearts == 0 && e.hp() > 0) halfHearts = 1;
		int fullHearts = halfHearts / 2;
		boolean hasHalf = (halfHearts % 2) == 1;

		for (int i = 0; i < HEARTS; i++) {
			int hX = heartsX + i * (HEART_PX - 1);
			guiGraphics.blitSprite(HEART_CONTAINER, hX, heartsY, HEART_PX, HEART_PX);
			if (i < fullHearts) {
				guiGraphics.blitSprite(HEART_FULL, hX, heartsY, HEART_PX, HEART_PX);
			} else if (i == fullHearts && hasHalf) {
				guiGraphics.blitSprite(HEART_HALF, hX, heartsY, HEART_PX, HEART_PX);
			}
		}
	}

	private void renderEntity(GuiGraphics guiGraphics, ResourceLocation key, int x, int y, int w, int h) {
		LivingEntity entity = entityFor(key);
		if (entity != null) {
			int boxX1 = x;
			int boxY1 = y - 4;
			int boxX2 = x + w;
			int boxY2 = y + h * 2;
			int scale = (int) (h / Math.max(0.6f, entity.getBbHeight()) * 1.4f);
			scale = Math.max(18, Math.min(scale, 48));
			InventoryScreen.renderEntityInInventoryFollowsMouse(
					guiGraphics,
					boxX1,
					boxY1,
					boxX2,
					boxY2,
					scale,
					0.0625f,
					boxX2 + 30,
					boxY1 - 20,
					entity
			);
			return;
		}

		ItemStack icon = fallbackIconFor(key);
		guiGraphics.renderItem(icon, x + (w - 16) / 2, y + (h - 16) / 2);
	}

	private LivingEntity entityFor(ResourceLocation id) {
		return entityMap.computeIfAbsent(id, key -> {
			if (key.equals(ENVIRONMENT_SOURCE)) return null;
			if (key.equals(PLAYER_ID)) return Minecraft.getInstance().player;

			EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(key);
			if (Minecraft.getInstance().level == null) return null;
			Entity created = type.create(Minecraft.getInstance().level);
			return (created instanceof LivingEntity entity) ? entity : null;
		});
	}

	private ItemStack fallbackIconFor(ResourceLocation key) {
		if (key.equals(ENVIRONMENT_SOURCE)) return new ItemStack(Items.LAVA_BUCKET);
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(key);
		SpawnEggItem egg = SpawnEggItem.byId(type);
		if (egg != null) return new ItemStack(egg);
		return new ItemStack(Items.BARRIER);
	}

	private static String formatHp(float hp) {
		if (Math.abs(hp) >= 1_000_000f) return String.format(Locale.ROOT, "%.1fM HP", hp / 1_000_000f);
		if (Math.abs(hp) >= 10_000f) return String.format(Locale.ROOT, "%.1fk HP", hp / 1_000f);
		return String.format(Locale.ROOT, "%.1f HP", hp);
	}

	private Component nameFor(ResourceLocation id) {
		if (id.equals(ENVIRONMENT_SOURCE)) {
			return Component.translatable("pathed.gui.stats.source.environment");
		}
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
		return type.getDescription();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int b) {
		return scrollBar.mouseClicked(mouseX, mouseY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int b) {
		scrollBar.release();
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int b, double dragX, double dragY) {
		return scrollBar.mouseDragged(mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double vertical) {
		if (!isHovered(mouseX, mouseY, panelX, panelY, panelWidth, panelHeight)) return false;
		return scrollBar.mouseScrolled(vertical, 6);
	}
}
