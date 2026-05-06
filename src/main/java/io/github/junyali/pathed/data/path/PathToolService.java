package io.github.junyali.pathed.data.path;

import io.github.junyali.pathed.attachment.PathDataHolder;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.item.tool.PathTool;
import io.github.junyali.pathed.item.tool.TierAttributeMapping;
import io.github.junyali.pathed.item.tool.ToolTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class PathToolService {
	private PathToolService() {}

	public static void refreshAll(ServerPlayer player) {
		Path path = PathDataHolder.get(player).getPath();
		if (path == null) return;
		ResourceLocation pathId = path.getId();

		ProgressionAttachment.UpgradeData data = ProgressionAttachment.get(player).getUpgradeData();
		int tierLevel = data.getSelectedLevel(TierAttributeMapping.TIER_ATTRIBUTE_ID);
		if (tierLevel <= 0) tierLevel = data.getAttributeLevel(TierAttributeMapping.TIER_ATTRIBUTE_ID);
		ToolTier target = TierAttributeMapping.forAttributeLevel(tierLevel);

		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.getItem() instanceof PathTool tool && tool.getPathId().equals(pathId)) {
				PathTool.retier(stack, target);
			}
		}
	}
}
