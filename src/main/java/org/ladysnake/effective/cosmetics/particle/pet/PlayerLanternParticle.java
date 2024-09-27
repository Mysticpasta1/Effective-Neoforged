package org.ladysnake.effective.cosmetics.particle.pet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.effective.cosmetics.render.GlowyRenderLayer;
import org.ladysnake.effective.cosmetics.render.entity.model.pet.LanternModel;

public class PlayerLanternParticle extends Particle {
	public final ResourceLocation texture;
	final RenderType layer;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	protected Player owner;
	Model model;

	protected PlayerLanternParticle(ClientLevel world, double x, double y, double z, ResourceLocation texture, float red, float green, float blue) {
		super(world, x, y, z);
		this.texture = texture;
		this.model = new LanternModel(Minecraft.getInstance().getEntityModels().bakeLayer(LanternModel.MODEL_LAYER));
		this.layer = RenderType.entityTranslucent(texture);
		this.gravity = 0.0F;

		this.lifetime = 35;
		this.owner = world.getNearestPlayer((TargetingConditions.forNonCombat()).range(1D), this.x, this.y, this.z);

		if (this.owner == null) {
			this.remove();
		}

		this.rCol = red;
		this.gCol = green;
		this.bCol = blue;
		this.alpha = 0;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}



	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3 vec3d = camera.getPosition();
		float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
		float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
		float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());

		PoseStack matrixStack = new PoseStack();
		matrixStack.translate(f, g, h);
		matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(g, this.prevYaw, this.yaw) - 180));
		matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(g, this.prevPitch, this.pitch)));
		matrixStack.scale(0.5F, -0.5F, 0.5F);
		matrixStack.translate(0, -1, 0);
		MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer vertexConsumer2 = immediate.getBuffer(GlowyRenderLayer.get(texture));
		if (this.alpha > 0) {
			this.model.renderToBuffer(matrixStack, vertexConsumer2, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
		}
		immediate.endBatch();
	}

	@Override
	public void tick() {
		if (this.age > 10) {
			this.alpha = 1f;
		} else {
			this.alpha = 0;
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

			this.prevYaw = this.yaw;
			this.yaw = owner.tickCount * 2;
		} else {
			this.remove();
		}
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final ResourceLocation texture;
		private final float red;
		private final float green;
		private final float blue;

		public DefaultFactory(SpriteSet spriteProvider, ResourceLocation texture, float red, float green, float blue) {
			this.texture = texture;
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new PlayerLanternParticle(world, x, y, z, this.texture, this.red, this.green, this.blue);
		}
	}
}
