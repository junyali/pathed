package io.github.junyali.pathed.data.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SkillCategory {
	private final String nameKey;
	private final String iconItem;
	private final ResourceLocation pathLocked;
	private final List<SkillNode> nodes = new ArrayList<>();
	private final ResourceLocation background;

	public SkillCategory(String nameKey, String iconItem, ResourceLocation pathLocked, ResourceLocation background) {
		this.nameKey = nameKey;
		this.iconItem = iconItem;
		this.pathLocked = pathLocked;
		this.background = background;
	}

	public static final Codec<SkillCategory> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.STRING.fieldOf("name").forGetter(SkillCategory::getNameKey),
			Codec.STRING.fieldOf("icon_item").forGetter(SkillCategory::getIconItem),
			ResourceLocation.CODEC.optionalFieldOf("path_locked").forGetter(c -> Optional.ofNullable(c.pathLocked)),
			ResourceLocation.CODEC.optionalFieldOf("background", ResourceLocation.withDefaultNamespace("textures/block/dirt.png")).forGetter(SkillCategory::getBackground)
	).apply(i, (name, icon, optPath, bg) -> new SkillCategory(name, icon, optPath.orElse(null), bg)));

	public void addNode(SkillNode node) {
		nodes.add(node);
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

	public ResourceLocation getBackground() {
		return background;
	}
}
