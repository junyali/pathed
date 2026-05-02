package io.github.junyali.pathed.data.skill;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.junyali.pathed.Pathed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkillNodeLoader extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().create();

	private record Snapshot(
		Map<ResourceLocation, SkillNode> nodes,
		Map<ResourceLocation, SkillCategory> categories
	) {
		static final Snapshot EMPTY = new Snapshot(Map.of(), Map.of());
	}

	private static volatile Snapshot snapshot = Snapshot.EMPTY;

	public SkillNodeLoader() {
		super(GSON, "skill_nodes");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
		Map<ResourceLocation, SkillNode> parsedNodes = new LinkedHashMap<>();
		Set<ResourceLocation> invalidNodes = new HashSet<>();
		int parseFailures = 0;

		for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
			ResourceLocation fileId = entry.getKey();

			try {
				SkillNode parsed = SkillNode.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
				SkillNode node = new SkillNode(
						fileId,
						parsed.nameKey(),
						parsed.descriptionKey(),
						parsed.category(),
						parsed.position(),
						parsed.icon(),
						parsed.nodeType(),
						parsed.type(),
						parsed.base(),
						parsed.prerequisites(),
						parsed.previousNodes(),
						parsed.requirements(),
						parsed.rewards(),
						parsed.pathLocked()
				);
				parsedNodes.put(fileId, node);
			} catch (Exception e) {
				parseFailures++;
				invalidNodes.add(fileId);
				Pathed.LOGGER.warn("Failed to parse skill node '{}': {}", fileId, e.getMessage());
			}
		}

		Map<ResourceLocation, SkillNode> validNodes = new LinkedHashMap<>();
		Set<ResourceLocation> skippedNodes = new HashSet<>();

		for (SkillNode node: parsedNodes.values()) {
			if (isNodeValid(node, parsedNodes, invalidNodes, skippedNodes)) {
				validNodes.put(node.id(), node);

				SkillCategory cat = SkillCategoryLoader.getCategories().get(node.category());
				if (cat != null) {
					cat.addNode(node);
				}
			} else {
				skippedNodes.add(node.id());
			}
		}

		snapshot = new Snapshot(
				Collections.unmodifiableMap(validNodes),
				SkillCategoryLoader.getCategories()
		);

		int totalValid = validNodes.size();
		int totalSkipped = skippedNodes.size();

		Pathed.LOGGER.info("Loaded {} skill nodes across {} categories ({} parsed, {} skipped due to dependencies, {} failed to parse)",
				totalValid,
				SkillCategoryLoader.getCategories().size(),
				parsedNodes.size(),
				totalSkipped,
				parseFailures
		);

		if (!skippedNodes.isEmpty()) {
			Pathed.LOGGER.warn("Skipped nodes due to missing dependencies or categories: {}", skippedNodes);
		}
	}

	private boolean isNodeValid(SkillNode node, Map<ResourceLocation, SkillNode> parsedNodes, Set<ResourceLocation> invalidNodes, Set<ResourceLocation> skippedNodes) {
		if (!SkillCategoryLoader.getCategories().containsKey(node.category())) {
			Pathed.LOGGER.warn("Node '{}' references non-existent category '{}'", node.id(), node.category());
			return false;
		}

		for (ResourceLocation prereq : node.prerequisites()) {
			if (invalidNodes.contains(prereq) || skippedNodes.contains(prereq)) {
				Pathed.LOGGER.warn("Node '{}' depends on invalid/skipped prerequisite '{}'", node.id(), prereq);
				return false;
			}
			if (!parsedNodes.containsKey(prereq)) {
				Pathed.LOGGER.warn("Node '{}' references non-existent prerequisite '{}'", node.id(), prereq);
				return false;
			}
		}

		for (ResourceLocation prevNode : node.previousNodes()) {
			if (invalidNodes.contains(prevNode) || skippedNodes.contains(prevNode)) {
				Pathed.LOGGER.warn("Node '{}' depends on invalid/skipped previous node '{}'", node.id(), prevNode);
				return false;
			}
			if (!parsedNodes.containsKey(prevNode)) {
				Pathed.LOGGER.warn("Node '{}' references non-existent previous node '{}'", node.id(), prevNode);
				return false;
			}
		}

		return true;
	}

	public static Map<ResourceLocation, SkillNode> getNodes() {
		return snapshot.nodes;
	}

	public static Map<ResourceLocation, SkillCategory> getCategories() {
		return snapshot.categories;
	}

	public static Collection<SkillNode> getNodesForCategory(ResourceLocation categoryId) {
		SkillCategory cat = snapshot.categories.get(categoryId);
		return cat == null ? List.of() : cat.getNodes();
	}

	public static SkillNode getNode(ResourceLocation id) {
		return snapshot.nodes.get(id);
	}
}
