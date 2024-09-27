package org.ladysnake.effective.cosmetics.particle.aura;

import net.minecraft.client.particle.*;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ConfettiParticle extends TextureSheetParticle {

	private static final Random RANDOM = new Random();
	private final double rotationXmod;
	private final double rotationYmod;
	private final double rotationZmod;
	private final float groundOffset;
	private float rotationX;
	private float rotationY;
	private float rotationZ;

	public ConfettiParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd);

		this.quadSize *= 0.1f + new Random().nextFloat() * 0.5f;
		this.hasPhysics = true;
		this.setSpriteFromAge(spriteProvider);
		this.alpha = 1f;

		this.lifetime = ThreadLocalRandom.current().nextInt(400, 420); // live approx 20s
		this.gCol = RANDOM.nextFloat();
		this.gCol = RANDOM.nextFloat();
		this.gCol = RANDOM.nextFloat();

		this.gravity = 0.1f;
		this.xd = xd * 10f;
		this.yd = yd * 10f;
		this.zd = zd * 10f;
		this.friction = 0.5f;

		this.rotationX = RANDOM.nextFloat() * 360f;
		this.rotationY = RANDOM.nextFloat() * 360f;
		this.rotationZ = RANDOM.nextFloat() * 360f;
		this.rotationXmod = RANDOM.nextFloat() * 10f * (random.nextBoolean() ? -1 : 1);
		this.rotationYmod = RANDOM.nextFloat() * 10f * (random.nextBoolean() ? -1 : 1);
		this.rotationZmod = RANDOM.nextFloat() * 10f * (random.nextBoolean() ? -1 : 1);

		this.groundOffset = RANDOM.nextFloat() / 100f + 0.001f;

		this.setPos(this.x + TwilightLegacyFireflyParticle.getWanderingDistance(this.random), this.y + random.nextFloat() * 2d, this.z + TwilightLegacyFireflyParticle.getWanderingDistance(this.random));
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

		Vector3f[] Vec3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float j = this.getQuadSize(tickDelta);

		if (!this.onGround) {
			rotationX += rotationXmod;
			rotationY += rotationYmod;
			rotationZ += rotationZmod;

			for (int k = 0; k < 4; ++k) {
				Vector3f Vec3f2 = Vec3fs[k];
				Vec3f2.rotate(new Quaternionf().rotateXYZ((float) Math.toRadians(rotationX), (float) Math.toRadians(rotationY), (float) Math.toRadians(rotationZ)));
				Vec3f2.mul(j);
				Vec3f2.add(f, g, h);
			}
		} else {
			rotationX = 90f;
			rotationY = 0;

			for (int k = 0; k < 4; ++k) {
				Vector3f Vec3f2 = Vec3fs[k];
				Vec3f2.rotate(new Quaternionf().rotateXYZ((float) Math.toRadians(rotationX), (float) Math.toRadians(rotationY), (float) Math.toRadians(rotationZ)));
				Vec3f2.mul(j);
				Vec3f2.add(f, g + this.groundOffset, h);
			}
		}

		float minU = this.getU0();
		float maxU = this.getU1();
		float minV = this.getV0();
		float maxV = this.getV1();
		int l = LightTexture.FULL_BRIGHT;

		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			if (this.level.getFluidState(BlockPos.containing(this.x, this.y + 0.2, this.z)).isEmpty()) {
				if (this.level.getFluidState(BlockPos.containing(this.x, this.y - 0.01, this.z)).is(FluidTags.WATER)) {
					this.onGround = true;
					this.yd = 0;
				} else {
					this.yd -= 0.04D * (double) this.gravity;
					this.move(this.xd, this.yd, this.zd);
					if (this.onGround && this.y == this.yo) {
						this.xd *= 1.1D;
						this.zd *= 1.1D;
					}

					this.xd *= this.friction;
					this.yd *= this.friction;
					this.zd *= this.friction;

					this.friction = Math.min(0.98f, this.friction * 1.15f);

					if (this.onGround) {
						this.xd *= 0.699999988079071D;
						this.zd *= 0.699999988079071D;
					}
				}
			} else {
				this.remove();
			}
		}
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new ConfettiParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
