package io.github.junyali.pathed.event;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.registry.PathedKeyMappings;
import io.github.junyali.pathed.screen.PathMenuScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = Pathed.MODID, value = Dist.CLIENT)
public class PathedClientEvents {
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();

		if (PathedKeyMappings.OPEN_PATH_MENU.consumeClick()) {
			if (mc.screen == null) {
				mc.setScreen(new PathMenuScreen(false));
			}
		}
	}
}
