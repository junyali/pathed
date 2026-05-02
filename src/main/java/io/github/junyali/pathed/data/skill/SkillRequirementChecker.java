package io.github.junyali.pathed.data.skill;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class SkillRequirementChecker {
	private SkillRequirementChecker() {}

	public static boolean isMet(ServerPlayer player, ProgressionAttachment p, SkillNodeRequirement req) {
		return switch (req) {
			case SkillNodeRequirement.StatRequirement s     -> SkillStatLookup.getValue(p, s.stat(), s.target()) >= s.count();
			case SkillNodeRequirement.PointRequirement pr   -> (pr.classPoints() ? p.getClassPoints() : p.getGeneralPoints()) >= pr.amount();
			case SkillNodeRequirement.NodeRequirement nr    -> p.getCompletedNodes().contains(nr.nodeId());
			case SkillNodeRequirement.ItemRequirement ir    -> countItem(player, ir.item()) >= ir.count();
		};
	}

	private static int countItem(ServerPlayer player, ResourceLocation itemId) {
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
