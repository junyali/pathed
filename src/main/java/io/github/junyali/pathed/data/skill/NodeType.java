package io.github.junyali.pathed.data.skill;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum NodeType implements StringRepresentable {
	SKILL("pathed:skill"),
	PROGRESSION("pathed:progression");

	public static final Codec<NodeType> CODEC =
			StringRepresentable.fromValues(NodeType::values);

	private final String id;
	NodeType(String id) {
		this.id = id;
	}

	@Override
	@NotNull
	public String getSerializedName() {
		return id;
	}
}
