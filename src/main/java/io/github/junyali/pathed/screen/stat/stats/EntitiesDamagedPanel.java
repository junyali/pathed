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

public class EntitiesDamagedPanel extends DamageListPanel {
	public EntitiesDamagedPanel(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	public Component getTitle() {
		return Component.translatable("pathed.gui.stats.entities_damaged.title");
	}

	@Override
	protected Map<ResourceLocation, Integer> rawData() {
		Player player = Minecraft.getInstance().player;
		if (player == null) return Collections.emptyMap();
		return ProgressionAttachment.get(player).getDamageDealt();
	}
}
