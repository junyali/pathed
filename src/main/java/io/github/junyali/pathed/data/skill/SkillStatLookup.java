package io.github.junyali.pathed.data.skill;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class SkillStatLookup {
	private SkillStatLookup() {}

	public static int getValue(ProgressionAttachment p, String stat, Optional<ResourceLocation> target) {
		return switch (stat) {
			case "blocks_broken"        -> target.map(p::getBlocksBrokenCount).orElseGet(() -> sum(p.getBlocksBroken().values()));
			case "entities_killed"      -> target.map(p::getEntitiesKilledCount).orElseGet(() -> sum(p.getEntitiesKilled().values()));
			case "damage_dealt"         -> target.map(t -> Math.round(p.getDamageDealtTo(t))).orElseGet(() -> sumFloat(p.getDamageDealt().values()) / 10);
			case "damage_taken"         -> target.map(t -> Math.round(p.getDamageTakenFrom(t))).orElseGet(() -> sumFloat(p.getDamageTaken().values()) / 10);
			case "items_crafted"        -> target.map(p::getItemsCraftedCount).orElseGet(() -> sum(p.getItemsCrafted().values()));
			case "distance_travelled"   -> (int) p.getDistanceTravelledBlocks();
			case "dimensions_visited"   -> target.map(t -> p.hasVisitedDimension(t) ? 1 : 0).orElse(p.getDimensionsVisited().size());
			case "death_count"          -> p.getDeathCount();
			case "sleep_count"          -> p.getSleepCount();
			case "trading_count"        -> target.map(p::getTradingCountFor).orElseGet(() -> sum(p.getTradingCount().values()));
			case "level"                -> p.getLevel();
			case "experience"           -> p.getExperience();
			default                     -> 0;
		};
	}

	private static int sum(Iterable<Integer> values) {
		int total = 0;
		for (int v : values) total += v;
		return total;
	}

	private static int sumFloat(Iterable<Integer> values) {
		int total = 0;
		for (int v : values) total += v;
		return total;
	}
}
