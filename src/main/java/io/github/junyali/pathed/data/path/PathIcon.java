package io.github.junyali.pathed.data.path;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class PathIcon {
	private final ItemStack itemIcon;
	private final ResourceLocation textureIcon;
	private final boolean isPlayerHead;

	private PathIcon(ItemStack itemIcon, ResourceLocation textureIcon) {
		this.itemIcon = itemIcon;
		this.textureIcon = textureIcon;
		this.isPlayerHead = false;
	}

	private PathIcon(ItemStack itemIcon, ResourceLocation textureIcon, boolean isPlayerHead) {
		this.itemIcon = itemIcon;
		this.textureIcon = textureIcon;
		this.isPlayerHead = isPlayerHead;
	}

	public static PathIcon ofItem(ItemStack item) {
		return new PathIcon(item, null);
	}

	public static PathIcon ofTexture(ResourceLocation texture) {
		return new PathIcon(ItemStack.EMPTY, texture);
	}

	public static PathIcon ofPlayerHead() {
		return new PathIcon(null, null, true);
	}

	public boolean isItem() {
		return textureIcon == null;
	}

	public boolean isTexture() {
		return textureIcon != null;
	}

	public boolean isPlayerHead() {
		return isPlayerHead;
	}

	public ItemStack getItem() {
		return itemIcon;
	}

	public Optional<ResourceLocation> getTexture() {
		return Optional.ofNullable(textureIcon);
	}
}
