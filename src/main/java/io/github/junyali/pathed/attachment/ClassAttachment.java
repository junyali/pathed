package io.github.junyali.pathed.attachment;

import io.github.junyali.pathed.classsystem.PathedClass;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

public class ClassAttachment {
	private PathedClass pathedClass;
	private boolean hasChosen;

	public ClassAttachment() {
		this.pathedClass = PathedClass.NONE;
		this.hasChosen = false;
	}

	public PathedClass getPathedClass() {
		return pathedClass;
	}

	public void setPathedClass(PathedClass pathedClass) {
		this.pathedClass = pathedClass;
		this.hasChosen = true;
	}

	public boolean hasChosen() {
		return hasChosen;
	}

	public static class Serializer implements IAttachmentSerializer<CompoundTag, ClassAttachment> {
		@Override
		public @NotNull ClassAttachment read(@NotNull IAttachmentHolder iAttachmentHolder, CompoundTag tag, HolderLookup.@NotNull Provider provider) {
			ClassAttachment attachment = new ClassAttachment();
			attachment.pathedClass = PathedClass.valueOf(tag.getString("class"));
			attachment.hasChosen = tag.getBoolean("hasChosen");
			return attachment;
		}

		@Override
		public CompoundTag write(ClassAttachment attachment, HolderLookup.@NotNull Provider provider) {
			CompoundTag tag = new CompoundTag();
			tag.putString("class", attachment.pathedClass.name());
			tag.putBoolean("hasChosen", attachment.hasChosen);
			return tag;
		}
	}
}
