package io.github.junyali.pathed.data.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record SkillNode(
		ResourceLocation id,
		String nameKey,
		String descriptionKey,
		ResourceLocation category,
		NodePosition position,
		NodeIcon icon,
		List<ResourceLocation> prerequisites,
		// List<something> requirements,
		// List<something> rewards,
		Optional<ResourceLocation> pathLocked
) {
	public record NodePosition(int x, int y) {
		public static final Codec<NodePosition> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("x").forGetter(NodePosition::x),
				Codec.INT.fieldOf("y").forGetter(NodePosition::y)
		).apply(i, NodePosition::new));
	}

	public record NodeIcon(String type, String value) {
		public static final Codec<NodeIcon> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.STRING.fieldOf("type").forGetter(NodeIcon::type),
				Codec.STRING.fieldOf("value").forGetter(NodeIcon::value)
		).apply(i, NodeIcon::new));
	}

	public static final Codec<SkillNode> CODEC = RecordCodecBuilder.create(i -> i.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(SkillNode::id),
			Codec.STRING.fieldOf("name").forGetter(SkillNode::nameKey),
			Codec.STRING.fieldOf("description").forGetter(SkillNode::descriptionKey),
			ResourceLocation.CODEC.fieldOf("category").forGetter(SkillNode::category),
			NodePosition.CODEC.fieldOf("position").forGetter(SkillNode::position),
			NodeIcon.CODEC.fieldOf("icon").forGetter(SkillNode::icon),
			ResourceLocation.CODEC.listOf().optionalFieldOf("prerequisites", List.of()).forGetter(SkillNode::prerequisites),
			// requirement field
			// reward field
			ResourceLocation.CODEC.optionalFieldOf("path_locked").forGetter(SkillNode::pathLocked)
	).apply(i, SkillNode::new));
}
