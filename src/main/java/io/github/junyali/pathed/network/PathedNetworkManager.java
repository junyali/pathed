package io.github.junyali.pathed.network;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.network.payload.c2s.ChoosePathPacket;
import io.github.junyali.pathed.network.payload.s2c.OpenPathSelectPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Pathed.MODID)
public class PathedNetworkManager {
	@SubscribeEvent
	public static void registerPayloads(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		registrar.playToClient(
				OpenPathSelectPacket.TYPE,
				OpenPathSelectPacket.STREAM_CODEC,
				OpenPathSelectPacket::handle
		);

		registrar.playToServer(
				ChoosePathPacket.TYPE,
				ChoosePathPacket.STREAM_CODEC,
				ChoosePathPacket::handle
		);
	}
}
