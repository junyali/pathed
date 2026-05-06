package io.github.junyali.pathed.item.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public enum ToolRole {
	SWORD(null, 3.0f, -2.4f),
	PICKAXE(BlockTags.MINEABLE_WITH_PICKAXE, 1.0f, -2.8f),
	AXE(BlockTags.MINEABLE_WITH_AXE, 6.0f, -3.2f),
	SHOVEL(BlockTags.MINEABLE_WITH_SHOVEL, 1.5f, -3.0f),
	HOE(BlockTags.MINEABLE_WITH_HOE, 0.0f, -3.0f);

	private static final float OFF_ROLE_MULTIPLIER = 0.5f;

	public final TagKey<Block> primaryTag;
	public final float baseDamage;
	public final float baseAttackSpeed;
	ToolRole(TagKey<Block> primaryTag, float baseDamage, float baseAttackSpeed) {
		this.primaryTag = primaryTag;
		this.baseDamage = baseDamage;
		this.baseAttackSpeed = baseAttackSpeed;
	}

	public Tool buildTool(ToolTier tier) {
		List<Tool.Rule> rules = new ArrayList<>();

		rules.add(Tool.Rule.deniesDrops(tier.getIncorrectTag()));

		if (this == SWORD) {
			rules.add(Tool.Rule.minesAndDrops(BlockTags.SWORD_EFFICIENT, 1.5f));
			rules.add(Tool.Rule.overrideSpeed(BlockTags.create(ResourceLocation.withDefaultNamespace("cobwebs")), 15.0f));
			float off = tier.getSpeed() * OFF_ROLE_MULTIPLIER;
			rules.add(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, off));
			rules.add(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, off));
			rules.add(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, off));
			rules.add(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_HOE, off));
			return new Tool(rules, 1.0f, 2);
		}

		float fullSpeed = tier.getSpeed();
		float offSpeed = tier.getSpeed() * OFF_ROLE_MULTIPLIER;

		for (TagKey<Block> tag : List.of(
				BlockTags.MINEABLE_WITH_PICKAXE,
				BlockTags.MINEABLE_WITH_AXE,
				BlockTags.MINEABLE_WITH_SHOVEL,
				BlockTags.MINEABLE_WITH_HOE
		)) {
			rules.add(Tool.Rule.minesAndDrops(tag, tag == primaryTag ? fullSpeed : offSpeed));
		}

		return new Tool(rules, 1.0f, 1);
	}

	public ItemAttributeModifiers buildAttributes(ToolTier tier) {
		ResourceLocation dmgId = ResourceLocation.withDefaultNamespace("base_attack_damage");
		ResourceLocation spdId = ResourceLocation.withDefaultNamespace("base_attack_speed");
		double damage = baseDamage + tier.getLevel() - 1.0;
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE,
						new AttributeModifier(dmgId, damage - 1.0, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED,
						new AttributeModifier(spdId, baseAttackSpeed, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND)
				.build();
	}
}
