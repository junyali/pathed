package io.github.junyali.pathed.registry;

import io.github.junyali.pathed.Pathed;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class PathedKeyMappings {
	public static final String CATEGORY = "category." + Pathed.MODID;
	public static final KeyMapping OPEN_PATH_MENU = new KeyMapping(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "open_path_menu").toLanguageKey("key"), GLFW.GLFW_KEY_P, CATEGORY);

	@SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		event.register(OPEN_PATH_MENU);
	}
}
