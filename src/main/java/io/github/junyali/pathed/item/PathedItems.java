package io.github.junyali.pathed.item;

import io.github.junyali.pathed.Pathed;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PathedItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Pathed.MODID);

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}
}
