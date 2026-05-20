package io.github.junyali.pathed.screen.stat.stats;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.screen.stat.IconCountStatPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Collections;
import java.util.Map;

public class BlocksBrokenPanel extends IconCountStatPanel<ResourceLocation> {
	public BlocksBrokenPanel(int x, int y, int w, int h) {
		super(x, y, w, h, 32, 32);
	}

	@Override
	public Component getTitle() {
		return Component.translatable("pathed.gui.stats.blocks_broken.title");
	}

	@Override
	protected ItemStack headerIcon() {
		return new ItemStack(Items.IRON_PICKAXE);
	}

	@Override
	protected Map<ResourceLocation, Integer> data() {
		Player player = Minecraft.getInstance().player;
		if (player == null) return Collections.emptyMap();
		return ProgressionAttachment.get(player).getBlocksBroken();
	}

	@Override
	protected ItemStack iconFor(ResourceLocation id) {
		Block block = BuiltInRegistries.BLOCK.get(id);
		ItemStack stack = new ItemStack(block);
		return stack.isEmpty() ? new ItemStack(BuiltInRegistries.ITEM.get(id)) : stack;
	}

	@Override
	protected Component nameFor(ResourceLocation id) {
		Block block = BuiltInRegistries.BLOCK.get(id);
		if (block == Blocks.AIR) {
			return Component.literal(id.toString());
		}
		return block.getName();
	}
}
