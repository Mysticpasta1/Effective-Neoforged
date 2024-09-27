package org.ladysnake.effective.core.mixin.chest_bubbles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(EnderChestBlockEntity.class)
public class UnderwaterEnderChestRandomOpener {
	private static final HashMap<BlockPos, Integer> CHESTS_TIMERS = new HashMap<>();

	@Inject(method = "lidAnimateTick", at = @At("TAIL"))
	private static void clientTick(Level world, BlockPos pos, BlockState state, EnderChestBlockEntity blockEntity, CallbackInfo ci) {
		if ((EffectiveConfig.underwaterChestsOpenRandomly == EffectiveConfig.ChestsOpenOptions.RANDOMLY
			|| (EffectiveConfig.underwaterChestsOpenRandomly == EffectiveConfig.ChestsOpenOptions.ON_SOUL_SAND && world.getBlockState(pos.relative(Direction.DOWN, 1)).is(Blocks.SOUL_SAND)))
			&& world.isWaterAt(pos) && world.isWaterAt(pos.relative(Direction.UP, 1))) {

			// tick down chest timers
			if (CHESTS_TIMERS.containsKey(pos)) {
				if (CHESTS_TIMERS.get(pos) > 0 && blockEntity.openersCounter.getOpenerCount() <= 0) {
					CHESTS_TIMERS.put(pos, CHESTS_TIMERS.get(pos) - 1);
				} else {
					world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENDER_CHEST_CLOSE, SoundSource.AMBIENT, 0.1f, 1.0f, false);
					blockEntity.chestLidController.shouldBeOpen(false);
					CHESTS_TIMERS.remove(pos);
				}
			}

			// randomly open chests
			if (world.random.nextInt(200) == 0
				&& blockEntity.openersCounter.getOpenerCount() <= 0
				&& !CHESTS_TIMERS.containsKey(pos)) {
				CHESTS_TIMERS.put(pos, 100);
				world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.AMBIENT, 0.1f, 1.0f, false);
				blockEntity.chestLidController.shouldBeOpen(true);
			}
		} else {
			// remove chests if config option not enabled
			if (CHESTS_TIMERS.containsKey(pos)) {
				blockEntity.chestLidController.shouldBeOpen(false);
				CHESTS_TIMERS.remove(pos);
			}
		}
	}

}
