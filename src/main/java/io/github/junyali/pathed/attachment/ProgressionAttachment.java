package io.github.junyali.pathed.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
	public static final Codec<ProgressionAttachment> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("classPoints").forGetter(ProgressionAttachment::getClassPoints),
			Codec.INT.fieldOf("generalPoints").forGetter(ProgressionAttachment::getGeneralPoints),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
					.fieldOf("blocksBroken").forGetter(ProgressionAttachment::getBlocksBroken),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
					.fieldOf("entitiesKilled").forGetter(ProgressionAttachment::getEntitiesKilled),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
					.fieldOf("damageDealt").forGetter(ProgressionAttachment::getDamageDealt),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
					.fieldOf("damageTaken").forGetter(ProgressionAttachment::getDamageTaken),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
					.fieldOf("itemsCrafted").forGetter(ProgressionAttachment::getItemsCrafted),
			Codec.INT.fieldOf("distanceTravelled").forGetter(ProgressionAttachment::getDistanceTravelled),
			ResourceLocation.CODEC.listOf().xmap(HashSet::new, list -> list.stream().toList())
					.fieldOf("dimensionsVisited").forGetter(a -> new HashSet<>(a.getDimensionsVisited())),
			Codec.INT.fieldOf("deathCount").forGetter(ProgressionAttachment::getDeathCount),
			Codec.INT.fieldOf("sleepCount").forGetter(ProgressionAttachment::getSleepCount),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
					.fieldOf("tradingCount").forGetter(ProgressionAttachment::getTradingCount)
	).apply(i, ProgressionAttachment::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ProgressionAttachment> STREAM_CODEC =
			ByteBufCodecs.fromCodecWithRegistries(CODEC);

	private int classPoints;
	private int generalPoints;
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

	public ProgressionAttachment() {
		this.classPoints = 0;
		this.generalPoints = 0;
		this.blocksBroken = new HashMap<>();
		this.entitiesKilled = new HashMap<>();
		this.damageDealt = new HashMap<>();
		this.damageTaken = new HashMap<>();
		this.itemsCrafted = new HashMap<>();
		this.distanceTravelled = 0;
		this.dimensionsVisited = new HashSet<>();
		this.deathCount = 0;
		this.sleepCount = 0;
		this.tradingCount = new HashMap<>();
	}

	private ProgressionAttachment(
			int classPoints,
			int generalPoints,
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
		this.classPoints = classPoints;
		this.generalPoints = generalPoints;
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

	public int getClassPoints() {
		return classPoints;
	}

	public int getGeneralPoints() {
		return generalPoints;
	}

	public void addClassPoints(int amount) {
		this.classPoints += amount;
	}

	public void addGeneralPoints(int amount) {
		this.generalPoints += amount;
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

	public void incrementBlocksBroken(ResourceLocation block) {
		blocksBroken.merge(block, 1, Integer::sum);
	}

	public void incrementEntitiesKilled(ResourceLocation entity) {
		entitiesKilled.merge(entity, 1, Integer::sum);
	}

	public void addDamageDealt(ResourceLocation entityType, float amount) {
		damageDealt.merge(entityType, Math.round(amount * 10), Integer::sum);
	}

	public void addDamageTaken(ResourceLocation sourceType, float amount) {
		damageTaken.merge(sourceType, Math.round(amount * 10), Integer::sum);
	}

	public void incrementItemsCrafted(ResourceLocation item, int count) {
		itemsCrafted.merge(item, count, Integer::sum);
	}

	public void addDistanceTravelled(int fixedPoint) {
		distanceTravelled += fixedPoint;
	}

	public void visitDimension(ResourceLocation dimension) {
		dimensionsVisited.add(dimension);
	}

	public void incrementDeathCount() {
		deathCount++;
	}

	public void incrementSleepCount() {
		sleepCount++;
	}

	public void incrementTradingCount(ResourceLocation profession) {
		tradingCount.merge(profession, 1, Integer::sum);
	}

	public int getBlocksBrokenCount(ResourceLocation block) {
		return blocksBroken.getOrDefault(block, 0);
	}

	public int getEntitiesKilledCount(ResourceLocation entity) {
		return entitiesKilled.getOrDefault(entity, 0);
	}

	public float getDamageDealtTo(ResourceLocation entityType) {
		return damageDealt.getOrDefault(entityType, 0) / 10f;
	}

	public float getDamageTakenFrom(ResourceLocation sourceType) {
		return damageTaken.getOrDefault(sourceType, 0) / 10f;
	}

	public int getItemsCraftedCount(ResourceLocation item) {
		return itemsCrafted.getOrDefault(item, 0);
	}

	public float getDistanceTravelledBlocks() {
		return distanceTravelled / 100f;
	}

	public boolean hasVisitedDimension(ResourceLocation dimension) {
		return dimensionsVisited.contains(dimension);
	}

	public int getTradingCountFor(ResourceLocation profession) {
		return tradingCount.getOrDefault(profession, 0);
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
