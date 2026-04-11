package io.github.junyali.pathed.data.path;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class PathIcon {
	private final ItemStack itemIcon;
	private final ResourceLocation textureIcon;

	private PathIcon(ItemStack itemIcon, ResourceLocation textureIcon) {
		this.itemIcon = itemIcon;
		this.textureIcon = textureIcon;
	}

	public static PathIcon ofItem(ItemStack item) {
		return new PathIcon(item, null);
	}

	public static PathIcon ofTexture(ResourceLocation texture) {
		return new PathIcon(ItemStack.EMPTY, texture);
	}

	public boolean isItem() {
		return textureIcon == null;
	}

	public boolean isTexture() {
		return textureIcon != null;
	}

	public ItemStack getItem() {
		return itemIcon;
	}

	public Optional<ResourceLocation> getTexture() {
		return Optional.ofNullable(textureIcon);
	}
}
