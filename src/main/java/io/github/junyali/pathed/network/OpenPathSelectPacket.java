package io.github.junyali.pathed.network;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.PathedClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenPathSelectPacket() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<OpenPathSelectPacket> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "open_class_select"));

	public static final StreamCodec<RegistryFriendlyByteBuf, OpenPathSelectPacket> STREAM_CODEC =
			StreamCodec.unit(new OpenPathSelectPacket());

	@Override
	public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(OpenPathSelectPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			PathedClient.shouldShowPathSelection = true;
		});
	}
}
