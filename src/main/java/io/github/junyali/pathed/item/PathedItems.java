package io.github.junyali.pathed.item;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.paths.SpelunkerPath;
import io.github.junyali.pathed.item.tool.PathTool;
import io.github.junyali.pathed.item.tool.ToolRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PathedItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Pathed.MODID);

	public static final DeferredItem<PathTool> SPELUNKER_PICKAXE = ITEMS.register(
			"spelunker_pickaxe",
			() -> new PathTool(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "spelunker"), ToolRole.PICKAXE, new Item.Properties())
	);

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}
}
