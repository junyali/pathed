package io.github.junyali.pathed.item.tool;

import io.github.junyali.pathed.component.PathedDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PathTool extends Item implements IPathTool {
	private final ResourceLocation pathId;
	private final ToolRole role;

	public PathTool(ResourceLocation pathId, ToolRole role, Properties properties) {
		super(
				properties
						.stacksTo(1)
						.component(PathedDataComponents.TOOL_TIER.get(), ToolTier.WOOD)
						.component(DataComponents.TOOL, role.buildTool(ToolTier.WOOD))
						.component(DataComponents.ATTRIBUTE_MODIFIERS, role.buildAttributes(ToolTier.WOOD))
		);
		this.pathId = pathId;
		this.role = role;
	}

	public ResourceLocation getPathId() {
		return pathId;
	}

	public ToolRole getRole() {
		return role;
	}

	@Override
	public boolean isEnchantable(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public int getEnchantmentValue(@NotNull ItemStack stack) {
		return 0;
	}

	@Override
	public boolean isDamageable(@NotNull ItemStack stack) {
		return false;
	}

	public static void retier(ItemStack stack, ToolTier newTier) {
		if (!(stack.getItem() instanceof PathTool tool)) return;
		ToolTier current = stack.get(PathedDataComponents.TOOL_TIER.get());
		if (current == newTier) return;
		stack.set(PathedDataComponents.TOOL_TIER.get(), newTier);
		stack.set(DataComponents.TOOL, tool.role.buildTool(newTier));
		stack.set(DataComponents.ATTRIBUTE_MODIFIERS, tool.role.buildAttributes(newTier));
	}
}
