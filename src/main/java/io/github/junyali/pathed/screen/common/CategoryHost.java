package io.github.junyali.pathed.screen.common;

import net.minecraft.client.Minecraft;

public interface CategoryHost {
	String getSelectedCategory();
	void selectCategory(String id);
	Minecraft getMinecraft();
}
