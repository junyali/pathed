package io.github.junyali.pathed.data.skill;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ClientRequirementCheck {
	private ClientRequirementCheck() {}

	public static boolean isMet(Player player, SkillNodeRequirement req) {
		ProgressionAttachment p = ProgressionAttachment.get(player);
		return switch (req) {
			case SkillNodeRequirement.StatRequirement s -> SkillStatLookup.getValue(p, s.stat(), s.target()) >= s.count();
			case SkillNodeRequirement.PointRequirement pr -> (pr.classPoints() ? p.getClassPoints() : p.getGeneralPoints()) >= pr.amount();
			case SkillNodeRequirement.NodeRequirement nr -> p.getCompletedNodes().contains(nr.nodeId());
			case SkillNodeRequirement.ItemRequirement ir -> countItem(player, ir.item()) >= ir.count();
		};
	}

	public static boolean isPrereqMet(Player player, ResourceLocation prereqNodeId) {
		return ProgressionAttachment.get(player).getCompletedNodes().contains(prereqNodeId);
	}

	public static boolean isCompleted(Player player, SkillNode node) {
		return ProgressionAttachment.get(player).getCompletedNodes().contains(node.id());
	}

	private static int countItem(Player player, ResourceLocation itemId) {
		Item target = BuiltInRegistries.ITEM.get(itemId);
		if (target == Items.AIR) return 0;
		int total = 0;
		total += countIn(player.getInventory(), target);
		total += countIn(player.getEnderChestInventory(), target);
		return total;
	}

	private static int countIn(Container container, Item target) {
		int n = 0;
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			if (stack.getItem() == target) n += stack.getCount();
		}
		return n;
	}
}
