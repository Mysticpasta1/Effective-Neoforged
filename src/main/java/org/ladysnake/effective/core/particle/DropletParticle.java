package org.ladysnake.effective.core.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ladysnake.effective.core.Effective;

public class DropletParticle extends TextureSheetParticle {
	private final SpriteSet spriteProvider;

	public DropletParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd);

		this.xd = xd;
		this.yd = yd;
		this.zd = zd;

		this.spriteProvider = spriteProvider;
		this.lifetime = 500;
		this.quadSize = .05f;
		this.setSpriteFromAge(spriteProvider);
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		if (this.onGround || (this.age > 5 && this.level.getBlockState(BlockPos.containing(this.x, this.y + this.yd, this.z)).getBlock() == Blocks.WATER)) {
			this.remove();
		}

		if (this.level.getBlockState(BlockPos.containing(this.x, this.y + this.yd, this.z)).getBlock() == Blocks.WATER && this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
			for (int i = 0; i > -10; i--) {
				BlockPos pos = BlockPos.containing(this.x, Math.round(this.y) + i, this.z);
				if (this.level.getBlockState(pos).getBlock() == Blocks.WATER && this.level.getBlockState(BlockPos.containing(this.x, Math.round(this.y) + i, this.z)).getFluidState().isSource() && this.level.getBlockState(BlockPos.containing(this.x, Math.round(this.y) + i + 1, this.z)).isAir()) {
					this.level.addParticle(Effective.RIPPLE, this.x, Math.round(this.y) + i + 0.9f, this.z, 0, 0, 0);
					break;
				}
			}

			this.remove();
		}

		this.xo *= 0.99f;
		this.yo -= 0.05f;
		this.zo *= 0.99f;

		this.move(xo, yo, zo);
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
		int l = this.getLightColor(tickDelta);

		vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new DropletParticle(world, x, y, z, xd, yd, zd, this.spriteProvider);
		}
	}
}
