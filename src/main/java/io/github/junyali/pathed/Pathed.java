package io.github.junyali.pathed;

import io.github.junyali.pathed.registry.PathedAttachments;
import io.github.junyali.pathed.data.path.PathRegistry;
import io.github.junyali.pathed.item.PathedItems;
import io.github.junyali.pathed.screen.PathedMenuTypes;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Pathed.MODID)
public class Pathed {
    public static final String MODID = "pathed";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String PROTOCOL_VERSION = "1";

    public Pathed(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        PathedAttachments.register(modEventBus);
        PathedMenuTypes.register(modEventBus);
        PathedItems.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        PathRegistry.init();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
