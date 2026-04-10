package io.github.junyali.pathed.screen;

import io.github.junyali.pathed.Pathed;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PathedMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENUS =
			DeferredRegister.create(Registries.MENU, Pathed.MODID);

	public static void register(IEventBus eventBus) {
		MENUS.register(eventBus);
	}
}
