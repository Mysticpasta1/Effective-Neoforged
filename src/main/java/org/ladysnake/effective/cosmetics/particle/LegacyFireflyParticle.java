package org.ladysnake.effective.cosmetics.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.particle.type.LegacyFireflyParticleType;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class LegacyFireflyParticle extends TextureSheetParticle {
	protected static final float BLINK_STEP = 0.05f;
	private final SpriteSet spriteProvider;
	private final boolean isAttractedByLight = true;
	protected float nextAlphaGoal = 0f;
	protected double xTarget;
	protected double yTarget;
	protected double zTarget;
	protected int targetChangeCooldown = 0;
	protected int maxHeight;
	private BlockPos lightTarget;

	public LegacyFireflyParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
		super(world, x, y, z, 0f, 0f, 0f);
		this.spriteProvider = spriteProvider;

		this.quadSize *= 0.25f + random.nextFloat() * 0.50f;
		this.lifetime = ThreadLocalRandom.current().nextInt(400, 1201); // live between 20 seconds and one minute
		this.maxHeight = 4;
		this.setSpriteFromAge(spriteProvider);
		this.alpha = 0f;
		this.hasPhysics = false;
	}

	public static boolean canFlyThroughBlock(Level world, BlockPos blockPos, BlockState blockState) {
		return !blockState.isSuffocating(world, blockPos);
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(1f, 1f, 1f, 0.5f).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV + (maxV - minV) / 2.0F).color(1f, 1f, 1f, 0.5f).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV + (maxV - minV) / 2.0F).color(1f, 1f, 1f, 0.5f).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, maxV).color(1f, 1f, 1f, 0.5f).uv2(l).endVertex();
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

		this.targetChangeCooldown -= (new Vec3(x, y, z).distanceToSqr(xo, yo, zo) < 0.0125) ? 10 : 1;

		if ((this.level.getGameTime() % 20 == 0) && ((xTarget == 0 && yTarget == 0 && zTarget == 0) || new Vec3(x, y, z).distanceToSqr(xTarget, yTarget, zTarget) < 9 || targetChangeCooldown <= 0)) {
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

			if (this.isAttractedByLight) {
				this.lightTarget = getMostLitBlockAround();
			}
		} else {
			this.xTarget = this.lightTarget.getX() + random.nextGaussian();
			this.yTarget = this.lightTarget.getY() + random.nextGaussian();
			this.zTarget = this.lightTarget.getZ() + random.nextGaussian();

			this.x = this.lightTarget.getX();
			this.y = this.lightTarget.getY() + 1;
			this.z = this.lightTarget.getZ();

			if (this.level.getBrightness(LightLayer.BLOCK, BlockPos.containing(x, y, z)) > 0 && !this.level.isDay()) {
				this.lightTarget = getMostLitBlockAround();
			} else {
				this.lightTarget = null;
			}
		}

		targetChangeCooldown = random.nextInt() % 100;
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

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			LegacyFireflyParticle instance = new LegacyFireflyParticle(world, x, y, z, spriteProvider);
			if (parameters instanceof LegacyFireflyParticleType fireflyParameters && fireflyParameters.initialData != null) {
				int color = fireflyParameters.initialData.color;

				float r = (float) (color >> 16 & 0xFF) / 255.0f;
				float g = (float) (color >> 8 & 0xFF) / 255.0f;
				float b = (float) (color & 0xFF) / 255.0f;

				instance.rCol = r;
				instance.gCol = g;
				instance.bCol = b;
			}
			return instance;
		}
	}
}
