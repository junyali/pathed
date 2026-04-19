package io.github.junyali.pathed.item.tool;

import com.mojang.serialization.Codec;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum ToolTier {
	WOOD(0, 2.0f, BlockTags.INCORRECT_FOR_WOODEN_TOOL),
	GOLD(0, 12.0f, BlockTags.INCORRECT_FOR_GOLD_TOOL),
	STONE(1, 4.0f, BlockTags.INCORRECT_FOR_STONE_TOOL),
	COPPER(1, 5.0f, BlockTags.INCORRECT_FOR_STONE_TOOL),
	IRON(2, 6.0f, BlockTags.INCORRECT_FOR_IRON_TOOL),
	DIAMOND(3, 8.0f, BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
	NETHERITE(4, 9.0f, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);

	private final int level;
	private final float speed;
	private final TagKey<Block> incorrectTag;

	ToolTier(int level, float speed, TagKey<Block> incorrectTag) {
		this.level = level;
		this.speed = speed;
		this.incorrectTag = incorrectTag;
	}

	public int getLevel() {
		return level;
	}

	public float getSpeed() {
		return speed;
	}

	public TagKey<Block> getIncorrectTag() {
		return incorrectTag;
	}

	public static final Codec<ToolTier> CODEC = Codec.STRING.xmap(
			s -> ToolTier.valueOf(s.toUpperCase()),
			t -> t.name().toLowerCase()
	);
}
