package org.ladysnake.effective.core.mixin.water;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.world.Waterfall;
import org.ladysnake.effective.core.world.WaterfallCloudGenerators;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

@Mixin(BlockBehaviour.class)
public class WaterfallGeneratorTicker {
	@Inject(method = "updateShape", at = @At("HEAD"))
	protected void effective$forceParticles(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (neighborState.getBlock() == Blocks.LAPIS_BLOCK && state.getBlock() == Blocks.WATER) {
			float chance = EffectiveConfig.lapisBlockUpdateParticleChance;
			if (chance > 0f) {
				gatherWater(new HashSet<>(), world, new BlockPos.MutableBlockPos().set(pos)).forEach(waterPos -> {
					if (world.getRandom().nextFloat() * 10f < chance) {
						WaterfallCloudGenerators.scheduleParticleTick(new Waterfall(waterPos, state.getFluidState().getOwnHeight(), true, new Color(0xFFFFFF)), 1);
					}
				});
			}
		}
	}

	@Unique
	private Set<BlockPos> gatherWater(Set<BlockPos> flowingWater, LevelAccessor world, BlockPos.MutableBlockPos pos) {
		if (flowingWater.size() < 1024) {
			int originalX = pos.getX(), originalY = pos.getY(), originalZ = pos.getZ();
			for (Direction direction : Direction.values()) {
				FluidState state = world.getFluidState(pos.set(originalX + direction.getStepX(), originalY + direction.getStepY(), originalZ + direction.getStepZ()));
				if (!flowingWater.contains(pos) && state.getType() == Fluids.FLOWING_WATER) {
					flowingWater.add(pos.mutable());
					gatherWater(flowingWater, world, pos);
				}
			}
		}
		return flowingWater;
	}
}
