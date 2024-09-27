package org.ladysnake.effective.core.particle;

import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import team.lodestar.lodestone.config.ClientConfig;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;

public class PlayerFacingParticle extends LodestoneWorldParticle {
	public PlayerFacingParticle(ClientLevel world, WorldParticleOptions data, ParticleEngine.MutableSpriteSet spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, data, spriteSet, x, y, z, xd, yd, zd);
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		VertexConsumer consumer = vertexConsumer;
		if (ClientConfig.DELAYED_PARTICLE_RENDERING.getConfigValue()) {
			if (getRenderType().equals(LodestoneWorldParticleRenderType.ADDITIVE)) {
				consumer = RenderHandler.DELAYED_RENDER.getParticleBuffers().get(LodestoneWorldParticleRenderType.ADDITIVE);
			}
			if (getRenderType().equals(LodestoneWorldParticleRenderType.TRANSPARENT)) {
				consumer = RenderHandler.DELAYED_RENDER.getParticleBuffers().get(LodestoneWorldParticleRenderType.TRANSPARENT);
			}
		}

		Vec3 vec3d = camera.getPosition();
		float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
		float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
		float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());
		Quaternionf quaternion;
		if (this.roll == 0.0F) {
			quaternion = camera.rotation();
		} else {
			quaternion = new Quaternionf(camera.rotation());
			float i = Mth.lerp(tickDelta, this.oRoll, this.roll);
			quaternion.rotateZ(i);
		}

		Vector3f[] vec3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float j = this.getQuadSize(tickDelta);

		Vec3 v1 = new Vec3(0, 0, 1);
		Vec3 v2 = new Vec3(this.x, this.y, this.z).subtract(camera.getPosition()).normalize();
		Vec3 a = v1.cross(v2);
		Quaternionf q = new Quaternionf(0f, (float) a.y(), (float) a.z(), (float) (Mth.sqrt((float) ((v1.length() * v1.length()) * (v2.length() * v2.length()))) + v1.dot(v2)));
		q.normalize();

		for (int k = 0; k < 4; ++k) {
			Vector3f vec3f2 = vec3fs[k];
			vec3f2.rotate(q);
			vec3f2.mul(j);
			vec3f2.add(f, g, h);
		}

		float minU = this.getU0();
		float maxU = this.getU1();
		float minV = this.getV0();
		float maxV = this.getV1();
		int l = this.getLightColor(tickDelta);

		consumer.vertex(vec3fs[0].x(), vec3fs[0].y(), vec3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		consumer.vertex(vec3fs[1].x(), vec3fs[1].y(), vec3fs[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		consumer.vertex(vec3fs[2].x(), vec3fs[2].y(), vec3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		consumer.vertex(vec3fs[3].x(), vec3fs[3].y(), vec3fs[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
	}
}
