package io.github.junyali.pathed.data.path;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class Path {
	private final ResourceLocation id;
	private final boolean selectable;
	private final int order;

	protected Path(ResourceLocation id, boolean selectable, int order) {
		this.id = id;
		this.selectable = selectable;
		this.order = order;
	}

	public ResourceLocation getId() {
		return id;
	}

	public Component getName() {
		return Component.translatable(id.toLanguageKey("path", "name"));
	}

	public Component getDescription() {
		return Component.translatable(id.toLanguageKey("path", "description"));
	}

	public abstract ItemStack getIcon();

	public abstract List<ItemStack> getStartingItems();

	public boolean isSelectable() {
		return selectable;
	}

	public int getOrder() {
		return order;
	}
}
