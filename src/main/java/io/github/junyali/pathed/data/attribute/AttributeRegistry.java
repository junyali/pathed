package io.github.junyali.pathed.data.attribute;

import io.github.junyali.pathed.Pathed;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttributeRegistry {
	private static final Map<ResourceLocation, Attribute> ATTRIBUTES = new LinkedHashMap<>();

	private AttributeRegistry() {}

	public static Attribute register(Attribute attribute) {
		if (ATTRIBUTES.containsKey(attribute.getId())) {
			Pathed.LOGGER.warn("Duplicated attribute {}, overriding...", attribute.getId());
		}
		ATTRIBUTES.put(attribute.getId(), attribute);
		return attribute;
	}

	public static Attribute get(ResourceLocation id) {
		return ATTRIBUTES.get(id);
	}

	public static boolean exists(ResourceLocation id) {
		return ATTRIBUTES.containsKey(id);
	}

	public static Collection<Attribute> all() {
		return Collections.unmodifiableCollection(ATTRIBUTES.values());
	}

	public static void init() {
		Pathed.LOGGER.info("Registered {} attributes", ATTRIBUTES.size());
	}
}
