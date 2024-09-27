package org.ladysnake.effective.cosmetics.particle.pet;

import net.minecraft.client.particle.*;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class PetParticle extends TextureSheetParticle {
	private static final Random RANDOM = new Random();
	protected final Player owner;
	private final SpriteSet spriteProvider;
	protected float alpha = 0f;

	public PetParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
		super(world, x, y, z);
		this.spriteProvider = spriteProvider;
		this.setSpriteFromAge(spriteProvider);

		this.alpha = 0;
		this.lifetime = 40;
		this.owner = world.getNearestPlayer(TargetingConditions.forNonCombat().range(1D), this.x, this.y, this.z);

		this.quadSize = 0.2f;

		if (this.owner == null) {
			this.remove();
		}
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

		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(1f, 1f, 1f, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV).color(1f, 1f, 1f, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV).color(1f, 1f, 1f, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, maxV).color(1f, 1f, 1f, alpha).uv2(l).endVertex();
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		if (this.age > 10) {
			alpha = 1;
		} else {
			alpha = 0;
		}

		if (owner != null) {
			this.xo = this.x;
			this.yo = this.y;
			this.zo = this.z;

			// die if old enough
			if (this.age++ >= this.lifetime) {
				this.remove();
			}

			this.setPos(owner.getX() + Math.cos(owner.yBodyRot / 50) * 0.5, owner.getY() + owner.getBbHeight() + 0.5f + Math.sin(owner.tickCount / 12f) / 12f, owner.getZ() - Math.cos(owner.yBodyRot / 50) * 0.5);
		} else {
			this.remove();
		}
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new PetParticle(clientWorld, d, e, f, this.spriteProvider);
		}
	}

}
