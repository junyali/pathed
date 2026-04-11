package io.github.junyali.pathed.item.tool;

import net.minecraft.world.item.ItemStack;

public class PathTool {
	private PathTool() {}

	public static boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public static int getEnchantmentValue() {
		return 0;
	}
}
