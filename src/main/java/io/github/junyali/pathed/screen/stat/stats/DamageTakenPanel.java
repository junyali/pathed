package io.github.junyali.pathed.screen.stat.stats;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.screen.stat.DamageListPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.Map;

public class DamageTakenPanel extends DamageListPanel {
	public DamageTakenPanel(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	public Component getTitle() {
		return Component.translatable("pathed.gui.stats.damage_taken.title");
	}

	@Override
	protected ItemStack headerIcon() {
		return new ItemStack(Items.SHIELD);
	}

	@Override
	protected Map<ResourceLocation, Integer> rawData() {
		Player player = Minecraft.getInstance().player;
		if (player == null) return Collections.emptyMap();
		return ProgressionAttachment.get(player).getDamageTaken();
	}
}
