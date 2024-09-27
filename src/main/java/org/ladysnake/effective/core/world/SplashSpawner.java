package org.ladysnake.effective.core.world;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.particle.contracts.SplashParticleInitialData;
import org.ladysnake.effective.core.particle.types.SplashParticleType;
import org.ladysnake.effective.core.utils.EffectiveUtils;

public final class SplashSpawner {
	public static void trySpawnSplash(Entity entity) {
		Entity topMostEntity = entity.isVehicle() && entity.getFirstPassenger() != null ? entity.getFirstPassenger() : entity;
		if (!(topMostEntity instanceof FishingHook)) {
			float amplifier = topMostEntity == entity ? 0.2f : 0.9f;
			Vec3 impactVelocity = topMostEntity.getDeltaMovement();

			if (impactVelocity.length() < EffectiveConfig.splashThreshold) return;

			float splashIntensity = Math.min(1.0f, computeSplashIntensity(impactVelocity, amplifier));

			for (int y = -10; y < 10; y++) {
				if (isValidSplashPosition(entity, y)) {
					float splashY = Math.round(entity.getY()) + y + 0.9f;
					entity.level().playLocalSound(
						entity.getX(),
						splashY,
						entity.getZ(),
						topMostEntity instanceof Player ? SoundEvents.PLAYER_SPLASH : SoundEvents.GENERIC_SPLASH,
						SoundSource.AMBIENT,
						splashIntensity * 10f,
						0.8f,
						true
					);
					SplashParticleInitialData data = new SplashParticleInitialData(topMostEntity.getBbWidth(), impactVelocity.y());
					spawnSplash(
						entity.level(),
						entity.getX(),
						splashY,
						entity.getZ(),
						data
					);
					break;
				}
			}

			spawnWaterEffects(entity);
		}
	}

	private static float computeSplashIntensity(Vec3 impactVelocity, float f) {
		return (float) Math.sqrt(
			impactVelocity.x * impactVelocity.x * (double) 0.2f +
				impactVelocity.y * impactVelocity.y +
				impactVelocity.z * impactVelocity.z * (double) 0.2f
		) * f;
	}

	private static void spawnWaterEffects(Entity entity) {
		RandomSource random = entity.level().getRandom();

		for (int j = 0; j < entity.getBbWidth() * 25f; j++) {
			EffectiveUtils.spawnWaterEffect(entity.level(), new Vec3(entity.getX() + random.nextGaussian() * entity.getBbWidth() / 5f, entity.getY(), entity.getZ() + random.nextGaussian() * entity.getBbWidth()), random.nextGaussian() / 15f, random.nextFloat() / 2.5f, random.nextGaussian() / 15f, EffectiveUtils.WaterEffectType.DROPLET);
		}
	}

	private static boolean isValidSplashPosition(Entity entity, int yOffset) {
		BlockPos pos = BlockPos.containing(entity.getX(), Math.round(entity.getY()) + yOffset, entity.getZ());
		BlockState blockState = entity.level().getBlockState(pos);

		if (blockState.getFluidState().getType() == Fluids.WATER) {
			if (blockState.getFluidState().isSource()) {
				return entity.level().getBlockState(pos.above()).isAir();
			}
		}
		return false;
	}

	/**
	 * Chooses between spawning a normal splash or glow splash depending on biome
	 */
	private static void spawnSplash(Level world, double x, double y, double z, @Nullable SplashParticleInitialData data) {
		SplashParticleType splash = EffectiveUtils.isGlowingWater(world, BlockPos.containing(x, y, z)) ? Effective.GLOW_SPLASH : Effective.SPLASH;
		world.addParticle(splash.setData(data), x, y, z, 0, 0, 0);
	}
}
