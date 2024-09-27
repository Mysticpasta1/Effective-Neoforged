package org.ladysnake.effective.core.mixin.choruspetals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.mixin.RandomDisplayTickBlockMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChorusFlowerBlock.class)
public abstract class ChorusPetalSpawner extends RandomDisplayTickBlockMixin {
	@Override
	protected void effective$randomDisplayTick(BlockState state, Level world, BlockPos pos, RandomSource random, CallbackInfo ci) {
		for (int i = 0; i < (6 - state.getValue(ChorusFlowerBlock.AGE)) * EffectiveConfig.chorusPetalDensity; i++) {
			world.addParticle(Effective.CHORUS_PETAL, true, pos.getX() + 0.5 + random.nextGaussian() * 5, pos.getY() + 0.5 + random.nextGaussian() * 5, pos.getZ() + 0.5 + random.nextGaussian() * 5, 0f, 0f, 0f);
		}
	}
}
