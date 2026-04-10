package io.github.junyali.pathed.classsystem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum PathedClass {
	NONE("none"),
	BLADEMASTER("blademaster"),
	SPELUNKER("spelunker"),
	LUMBERJACK("lumberjack"),
	EXCAVATOR("excavator"),
	CULTIVATOR("cultivator");

	private final String id;

	PathedClass(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public ItemStack getStartingTool() {
		return switch (this) {
			case BLADEMASTER    -> new ItemStack(Items.WOODEN_SWORD);
			case SPELUNKER      -> new ItemStack(Items.WOODEN_PICKAXE);
			case LUMBERJACK     -> new ItemStack(Items.WOODEN_AXE);
			case EXCAVATOR      -> new ItemStack(Items.WOODEN_SHOVEL);
			case CULTIVATOR     -> new ItemStack(Items.WOODEN_HOE);
			default             -> ItemStack.EMPTY;
		};
	}
}
