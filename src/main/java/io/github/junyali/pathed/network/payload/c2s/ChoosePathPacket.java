package io.github.junyali.pathed.network.payload.c2s;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.PathDataHolder;
import io.github.junyali.pathed.registry.PathedAttachments;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ChoosePathPacket(ResourceLocation pathId) implements CustomPacketPayload {
	public static final Type<ChoosePathPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "choose_path"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ChoosePathPacket> STREAM_CODEC =
			StreamCodec.composite(
					ResourceLocation.STREAM_CODEC,
					ChoosePathPacket::pathId,
					ChoosePathPacket::new
			);

	@Override
	@NotNull
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ChoosePathPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				Path path = PathRegistry.get(packet.pathId());
				if (path != null && path.isSelectable()) {
					PathDataHolder.get(player).setPath(path);
					path.getStartingItems().giveToPlayer(player);
				}
			}
		});
	}
}
