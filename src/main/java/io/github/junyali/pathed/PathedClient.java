package io.github.junyali.pathed;

import io.github.junyali.pathed.screen.ClassSelectionScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Pathed.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Pathed.MODID, value = Dist.CLIENT)
public class PathedClient {
    public PathedClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static boolean shouldShowClassSelection = false;

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Pathed.LOGGER.info("CLIENT SETUP: MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (shouldShowClassSelection && minecraft.level != null && minecraft.player != null && minecraft.screen == null) {
            minecraft.setScreen(new ClassSelectionScreen());
            shouldShowClassSelection = false;
        }
    }
}
