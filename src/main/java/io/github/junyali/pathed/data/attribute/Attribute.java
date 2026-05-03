package io.github.junyali.pathed.data.attribute;

import io.github.junyali.pathed.Pathed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public abstract class Attribute {
	private final ResourceLocation id;
	private final int maxLevel;
	private final ItemStack icon;
	private final Optional<ResourceLocation> pathLocked;

	protected Attribute(String path, int maxLevel, ItemStack icon) {
		this(path, maxLevel, icon, Optional.empty());
	}

	protected Attribute(String path, int maxLevel, ItemStack icon, Optional<ResourceLocation> pathLocked) {
		this.id = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, path);
		this.maxLevel = maxLevel;
		this.icon = icon;
		this.pathLocked = pathLocked;
	}

	public ResourceLocation getId() {
		return id;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public Optional<ResourceLocation> getPathLocked() {
		return pathLocked;
	}

	public String getNameKey() {
		return "pathed.attribute." + id.getPath() + ".name";
	}

	public String getDescriptionKey() {
		return "pathed.attribute." + id.getPath() + ".desc";
	}

	public void onLevelChange(ServerPlayer player, int oldLevel, int newLevel) {
		// on level change
	}

	public void tickPlayer(ServerPlayer player, int level) {
		// for level >= 1
	}
}
