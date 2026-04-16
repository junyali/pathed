package io.github.junyali.pathed.data.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public sealed interface SkillNodeRequirement permits
		SkillNodeRequirement.StatRequirement,
		SkillNodeRequirement.PointRequirement,
		SkillNodeRequirement.NodeRequirement {

	Codec<SkillNodeRequirement> CODEC = Codec.STRING.dispatch(
			req -> switch (req) {
				case StatRequirement r -> "pathed:stat_count";
				case PointRequirement r -> r.classPoints() ? "pathed:class_points" : "pathed:general_points";
				case NodeRequirement r -> "pathed:node";
			},
			type -> switch (type) {
				case "pathed:stat_count" -> StatRequirement.CODEC;
				case "pathed:class_points" -> PointRequirement.CLASS_CODEC;
				case "pathed:general_points" -> PointRequirement.GENERAL_CODEC;
				case "pathed:node" -> NodeRequirement.CODEC;
				default -> throw new IllegalArgumentException("Unknown req type: " + type);
			}
	);

	record StatRequirement(String stat, Optional<ResourceLocation> target, int count) implements SkillNodeRequirement{
		static final MapCodec<StatRequirement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.STRING.fieldOf("stat").forGetter(StatRequirement::stat),
				ResourceLocation.CODEC.optionalFieldOf("target").forGetter(StatRequirement::target),
				Codec.INT.fieldOf("count").forGetter(StatRequirement::count)
		).apply(i, StatRequirement::new));
	}

	record PointRequirement(int amount, boolean classPoints) implements SkillNodeRequirement {
		static final MapCodec<PointRequirement> CLASS_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.INT.fieldOf("amount").forGetter(PointRequirement::amount)
		).apply(i, amount -> new PointRequirement(amount, true)));
		static final MapCodec<PointRequirement> GENERAL_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.INT.fieldOf("amount").forGetter(PointRequirement::amount)
		).apply(i, amount -> new PointRequirement(amount, false)));
	}

	record NodeRequirement(ResourceLocation nodeId) implements SkillNodeRequirement {
		static final MapCodec<NodeRequirement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				ResourceLocation.CODEC.fieldOf("node").forGetter(NodeRequirement::nodeId)
		).apply(i, NodeRequirement::new));
	}
}
