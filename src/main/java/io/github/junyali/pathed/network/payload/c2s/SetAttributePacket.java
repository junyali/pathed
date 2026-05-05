package io.github.junyali.pathed.network.payload.c2s;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.data.attribute.AttributeRegistry;
import io.github.junyali.pathed.data.path.Path;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SetAttributePacket(List<Entry> changes) implements CustomPacketPayload {
	public static final Type<SetAttributePacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "set_attribute"));

	public record Entry(ResourceLocation id, int selectedLevel, boolean active) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC =
				StreamCodec.composite(
						ResourceLocation.STREAM_CODEC,
						Entry::id,
						ByteBufCodecs.VAR_INT,
						Entry::selectedLevel,
						ByteBufCodecs.BOOL,
						Entry::active,
						Entry::new
				);
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, SetAttributePacket> STREAM_CODEC =
			StreamCodec.composite(
					Entry.STREAM_CODEC.apply(ByteBufCodecs.list()),
					SetAttributePacket::changes,
					SetAttributePacket::new
			);

	@Override
	@NotNull
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SetAttributePacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer player)) return;

			ProgressionAttachment.UpgradeData data = ProgressionAttachment.get(player).getUpgradeData();
			boolean changed = false;

			for (Entry e : packet.changes()) {
				Attribute attr = AttributeRegistry.get(e.id());
				if (attr == null) {
					Pathed.LOGGER.warn("{} sent SetAttribute for unknown id {}", player.getGameProfile().getName(), e.id());
					continue;
				}

				int obtained = data.getAttributeLevel(e.id());
				if (obtained <= 0) continue;

				int selected = Math.clamp(e.selectedLevel(), 1, obtained);

				boolean wantActive = e.active();
				if (wantActive) {
					if (attr.getPathLocked().isPresent()) {
						Path pathId = PathAttachment.get(player).getPath();
						if (pathId == null || !pathId.getId().equals(attr.getPathLocked().get())) {
							wantActive = false;
						}
					}
					if (wantActive) {
						for (ResourceLocation incompatible : attr.getIncompatibleWith()) {
							if (data.isActive(incompatible)) {
								wantActive = false;
								break;
							}
						}
					}
				}

				int oldLevel = data.getSelectedLevel(e.id());
				boolean oldActive = data.isActive(e.id());

				if (oldLevel != selected) {
					data.setSelectedLevel(e.id(), selected);
					changed = true;
				}
				if (oldActive != wantActive) {
					data.setActive(e.id(), wantActive);
					attr.onLevelChange(player, oldActive ? oldLevel : 0, wantActive ? selected : 0);
					changed = true;
				}
			}

			if (changed) ProgressionAttachment.get(player).sync(player);
		});
	}
}
