package io.github.junyali.pathed.attachment;

import io.github.junyali.pathed.Pathed;
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
							.serialize(new PathAttachment.Serializer())
							.build()
			);

	public static void register(IEventBus eventBus) {
		ATTACHMENT_TYPES.register(eventBus);
	}
}
