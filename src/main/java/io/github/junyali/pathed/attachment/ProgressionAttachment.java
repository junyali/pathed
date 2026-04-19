package io.github.junyali.pathed.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.junyali.pathed.item.tool.ToolTier;
import io.github.junyali.pathed.registry.PathedAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProgressionAttachment {
	public static class ProgressionStats {
		private final Map<ResourceLocation, Integer> blocksBroken;
		private final Map<ResourceLocation, Integer> entitiesKilled;
		private final Map<ResourceLocation, Integer> damageDealt;
		private final Map<ResourceLocation, Integer> damageTaken;
		private final Map<ResourceLocation, Integer> itemsCrafted;
		private int distanceTravelled;
		private final Set<ResourceLocation> dimensionsVisited;
		private int deathCount;
		private int sleepCount;
		private final Map<ResourceLocation, Integer> tradingCount;

		public ProgressionStats() {
			this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), 0, new HashSet<>(), 0, 0, new HashMap<>());
		}

		private ProgressionStats(
				Map<ResourceLocation, Integer> blocksBroken,
				Map<ResourceLocation, Integer> entitiesKilled,
				Map<ResourceLocation, Integer> damageDealt,
				Map<ResourceLocation, Integer> damageTaken,
				Map<ResourceLocation, Integer> itemsCrafted,
				int distanceTravelled,
				Set<ResourceLocation> dimensionsVisited,
				int deathCount,
				int sleepCount,
				Map<ResourceLocation, Integer> tradingCount
		) {
			this.blocksBroken = new HashMap<>(blocksBroken);
			this.entitiesKilled = new HashMap<>(entitiesKilled);
			this.damageDealt = new HashMap<>(damageDealt);
			this.damageTaken = new HashMap<>(damageTaken);
			this.itemsCrafted = new HashMap<>(itemsCrafted);
			this.distanceTravelled = distanceTravelled;
			this.dimensionsVisited = new HashSet<>(dimensionsVisited);
			this.deathCount = deathCount;
			this.sleepCount = sleepCount;
			this.tradingCount = new HashMap<>(tradingCount);
		}

		public Map<ResourceLocation, Integer> getBlocksBroken() {
			return blocksBroken;
		}

		public Map<ResourceLocation, Integer> getEntitiesKilled() {
			return entitiesKilled;
		}

		public Map<ResourceLocation, Integer> getDamageDealt() {
			return damageDealt;
		}

		public Map<ResourceLocation, Integer> getDamageTaken() {
			return damageTaken;
		}

		public Map<ResourceLocation, Integer> getItemsCrafted() {
			return itemsCrafted;
		}

		public int getDistanceTravelled() {
			return distanceTravelled;
		}

		public Set<ResourceLocation> getDimensionsVisited() {
			return dimensionsVisited;
		}

		public int getDeathCount() {
			return deathCount;
		}

		public int getSleepCount() {
			return sleepCount;
		}

		public Map<ResourceLocation, Integer> getTradingCount() {
			return tradingCount;
		}

		public void addDistanceTravelled(int amount) {
			this.distanceTravelled += amount;
		}

		public void incrementDeathCount() {
			this.deathCount++;
		}

		public void incrementSleepCount() {
			this.sleepCount++;
		}

		static final Codec<ProgressionStats> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
						.fieldOf("blocksBroken").forGetter(ProgressionStats::getBlocksBroken),
				Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
						.fieldOf("entitiesKilled").forGetter(ProgressionStats::getEntitiesKilled),
				Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
						.fieldOf("damageDealt").forGetter(ProgressionStats::getDamageDealt),
				Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
						.fieldOf("damageTaken").forGetter(ProgressionStats::getDamageTaken),
				Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
						.fieldOf("itemsCrafted").forGetter(ProgressionStats::getItemsCrafted),
				Codec.INT.fieldOf("distanceTravelled").forGetter(ProgressionStats::getDistanceTravelled),
				ResourceLocation.CODEC.listOf().xmap(HashSet::new, list -> list.stream().toList())
						.fieldOf("dimensionsVisited").forGetter(s -> new HashSet<>(s.getDimensionsVisited())),
				Codec.INT.fieldOf("deathCount").forGetter(ProgressionStats::getDeathCount),
				Codec.INT.fieldOf("sleepCount").forGetter(ProgressionStats::getSleepCount),
				Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
						.fieldOf("tradingCount").forGetter(ProgressionStats::getTradingCount)
		).apply(i, ProgressionStats::new));
	}

	public static class UpgradeData {
		private ToolTier currentToolTier;
		private final Set<ResourceLocation> unlockedAttributes;

		public UpgradeData() {
			this(ToolTier.WOOD, new HashSet<>());
		}

		private UpgradeData(ToolTier currentToolTier, Set<ResourceLocation> unlockedAttributes) {
			this.currentToolTier = currentToolTier;
			this.unlockedAttributes = new HashSet<>(unlockedAttributes);
		}

		public ToolTier getCurrentToolTier() {
			return currentToolTier;
		}

		public void setToolTier(ToolTier tier) {
			this.currentToolTier = tier;
		}

		public Set<ResourceLocation> getUnlockedAttributes() {
			return unlockedAttributes;
		}

		public void unlockAttribute(ResourceLocation attribute) {
			unlockedAttributes.add(attribute);
		}

		public boolean hasAttribute(ResourceLocation attribute) {
			return unlockedAttributes.contains(attribute);
		}

		static final Codec<UpgradeData> CODEC = RecordCodecBuilder.create(i -> i.group(
				ToolTier.CODEC.optionalFieldOf("currentToolTier", ToolTier.WOOD)
						.forGetter(UpgradeData::getCurrentToolTier),
				ResourceLocation.CODEC.listOf().xmap(HashSet::new, list -> list.stream().toList())
						.optionalFieldOf("unlockedAttributes", new HashSet<>())
						.forGetter(d -> new HashSet<>(d.getUnlockedAttributes()))
		).apply(i, UpgradeData::new));
	}

	public static final Codec<ProgressionAttachment> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("classPoints").forGetter(ProgressionAttachment::getClassPoints),
			Codec.INT.fieldOf("generalPoints").forGetter(ProgressionAttachment::getGeneralPoints),
			Codec.INT.fieldOf("level").forGetter(ProgressionAttachment::getLevel),
			Codec.INT.fieldOf("experience").forGetter(ProgressionAttachment::getExperience),
			ProgressionStats.CODEC.fieldOf("progressionStats").forGetter(a -> a.progressionStats),
			UpgradeData.CODEC.optionalFieldOf("upgradeData", new UpgradeData()).forGetter(a -> a.upgradeData),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, list -> list.stream().toList())
					.fieldOf("completedNodes").forGetter(a -> new HashSet<>(a.completedNodes)),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, list -> list.stream().toList())
					.fieldOf("availableNodes").forGetter(a -> new HashSet<>(a.availableNodes))
	).apply(i, ProgressionAttachment::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ProgressionAttachment> STREAM_CODEC =
			ByteBufCodecs.fromCodecWithRegistries(CODEC);

	private int classPoints;
	private int generalPoints;
	private int level;
	private int experience;
	private final ProgressionStats progressionStats;
	private final UpgradeData upgradeData;
	private final Set<ResourceLocation> completedNodes;
	private final Set<ResourceLocation> availableNodes;

	public ProgressionAttachment() {
		this.classPoints = 0;
		this.generalPoints = 0;
		this.level = 1;
		this.experience = 0;
		this.progressionStats = new ProgressionStats();
		this.upgradeData = new UpgradeData();
		this.completedNodes = new HashSet<>();
		this.availableNodes = new HashSet<>();
	}

	private ProgressionAttachment(
			int classPoints,
			int generalPoints,
			int level,
			int experience,
			ProgressionStats progressionStats,
			UpgradeData upgradeData,
			Set<ResourceLocation> completedNodes,
			Set<ResourceLocation> availableNodes
	) {
		this.classPoints = classPoints;
		this.generalPoints = generalPoints;
		this.level = level;
		this.experience = experience;
		this.progressionStats = progressionStats;
		this.upgradeData = upgradeData;
		this.completedNodes = new HashSet<>(completedNodes);
		this.availableNodes = new HashSet<>(availableNodes);
	}

	public int getClassPoints() {
		return classPoints;
	}

	public int getGeneralPoints() {
		return generalPoints;
	}

	public int getLevel() {
		return level;
	}

	public int getExperience() {
		return experience;
	}

	public UpgradeData getUpgradeData() {
		return upgradeData;
	}

	public void addClassPoints(int amount) {
		this.classPoints += amount;
	}

	public void addGeneralPoints(int amount) {
		this.generalPoints += amount;
	}

	public void addLevel(int amount) {
		this.level += amount;
	}

	public void addExperience(int amount) {
		this.experience += amount;
		while (this.experience >= getExperienceRequiredForLevel(this.level + 1)) {
			this.level++;
		}
	}

	public boolean spendClassPoints(int amount) {
		if (classPoints < amount) return false;
		classPoints -= amount;
		return true;
	}

	public boolean spendGeneralPoints(int amount) {
		if (generalPoints < amount) return false;
		generalPoints -= amount;
		return true;
	}

	public static long getExperienceRequiredForLevel(int level) {
		return (long) (100 * Math.pow(level, 1.5));
	}

	public long getExperienceForCurrentLevel() {
		return getExperienceRequiredForLevel(this.level);
	}
	public long getExperienceForNextLevel() {
		return getExperienceRequiredForLevel(this.level + 1);
	}

	public float getLevelProgress() {
		long current = this.level;
		long floor = getExperienceForCurrentLevel();
		long ceiling = getExperienceForNextLevel();
		if (ceiling <= floor) return 0f;
		return (float) (this.experience - floor) / (float) (ceiling - floor);
	}

	public Map<ResourceLocation, Integer> getBlocksBroken() {
		return progressionStats.getBlocksBroken();
	}

	public Map<ResourceLocation, Integer> getEntitiesKilled() {
		return progressionStats.getEntitiesKilled();
	}

	public Map<ResourceLocation, Integer> getDamageDealt() {
		return progressionStats.getDamageDealt();
	}

	public Map<ResourceLocation, Integer> getDamageTaken() {
		return progressionStats.getDamageTaken();
	}

	public Map<ResourceLocation, Integer> getItemsCrafted() {
		return progressionStats.getItemsCrafted();
	}

	public int getDistanceTravelled() {
		return progressionStats.getDistanceTravelled();
	}

	public Set<ResourceLocation> getDimensionsVisited() {
		return progressionStats.getDimensionsVisited();
	}

	public int getDeathCount() {
		return progressionStats.getDeathCount();
	}

	public int getSleepCount() {
		return progressionStats.getSleepCount();
	}

	public Map<ResourceLocation, Integer> getTradingCount() {
		return progressionStats.getTradingCount();
	}

	public Set<ResourceLocation> getCompletedNodes() {
		return completedNodes;
	}

	public Set<ResourceLocation> getAvailableNodes() {
		return availableNodes;
	}

	public void incrementBlocksBroken(ResourceLocation block) {
		progressionStats.getBlocksBroken().merge(block, 1, Integer::sum);
	}

	public void incrementEntitiesKilled(ResourceLocation entity) {
		progressionStats.getEntitiesKilled().merge(entity, 1, Integer::sum);
	}

	public void addDamageDealt(ResourceLocation entityType, float amount) {
		progressionStats.getDamageDealt().merge(entityType, Math.round(amount * 10), Integer::sum);
	}

	public void addDamageTaken(ResourceLocation sourceType, float amount) {
		progressionStats.getDamageTaken().merge(sourceType, Math.round(amount * 10), Integer::sum);
	}

	public void incrementItemsCrafted(ResourceLocation item, int count) {
		progressionStats.getItemsCrafted().merge(item, count, Integer::sum);
	}

	public void addDistanceTravelled(int fixedPoint) {
		progressionStats.addDistanceTravelled(fixedPoint);
	}

	public void visitDimension(ResourceLocation dimension) {
		progressionStats.getDimensionsVisited().add(dimension);
	}

	public void incrementDeathCount() {
		progressionStats.incrementDeathCount();
	}

	public void incrementSleepCount() {
		progressionStats.incrementSleepCount();
	}

	public void incrementTradingCount(ResourceLocation profession) {
		progressionStats.getTradingCount().merge(profession, 1, Integer::sum);
	}

	public void addCompletedNode(ResourceLocation node) {
		completedNodes.add(node);
	}

	public void addAvailableNode(ResourceLocation node) {
		availableNodes.add(node);
	}

	public int getBlocksBrokenCount(ResourceLocation block) {
		return progressionStats.getBlocksBroken().getOrDefault(block, 0);
	}

	public int getEntitiesKilledCount(ResourceLocation entity) {
		return progressionStats.getEntitiesKilled().getOrDefault(entity, 0);
	}

	public float getDamageDealtTo(ResourceLocation entityType) {
		return progressionStats.getDamageDealt().getOrDefault(entityType, 0) / 10f;
	}

	public float getDamageTakenFrom(ResourceLocation sourceType) {
		return progressionStats.getDamageTaken().getOrDefault(sourceType, 0) / 10f;
	}

	public int getItemsCraftedCount(ResourceLocation item) {
		return progressionStats.getItemsCrafted().getOrDefault(item, 0);
	}

	public float getDistanceTravelledBlocks() {
		return progressionStats.getDistanceTravelled() / 100f;
	}

	public boolean hasVisitedDimension(ResourceLocation dimension) {
		return progressionStats.getDimensionsVisited().contains(dimension);
	}

	public int getTradingCountFor(ResourceLocation profession) {
		return progressionStats.getTradingCount().getOrDefault(profession, 0);
	}

	public static ProgressionAttachment get(Entity entity) {
		return entity.getData(PathedAttachments.PROGRESSION_ATTACHMENT);
	}

	public void sync(Entity entity) {
		if (entity instanceof ServerPlayer serverPlayer) {
			serverPlayer.syncData(PathedAttachments.PROGRESSION_ATTACHMENT);
		}
	}
}
