package org.ladysnake.effective.cosmetics.particle.aura;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.particle.LegacyFireflyParticle;

import java.util.Optional;

public class TwilightLegacyFireflyParticle extends LegacyFireflyParticle {
	private final Player owner;

	public TwilightLegacyFireflyParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
		super(world, x, y, z, spriteProvider);

		this.lifetime = 20;
		this.owner = world.getNearestPlayer(TargetingConditions.forNonCombat().range(1D), this.x, this.y, this.z);
		this.maxHeight = 2;

		Optional.ofNullable(owner).map(EffectiveCosmetics::getCosmeticData).ifPresentOrElse(
			data -> {
				this.rCol = data.getColor1Red() / 255f;
				this.gCol = data.getColor1Green() / 255f;
				this.bCol = data.getColor1Blue() / 255f;
				this.nextAlphaGoal = 1f;
			},
			this::remove
		);

		this.setPos(this.x + TwilightLegacyFireflyParticle.getWanderingDistance(this.random), this.y + random.nextFloat() * 2d, this.z + TwilightLegacyFireflyParticle.getWanderingDistance(this.random));
	}

	public static double getWanderingDistance(RandomSource random) {
		return random.nextGaussian() / 5d;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3 vec3d = camera.getPosition();
		float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
		float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
		float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());
		Quaternionf quaternion2;
		if (this.roll == 0.0F) {
			quaternion2 = camera.rotation();
		} else {
			quaternion2 = new Quaternionf(camera.rotation());
			float i = Mth.lerp(tickDelta, this.oRoll, this.roll);
			quaternion2.rotateZ(i);
		}

		Vector3f Vec3f = new Vector3f(-1.0F, -1.0F, 0.0F);
		Vec3f.rotate(quaternion2);
		Vector3f[] Vec3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float j = this.getQuadSize(tickDelta);

		for (int k = 0; k < 4; ++k) {
			Vector3f Vec3f2 = Vec3fs[k];
			Vec3f2.rotate(quaternion2);
			Vec3f2.mul(j);
			Vec3f2.add(f, g, h);
		}

		float minU = this.getU0();
		float maxU = this.getU1();
		float minV = this.getV0();
		float maxV = this.getV1();
		int l = LightTexture.FULL_BRIGHT;
		float a = Math.min(1f, Math.max(0f, this.alpha));

		// colored layer
		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, minV + (maxV - minV) / 2.0F).color(this.rCol, this.gCol, this.bCol, a).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV).color(this.rCol, this.gCol, this.bCol, a).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV).color(this.rCol, this.gCol, this.bCol, a).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, minV + (maxV - minV) / 2.0F).color(this.rCol, this.gCol, this.bCol, a).uv2(l).endVertex();

		// white center
		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(1f, 1f, 1f, 0.5f / 100f).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV + (maxV - minV) / 2.0F).color(1f, 1f, 1f, 0.5f / 100f).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV + (maxV - minV) / 2.0F).color(1f, 1f, 1f, 0.5f / 100f).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, maxV).color(1f, 1f, 1f, 0.5f / 100f).uv2(l).endVertex();
	}


	@Override
	public void tick() {
		if (owner != null) {
			this.xo = this.x;
			this.yo = this.y;
			this.zo = this.z;

			// fade and die on daytime or if old enough
			if (this.age++ >= this.lifetime) {
				nextAlphaGoal = -BLINK_STEP;
				if (this.alpha < 0f) {
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

			this.targetChangeCooldown -= (new Vec3(x, y, z).distanceToSqr(xo, yo, zo) < 0.0125) ? 10 : 1;

			if ((this.level.getGameTime() % 20 == 0) && ((xTarget == 0 && yTarget == 0 && zTarget == 0) || new Vec3(x, y, z).distanceToSqr(xTarget, yTarget, zTarget) < 9 || targetChangeCooldown <= 0)) {
				selectBlockTarget();
			}

			Vec3 targetVector = new Vec3(this.xTarget - this.x, this.yTarget - this.y, this.zTarget - this.z);
			double length = targetVector.length();
			targetVector = targetVector.scale(0.025 / length);

			BlockState stateBelow = this.level.getBlockState(BlockPos.containing(this.x, this.y - 0.1, this.z));
			if (!stateBelow.getBlock().isPossibleToRespawnInThis(stateBelow)) {
				xd = (0.9) * xd + (0.1) * targetVector.x;
				yd = 0.05;
				zd = (0.9) * zd + (0.1) * targetVector.z;
			} else {
				xd = (0.9) * xd + (0.1) * targetVector.x;
				yd = (0.2) * yd + (0.1) * targetVector.y;
				zd = (0.9) * zd + (0.1) * targetVector.z;
			}

			if (!BlockPos.containing(x, y, z).equals(this.getTargetPosition())) {
				this.move(xd, yd, zd);
			}
		} else {
			this.remove();
		}
	}

	private void selectBlockTarget() {
		// Behaviour
		double groundLevel = 0;
		for (int i = 0; i < 20; i++) {
			BlockState checkedBlock = this.level.getBlockState(BlockPos.containing(this.x, this.y - i, this.z));
			if (!checkedBlock.getBlock().isPossibleToRespawnInThis(checkedBlock)) {
				groundLevel = this.y - i;
			}
			if (groundLevel != 0) break;
		}

		this.xTarget = owner.getX() + random.nextGaussian();
		this.yTarget = Math.min(Math.max(owner.getY() + random.nextGaussian(), groundLevel), groundLevel + maxHeight);
		this.zTarget = owner.getZ() + random.nextGaussian();

		BlockPos targetPos = BlockPos.containing(this.xTarget, this.yTarget, this.zTarget);
		if (this.level.getBlockState(targetPos).isCollisionShapeFullBlock(level, targetPos)
			&& this.level.getBlockState(targetPos).isRedstoneConductor(level, targetPos)) {
			this.yTarget += 1;
		}

		targetChangeCooldown = random.nextInt() % 100;
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new TwilightLegacyFireflyParticle(clientWorld, d, e, f, this.spriteProvider);
		}
	}

}
