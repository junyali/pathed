package io.github.junyali.pathed.mixin;

import io.github.junyali.pathed.attachment.ProgressionAttachment;
import io.github.junyali.pathed.item.tool.IPathTool;
import io.github.junyali.pathed.item.tool.ToolTier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
	@Inject(
			method = "getDestroySpeed",
			at = @At("HEAD"),
			cancellable = true
	)
	private void pathed_getDestroySpeed(BlockState blockState, CallbackInfoReturnable<Float> cir) {
		Player self = (Player) (Object) this;
		ItemStack held = self.getMainHandItem();
		if (!(held.getItem() instanceof IPathTool)) return;

		ToolTier tier = ProgressionAttachment.get(self).getUpgradeData().getCurrentToolTier();
		cir.setReturnValue(tier.getSpeed());
	}

	@Inject(
			method = "hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void pathed_hasCorrectToolForDrops(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
		Player self = (Player) (Object) this;
		ItemStack held = self.getMainHandItem();
		if (!(held.getItem() instanceof IPathTool)) return;
		if (blockState.getBlock().defaultDestroyTime() < 0f) return;

		ToolTier tier = ProgressionAttachment.get(self).getUpgradeData().getCurrentToolTier();
		if (blockState.is(tier.getIncorrectTag())) {
			cir.setReturnValue(false);
		} else {
			cir.setReturnValue(true);
		}
		cir.cancel();
	}
}
