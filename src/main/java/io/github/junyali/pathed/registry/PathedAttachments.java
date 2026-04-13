package io.github.junyali.pathed.registry;

import io.github.junyali.pathed.Pathed;
import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PathedAttachments {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Pathed.MODID);

	public static final Supplier<AttachmentType<PathAttachment>> PATH_ATTACHMENT =
			ATTACHMENT_TYPES.register("path_data", () ->
					AttachmentType.builder(PathAttachment::new)
							.serialize(PathAttachment.CODEC)
							.copyOnDeath()
							.build()
			);

	public static final Supplier<AttachmentType<ProgressionAttachment>> PROGRESSION_ATTACHMENT =
			ATTACHMENT_TYPES.register("progression_data", () ->
					AttachmentType.builder(ProgressionAttachment::new)
							.serialize(ProgressionAttachment.CODEC)
							.copyOnDeath()
							.build()
			);

	public static void register(IEventBus eventBus) {
		ATTACHMENT_TYPES.register(eventBus);
	}
}
