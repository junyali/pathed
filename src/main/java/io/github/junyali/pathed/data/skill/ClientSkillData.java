package io.github.junyali.pathed.data.skill;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ClientSkillData {
	private static Map<ResourceLocation, SkillCategory> categories = Map.of();
	private static Map<ResourceLocation, SkillNode> nodes = Map.of();

	public static void setCategories(Map<ResourceLocation, SkillCategory> cats) {
		// meow?
		categories = cats;
	}

	public static void setNodes(Map<ResourceLocation, SkillNode> n) {
		nodes = n;
	}

	public static Map<ResourceLocation, SkillCategory> getCategories() {
		return categories;
	}

	public static Map<ResourceLocation, SkillNode> getNodes() {
		return nodes;
	}
}
