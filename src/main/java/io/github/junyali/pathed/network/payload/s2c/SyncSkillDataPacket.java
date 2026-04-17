package io.github.junyali.pathed.network.payload.s2c;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.skill.SkillCategory;
import io.github.junyali.pathed.data.skill.SkillNode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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
						cats.put(id, new SkillCategory(name, icon, pathLocked));
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
						int preCount = buf.readVarInt();
						List<ResourceLocation> prereqs = new ArrayList<>();
						for (int j = 0; j < preCount; j++) {
							prereqs.add(ResourceLocation.STREAM_CODEC.decode(buf));
						}
						ResourceLocation nodePath = buf.readBoolean() ? ResourceLocation.STREAM_CODEC.decode(buf) : null;
						// OH MY GOODNESS HOW LONG IS THIS GOING ON FOR
						SkillNode node = new SkillNode(id, name, desc, category,
								new SkillNode.NodePosition(x, y),
								new SkillNode.NodeIcon(iconType, iconValue),
								prereqs,
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
			}
}
