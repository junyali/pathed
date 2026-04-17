package io.github.junyali.pathed.event;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathDataHolder;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.skill.SkillCategoryLoader;
import io.github.junyali.pathed.data.skill.SkillNodeLoader;
import io.github.junyali.pathed.network.payload.s2c.OpenPathSelectPacket;
import io.github.junyali.pathed.network.payload.s2c.SyncSkillDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Pathed.MODID)
public class PathedEvents {
	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;

		PacketDistributor.sendToPlayer(player, new SyncSkillDataPacket(
				SkillCategoryLoader.getCategories(),
				SkillNodeLoader.getNodes()
		));

		PathDataHolder holder = PathDataHolder.get(player);
		if (!holder.hasChosen() || holder.getPath() == null) {
			PacketDistributor.sendToPlayer(player, new OpenPathSelectPacket());
		}

		ProgressionAttachment.get(player).sync(player);
	}

	@SubscribeEvent
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		SyncSkillDataPacket packet = new SyncSkillDataPacket(
				SkillCategoryLoader.getCategories(),
				SkillNodeLoader.getNodes()
		);
		if (event.getPlayer() != null) {
			PacketDistributor.sendToPlayer(event.getPlayer(), packet);
		} else {
			PacketDistributor.sendToAllPlayers(packet);
		}
	}

	@SubscribeEvent
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new SkillCategoryLoader());
		event.addListener(new SkillNodeLoader());
	}
}
