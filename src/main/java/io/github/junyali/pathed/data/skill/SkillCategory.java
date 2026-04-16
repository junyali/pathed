package io.github.junyali.pathed.data.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SkillCategory {
	private final ResourceLocation id;
	private final String nameKey;
	private final String iconItem;
	private final ResourceLocation pathLocked;
	private final List<SkillNode> nodes = new ArrayList<>();

	public SkillCategory(ResourceLocation id, String nameKey, String iconItem, ResourceLocation pathLocked) {
		this.id = id;
		this.nameKey = nameKey;
		this.iconItem = iconItem;
		this.pathLocked = pathLocked;
	}

	public static final Codec<SkillCategory> CODEC = RecordCodecBuilder.create(i -> i.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(SkillCategory::getId),
			Codec.STRING.fieldOf("name").forGetter(SkillCategory::getNameKey),
			Codec.STRING.fieldOf("icon_item").forGetter(SkillCategory::getIconItem),
			ResourceLocation.CODEC.optionalFieldOf("path_locked").forGetter(c -> Optional.ofNullable(c.pathLocked))
	).apply(i, (id, name, icon, optPath) -> new SkillCategory(id, name, icon, optPath.orElse(null))));

	void addNode(SkillNode node) {
		nodes.add(node);
	}

	public ResourceLocation getId() {
		return id;
	}

	public String getNameKey() {
		return nameKey;
	}

	public String getIconItem() {
		return iconItem;
	}

	public Optional<ResourceLocation> getPathLocked() {
		return Optional.ofNullable(pathLocked);
	}

	public List<SkillNode> getNodes() {
		return Collections.unmodifiableList(nodes);
	}
}
