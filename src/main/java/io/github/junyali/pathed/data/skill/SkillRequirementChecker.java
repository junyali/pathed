package io.github.junyali.pathed.data.skill;

import io.github.junyali.pathed.attachment.ProgressionAttachment;

public final class SkillRequirementChecker {
	private SkillRequirementChecker() {}

	public static boolean isMet(ProgressionAttachment p, SkillNodeRequirement req) {
		return switch (req) {
			case SkillNodeRequirement.StatRequirement s     -> SkillStatLookup.getValue(p, s.stat(), s.target()) >= s.count();
			case SkillNodeRequirement.PointRequirement pr   -> (pr.classPoints() ? p.getClassPoints() : p.getGeneralPoints()) >= pr.amount();
			case SkillNodeRequirement.NodeRequirement nr    -> p.getCompletedNodes().contains(nr.nodeId());
		};
	}
}
