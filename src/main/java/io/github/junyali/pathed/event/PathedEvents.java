package io.github.junyali.pathed.event;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathDataHolder;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
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

		PathDataHolder holder = PathDataHolder.get(player);
		if (!holder.hasChosen() || holder.getPath() == null) {
			PacketDistributor.sendToPlayer(player, new OpenPathSelectPacket());
		}

		ProgressionAttachment.get(player).sync(player);
	}
}
