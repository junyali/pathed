package io.github.junyali.pathed.data.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public sealed interface SkillNodeReward permits
		SkillNodeReward.ItemReward,
		SkillNodeReward.PointReward {

	Codec<SkillNodeReward> CODEC = Codec.STRING.dispatch(
			reward -> switch (reward) {
				case ItemReward r -> "pathed:item";
				case PointReward r -> "pathed:points";
			},
			type -> switch (type) {
				case "pathed:item" -> ItemReward.CODEC;
				case "pathed:points" -> PointReward.CODEC;
				default -> throw new IllegalArgumentException("Unknown reward type: " + type);
			}
	);

	record ItemReward(ResourceLocation item, int count) implements SkillNodeReward {
		static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				ResourceLocation.CODEC.fieldOf("item").forGetter(ItemReward::item),
				Codec.INT.fieldOf("count").forGetter(ItemReward::count)
		).apply(i, ItemReward::new));
	}

	record PointReward(int classPoints, int generalPoints) implements SkillNodeReward {
		static final MapCodec<PointReward> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.INT.optionalFieldOf("class_points", 0).forGetter(PointReward::classPoints),
				Codec.INT.optionalFieldOf("general_points", 0).forGetter(PointReward::generalPoints)
		).apply(i, PointReward::new));
	}
}
