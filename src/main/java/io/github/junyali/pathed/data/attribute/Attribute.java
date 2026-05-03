package io.github.junyali.pathed.data.attribute;

import io.github.junyali.pathed.Pathed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class Attribute {
	private final ResourceLocation id;
	private final int maxLevel;
	private final ItemStack icon;
	@Nullable
	private final ResourceLocation pathLocked;

	protected Attribute(String path, int maxLevel, ItemStack icon) {
		this(path, maxLevel, icon, null);
	}

	protected Attribute(String path, int maxLevel, ItemStack icon, @Nullable ResourceLocation pathLocked) {
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
		return Optional.ofNullable(pathLocked);
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
