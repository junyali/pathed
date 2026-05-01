package io.github.junyali.pathed.data.skill;

import io.github.junyali.pathed.attachment.PathAttachment;
import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.data.path.Path;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class SkillNodeEvaluator {
	private SkillNodeEvaluator() {}

	public static boolean isAvailable(ServerPlayer player, SkillNode node) {
		ProgressionAttachment p = ProgressionAttachment.get(player);

		if (node.pathLocked().isPresent()) {
			Path path = PathAttachment.get(player).getPath();
			ResourceLocation pathId = path == null ? null : path.getId();
			if (!node.pathLocked().get().equals(pathId)) return false;
		}

		for (ResourceLocation prereq : node.prerequisites()) {
			if (!p.getCompletedNodes().contains(prereq)) return false;
		}

		for (ResourceLocation prev : node.previousNodes()) {
			if (!p.getCompletedNodes().contains(prev)) return false;
		}

		return true;
	}

	public static boolean meetsRequirements(ServerPlayer player, SkillNode node) {
		ProgressionAttachment p = ProgressionAttachment.get(player);
		for (SkillNodeRequirement req : node.requirements()) {
			if (!SkillRequirementChecker.isMet(p, req)) return false;
		}

		return true;
	}

	public static boolean isCompleted(ServerPlayer player, SkillNode node) {
		return ProgressionAttachment.get(player).getCompletedNodes().contains(node.id());
	}

	public static boolean tryComplete(ServerPlayer player, SkillNode node) {
		if (isCompleted(player, node)) return false;
		if (!isAvailable(player, node)) return false;
		if (!meetsRequirements(player, node)) return false;

		ProgressionAttachment p = ProgressionAttachment.get(player);

		for (SkillNodeRequirement req : node.requirements()) {
			if (req instanceof SkillNodeRequirement.PointRequirement pr && pr.consumed()) {
				boolean ok = pr.classPoints()
						? p.spendClassPoints(pr.amount())
						: p.spendGeneralPoints(pr.amount());
				if (!ok) return false;
			}
		}

		p.addCompletedNode(node.id());
		// TODO: grant rewards here
		p.sync(player);
		return true;
	}

	public static void evaluateAll(ServerPlayer player) {
		ProgressionAttachment p = ProgressionAttachment.get(player);
		boolean changed = false;

		for (SkillNode node : SkillNodeLoader.getNodes().values()) {
			if (p.getCompletedNodes().contains(node.id())) continue;
			if (isAvailable(player, node) && !p.getAvailableNodes().contains(node.id())) {
				p.addAvailableNode(node.id());
				changed = true;
			}

			if (hasNoConsumedReqs(node) && isAvailable(player, node) && meetsRequirements(player, node)) {
				p.addCompletedNode(node.id());
				changed = true;
			}
		}
	}

	private static boolean hasNoConsumedReqs(SkillNode node) {
		for (SkillNodeRequirement req : node.requirements()) {
			if (req instanceof SkillNodeRequirement.StatRequirement s && s.consumed()) return false;
			if (req instanceof SkillNodeRequirement.PointRequirement p && p.consumed()) return false;
		}
		return true;
	}
}
