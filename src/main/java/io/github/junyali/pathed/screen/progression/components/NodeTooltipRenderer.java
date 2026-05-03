package io.github.junyali.pathed.screen.progression.components;

import io.github.junyali.pathed.data.skill.ClientRequirementCheck;
import io.github.junyali.pathed.data.skill.SkillNode;
import io.github.junyali.pathed.data.skill.SkillNodeRequirement;
import io.github.junyali.pathed.data.skill.SkillNodeReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NodeTooltipRenderer {
	private static final int COLOUR_NAME = 0xFFFFFF;
	private static final int COLOUR_DESC = 0xAAAAAA;
	private static final int COLOUR_PREREQ = 0xFFAA00;
	private static final int COLOUR_REQUIREMENT = 0xFF5555;
	private static final int COLOUR_REWARD = 0x55FF55;
	private static final int COLOUR_MUTED = 0x808080;
	private static final int COLOUR_FULFILLED = 0x55FF55;
	private static final int COLOUR_UNFULFILLED = 0xFF5555;

	private static final String GLYPH_CHECK = "\u2714";
	private static final String GLYPH_CROSS = "\u2718";

	public static void render(GuiGraphics guiGraphics, SkillNode node, Font font, int mouseX, int mouseY) {
		Player player = Minecraft.getInstance().player;
		boolean nodeCompleted = player != null && ClientRequirementCheck.isCompleted(player, node);

		List<Component> lines = buildTooltipLines(node, player, nodeCompleted);
		if (lines.isEmpty()) {
			return;
		}

		List<FormattedCharSequence> visual = new ArrayList<>(lines.size());
		for (Component line : lines) {
			visual.add(line.getVisualOrderText());
		}

		guiGraphics.renderTooltip(font, visual, mouseX, mouseY);
	}

	private static List<Component> buildTooltipLines(SkillNode node, Player player, boolean nodeCompleted) {
		List<Component> lines = new ArrayList<>();

		String categoryId = node.category().getPath();
		String nodeId = node.id().getPath().replace(categoryId + "/", "");

		lines.add(Component.translatable("pathed.skill." + categoryId + "." + nodeId + ".name")
				.withStyle(style -> style.withColor(COLOUR_NAME).withBold(true)));

		Component description = Component.translatable("pathed.skill." + categoryId + "." + nodeId + ".desc")
				.withStyle(style -> style.withColor(COLOUR_DESC));
		lines.add(description);

		if (node.prerequisites() != null && !node.prerequisites().isEmpty()) {
			lines.add(Component.empty());
			lines.add(Component.translatable("pathed.skill.tooltip.prerequisites")
					.withStyle(style -> style.withColor(COLOUR_PREREQ).withBold(true)));

			for (ResourceLocation prereq : node.prerequisites()) {
				boolean met = nodeCompleted || (player != null && ClientRequirementCheck.isPrereqMet(player, prereq));

				String prereqCategory = prereq.getPath().substring(0, prereq.getPath().lastIndexOf("/"));
				String prereqNode = prereq.getPath().substring(prereq.getPath().lastIndexOf("/") + 1);

				MutableComponent name = Component.translatable("pathed.skill." + prereqCategory + "." + prereqNode + ".name")
								.withStyle(style -> style.withColor(0xFFFFFF));

				lines.add(Component.literal(" ")
						.append(prefix(met))
						.append(name));
			}
		}

		if (node.requirements() != null && !node.requirements().isEmpty()) {
			lines.add(Component.empty());
			lines.add(Component.translatable("pathed.skill.tooltip.requirements")
					.withStyle(style -> style.withColor(COLOUR_REQUIREMENT).withBold(true)));
			for (SkillNodeRequirement req : node.requirements()) {
				boolean met = nodeCompleted || (player != null && ClientRequirementCheck.isMet(player, req));
				lines.add(formatRequirement(req, met));
			}
		}

		if (node.rewards() != null && !node.rewards().isEmpty()) {
			lines.add(Component.empty());
			lines.add(Component.translatable("pathed.skill.tooltip.rewards")
					.withStyle(style -> style.withColor(COLOUR_REWARD).withBold(true)));
			for (SkillNodeReward reward : node.rewards()) {
				lines.add(formatReward(reward));
			}
		}

		return lines;
	}

	private static Component formatRequirement(SkillNodeRequirement req, boolean met) {
		MutableComponent body = switch (req) {
			case SkillNodeRequirement.StatRequirement r -> {
				Component target = r.target()
						.map(NodeTooltipRenderer::lookupTargetName)
						.orElse(Component.empty());

				yield Component.translatable(
						"pathed.skill.tooltip.req.stat." + r.stat(),
						r.count(),
						target
				);
			}
			case SkillNodeRequirement.PointRequirement r -> Component.translatable(
					r.classPoints()
							? "pathed.skill.tooltip.req.class_points"
							: "pathed.skill.tooltip.req.general_points",
					r.amount()
			);
			case  SkillNodeRequirement.NodeRequirement r -> {
				int slash = r.nodeId().getPath().lastIndexOf("/");
				// meow?
				String cat = r.nodeId().getPath().substring(0, slash);
				String node = r.nodeId().getPath().substring(slash + 1);
				yield Component.translatable("pathed.skill.tooltip.req.node",
						Component.translatable("pathed.skill." + cat + "." + node + ".name"));
			}
			case SkillNodeRequirement.ItemRequirement r -> {
				ItemStack stack = BuiltInRegistries.ITEM.get(r.item()).getDefaultInstance();
				String key = r.consumed()
						? "pathed.skill.tooltip.req.item_consumed"
						: "pathed.skill.tooltip.req.item";
				yield Component.translatable(key, r.count(), stack.getHoverName());
			}
		};

		body.withStyle(style -> style.withColor(0xFFFFFF));

		return Component.literal(" ")
				.append(prefix(met))
				.append(body);
	}

	private static Component formatReward(SkillNodeReward reward) {
		MutableComponent line = switch(reward) {
			case SkillNodeReward.ItemReward r -> {
				ItemStack stack = BuiltInRegistries.ITEM.get(r.item()).getDefaultInstance();
				yield Component.translatable("pathed.skill.tooltip.reward.item",
						r.count(), stack.getHoverName());
			}
			case SkillNodeReward.PointReward r -> {
				MutableComponent c = Component.empty();
				if (r.classPoints() > 0) {
					c.append(Component.translatable("pathed.skill.tooltip.reward.class_points", r.classPoints()));
				}
				if (r.generalPoints() > 0) {
					c.append(Component.translatable("pathed.skill.tooltip.reward.general_points"));
				}
				yield c;
			}
			case SkillNodeReward.AttributeReward r -> {
				yield Component.translatable("pathed.skill.tooltip.reward.attribute",
						Component.translatable("pathed.attribute." + r.attribute().getPath()),
						r.level()
				);
			}
			case SkillNodeReward.AttributeUpgradeReward r -> {
				yield Component.translatable("pathed.skill.tooltip.reward.attribute_upgrade",
						Component.translatable("pathed.attribute." + r.attribute().getPath()),
						r.levels()
				);
			}
			case SkillNodeReward.ExperienceReward r -> {
				if (r.vanilla()) {
					if (r.amount().endsWith("L")) {
						String levels = r.amount().substring(0, r.amount().length() - 1);
						yield Component.translatable("pathed.skill.tooltip.reward.minecraft_experience_levels", levels);
					} else {
						yield Component.translatable("pathed.skill.tooltip.reward.minecraft_experience", r.amount());
					}
				} else {
					if (r.amount().endsWith("L")) {
						String levels = r.amount().substring(0, r.amount().length() - 1);
						yield Component.translatable("pathed.skill.tooltip.reward.pathed_experience_levels", levels);
					} else {
						yield Component.translatable("pathed.skill.tooltip.reward.pathed_experience", r.amount());
					}
				}
			}
			case SkillNodeReward.EffectReward r -> {
				MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(r.effect());
				Component effectName = effect != null
						? effect.getDisplayName()
						: Component.literal(r.effect().toString());
				yield Component.translatable("pathed.skill.tooltip.reward.effect",
						effectName,
						r.duration() / 20,
						r.amplifier() + 1
				);
			}
			case SkillNodeReward.RecipeReward r -> {
				yield Component.translatable("pathed.skill.tooltip.reward.recipe",
						Component.literal(r.recipe().getPath())
				);
			}
		};

		return Component.literal(" - ").append(line)
				.withStyle(style -> style.withColor(COLOUR_REWARD));
	}

	private static Component lookupTargetName(ResourceLocation target) {
		var entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(target).orElse(null);
		if (entityType != null) return entityType.getDescription();

		var item = BuiltInRegistries.ITEM.getOptional(target).orElse(null);
		if (item != null) return item.getDescription();

		return Component.literal(target.toString());
	}

	private static MutableComponent prefix(boolean met) {
		return Component.literal((met ? GLYPH_CHECK : GLYPH_CROSS) + " ")
				.withStyle(style -> style.withColor(met ? COLOUR_FULFILLED : COLOUR_UNFULFILLED));
	}
}
