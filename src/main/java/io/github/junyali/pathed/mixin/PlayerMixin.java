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
}
