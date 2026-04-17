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
	private static final Map<ResourceLocation, SkillCategory> CATEGORIES = new LinkedHashMap<>();

	public SkillCategoryLoader() {
		super(GSON, "skill_categories");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
		CATEGORIES.clear();
		for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
			try {
				SkillCategory cat = SkillCategory.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
				CATEGORIES.put(entry.getKey(), cat);
			} catch (Exception e) {
				Pathed.LOGGER.error("Error loading category {}", entry.getKey(), e);
			}
		}
	}

	public static Map<ResourceLocation, SkillCategory> getCategories() {
		return Collections.unmodifiableMap(CATEGORIES);
	}
}
