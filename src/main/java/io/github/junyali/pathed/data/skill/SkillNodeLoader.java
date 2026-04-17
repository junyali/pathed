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
		Map<ResourceLocation, SkillNode> nodes = new LinkedHashMap<>();
		Map<ResourceLocation, SkillCategory> categories = new LinkedHashMap<>();

		for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
			ResourceLocation fileId = entry.getKey();

			try {
				SkillNode parsed = SkillNode.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
				SkillNode node = new SkillNode(fileId, parsed.nameKey(), parsed.descriptionKey(), parsed.category(), parsed.position(), parsed.icon(), parsed.prerequisites(), parsed.requirements(), parsed.rewards(), parsed.pathLocked());
				nodes.put(node.id(), node);
				categories.computeIfAbsent(node.category(), catId ->
						new SkillCategory(catId,
								catId.toLanguageKey("skill_category", "name"),
								"minecraft:nether_star",
								node.pathLocked().orElse(null)
						)
				).addNode(node);
			} catch (Exception e) {
				Pathed.LOGGER.error("Exception loading node {}", fileId, e);
			}
		}

		snapshot = new Snapshot(
				Collections.unmodifiableMap(nodes),
				Collections.unmodifiableMap(categories)
		);

		Pathed.LOGGER.info("Loaded {} nodes across {} categories", nodes.size(), categories.size());
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
