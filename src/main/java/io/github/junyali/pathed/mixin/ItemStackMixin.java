package io.github.junyali.pathed.mixin;

import io.github.junyali.pathed.item.tool.IPathTool;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(
			method = "isCorrectToolForDrops",
			at = @At("HEAD"),
			cancellable = true
	)
	private void pathed_isCorrectToolForDrops(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
		ItemStack self = (ItemStack) (Object) this;
		if (!(self.getItem() instanceof IPathTool)) return;
		if (blockState.getBlock().defaultDestroyTime() < 0f) return;
		cir.setReturnValue(true);
		cir.cancel();
	}
}
