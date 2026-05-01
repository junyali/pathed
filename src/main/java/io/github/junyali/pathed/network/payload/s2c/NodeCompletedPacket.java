package io.github.junyali.pathed.network.payload.s2c;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.toast.NodeGetToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record NodeCompletedPacket(ResourceLocation nodeId) implements CustomPacketPayload {
	public static final Type<NodeCompletedPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "node_completed"));

	public static final StreamCodec<RegistryFriendlyByteBuf, NodeCompletedPacket> STREAM_CODEC =
			StreamCodec.composite(
					ResourceLocation.STREAM_CODEC,
					NodeCompletedPacket::nodeId,
					NodeCompletedPacket::new
			);

	@Override
	@NotNull
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(NodeCompletedPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player == null) return;
			mc.getToasts().addToast(new NodeGetToast(packet.nodeId));
			mc.player.playSound(
					SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f
			);
		});
	}
}
