package io.github.junyali.pathed.event;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Pathed.MODID)
public class ProgressionStatEvents {
	private static final Map<UUID, Vec3> lastPositions = new HashMap<>();
	private static final ResourceLocation ENVIRONMENT_SOURCE =
			ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "environment");

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!(event.getPlayer() instanceof ServerPlayer player)) return;

		ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(event.getState().getBlock());
		ProgressionAttachment progressionAttachment = ProgressionAttachment.get(player);
		progressionAttachment.getBlocksBroken().merge(blockId, 1, Integer::sum);
		progressionAttachment.sync(player);
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof ServerPlayer victim) {
			ProgressionAttachment progressionAttachment = ProgressionAttachment.get(victim);
			progressionAttachment.incrementDeathCount();
			progressionAttachment.sync(victim);
		}

		if (event.getSource().getEntity() instanceof ServerPlayer killer) {
			ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType());
			ProgressionAttachment progressionAttachment = ProgressionAttachment.get(killer);
			progressionAttachment.getEntitiesKilled().merge(entityId, 1, Integer::sum);
			progressionAttachment.sync(killer);
		}
	}

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent.Post event) {
		float amount = event.getNewDamage();
		int fixedPoint = Math.round(amount * 10);

		if (event.getSource().getEntity() instanceof ServerPlayer attacker) {
			ResourceLocation targetId = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType());
			ProgressionAttachment progressionAttachment = ProgressionAttachment.get(attacker);
			progressionAttachment.getDamageDealt().merge(targetId, fixedPoint, Integer::sum);
			progressionAttachment.sync(attacker);
		}

		if (event.getEntity() instanceof ServerPlayer victim) {
			ResourceLocation sourceId = event.getSource().getEntity() != null
					? BuiltInRegistries.ENTITY_TYPE.getKey(event.getSource().getEntity().getType())
					: ENVIRONMENT_SOURCE;
			ProgressionAttachment progressionAttachment = ProgressionAttachment.get(victim);
			progressionAttachment.getDamageTaken().merge(sourceId, fixedPoint, Integer::sum);
			progressionAttachment.sync(victim);
		}
	}

	@SubscribeEvent
	public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;

		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getCrafting().getItem());
		ProgressionAttachment progressionAttachment = ProgressionAttachment.get(player);
		progressionAttachment.getItemsCrafted().merge(itemId, event.getCrafting().getCount(), Integer::sum);
		progressionAttachment.sync(player);
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		if (player.tickCount % 20 != 0) return;

		Vec3 current = player.position();
		Vec3 last = lastPositions.get(player.getUUID());

		if (last != null) {
			double dx = current.x - last.x;
			double dz = current.z - last.z;
			double dist = Math.sqrt(dx * dx + dz * dz);

			if (dist > 0.01) {
				int fixedPoint = (int)(dist * 100);
				ProgressionAttachment progressionAttachment = ProgressionAttachment.get(player);
				progressionAttachment.addDistanceTravelled(fixedPoint);
				progressionAttachment.sync(player);
			}
		}

		lastPositions.put(player.getUUID(), current);
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		lastPositions.remove(event.getEntity().getUUID());
	}

	@SubscribeEvent
	public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;

		ResourceLocation dimensionId = event.getTo().location();
		ProgressionAttachment progressionAttachment = ProgressionAttachment.get(player);
		progressionAttachment.getDimensionsVisited().add(dimensionId);
		progressionAttachment.sync(player);
	}



	@SubscribeEvent
	public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		if (event.wakeImmediately()) return;

		ProgressionAttachment progressionAttachment = ProgressionAttachment.get(player);
		progressionAttachment.incrementSleepCount();
		progressionAttachment.sync(player);
	}

	@SubscribeEvent
	public static void onPlayerTrade(TradeWithVillagerEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;

		ResourceLocation professionId;
		if (event.getAbstractVillager() instanceof Villager villager) {
			VillagerProfession profession = villager.getVillagerData().getProfession();
			professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
		} else {
			professionId = ResourceLocation.withDefaultNamespace("wandering_trader");
		}

		ProgressionAttachment progressionAttachment = ProgressionAttachment.get(player);
		progressionAttachment.incrementTradingCount(professionId);
		progressionAttachment.sync(player);
	}
}
