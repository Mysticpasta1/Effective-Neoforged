package org.ladysnake.effective.cosmetics.particle.aura;

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
import net.minecraftforge.event.level.NoteBlockEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class GhostlyAuraParticle extends TextureSheetParticle {
	private static final Random RANDOM = new Random();
	private final float MAXIMUM_ALPHA = 0.02f;
	private final Player owner;
	private final int variant = RANDOM.nextInt(4);
	private final SpriteSet spriteProvider;
	protected float alpha = 0f;
	protected float offsetX = RANDOM.nextFloat() * .7f - 0.35f;
	protected float offsetZ = RANDOM.nextFloat() * .7f - 0.35f;
	protected float offsetY = 0;

	public GhostlyAuraParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd);
		this.spriteProvider = spriteProvider;
		this.owner = world.getNearestPlayer((TargetingConditions.forNonCombat()).range(1D), this.x, this.y, this.z);

		this.quadSize *= 1f + RANDOM.nextFloat();
		this.lifetime = RANDOM.nextInt(5) + 8;
		this.hasPhysics = true;
		this.setSprite(spriteProvider.get(variant, 3));

		if (this.owner != null) {
			this.rCol = 1f;
			this.gCol = 1f;
			this.bCol = 1f;
			this.setPos(owner.getX() + offsetX, owner.getY() + offsetY, owner.getZ() + offsetZ);
		} else {
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
			float tmpY = Vec3f2.y();
			Vec3f2.set(Vec3f2.x(), 0, Vec3f2.z());
			// rotate so it always faces the player
			Vec3f2.rotate(quaternion2);
			Vec3f2.set(Vec3f2.x() / (1 + offsetY * offsetY), tmpY * offsetY, Vec3f2.z() / (1 + offsetY * offsetY));
			Vec3f2.mul(j);
			Vec3f2.add(f, g, h);
		}

		float minU = this.getU0();
		float maxU = this.getU1();
		float minV = this.getV0();
		float maxV = this.getV1();
		int l = LightTexture.FULL_BRIGHT;
//        float a = Mth.clamp(this.alpha, 0.0F, MAXIMUM_ALPHA);

		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public void tick() {
		if (owner != null) {
			this.xo = this.x;
			this.yo = this.y;
			this.zo = this.z;

			if (age++ < lifetime) {
				alpha += 0.01;
			} else {
				alpha -= 0.01;
				if (alpha <= 0) {
					this.remove();
				}
			}

			offsetY += 0.1;
			this.setPos(owner.getX() + offsetX, owner.getY() + offsetY, owner.getZ() + offsetZ);
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
			return new GhostlyAuraParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

}
