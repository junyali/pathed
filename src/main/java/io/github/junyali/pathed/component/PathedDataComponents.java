package io.github.junyali.pathed.component;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.item.tool.ToolTier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PathedDataComponents {
	public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Pathed.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToolTier>> TOOL_TIER =
			COMPONENTS.register("tool_tier", () -> DataComponentType.<ToolTier>builder()
					.persistent(ToolTier.CODEC)
					.networkSynchronized(ByteBufCodecs.fromCodec(ToolTier.CODEC))
					.build()
			);

	public static void register(IEventBus eventBus) {
		COMPONENTS.register(eventBus);
	}
}
