package io.github.junyali.pathed.data.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public sealed interface SkillNodeReward permits
		SkillNodeReward.ItemReward,
		SkillNodeReward.PointReward,
		SkillNodeReward.AttributeReward,
		SkillNodeReward.AttributeUpgradeReward,
		SkillNodeReward.ExperienceReward,
		SkillNodeReward.EffectReward,
		SkillNodeReward.RecipeReward {

	Codec<SkillNodeReward> CODEC = Codec.STRING.dispatch(
			reward -> switch (reward) {
				case ItemReward r               -> "pathed:item";
				case PointReward r              -> "pathed:points";
				case AttributeReward r          -> "pathed:attribute";
				case AttributeUpgradeReward r   -> "pathed:attribute_upgrade";
				case ExperienceReward r         -> "pathed:experience";
				case EffectReward r             -> "pathed:effect";
				case RecipeReward r             -> "pathed:recipe";
			},
			type -> switch (type) {
				case "pathed:item"              -> ItemReward.CODEC;
				case "pathed:points"            -> PointReward.CODEC;
				case "pathed:attribute"         -> AttributeReward.CODEC;
				case "pathed:attribute_upgrade" -> AttributeUpgradeReward.CODEC;
				case "pathed:experience"        -> ExperienceReward.CODEC;
				case "pathed:effect"            -> EffectReward.CODEC;
				case "pathed:recipe"            -> RecipeReward.CODEC;
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

	record AttributeReward(ResourceLocation attribute, int level) implements SkillNodeReward {
		static final MapCodec<AttributeReward> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				ResourceLocation.CODEC.fieldOf("attribute").forGetter(AttributeReward::attribute),
				Codec.INT.fieldOf("level").forGetter(AttributeReward::level)
		).apply(i, AttributeReward::new));
	}

	record AttributeUpgradeReward(ResourceLocation attribute, int levels) implements SkillNodeReward {
		static final MapCodec<AttributeUpgradeReward> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				ResourceLocation.CODEC.fieldOf("attribute").forGetter(AttributeUpgradeReward::attribute),
				Codec.INT.fieldOf("levels").forGetter(AttributeUpgradeReward::levels)
		).apply(i, AttributeUpgradeReward::new));
	}

	record ExperienceReward(int amount, boolean levels, boolean vanilla) implements SkillNodeReward {
		static final MapCodec<ExperienceReward> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.INT.fieldOf("amount").forGetter(ExperienceReward::amount),
				Codec.BOOL.optionalFieldOf("levels", false).forGetter(ExperienceReward::levels),
				Codec.BOOL.optionalFieldOf("vanilla", false).forGetter(ExperienceReward::vanilla)
		).apply(i, ExperienceReward::new));
	}

	record EffectReward(ResourceLocation effect, int duration, int amplifier) implements SkillNodeReward {
		static final MapCodec<EffectReward> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				ResourceLocation.CODEC.fieldOf("effect").forGetter(EffectReward::effect),
				Codec.INT.fieldOf("duration").forGetter(EffectReward::duration),
				Codec.INT.fieldOf("amplifier").forGetter(EffectReward::amplifier)
		).apply(i, EffectReward::new));
	}

	record RecipeReward(ResourceLocation recipe) implements SkillNodeReward {
		static final MapCodec<RecipeReward> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				ResourceLocation.CODEC.fieldOf("recipe").forGetter(RecipeReward::recipe)
		).apply(i, RecipeReward::new));
	}
}
