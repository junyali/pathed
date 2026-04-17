package io.github.junyali.pathed.network.payload.s2c;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.skill.ClientSkillData;
import io.github.junyali.pathed.data.skill.SkillCategory;
import io.github.junyali.pathed.data.skill.SkillNode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record SyncSkillDataPacket (
		Map<ResourceLocation, SkillCategory> categories,
		Map<ResourceLocation, SkillNode> nodes
) implements CustomPacketPayload {
	public static final Type<SyncSkillDataPacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(Pathed.MODID, "sync_skill_data"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SyncSkillDataPacket> STREAM_CODEC =
			new StreamCodec<>() {
				@Override
				@NotNull
				public SyncSkillDataPacket decode(RegistryFriendlyByteBuf buf) {
					int catCount = buf.readVarInt();
					// meow?
					Map<ResourceLocation, SkillCategory> cats = new LinkedHashMap<>();
					for (int i = 0; i < catCount; i++) {
						ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
						String name = buf.readUtf();
						String icon = buf.readUtf();
						ResourceLocation pathLocked = buf.readBoolean() ? ResourceLocation.STREAM_CODEC.decode(buf) : null;
						ResourceLocation background = ResourceLocation.STREAM_CODEC.decode(buf);
						cats.put(id, new SkillCategory(name, icon, pathLocked, background));
					}

					int nodeCount = buf.readVarInt();
					Map<ResourceLocation, SkillNode> nodes = new LinkedHashMap<>();
					for (int i = 0; i < nodeCount; i++) {
						ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
						String name = buf.readUtf();
						String desc = buf.readUtf();
						ResourceLocation category = ResourceLocation.STREAM_CODEC.decode(buf);
						int x = buf.readVarInt();
						int y = buf.readVarInt();
						String iconType = buf.readUtf();
						String iconValue = buf.readUtf();
						String nodeType = buf.readUtf();
						int preCount = buf.readVarInt();
						List<ResourceLocation> prereqs = new ArrayList<>();
						for (int j = 0; j < preCount; j++) {
							prereqs.add(ResourceLocation.STREAM_CODEC.decode(buf));
						}
						int prevCount = buf.readVarInt();
						List<ResourceLocation> prevnodes = new ArrayList<>();
						for (int j = 0; j < prevCount; j++) {
							prevnodes.add(ResourceLocation.STREAM_CODEC.decode(buf));
						}
						ResourceLocation nodePath = buf.readBoolean() ? ResourceLocation.STREAM_CODEC.decode(buf) : null;
						// OH MY GOODNESS HOW LONG IS THIS GOING ON FOR
						SkillNode node = new SkillNode(id, name, desc, category,
								new SkillNode.NodePosition(x, y),
								new SkillNode.NodeIcon(iconType, iconValue),
								nodeType,
								prereqs,
								prevnodes,
								List.of(),
								List.of(),
								Optional.ofNullable(nodePath));
						nodes.put(id, node);

						// mrow?
						SkillCategory cat = cats.get(category);
						if (cat != null) {
							cat.addNode(node);
						}
					}

					return new SyncSkillDataPacket(cats, nodes);
				}

				@Override
				public void encode(RegistryFriendlyByteBuf buf, SyncSkillDataPacket pkt) {
					// AHHHH so much to write D:
					buf.writeVarInt(pkt.categories.size());
					for (var entry : pkt.categories.entrySet()) {
						ResourceLocation.STREAM_CODEC.encode(buf, entry.getKey());
						buf.writeUtf(entry.getValue().getNameKey());
						buf.writeUtf(entry.getValue().getIconItem());
						boolean hasPath = entry.getValue().getPathLocked().isPresent();
						buf.writeBoolean(hasPath);
						if (hasPath) ResourceLocation.STREAM_CODEC.encode(buf, entry.getValue().getPathLocked().get());
						ResourceLocation.STREAM_CODEC.encode(buf, entry.getValue().getBackground());
					}

					buf.writeVarInt(pkt.nodes.size());
					for (var entry : pkt.nodes.entrySet()) {
						SkillNode n = entry.getValue();
						ResourceLocation.STREAM_CODEC.encode(buf, n.id());
						buf.writeUtf(n.nameKey());
						buf.writeUtf(n.descriptionKey());
						ResourceLocation.STREAM_CODEC.encode(buf, n.category());
						buf.writeVarInt(n.position().x());
						buf.writeVarInt(n.position().y());
						buf.writeUtf(n.icon().type());
						buf.writeUtf(n.icon().value());
						buf.writeUtf(n.nodeType());
						buf.writeVarInt(n.prerequisites().size());
						for (ResourceLocation pre : n.prerequisites()) {
							ResourceLocation.STREAM_CODEC.encode(buf, pre);
						}
						buf.writeVarInt(n.previousNodes().size());
						for (ResourceLocation prev : n.previousNodes()) {
							ResourceLocation.STREAM_CODEC.encode(buf, prev);
						}
						boolean hasPath = n.pathLocked().isPresent();
						buf.writeBoolean(hasPath);
						if (hasPath) ResourceLocation.STREAM_CODEC.encode(buf, n.pathLocked().get());
					}
				}
			};

	@Override
	public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SyncSkillDataPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientSkillData.setCategories(packet.categories);
			ClientSkillData.setNodes(packet.nodes);
		});
	}
}
