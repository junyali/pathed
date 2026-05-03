package io.github.junyali.pathed.data.skill;

import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.network.payload.s2c.NodeCompletedPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SkillNodeEvaluator {
	private SkillNodeEvaluator() {}

	public static boolean isAvailable(ServerPlayer player, SkillNode node) {
		ProgressionAttachment p = ProgressionAttachment.get(player);

		if (node.pathLocked().isPresent()) {
			Path path = PathAttachment.get(player).getPath();
			ResourceLocation pathId = path == null ? null : path.getId();
			if (!node.pathLocked().get().equals(pathId)) return false;
		}

		for (ResourceLocation prereq : node.prerequisites()) {
			if (!p.getCompletedNodes().contains(prereq)) return false;
		}

		for (ResourceLocation prev : node.previousNodes()) {
			if (!p.getCompletedNodes().contains(prev)) return false;
		}

		return true;
	}

	public static boolean meetsRequirements(ServerPlayer player, SkillNode node) {
		ProgressionAttachment p = ProgressionAttachment.get(player);
		for (SkillNodeRequirement req : node.requirements()) {
			if (!SkillRequirementChecker.isMet(player, p, req)) return false;
		}

		return true;
	}

	public static boolean isCompleted(ServerPlayer player, SkillNode node) {
		return ProgressionAttachment.get(player).getCompletedNodes().contains(node.id());
	}

	public static boolean tryComplete(ServerPlayer player, SkillNode node) {
		if (isCompleted(player, node)) return false;
		if (!isAvailable(player, node)) return false;
		if (!meetsRequirements(player, node)) return false;

		ProgressionAttachment p = ProgressionAttachment.get(player);

		for (SkillNodeRequirement req : node.requirements()) {
			if (req instanceof SkillNodeRequirement.PointRequirement pr && pr.consumed()) {
				boolean ok = pr.classPoints()
						? p.spendClassPoints(pr.amount())
						: p.spendGeneralPoints(pr.amount());
				if (!ok) return false;
			} else if (req instanceof SkillNodeRequirement.ItemRequirement ir && ir.consumed()) {
				if (!removeItems(player, ir.item(), ir.count())) return false;
			}
		}

		completeNode(player, p, node, true);
		p.sync(player);
		return true;
	}

	public static void evaluateAll(ServerPlayer player) {
		ProgressionAttachment p = ProgressionAttachment.get(player);
		boolean changed = false;

		for (SkillNode node : SkillNodeLoader.getNodes().values()) {
			if (p.getCompletedNodes().contains(node.id())) continue;
			if (isAvailable(player, node) && !p.getAvailableNodes().contains(node.id())) {
				p.addAvailableNode(node.id());
				changed = true;
			}

			if (node.type() == NodeType.PROGRESSION && hasNoConsumedReqs(node) && isAvailable(player, node) && meetsRequirements(player, node)) {
				completeNode(player, p, node, true);
				changed = true;
			}
		}

		if (changed) p.sync(player);
	}

	public static void applyBaseNodes(ServerPlayer player) {
		ProgressionAttachment p = ProgressionAttachment.get(player);
		boolean changed = false;
		for (SkillNode node : SkillNodeLoader.getNodes().values()) {
			if (!node.base()) continue;
			if (p.getCompletedNodes().contains(node.id())) continue;
			completeNode(player, p, node, false);
			changed = true;
		}
		if (changed) p.sync(player);
	}

	private static void completeNode(ServerPlayer player, ProgressionAttachment p, SkillNode node, boolean grant) {
		p.addCompletedNode(node.id());
		if (grant) {
			grantRewards(player, p, node);
			if (!node.base() && node.type() == NodeType.PROGRESSION) {
				announceCompletion(player, node);
			}
		}
	}

	private static boolean hasNoConsumedReqs(SkillNode node) {
		for (SkillNodeRequirement req : node.requirements()) {
			if (req instanceof SkillNodeRequirement.StatRequirement s && s.consumed()) return false;
			if (req instanceof SkillNodeRequirement.PointRequirement p && p.consumed()) return false;
			if (req instanceof SkillNodeRequirement.ItemRequirement i && i.consumed()) return false;
		}
		return true;
	}

	private static void announceCompletion(ServerPlayer player, SkillNode node) {
		PacketDistributor.sendToPlayer(player, new NodeCompletedPacket(node.id()));

		/*
		Component name = Component.translatable(node.nameKey());
		player.sendSystemMessage(
				Component.translatable("pathed.chat.node_get", player.getDisplayName(), name)
		);
		*/
	}

	private static void grantRewards(ServerPlayer player, ProgressionAttachment p, SkillNode node) {
		for (SkillNodeReward reward : node.rewards()) {
			switch (reward) {
				case SkillNodeReward.ItemReward r -> giveItem(player, r.item(), r.count());
				case SkillNodeReward.PointReward r -> {
					p.addClassPoints(r.classPoints());
					p.addGeneralPoints(r.generalPoints());
				}
				case SkillNodeReward.AttributeReward r -> p.getUpgradeData().setAttributeLevel(r.attribute(), r.level());
				case SkillNodeReward.AttributeUpgradeReward r -> p.getUpgradeData().incrementAttribute(r.attribute(), r.levels());
				case SkillNodeReward.ExperienceReward r -> {
					if (r.vanilla()) {
						if (r.levels()) {
							player.giveExperienceLevels(r.amount());
						} else {
							player.giveExperiencePoints(r.amount());
						}
					} else {
						if (r.levels()) {
							p.addLevel(r.amount());
						} else {
							p.addExperience(r.amount());
						}
					}
				}
				case SkillNodeReward.EffectReward r -> {
					MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(r.effect());
					if (effect != null) {
						player.addEffect(new MobEffectInstance(
								BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect),
								r.duration(),
								r.amplifier()
						));
					}
				}
				case SkillNodeReward.RecipeReward r -> {
					Optional<RecipeHolder<?>> recipe = Objects.requireNonNull(player.getServer()).getRecipeManager().byKey(r.recipe());
					recipe.ifPresent(recipeHolder -> player.awardRecipes(List.of(recipeHolder)));
				}
			}
		}
	}

	private static void giveItem(ServerPlayer player, ResourceLocation itemId, int count) {
		// TODO: replace with stash system

		Item item = BuiltInRegistries.ITEM.get(itemId);
		if (item == Items.AIR || count <= 0) return;

		int remaining = count;
		int max = item.getDefaultMaxStackSize();
		while (remaining > 0) {
			int n = Math.min(remaining, max);
			ItemStack stack = new ItemStack(item, n);
			if (!player.getInventory().add(stack)) {
				player.drop(stack, false);
			}
			remaining -= n;
		}
	}

	private static boolean removeItems(ServerPlayer player, ResourceLocation itemId, int count) {
		Item item = BuiltInRegistries.ITEM.get(itemId);
		if (item == Items.AIR) return false;

		int remaining = count;

		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.getItem() != item) continue;

			int toRemove = Math.min(remaining, stack.getCount());
			stack.shrink(toRemove);
			remaining -= toRemove;

			if (remaining <= 0) return true;
		}

		for (int i = 0; i < player.getEnderChestInventory().getContainerSize(); i++) {
			ItemStack stack = player.getEnderChestInventory().getItem(i);
			if (stack.getItem() != item) continue;

			int toRemove = Math.min(remaining, stack.getCount());
			stack.shrink(toRemove);
			remaining -= toRemove;

			if (remaining <= 0) return true;
		}

		return false;
	}
}
