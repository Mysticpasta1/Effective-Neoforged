package org.ladysnake.effective.core.utils;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.Vec3;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;

import java.awt.*;

public class EffectiveUtils {
	public static boolean isGoingFast(Allay allayEntity) {
		Vec3 velocity = allayEntity.getDeltaMovement();
		float speedRequired = 0.1f;

		return (velocity.x() >= speedRequired || velocity.x() <= -speedRequired)
			|| (velocity.y() >= speedRequired || velocity.y() <= -speedRequired)
			|| (velocity.z() >= speedRequired || velocity.z() <= -speedRequired);
	}

	/**
	 * chooses between spawning a normal droplet / ripple / waterfall cloud or glow one depending on biome
	 */
	public static void spawnWaterEffect(Level world, Vec3 pos, double xd, double yd, double zd, WaterEffectType waterEffect) {
		SimpleParticleType particle = switch (waterEffect) {
			case DROPLET -> Effective.DROPLET;
			case RIPPLE -> Effective.RIPPLE;
		};
		if (isGlowingWater(world, pos)) {
			particle = switch (waterEffect) {
				case DROPLET -> Effective.GLOW_DROPLET;
				case RIPPLE -> Effective.GLOW_RIPPLE;
			};
		}

		world.addParticle(particle, pos.x(), pos.y(), pos.z(), xd, yd, zd);
	}

	public static boolean isGlowingWater(Level world, Vec3 pos) {
		return isGlowingWater(world, BlockPos.containing(pos));
	}

	public static boolean isGlowingWater(Level world, BlockPos pos) {
		return EffectiveConfig.glowingPlankton && Effective.isNightTime(world) && world.getBiome(pos).is(Biomes.WARM_OCEAN);
	}

	public static Color getGlowingWaterColor(Level world, BlockPos pos) {
		return new Color(Math.min(1, world.random.nextFloat() / 5f + world.getBrightness(LightLayer.BLOCK, pos) / 15f), Math.min(1, world.random.nextFloat() / 5f + world.getBrightness(LightLayer.BLOCK, pos) / 15f), 1f);
	}

	public enum WaterEffectType {
		DROPLET,
		RIPPLE,
	}

	public static final boolean isInCave(Level world, BlockPos pos) {
		return pos.getY() < world.getSeaLevel() && EffectiveUtils.hasStoneAbove(world, pos);
	}

	public static final boolean isInOverworld(Level world, BlockPos pos) {
		return world.getBiome(pos).is(ConventionalBiomeTags.IN_OVERWORLD);
	}

	// method to check if the player has a stone material type block above them, more reliable to detect caves compared to isSkyVisible
	// (okay nvm they removed materials we're using pickaxe mineable instead lmao oh god this is gonna be so unreliable)
	public static boolean hasStoneAbove(Level world, BlockPos pos) {
		BlockPos.MutableBlockPos mutable = pos.mutable();
		int startY = mutable.getY();
		for (int y = startY; y <= startY + 100; y++) {
			mutable.setY(y);
			if (world.getBlockState(mutable).isRedstoneConductor(world, pos) && world.getBlockState(mutable).is(BlockTags.MINEABLE_WITH_PICKAXE)) {
				return true;
			}
		}
		return false;
	}
}
