package io.github.junyali.pathed;

import io.github.junyali.pathed.attachment.PathedAttachments;
import io.github.junyali.pathed.data.path.PathRegistry;
import io.github.junyali.pathed.network.OpenClassSelectPacket;
import io.github.junyali.pathed.screen.PathedMenuTypes;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
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

    public Pathed(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPackets);

        PathedAttachments.register(modEventBus);
        PathedMenuTypes.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToClient(
                OpenClassSelectPacket.TYPE,
                OpenClassSelectPacket.STREAM_CODEC,
                OpenClassSelectPacket::handle
        );
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
