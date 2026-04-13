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
import java.util.Map;

public class ProgressionAttachment {
	public static final Codec<ProgressionAttachment> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("classPoints").forGetter(ProgressionAttachment::getClassPoints),
			Codec.INT.fieldOf("generalPoints").forGetter(ProgressionAttachment::getGeneralPoints),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
					.fieldOf("blocksBroken").forGetter(ProgressionAttachment::getBlocksBroken),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
					.fieldOf("entitiesKilled").forGetter(ProgressionAttachment::getEntitiesKilled)
	).apply(i, ProgressionAttachment::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ProgressionAttachment> STREAM_CODEC =
			ByteBufCodecs.fromCodecWithRegistries(CODEC);

	private int classPoints;
	private int generalPoints;
	private final Map<ResourceLocation, Integer> blocksBroken;
	private final Map<ResourceLocation, Integer> entitiesKilled;

	public ProgressionAttachment() {
		this.classPoints = 0;
		this.generalPoints = 0;
		this.blocksBroken = new HashMap<>();
		this.entitiesKilled = new HashMap<>();
	}

	private ProgressionAttachment(int classPoints, int generalPoints, Map<ResourceLocation, Integer> blocksBroken, Map<ResourceLocation, Integer>entitiesKilled) {
		this.classPoints = classPoints;
		this.generalPoints = generalPoints;
		this.blocksBroken = new HashMap<>(blocksBroken);
		this.entitiesKilled = new HashMap<>(entitiesKilled);
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

	public void incrementBlocksBroken(ResourceLocation block) {
		blocksBroken.merge(block, 1, Integer::sum);
	}

	public void incrementEntitiesKilled(ResourceLocation entity) {
		entitiesKilled.merge(entity, 1, Integer::sum);
	}

	public int getBlocksBrokenCount(ResourceLocation block) {
		return blocksBroken.getOrDefault(block, 0);
	}

	public int getEntitiesKilledCount(ResourceLocation entity) {
		return entitiesKilled.getOrDefault(entity, 0);
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
