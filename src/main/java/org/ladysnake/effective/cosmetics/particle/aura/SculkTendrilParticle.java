package org.ladysnake.effective.cosmetics.particle.aura;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class SculkTendrilParticle extends TextureSheetParticle {
	private static final Random RANDOM = new Random();
	private final SpriteSet provider;
	private boolean wasOnGround = false;

	public SculkTendrilParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x + (RANDOM.nextFloat() * 2f - 1.0f), y, z + (RANDOM.nextFloat() * 2f - 1.0f), xd, yd, zd);
		this.setSprite(spriteProvider.get(0, 1));
		provider = spriteProvider;
		this.lifetime = 100;

		this.quadSize = 0f;
		this.hasPhysics = true;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3 vec3d = camera.getPosition();
		float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
		float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
		float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());

		Vector3f[] Vec3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float j = this.getQuadSize(tickDelta);

		for (int k = 0; k < 4; ++k) {
			Vector3f Vec3f2 = Vec3fs[k];
			Vec3f2.rotate(new Quaternionf().rotateXYZ(0f, (float) Math.toRadians(-camera.getYRot()), 0f));
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

	@Override
	public void tick() {
		if (!this.onGround) {
			this.move(0, -1D, 0);
			wasOnGround = false;
			return;
		}
		if (!wasOnGround) {
			this.setLocationFromBoundingbox();
			wasOnGround = true;
		}
		if (this.age++ < this.lifetime) {
			if (this.quadSize == 0f) {
				this.y -= 0.4f;
			}
			if (this.quadSize < 0.4f) {
				this.quadSize = Math.min(0.4f, this.quadSize + 0.05f);
				this.y += 0.05f;
			}
			if (this.age == 75) {
				this.setSprite(provider.get(1, 1));
			}
		}
		this.setBoundingBox(this.getBoundingBox().inflate(0, 1, 0));


		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.oRoll = this.roll;

		if (this.age >= this.lifetime) {
			this.quadSize = Math.max(0f, this.quadSize - 0.05f);
			this.y -= 0.05f;

			if (this.quadSize <= 0f) {
				this.remove();
			}
		}
	}

	@Override
	protected void setLocationFromBoundingbox() {
		AABB box = this.getBoundingBox();
		this.x = (box.minX + box.maxX) / 2.0D;
		this.y = box.minY + 0.4D;
		this.z = (box.minZ + box.maxZ) / 2.0D;
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new SculkTendrilParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
