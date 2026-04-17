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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SkillCategoryLoader extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().create();
	private static volatile Map<ResourceLocation, SkillCategory> categories = Map.of();

	public SkillCategoryLoader() {
		super(GSON, "skill_categories");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
		Map<ResourceLocation, SkillCategory> built = new LinkedHashMap<>();
		for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
			try {
				SkillCategory cat = SkillCategory.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
				built.put(entry.getKey(), cat);
			} catch (Exception e) {
				Pathed.LOGGER.error("Error loading category {}", entry.getKey(), e);
			}
		}
		categories = Collections.unmodifiableMap(built);
	}

	public static Map<ResourceLocation, SkillCategory> getCategories() {
		return categories;
	}
}
