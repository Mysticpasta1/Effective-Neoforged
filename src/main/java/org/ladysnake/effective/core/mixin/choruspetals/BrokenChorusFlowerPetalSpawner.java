package org.ladysnake.effective.core.mixin.choruspetals;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ParticleEngine.class)
public abstract class BrokenChorusFlowerPetalSpawner {
	@Shadow
	protected ClientLevel level;
	@Shadow
	@Final
	private RandomSource random;

	@Shadow
	@Nullable
	protected abstract <T extends ParticleOptions> Particle makeParticle(T arg, double d, double e, double f, double g, double h, double i);

	@Inject(method = "destroy", at = @At(value = "RETURN"))
	public void addBlockBreakParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
		if (state.getBlock() == Blocks.CHORUS_FLOWER) {
			for (int i = 0; i < (6 - state.getValue(ChorusFlowerBlock.AGE)) * (EffectiveConfig.chorusPetalDensity * 10f); i++) {
				this.makeParticle(Effective.CHORUS_PETAL, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, random.nextGaussian() / 10f, random.nextGaussian() / 10f, random.nextGaussian() / 10f);
			}
		}
	}
}
