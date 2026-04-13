package io.github.junyali.pathed.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathRegistry;
import io.github.junyali.pathed.registry.PathedAttachments;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PathAttachment {
	public static final Codec<PathAttachment> CODEC = RecordCodecBuilder.create(i -> i.group(
			ResourceLocation.CODEC.optionalFieldOf("path").forGetter(a -> Optional.ofNullable(a.path).map(Path::getId)),
			Codec.BOOL.fieldOf("hasChosen").forGetter(PathAttachment::hasChosen)
	).apply(i, (optPathId, hasChosen) -> {
		Path resolved = optPathId.map(PathRegistry::get).orElse(null);
		PathAttachment pathAttachment = new PathAttachment();
		pathAttachment.path = resolved;
		pathAttachment.hasChosen = resolved != null && hasChosen;
		return pathAttachment;
	}));

	public static final StreamCodec<RegistryFriendlyByteBuf, PathAttachment> STREAM_CODEC =
			ByteBufCodecs.fromCodecWithRegistries(CODEC);

	@Nullable
	private Path path;
	private boolean hasChosen;

	public PathAttachment() {
		this.path = null;
		this.hasChosen = false;
	}

	private PathAttachment(@Nullable ResourceLocation pathId, boolean hasChosen) {
		Path resolved = pathId != null ? PathRegistry.get(pathId) : null;
		this.path = resolved;
		this.hasChosen = resolved != null && hasChosen;
	}

	@Nullable
	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
		this.hasChosen = true;
	}

	public boolean hasChosen() {
		return hasChosen;
	}

	public static PathAttachment get(Entity entity) {
		return entity.getData(PathedAttachments.PATH_ATTACHMENT);
	}

	public void sync(Entity entity) {
		if (entity instanceof ServerPlayer serverPlayer) {
			serverPlayer.syncData(PathedAttachments.PATH_ATTACHMENT);
		}
	}
}
