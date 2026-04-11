package io.github.junyali.pathed.data.paths;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import io.github.junyali.pathed.data.path.StartingKit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LumberjackPath extends Path {
	public LumberjackPath() {
		super(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "lumberjack"), true, 3);
	}

	@Override
	public PathIcon getIcon() {
		return PathIcon.ofItem(new ItemStack(Items.GOLDEN_AXE));
	}

	@Override
	public StartingKit getStartingItems() {
		return StartingKit.EMPTY;
	}
}
