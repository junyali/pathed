package io.github.junyali.pathed.data.paths;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathIcon;
import io.github.junyali.pathed.data.path.StartingKit;
import io.github.junyali.pathed.item.PathedItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class SpelunkerPath extends Path {
	public SpelunkerPath() {
		super(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "spelunker"), true, 2);
	}

	@Override
	public PathIcon getIcon() {
		return PathIcon.ofItem(new ItemStack(Items.NETHERITE_PICKAXE));
	}

	@Override
	public StartingKit getStartingItems() {
		return StartingKit.of(new ItemStack(PathedItems.SPELUNKER_PICKAXE.get()));
	}

	@Override
	public List<Item> getToolItems() {
		return List.of(PathedItems.SPELUNKER_PICKAXE.get());
	}
}
