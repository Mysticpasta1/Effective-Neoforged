package org.ladysnake.effective.cosmetics.particle.pet;

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
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;

public class JackoParticle extends PetParticle {
	private float glow;

	public JackoParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
		super(world, x, y, z, spriteProvider);

		this.glow = 0;
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
		int p = this.getLightColor(tickDelta);
		int l = LightTexture.FULL_BRIGHT;
		float a = Math.min(1f, Math.max(0f, this.alpha));
		float gl = Math.min(1f, Math.max(0f, this.glow));

		// pumpkin
		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, minV + (maxV - minV) / 2.0F).color(1f, 1f, 1f, a).uv2(p);
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV).color(1f, 1f, 1f, a).uv2(p);
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV).color(1f, 1f, 1f, a).uv2(p);
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, minV + (maxV - minV) / 2.0F).color(1f, 1f, 1f, a).uv2(p);

		// pumpkin glow
		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(1f, 1f, 1f, gl).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV + (maxV - minV) / 2.0F).color(1f, 1f, 1f, gl).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV + (maxV - minV) / 2.0F).color(1f, 1f, 1f, gl).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, maxV).color(1f, 1f, 1f, gl).uv2(l).endVertex();
	}

	@Override
	public void tick() {
		super.tick();

		if (owner != null) {
			// if night or dark enough
			if (EffectiveCosmetics.isNightTime(level) || (this.level.getLightEmission(BlockPos.containing(this.x, this.y, this.z)) < 10)) {
				glow = 1;
			} else {
				glow = 0;
			}
		}
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new JackoParticle(clientWorld, d, e, f, this.spriteProvider);
		}
	}

}
