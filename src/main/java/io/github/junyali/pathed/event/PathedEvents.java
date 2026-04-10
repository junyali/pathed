package io.github.junyali.pathed.event;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.ClassAttachment;
import io.github.junyali.pathed.attachment.PathedAttachments;
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

		ClassAttachment attachment = player.getData(PathedAttachments.CLASS_ATTACHMENT);

		if (!attachment.hasChosen()) {
			// PacketDistributor.sendToPlayer(player, classselectormethod);
		}
	}
}
