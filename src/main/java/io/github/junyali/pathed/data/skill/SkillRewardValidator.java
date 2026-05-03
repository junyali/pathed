package io.github.junyali.pathed.data.skill;

import io.github.junyali.pathed.data.attribute.Attribute;
import io.github.junyali.pathed.data.attribute.AttributeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.List;

public final class SkillRewardValidator {
	private SkillRewardValidator() {}

	public static List<String> validate(SkillNode node) {
		List<String> errors = new ArrayList<>();
		for (SkillNodeReward reward : node.rewards()) {
			switch (reward) {
				case SkillNodeReward.ItemReward r -> {
					if (!BuiltInRegistries.ITEM.containsKey(r.item())) {
						errors.add("unknown item '" + r.item() + "'");
					}
					if (r.count() <= 0) {
						errors.add("item reward count must be > 0 (got " + r.count() + ")");
					}
				}
				case SkillNodeReward.PointReward r -> {
					if (r.classPoints() < 0 || r.generalPoints() < 0) {
						errors.add("point reward cannot be negative");
					}
				}
				case SkillNodeReward.AttributeReward r -> {
					Attribute attr = AttributeRegistry.get(r.attribute());
					if (attr == null) {
						errors.add("unknown attribute '" + r.attribute() + "'");
					} else if (r.level() < 1 || r.level() > attr.getMaxLevel()) {
						errors.add("attribute '" + r.attribute() + "' level " + r.level() + " out of range [1.." + attr.getMaxLevel() + "]");
					}
				}
				case SkillNodeReward.AttributeUpgradeReward r -> {
					Attribute attr = AttributeRegistry.get(r.attribute());
					if (attr == null) {
						errors.add("unknown attribute '" + r.attribute() + "'");
					} else if (r.levels() <= 0) {
						errors.add("attribute upgrade levels must be > 0 (got " + r.levels() + ")");
					}
				}
				case SkillNodeReward.ExperienceReward r -> {
					int amount = r.amount();
					if (amount <= 0) {
						errors.add("experience reward amount '" + amount + "' is not valid");
					}
				}
				case SkillNodeReward.EffectReward r -> {
					if (!BuiltInRegistries.MOB_EFFECT.containsKey(r.effect())) {
						errors.add("unknown effect '" + r.effect() + "'");
					}
					if (r.duration() <= 0) {
						errors.add("effect duration must be > 0");
					}
					if (r.amplifier() < 0) {
						errors.add("effect amplifier must be >= 0");
					}
				}
				case SkillNodeReward.RecipeReward r -> {
					continue;
				}
			}
		}
		return errors;
	}
}
