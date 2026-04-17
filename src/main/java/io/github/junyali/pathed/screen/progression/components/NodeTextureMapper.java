package io.github.junyali.pathed.screen.progression.components;

import net.minecraft.resources.ResourceLocation;

public class NodeTextureMapper {
	public static ResourceLocation getTextureForNodeType(String nodeType, boolean completed) {
		String state = completed ? "obtained" : "unobtained";

		return switch (nodeType.toLowerCase()) {
			case "task" -> ResourceLocation.parse("minecraft:textures/gui/sprites/advancements/task_frame_" + state + ".png");
			case "goal" -> ResourceLocation.parse("minecraft:textures/gui/sprites/advancements/goal_frame_" + state + ".png");
			case "challenge" -> ResourceLocation.parse("minecraft:textures/gui/sprites/advancements/challenge_frame_" + state + ".png");
			default -> ResourceLocation.parse("minecraft:textures/gui/sprites/advancements/task_frame_" + state + ".png");
		};
	}
}
