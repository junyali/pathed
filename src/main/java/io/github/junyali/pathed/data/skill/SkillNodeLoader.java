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

import java.util.LinkedHashMap;
import java.util.Map;

public class SkillNodeLoader extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().create();

	private static final Map<ResourceLocation, SkillNode> NODES = new LinkedHashMap<>();
	private static final Map<ResourceLocation, SkillCategory> CATEGORIES = new LinkedHashMap<>();

	public SkillNodeLoader() {
		super(GSON, "skill_nodes");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
		NODES.clear();
		CATEGORIES.clear();

		for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
			ResourceLocation fileId = entry.getKey();

			try {
				SkillNode node = SkillNode.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
				NODES.put(node.id(), node);
				CATEGORIES.computeIfAbsent(node.category(), catId ->
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

		Pathed.LOGGER.info("Loaded {} nodes across {} categories", NODES.size(), CATEGORIES.size());
	}
}
