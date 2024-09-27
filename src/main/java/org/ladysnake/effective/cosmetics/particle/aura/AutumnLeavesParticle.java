package org.ladysnake.effective.cosmetics.particle.aura;

import net.minecraft.client.particle.*;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class AutumnLeavesParticle extends TextureSheetParticle {
	private static final Random RANDOM = new Random();
	private final int variant = RANDOM.nextInt(6);
	private final SpriteSet spriteProvider;

	private final double beginX;
	private final double beginY;
	private final double beginZ;

	public AutumnLeavesParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y + 0.05F, z, xd, yd, zd);
		this.spriteProvider = spriteProvider;

		this.quadSize *= 0.5F + RANDOM.nextFloat() / 2.0F;
		this.lifetime = 30 + RANDOM.nextInt(60);
		this.hasPhysics = true;
		this.setSprite(spriteProvider.get(this.variant % 3, 2));

		this.gCol = RANDOM.nextFloat() / 2f + 0.5f;
		this.bCol = 0f;

		beginX = x;
		beginY = y;
		beginZ = z;
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
			float i = this.roll;
			quaternion2.rotateZ(i);
		}

		Vector3f vec3f = new Vector3f(-1.0F, -1.0F, 0.0F);
		vec3f.rotate(quaternion2);
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

		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public void tick() {
		if (this.age++ < this.lifetime - 10) {
			this.alpha = Math.min(1f, this.alpha + 0.1f);
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.gCol *= 0.98;

		float fraction = this.age / (float) this.lifetime;
		this.x = Mth.cos(this.age / 15.0F + 1.0471973f * (variant + 0.5f)) * fraction + beginX;
		this.z = Mth.sin(this.age / 15.0F + 1.0471973f * (variant + 0.5f)) * fraction + beginZ;
		this.y = this.age / 34.0F + beginY + 0.05F;

		if (this.age >= this.lifetime - 10) {

			this.alpha = Math.max(0f, this.alpha - 0.1f);

			if (this.alpha <= 0f) {
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
			return new AutumnLeavesParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
