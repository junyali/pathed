package io.github.junyali.pathed.data.paths;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import io.github.junyali.pathed.data.path.PathToolBuilder;
import io.github.junyali.pathed.data.path.StartingKit;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlademasterPath extends Path {
	public BlademasterPath() {
		super(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "blademaster"), true, 0);
	}

	@Override
	public PathIcon getIcon() {
		return PathIcon.ofItem(new ItemStack(Items.IRON_SWORD));
	}

	@Override
	public StartingKit getStartingItems() {
		return StartingKit.builder()
				.add(PathToolBuilder.create(Items.WOODEN_SWORD, ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "the_first_edge"), ChatFormatting.RED))
				.build();
	}
}
