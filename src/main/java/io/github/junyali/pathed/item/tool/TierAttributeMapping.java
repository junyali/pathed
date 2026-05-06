package io.github.junyali.pathed.item.tool;

import io.github.junyali.pathed.Pathed;
import net.minecraft.resources.ResourceLocation;

public final class TierAttributeMapping {
	private static final ToolTier[] BY_LEVEL = {
			ToolTier.WOOD, ToolTier.STONE, ToolTier.IRON, ToolTier.DIAMOND, ToolTier.NETHERITE
	};

	private TierAttributeMapping() {}

	public static ToolTier forAttributeLevel(int level) {
		if (level <= 0) return ToolTier.WOOD;
		return BY_LEVEL[Math.min(level, BY_LEVEL.length) - 1];
	}

	public static final ResourceLocation TIER_ATTRIBUTE_ID = ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "tier");
}
