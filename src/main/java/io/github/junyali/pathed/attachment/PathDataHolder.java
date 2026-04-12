package io.github.junyali.pathed.attachment;

import io.github.junyali.pathed.data.path.Path;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record PathDataHolder(Entity entity, PathAttachment data) {
	@Nullable
	public Path getPath() {
		return data.getPath();
	}

	public void setPath(Path path) {
		data.setPath(path);
		sync();
	}

	public boolean hasChosen() {
		return data.hasChosen();
	}

	public void sync() {
		data.sync(entity);
	}

	public static PathDataHolder get(Entity entity) {
		return new PathDataHolder(entity, PathAttachment.get(entity));
	}
}
