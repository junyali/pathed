package io.github.junyali.pathed.data.path;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

public class PathToolBuilder {
	private PathToolBuilder() {}

	public static ItemStack create(Item item, ResourceLocation toolId, ChatFormatting nameColour) {
		String key = toolId.toLanguageKey("item", "pathtool");
		ItemStack stack = new ItemStack(item);

		stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
		// TODO: make path tools completely unenchantable
		// below implementation technically still allows for the tool to be enchanted :/
		stack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		stack.set(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
		stack.set(DataComponents.CUSTOM_NAME,
				Component.translatable(key + ".name")
						.withStyle(Style.EMPTY.withColor(nameColour).withBold(true).withItalic(false))
		);
		stack.set(DataComponents.LORE, new ItemLore(List.of(
				Component.translatable(key + ".description")
						.withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false))
		)));
		stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

		return stack;
	}
}
