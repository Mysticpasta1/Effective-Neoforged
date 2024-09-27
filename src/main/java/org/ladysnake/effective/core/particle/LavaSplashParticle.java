package org.ladysnake.effective.core.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.render.entity.model.SplashBottomModel;
import org.ladysnake.effective.core.render.entity.model.SplashModel;

import java.util.List;
import java.util.stream.Collectors;

public class LavaSplashParticle extends Particle {
	public ResourceLocation texture1;
	public ResourceLocation texture2;
	public float widthMultiplier;
	public float heightMultiplier;
	public int wave1End;
	public int wave2Start;
	public int wave2End;
	Model waveModel;
	Model waveBottomModel;
	RenderType layer1;
	RenderType layer2;

	protected LavaSplashParticle(ClientLevel world, double x, double y, double z, ResourceLocation texture) {
		super(world, x, y, z);
		this.texture1 = texture;
		this.texture2 = texture;
		this.waveModel = new SplashModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(SplashModel.MODEL_LAYER));
		this.waveBottomModel = new SplashBottomModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(SplashBottomModel.MODEL_LAYER));
		this.layer1 = RenderType.entityTranslucent(texture);
		this.layer2 = RenderType.entityTranslucent(texture);
		this.gravity = 0.0F;
		this.widthMultiplier = 0f;
		this.heightMultiplier = 0f;

		this.wave1End = 12;
		this.wave2Start = 7;
		this.wave2End = 24;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		// first splash
		if (age <= this.wave1End) {
			int frame1 = Math.round(((float) this.age / (float) this.wave1End) * 12);

			this.texture1 = Effective.id("textures/entity/splash/lava_splash_" + frame1 + ".png");
			this.layer1 = RenderType.entityTranslucent(texture1);

			Vec3 vec3d = camera.getPosition();
			float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
			float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
			float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());

			PoseStack matrixStack = new PoseStack();
			matrixStack.translate(f, g, h);
			matrixStack.scale(widthMultiplier, -heightMultiplier, widthMultiplier);
			matrixStack.translate(0, -1, 0);
			MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
			VertexConsumer vertexConsumer2 = immediate.getBuffer(layer1);

			int light = this.getLightColor(tickDelta);
			this.waveModel.renderToBuffer(matrixStack, vertexConsumer2, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0f);

			immediate.endBatch();
		}
		if (age <= this.wave1End) {
			int frame1 = Math.round(((float) this.age / (float) this.wave1End) * 12);

			this.texture1 = Effective.id("textures/entity/splash/lava_splash_" + frame1 + ".png");
			this.layer1 = RenderType.entityTranslucent(texture1);

			Vec3 vec3d = camera.getPosition();
			float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
			float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
			float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());

			PoseStack matrixStack = new PoseStack();
			matrixStack.translate(f, g, h);
			matrixStack.scale(widthMultiplier, heightMultiplier, widthMultiplier);
			matrixStack.translate(0, 0.001, 0);
			MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
			VertexConsumer vertexConsumer2 = immediate.getBuffer(layer1);

			int light = this.getLightColor(tickDelta);
			this.waveBottomModel.renderToBuffer(matrixStack, vertexConsumer2, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0f);

			immediate.endBatch();
		}

		// second splash
		if (age >= this.wave2Start) {
			int frame2 = Math.round(((float) (this.age - wave2Start) / (float) (this.wave2End - this.wave2Start)) * 12);

			this.texture2 = Effective.id("textures/entity/splash/lava_splash_" + frame2 + ".png");
			this.layer2 = RenderType.entityTranslucent(texture2);

			Vec3 vec3d = camera.getPosition();
			float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
			float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
			float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());

			PoseStack matrixStack = new PoseStack();
			matrixStack.translate(f, g, h);
			matrixStack.scale(widthMultiplier * 0.5f, -heightMultiplier * 2, widthMultiplier * 0.5f);
			matrixStack.translate(0, -1, 0);
			MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();;
			VertexConsumer vertexConsumer2 = immediate.getBuffer(layer2);

			int light = this.getLightColor(tickDelta);
			this.waveModel.renderToBuffer(matrixStack, vertexConsumer2, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0f);

			immediate.endBatch();
		}
		if (age >= this.wave2Start) {
			int frame2 = Math.round(((float) (this.age - wave2Start) / (float) (this.wave2End - this.wave2Start)) * 12);

			this.texture2 = Effective.id("textures/entity/splash/lava_splash_" + frame2 + ".png");
			this.layer2 = RenderType.entityTranslucent(texture2);

			Vec3 vec3d = camera.getPosition();
			float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
			float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
			float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());

			PoseStack matrixStack = new PoseStack();
			matrixStack.translate(f, g, h);
			matrixStack.scale(widthMultiplier * 0.5f, heightMultiplier * 2, widthMultiplier * 0.5f);
			matrixStack.translate(0, 0.001, 0);
			MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();;
			VertexConsumer vertexConsumer2 = immediate.getBuffer(layer2);

			int light = this.getLightColor(tickDelta);
			this.waveBottomModel.renderToBuffer(matrixStack, vertexConsumer2, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0f);

			immediate.endBatch();
		}
	}

	@Override
	public void tick() {
		if (this.widthMultiplier == 0f) {
			List<Entity> closeEntities = level.getEntities(null, this.getBoundingBox().inflate(5.0f)).stream().filter(entity -> level.getBlockState(entity.blockPosition().offset(Mth.floor(entity.getDeltaMovement().x), Mth.floor(entity.getDeltaMovement().y), Mth.floor(entity.getDeltaMovement().z))).getBlock() == Blocks.LAVA).collect(Collectors.toList());
			closeEntities.sort((o1, o2) -> (int) (o1.position().distanceToSqr(new Vec3(this.x, this.y, this.z)) - o2.distanceToSqr(new Vec3(this.x, this.y, this.z))));

			if (!closeEntities.isEmpty()) {
				this.widthMultiplier = closeEntities.get(0).getBbWidth() * 2f;
				this.heightMultiplier = (float) Math.max(-closeEntities.get(0).getDeltaMovement().x() * this.widthMultiplier, 0f);

				this.wave1End = 10 + Math.round(widthMultiplier * 1.2f);
				this.wave2Start = 6 + Math.round(widthMultiplier * 0.7f);
				this.wave2End = 20 + Math.round(widthMultiplier * 2.4f);
			} else {
				this.remove();
			}
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.widthMultiplier *= 1.03f;

		if (this.age++ >= this.wave2End) {
			this.remove();
		}

		if (this.age == 1) {
			for (int i = 0; i < this.widthMultiplier * 10f; i++) {
				this.level.addParticle(ParticleTypes.LAVA, this.x + (this.random.nextGaussian() * this.widthMultiplier / 10f), this.y, this.z + (this.random.nextGaussian() * this.widthMultiplier / 10f), random.nextGaussian() / 10f * this.widthMultiplier / 2.5f, random.nextFloat() / 10f + this.heightMultiplier / 2.8f, random.nextGaussian() / 10f * this.widthMultiplier / 2.5f);
			}
		} else if (this.age == wave2Start) {
			for (int i = 0; i < this.widthMultiplier * 5f; i++) {
				this.level.addParticle(ParticleTypes.LAVA, this.x + (this.random.nextGaussian() * this.widthMultiplier / 10f * .5f), this.y, this.z + (this.random.nextGaussian() * this.widthMultiplier / 10f * .5f), random.nextGaussian() / 10f * this.widthMultiplier / 5f, random.nextFloat() / 10f + this.heightMultiplier / 2.2f, random.nextGaussian() / 10f * this.widthMultiplier / 5f);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final ResourceLocation texture;

		public DefaultFactory(SpriteSet spriteProvider, ResourceLocation texture) {
			this.texture = texture;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new LavaSplashParticle(world, x, y, z, this.texture);
		}
	}
}
