package io.github.junyali.pathed.attachment;

import io.github.junyali.pathed.classsystem.PathedClass;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class PathedAttachment {
	private PathedClass pathedClass;
	private boolean hasChosen;

	public PathedAttachment() {
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

	public static class Serializer implements IAttachmentSerializer<CompoundTag, PathedAttachment> {
		@Override
		public PathedAttachment read (IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
			PathedAttachment attachment = new PathedAttachment();
			attachment.pathedClass = PathedClass.valueOf(String.valueOf(tag.getString("class")));
			attachment.hasChosen = tag.getBoolean("hasChosen");
			return attachment;
		}

		@Override
		public CompoundTag write(PathedAttachment attachment, HolderLookup.Provider provider) {
			CompoundTag tag = new CompoundTag();
			tag.putString("class", attachment.pathedClass.name());
			tag.putBoolean("hasChosen", attachment.hasChosen);
			return tag;
		}
	}
}
