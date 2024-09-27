package org.ladysnake.effective.core.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class FireflyParticle extends LodestoneWorldParticle {
	protected static final float BLINK_STEP = 0.05f;
	protected float nextAlphaGoal = 0f;
	protected double xTarget;
	protected double yTarget;
	protected double zTarget;
	protected int targetChangeCooldown = 0;
	protected int maxHeight;
	private BlockPos lightTarget;

	public FireflyParticle(ClientLevel world, WorldParticleOptions data, ParticleEngine.MutableSpriteSet spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, data, spriteSet, x, y, z, xd, yd, zd);

		this.lifetime = ThreadLocalRandom.current().nextInt(400, 1201); // live between 20 seconds and one minute
		this.maxHeight = 4;
		this.alpha = 0f;
		this.hasPhysics = false;
	}

	public static boolean canFlyThroughBlock(Level world, BlockPos blockPos, BlockState blockState) {
		return !blockState.isSuffocating(world, blockPos) && blockState.getFluidState().isEmpty();
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		// fade and die on daytime or if old enough unless fireflies can spawn any time of day
		if ((!level.dimensionType().hasFixedTime() && !EffectiveCosmetics.isNightTime(level)) || this.age++ >= this.lifetime) {
			nextAlphaGoal = 0;
			if (this.alpha <= 0.01f) {
				this.remove();
			}
		}

		// blinking
		if (this.alpha > nextAlphaGoal - BLINK_STEP && this.alpha < nextAlphaGoal + BLINK_STEP) {
			nextAlphaGoal = random.nextFloat();
		} else {
			if (nextAlphaGoal > this.alpha) {
				this.alpha = Math.min(this.alpha + BLINK_STEP, 1f);
			} else if (nextAlphaGoal < this.alpha) {
				this.alpha = Math.max(this.alpha - BLINK_STEP, 0f);
			}
		}

//		this.targetChangeCooldown -= (new Vec3(x, y, z).squaredDistanceTo(xo, yo, zo) < 0.0125) ? 10 : 1;

		if (random.nextInt(20) == 0 || (xTarget == 0 && yTarget == 0 && zTarget == 0) || new Vec3(x, y, z).distanceToSqr(xTarget, yTarget, zTarget) < 9) {
			selectBlockTarget();
		}

		Vec3 targetVector = new Vec3(this.xTarget - this.x, this.yTarget - this.y, this.zTarget - this.z);
		double length = targetVector.length();
		targetVector = targetVector.scale(0.1 / length);

		BlockPos blockPos = BlockPos.containing(this.x, this.y - 0.1, this.z);
		if (!canFlyThroughBlock(this.level, blockPos, this.level.getBlockState(blockPos))) {
			xd = (0.9) * xd + (0.1) * targetVector.x;
			yd = 0.05;
			zd = (0.9) * zd + (0.1) * targetVector.z;
		} else {
			xd = (0.9) * xd + (0.1) * targetVector.x;
			yd = (0.9) * yd + (0.1) * targetVector.y;
			zd = (0.9) * zd + (0.1) * targetVector.z;
		}
		if (!BlockPos.containing(x, y, z).equals(this.getTargetPosition())) {
			this.move(xd, yd, zd);
		}
	}

	private void selectBlockTarget() {
		if (this.lightTarget == null) {
			// Behaviour
			double groundLevel = 0;
			for (int i = 0; i < 20; i++) {
				BlockPos checkedPos = BlockPos.containing(this.x, this.y - i, this.z);
				BlockState checkedBlock = this.level.getBlockState(checkedPos);
				if (canFlyThroughBlock(this.level, checkedPos, checkedBlock)) {
					groundLevel = this.y - i;
				}
				if (groundLevel != 0) break;
			}

			this.xTarget = this.x + random.nextGaussian() * 10;
			this.yTarget = Math.min(Math.max(this.y + random.nextGaussian() * 2, groundLevel), groundLevel + maxHeight);
			this.zTarget = this.z + random.nextGaussian() * 10;

			BlockPos targetPos = BlockPos.containing(this.xTarget, this.yTarget, this.zTarget);
			if (!canFlyThroughBlock(this.level, targetPos, this.level.getBlockState(targetPos))) {
				this.yTarget += 1;
			}

			this.lightTarget = getMostLitBlockAround();
		} else {
			this.xTarget = this.lightTarget.getX() + random.nextGaussian();
			this.yTarget = this.lightTarget.getY() + random.nextGaussian();
			this.zTarget = this.lightTarget.getZ() + random.nextGaussian();

			if (this.level.getBrightness(LightLayer.BLOCK, BlockPos.containing(x, y, z)) > 0 && !this.level.isDay()) {
				this.lightTarget = getMostLitBlockAround();
			} else {
				this.lightTarget = null;
			}
		}

//		targetChangeCooldown = random.nextInt() % 100;
	}

	public BlockPos getTargetPosition() {
		return BlockPos.containing(this.xTarget, this.yTarget + 0.5, this.zTarget);
	}

	private BlockPos getMostLitBlockAround() {
		HashMap<BlockPos, Integer> randBlocks = new HashMap<>();

		// get blocks adjacent to the fly
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos bp = BlockPos.containing(this.x + x, this.y + y, this.z + z);
					randBlocks.put(bp, this.level.getBrightness(LightLayer.BLOCK, bp));
				}
			}
		}

		// get other random blocks to find a different light source
		for (int i = 0; i < 15; i++) {
			BlockPos randBP = BlockPos.containing(this.x + random.nextGaussian() * 10, this.y + random.nextGaussian() * 10, this.z + random.nextGaussian() * 10);
			randBlocks.put(randBP, this.level.getBrightness(LightLayer.BLOCK, randBP));
		}

		return randBlocks.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
	}
}
