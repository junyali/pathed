package io.github.junyali.pathed.event;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.PathedAttachments;
import io.github.junyali.pathed.network.payload.s2c.OpenPathSelectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Pathed.MODID)
public class PathedEvents {
	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;

		PathAttachment attachment = player.getData(PathedAttachments.PATH_ATTACHMENT);

		if (!attachment.hasChosen()) {
			PacketDistributor.sendToPlayer(player, new OpenPathSelectPacket());
		}
	}
}
