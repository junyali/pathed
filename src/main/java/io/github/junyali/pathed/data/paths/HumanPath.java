package io.github.junyali.pathed.data.paths;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import io.github.junyali.pathed.data.path.StartingKit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HumanPath extends Path {
	public HumanPath() {
		super(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "human"), true, 0);
	}

	@Override
	public PathIcon getIcon() {
		return PathIcon.ofPlayerHead();
	}

	@Override
	public StartingKit getStartingItems() {
		return StartingKit.builder().build();
	}
}
