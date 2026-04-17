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

	record StatRequirement(String stat, Optional<ResourceLocation> target, int count, boolean consumed) implements SkillNodeRequirement{
		static final MapCodec<StatRequirement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.STRING.fieldOf("stat").forGetter(StatRequirement::stat),
				ResourceLocation.CODEC.optionalFieldOf("target").forGetter(StatRequirement::target),
				Codec.INT.fieldOf("count").forGetter(StatRequirement::count),
				Codec.BOOL.optionalFieldOf("consumed", false).forGetter(StatRequirement::consumed)
		).apply(i, StatRequirement::new));
	}

	record PointRequirement(int amount, boolean classPoints, boolean consumed) implements SkillNodeRequirement {
		static final MapCodec<PointRequirement> CLASS_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.INT.fieldOf("amount").forGetter(PointRequirement::amount),
				Codec.BOOL.optionalFieldOf("consumed", false).forGetter(PointRequirement::consumed)
		).apply(i, (amount, consumed) -> new PointRequirement(amount, true, consumed)));
		static final MapCodec<PointRequirement> GENERAL_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.INT.fieldOf("amount").forGetter(PointRequirement::amount),
				Codec.BOOL.optionalFieldOf("consumed", false).forGetter(PointRequirement::consumed)
		).apply(i, (amount, consumed) -> new PointRequirement(amount, false, consumed)));
	}

	record NodeRequirement(ResourceLocation nodeId) implements SkillNodeRequirement {
		static final MapCodec<NodeRequirement> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				ResourceLocation.CODEC.fieldOf("node").forGetter(NodeRequirement::nodeId)
		).apply(i, NodeRequirement::new));
	}
}
