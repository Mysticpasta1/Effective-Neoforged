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

public class PrismarineCrystalParticle extends TextureSheetParticle {
	private static final Random RANDOM = new Random();
	protected final float rotationFactor;
	private final int variant = RANDOM.nextInt(3);
	private final SpriteSet spriteProvider;
	private final float groundOffset;

	public PrismarineCrystalParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd);
		this.spriteProvider = spriteProvider;

		this.quadSize *= 1f + RANDOM.nextFloat();
		this.lifetime = ThreadLocalRandom.current().nextInt(400, 1201); // live between 20 seconds and one minute
		this.hasPhysics = true;
		this.setSprite(spriteProvider.get(variant, 2));

		if (yd == 0f && xd == 0f && zd == 0f) {
			this.alpha = 0f;
		}

		this.xd = random.nextFloat() * 0.01d;
		this.yd = -random.nextFloat() * 0.01d;
		this.zd = random.nextFloat() * 0.01d;

		this.groundOffset = RANDOM.nextFloat() / 100f + 0.001f;

		this.rotationFactor = ((float) Math.random() - 0.5F) * 0.01F;
		this.roll = random.nextFloat() * 360f;
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
			if (this.onGround) {
				Vec3f2.rotate(new Quaternionf(90f, 0f, quaternion2.z(), 0));
			} else {
				Vec3f2.rotate(quaternion2);
			}
			Vec3f2.mul(j);
			Vec3f2.add(f, g + this.groundOffset, h);
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
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public void tick() {
		if (this.age++ < this.lifetime) {
			this.alpha = Math.min(1f, this.alpha + 0.01f);
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER)) {
			this.move(this.xd, this.yd, this.zd);
		} else {
			this.move(this.xd, this.yd, this.zd);
			this.xd *= 0.9D;
			this.yd = -0.9D;
			this.zd *= 0.9D;
		}

		if (this.age >= this.lifetime) {
			this.alpha = Math.max(0f, this.alpha - 0.01f);

			if (this.alpha <= 0f) {
				this.remove();
			}
		}

		this.rCol = 0.8f + (float) Math.sin(this.age / 100f) * 0.2f;
//        this.blue = 0.9f + (float) Math.cos(this.age/100f) * 0.1f;

		this.oRoll = this.roll;
		if (this.onGround) {
			this.xd = 0;
			this.yd = 0;
			this.zd = 0;
		}

		if (this.yd != 0) {
			this.roll += Math.PI * Math.sin(rotationFactor * this.age) / 2;
		}
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new PrismarineCrystalParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

}
