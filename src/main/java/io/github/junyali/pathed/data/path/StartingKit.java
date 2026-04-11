package io.github.junyali.pathed.data.path;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StartingKit {
	public static final StartingKit EMPTY = new StartingKit(List.of());

	private final List<ItemStack> items;

	private StartingKit(List<ItemStack> items) {
		this.items = items;
	}

	public static StartingKit of(ItemStack... items) {
		return new StartingKit(Arrays.asList(items));
	}

	public static Builder builder() {
		return new Builder();
	}

	public List<ItemStack> getItems() {
		return Collections.unmodifiableList(items);
	}

	public void giveToPlayer(Player player) {
		for (ItemStack stack : items) {
			player.getInventory().add(stack.copy());
		}
	}

	public static class Builder {
		private final List<ItemStack> items = new ArrayList<>();

		public Builder add(ItemStack stack) {
			items.add(stack);
			return this;
		}

		public Builder add(ItemStack stack, int count) {
			ItemStack copy = stack.copy();
			copy.setCount(count);
			items.add(copy);
			return this;
		}

		public StartingKit build() {
			return new StartingKit(List.copyOf(items));
		}
	}
}
