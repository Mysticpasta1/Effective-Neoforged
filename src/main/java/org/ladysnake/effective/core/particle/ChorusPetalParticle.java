package org.ladysnake.effective.core.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ladysnake.effective.core.Effective;

import java.util.Random;

public class ChorusPetalParticle extends TextureSheetParticle {
	private static final Random RANDOM = new Random();
	protected final float rotationFactor;
	private final float groundOffset;
	private boolean isInAir = true;

	public ChorusPetalParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd);

		this.quadSize *= 1f + RANDOM.nextFloat();
		this.lifetime = 300 + random.nextInt(60);
		this.hasPhysics = true;
		int variant = RANDOM.nextInt(3);
		this.setSprite(spriteProvider.get(variant, 2));

		if (yd == 0f && xd == 0f && zd == 0f) {
			this.alpha = 0f;
		}

		this.yd = yd - 0.15D - random.nextFloat() / 10;
		this.xd = xd - 0.05D - random.nextFloat() / 10;
		this.zd = zd - 0.05D - random.nextFloat() / 10;

		this.rotationFactor = ((float) Math.random() - 0.5F) * 0.01F;
		this.roll = random.nextFloat() * 360f;

		this.groundOffset = RANDOM.nextFloat() / 100f + 0.001f;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		this.rCol = Math.max(this.gCol, 0.3f);

		Vec3 vec3d = camera.getPosition();
		float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
		float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
		float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());
		Quaternionf quaternion2;

		float i = 0f;
		if (this.roll == 0.0F) {
			quaternion2 = camera.rotation();
		} else {
			quaternion2 = new Quaternionf(camera.rotation());
			i = Mth.lerp(tickDelta, this.oRoll, this.roll);
			quaternion2.rotateZ(i);
		}

		Vector3f vec3f = new Vector3f(-1.0F, -1.0F, 0.0F);
		vec3f.rotate(quaternion2);
		Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float j = this.getQuadSize(tickDelta);

		if (isInAir) {
			for (int k = 0; k < 4; ++k) {
				Vector3f Vec3f2 = vector3fs[k];
				Vec3f2.rotate(quaternion2);
				Vec3f2.mul(j);
				Vec3f2.add(f, g, h);
			}
		} else {
			for (int k = 0; k < 4; ++k) {
				Vector3f Vec3f2 = vector3fs[k];
				Vec3f2.rotate(new Quaternionf().rotateXYZ((float) Math.toRadians(90f), 0f, (float) Math.toRadians(this.roll)));
				Vec3f2.mul(j);
				Vec3f2.add(f, g + this.groundOffset, h);
			}
		}

		float minU = this.getU0();
		float maxU = this.getU1();
		float minV = this.getV0();
		float maxV = this.getV1();
		int l = LightTexture.FULL_BRIGHT;

		vertexConsumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
		vertexConsumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public void tick() {
		if (this.age++ < this.lifetime) {
			this.alpha = Math.min(1f, this.alpha + 0.1f);
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.99D;
		this.yd *= 0.99D;
		this.zd *= 0.99D;

		this.gCol /= 1.001;
		this.gCol /= 1.002;

		if (this.age >= this.lifetime) {
			this.alpha = Math.max(0f, this.alpha - 0.1f);

			if (this.alpha <= 0f) {
				this.remove();
			}
		}

		this.oRoll = this.roll;
		if (this.onGround || this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER)) {
			if (this.isInAir) {
				if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).getBlock() == Blocks.WATER) {
					for (int i = 0; i > -10; i--) {
						BlockPos pos = BlockPos.containing(this.x, Math.round(this.y) + i, this.z);
						if (this.level.getBlockState(pos).getBlock() == Blocks.WATER && this.level.getBlockState(BlockPos.containing(this.x, Math.round(this.y) + i, this.z)).getFluidState().isSource() && this.level.getBlockState(BlockPos.containing(this.x, Math.round(this.y) + i + 1, this.z)).isAir()) {
							this.level.addParticle(Effective.RIPPLE, this.x, Math.round(this.y) + i + 0.9f, this.z, 0, 0, 0);
							break;
						}
					}
				}

				this.xd = 0;
				this.yd = 0;
				this.zd = 0;
				this.isInAir = false;
			}
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
			return new ChorusPetalParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

}
