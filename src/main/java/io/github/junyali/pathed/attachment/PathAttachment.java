package io.github.junyali.pathed.attachment;

import io.github.junyali.pathed.data.path.Path;
import io.github.junyali.pathed.data.path.PathRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PathAttachment {
	@Nullable
	private Path path;
	private boolean hasChosen;

	public PathAttachment() {
		this.path = null;
		this.hasChosen = false;
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

	public static class Serializer implements IAttachmentSerializer<CompoundTag, PathAttachment> {
		@Override
		public @NotNull PathAttachment read(@NotNull IAttachmentHolder iAttachmentHolder, CompoundTag tag, HolderLookup.@NotNull Provider provider) {
			PathAttachment attachment = new PathAttachment();
			attachment.hasChosen = tag.getBoolean("hasChosen");
			if (tag.contains("path")) {
				String pathId = tag.getString("path");
				attachment.path = PathRegistry.get(ResourceLocation.parse(pathId));
			}
			return attachment;
		}

		@Override
		public CompoundTag write(PathAttachment attachment, HolderLookup.@NotNull Provider provider) {
			CompoundTag tag = new CompoundTag();
			tag.putBoolean("hasChosen", attachment.hasChosen);
			if (attachment.path != null) {
				tag.putString("path", attachment.path.getId().toString());
			}
			return tag;
		}
	}
}
