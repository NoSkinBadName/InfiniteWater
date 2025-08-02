package io.github.noskinbadname.infinitewater.mixin;

import io.github.noskinbadname.infinitewater.DelayedAction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BucketItem.class)
public class BucketItemMixin {
    @Redirect(method = "placeFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    public boolean modifiedSetBlockState(World instance, BlockPos pos, BlockState state, int flags) {
        boolean returnValue = instance.setBlockState(pos, state, flags);
        if (state.isOf(Blocks.WATER)) fillFluid(instance, pos, state, Block.NO_REDRAW);
        return returnValue;
    }

    @Unique
    public void fillFluid(World instance, BlockPos pos, BlockState state, int flags) {
        if (!instance.getBlockState(pos).isOf(Blocks.WATER)) instance.setBlockState(pos, state, flags);
        BlockPos[] blockPositions = {pos.down(), pos.north(), pos.east(), pos.south(), pos.west()};
        new DelayedAction(4, () -> {
            for (BlockPos currentPos : blockPositions) {
                BlockState currentBlockState = instance.getBlockState(currentPos);
                FluidState fluidState = currentBlockState.getFluidState();
                if (!currentBlockState.isAir() && !(fluidState.isOf(Fluids.FLOWING_WATER) && pos.down().equals(currentPos))) continue;
                fillFluid(instance, currentPos, state, flags);
            }
        });
    }
}