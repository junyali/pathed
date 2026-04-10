package io.github.junyali.pathed.network;

import io.github.junyali.pathed.Pathed;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenClassSelectPacket() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<OpenClassSelectPacket> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "open_class_select"));

	public static final StreamCodec<RegistryFriendlyByteBuf, OpenClassSelectPacket> STREAM_CODEC =
			StreamCodec.unit(new OpenClassSelectPacket());

	@Override
	public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(OpenClassSelectPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			//Minecraft.getInstance().setScreen(screenclass);
		});
	}
}
