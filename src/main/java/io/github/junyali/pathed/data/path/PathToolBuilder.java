package io.github.junyali.pathed.data.path;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Unbreakable;

import java.util.List;

public class PathToolBuilder {
	private PathToolBuilder() {}

	public static ItemStack create(Item item, String translationKey, ChatFormatting nameColour) {
		ItemStack stack = new ItemStack(item);

		stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
		stack.set(DataComponents.ENCHANTMENTS, null);
		stack.set(DataComponents.CUSTOM_NAME,
				Component.translatable(translationKey + ".name")
						.withStyle(Style.EMPTY.withColor(nameColour).withBold(true).withItalic(false))
		);
		stack.set(DataComponents.LORE, new ItemLore(List.of(
				Component.translatable(translationKey + ".description")
						.withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false))
		)));
		stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

		return stack;
	}
}
