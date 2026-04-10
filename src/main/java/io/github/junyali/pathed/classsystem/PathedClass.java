package io.github.junyali.pathed.classsystem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum PathedClass {
	NONE(
			"none",
			"pathed.class.none",
			"",
			false
	),
	BLADEMASTER(
			"blademaster",
			"pathed.class.blademaster",
			"",
			true
	),
	SPELUNKER(
			"spelunker",
			"pathed.class.spelunker",
			"",
			true
	),
	LUMBERJACK(
			"lumberjack",
			"pathed.class.lumberjack",
			"",
			true
	),
	EXCAVATOR(
			"excavator",
			"pathed.class.excavator",
			"",
			true
	),
	CULTIVATOR(
			"cultivator",
			"pathed.class.cultivator",
			"",
			true
	);

	private final String id;
	private final String name;
	private final String description;
	private final boolean selectable;

	PathedClass(String id, String name, String description, boolean selectable) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.selectable = selectable;
	}

	public String getId() {
		return id;
	}
	public String getTranslatableName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public boolean isSelectable() {
		return selectable;
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

	public static PathedClass[] selectableValues() {
		return java.util.Arrays.stream(values())
				.filter(PathedClass::isSelectable)
				.toArray(PathedClass[]::new);
	}
}
