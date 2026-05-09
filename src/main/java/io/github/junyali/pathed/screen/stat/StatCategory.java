package io.github.junyali.pathed.screen.stat;

import net.minecraft.world.item.ItemStack;

public record StatCategory(
		String id,
		String nameKey,
		ItemStack icon,
		PanelFactory factory
) {
	@FunctionalInterface
	public interface PanelFactory {
		AbstractStatPanel create(int x, int y, int w, int h);
	}
}
