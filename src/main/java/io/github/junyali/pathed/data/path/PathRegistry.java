package io.github.junyali.pathed.data.path;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.data.paths.*;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class PathRegistry {
	private static final Map<ResourceLocation, Path> PATHS = new LinkedHashMap<>();

	public static final Path HUMAN = register(new HumanPath());
	public static final Path BLADEMASTER = register(new BlademasterPath());
	public static final Path SPELUNKER = register(new SpelunkerPath());
	public static final Path LUMBERJACK = register(new LumberjackPath());
	public static final Path EXCAVATOR = register(new ExcavatorPath());
	public static final Path CULTIVATOR = register(new CultivatorPath());

	private PathRegistry() {}

	public static Path register(Path path) {
		PATHS.put(path.getId(), path);
		return path;
	}

	public static Path get(ResourceLocation id) {
		return PATHS.get(id);
	}

	public static Path get(String id) {
		return get(ResourceLocation.parse(id));
	}

	public static Collection<Path> all() {
		return Collections.unmodifiableCollection(PATHS.values());
	}

	public static List<Path> selectable() {
		return PATHS.values().stream().filter(Path::isSelectable).sorted(Comparator.comparingInt(Path::getOrder)).toList();
	}

	public static void init() {
		Pathed.LOGGER.info("Registered {} paths", PATHS.size());
	}
}
