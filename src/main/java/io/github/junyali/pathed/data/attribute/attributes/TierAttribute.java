package io.github.junyali.pathed.data.attribute.attributes;

import io.github.junyali.pathed.data.attribute.Attribute;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TierAttribute extends Attribute {
	public TierAttribute() {
		super("tier", 5, createIcon());
	}

	private static ItemStack createIcon() {
		ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
		stack.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
		stack.set(DataComponents.CUSTOM_NAME, Component.literal("Tier").withStyle(Style.EMPTY.withItalic(false)));
		stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
		stack.remove(DataComponents.LORE);
		return stack;
	}
}
