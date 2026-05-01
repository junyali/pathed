package io.github.junyali.pathed.toast;

import io.github.junyali.pathed.data.skill.ClientSkillData;
import io.github.junyali.pathed.data.skill.SkillNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class NodeGetToast implements Toast {
	private static final long DISPLAY_MS = 5000L;
	private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("toast/advancement");

	private final ResourceLocation nodeId;
	private final Component header = Component.translatable("pathed.toast.node_get");
	private long firstDrawn = -1L;
	private boolean played;

	public NodeGetToast(ResourceLocation nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	@NotNull
	public Visibility render(@NotNull GuiGraphics guiGraphics, @NotNull ToastComponent toastComponent, long timeSinceLastVisible) {
		if (firstDrawn == -1L) firstDrawn = timeSinceLastVisible;

		SkillNode node = ClientSkillData.getNodes().get(nodeId);

		guiGraphics.blitSprite(BACKGROUND, 0, 0, width(), height());
		Font font = Minecraft.getInstance().font;
		guiGraphics.drawString(font, header, 30, 7, 0xFFFFFFFF, false);

		Component title = node != null
				? Component.translatable(node.nameKey())
				: Component.literal(nodeId.toString());
		guiGraphics.drawString(font, title, 30, 18, 0xFFFFFFFF, false);

		ItemStack icon = node != null ? resolveIcon(node) : new ItemStack(Items.BARRIER);
		guiGraphics.renderFakeItem(icon, 8, 8);

		return (timeSinceLastVisible - firstDrawn) >= DISPLAY_MS * toastComponent.getNotificationDisplayTimeMultiplier()
				? Visibility.HIDE
				: Visibility.SHOW;
	}

	private static ItemStack resolveIcon(SkillNode node) {
		if ("item".equals(node.icon().type())) {
			ResourceLocation id = ResourceLocation.tryParse(node.icon().value());
			if (id != null) {
				return new ItemStack(BuiltInRegistries.ITEM.get(id));
			}
		}
		return new ItemStack(Items.BARRIER);
	}
}
