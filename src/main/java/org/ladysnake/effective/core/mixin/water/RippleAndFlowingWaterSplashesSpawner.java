package org.ladysnake.effective.core.mixin.water;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.utils.EffectiveUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterFluid.class)
public class RippleAndFlowingWaterSplashesSpawner {
	@Unique
	private static boolean shouldSplash(Level world, BlockPos pos) {
		if (EffectiveConfig.flowingWaterSplashingDensity > 0) {
			FluidState fluidState = world.getFluidState(pos);
			if (!fluidState.isSource() & fluidState.getOwnHeight() >= 0.77) {
				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
				for (Direction direction : Direction.values()) {
					if (direction != Direction.DOWN && world.getBlockState(mutable.setWithOffset(pos, direction)).isAir()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Unique
	private static boolean shouldRipple(Level world, BlockPos pos) {
		if (EffectiveConfig.rainRippleDensity > 0) {
			FluidState fluidState = world.getFluidState(pos);
			return fluidState.isSource() && world.isRaining() && world.getBlockState(pos.offset(0, 1, 0)).isAir();
		}
		return false;
	}

	@Inject(method = "animateTick", at = @At("HEAD"))
	protected void effective$splashAndRainRipples(Level world, BlockPos pos, FluidState state, RandomSource random, CallbackInfo ci) {
		// flowing water splashes
		if (shouldSplash(world, pos.above())) {
			Vec3 vec3d = state.getFlow(world, pos);
			for (int i = 0; i <= random.nextInt(EffectiveConfig.flowingWaterSplashingDensity); i++) {
				world.addParticle(ParticleTypes.SPLASH, pos.getX() + .5 + random.nextGaussian() / 2f, pos.getY() + 1 + random.nextFloat(), pos.getZ() + .5 + random.nextGaussian() / 2f, vec3d.x() * random.nextFloat(), random.nextFloat() / 10f, vec3d.z() * random.nextFloat());
			}
		}

		// still water rain ripples
		if (shouldRipple(world, pos)) {
			if (random.nextInt(10) <= EffectiveConfig.rainRippleDensity) {
				if (world.getBiome(pos).value().getPrecipitationAt(pos) == Biome.Precipitation.RAIN && world.canSeeSkyFromBelowWater(pos)) {
					EffectiveUtils.spawnWaterEffect(world, Vec3.atCenterOf(pos).add(random.nextFloat() - random.nextFloat(), .39f, random.nextFloat() - random.nextFloat()), 0f, 0f, 0f, EffectiveUtils.WaterEffectType.RIPPLE);
				}
			}
		}
	}
}
