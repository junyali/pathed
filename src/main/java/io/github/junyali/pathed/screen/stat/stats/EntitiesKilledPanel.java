package io.github.junyali.pathed.screen.stat.stats;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.screen.stat.IconCountStatPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EntitiesKilledPanel extends IconCountStatPanel<ResourceLocation> {
	private static final int cellSizeX = 64;
	private static final int cellSizeY = 88;

	private final Map<ResourceLocation, LivingEntity> entityCache = new HashMap<>();

	public EntitiesKilledPanel(int x, int y, int w, int h) {
		super(x, y, w, h, cellSizeX, cellSizeY);
	}

	@Override
	public Component getTitle() {
		return Component.translatable("pathed.gui.stats.entities_killed.title");
	}

	@Override
	protected Map<ResourceLocation, Integer> data() {
		Player player = Minecraft.getInstance().player;
		if (player == null) return Collections.emptyMap();
		return ProgressionAttachment.get(player).getEntitiesKilled();
	}

	@Override
	protected ItemStack iconFor(ResourceLocation id) {
		return ItemStack.EMPTY;
	}

	@Override
	protected Component nameFor(ResourceLocation id) {
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
		return type.getDescription();
	}

	@Override
	protected void renderIcon(GuiGraphics guiGraphics, Entry<ResourceLocation> e, int cellX, int cellY) {
		LivingEntity entity = entityFor(e.key());
		if (entity == null) return;

		int padding = 4;
		int boxX1 = cellX + padding;
		int boxY1 = cellY + padding;
		int boxX2 = cellX + cellSizeX - padding;
		int boxY2 = cellY + cellSizeX - (font.lineHeight + padding);
		int centreX = (boxX1 + boxX2) / 2;
		int centreY = boxY1 + (int) ((boxY2 - boxY1) * 0.65f);

		float entityHeight = entity.getBbHeight();
		int availableHeight = boxY2 - boxY1;
		int availableWidth = boxX2 - boxX1;
		int baseScale = (int) (availableHeight / Math.max(0.5f, entityHeight));
		int scale = Math.max(20, Math.min(baseScale, 45));

		float mouseX = centreX + 70;
		float mouseY = centreY - 70;

		InventoryScreen.renderEntityInInventoryFollowsMouse(
				guiGraphics,
				boxX1,
				boxY1,
				boxX2,
				boxY2,
				scale,
				0.0625f,
				mouseX,
				mouseY,
				entity
		);
	}

	private LivingEntity entityFor(ResourceLocation id) {
		return entityCache.computeIfAbsent(id, key -> {
			EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(key);
			if (Minecraft.getInstance().level != null) {
				Entity created = type.create(Minecraft.getInstance().level);
				return (created instanceof LivingEntity entity) ? entity : null;
			}
			return null;
		});
	}
}
