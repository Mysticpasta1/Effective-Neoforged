package org.ladysnake.effective.core.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.particle.types.SplashParticleType;

public class GlowSplashParticle extends SplashParticle {
	public float redAndGreen = random.nextFloat() / 5f;
	public float blue = 1.0f;
	public BlockPos pos;

	protected GlowSplashParticle(ClientLevel world, double x, double y, double z) {
		super(world, x, y, z);

		pos = BlockPos.containing(x, y, z);
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		// first splash
		if (age <= this.wave1End) {
			drawSplash(Math.round((this.age / (float) this.wave1End) * MAX_FRAME), camera, tickDelta);
		}
		// second splash
		if (age >= this.wave2Start) {
			drawSplash(Math.round(((float) (this.age - wave2Start) / (float) (this.wave2End - this.wave2Start)) * MAX_FRAME), camera, tickDelta, new Vector3f(0.5f, 2, 0.5f));
		}
	}

	private void drawSplash(int frame, Camera camera, float tickDelta, Vector3f multiplier) {
		if (waterColor == -1) {
			waterColor = BiomeColors.getAverageWaterColor(level, BlockPos.containing(this.x, this.y, this.z));
		}
		float r = (float) (waterColor >> 16 & 0xFF) / 255.0f;
		float g = (float) (waterColor >> 8 & 0xFF) / 255.0f;
		float b = (float) (waterColor & 0xFF) / 255.0f;

		ResourceLocation texture = Effective.id("textures/entity/splash/splash_" + Mth.clamp(frame, 0, MAX_FRAME) + ".png");
		RenderType layer = RenderType.entityTranslucent(texture);
		ResourceLocation rimTexture = Effective.id("textures/entity/splash/splash_rim_" + Mth.clamp(frame, 0, MAX_FRAME) + ".png");
		RenderType rimLayer = RenderType.entityTranslucent(rimTexture);

		// splash matrices
		PoseStack modelMatrix = getMatrixStackFromCamera(camera, tickDelta);
		modelMatrix.scale(widthMultiplier * multiplier.x(), -heightMultiplier * multiplier.y(), widthMultiplier * multiplier.z());
		modelMatrix.translate(0, -1, 0);
		PoseStack modelBottomMatrix = getMatrixStackFromCamera(camera, tickDelta);
		modelBottomMatrix.scale(widthMultiplier * multiplier.x(), heightMultiplier * multiplier.y(), widthMultiplier * multiplier.z());
		modelBottomMatrix.translate(0, 0.001, 0);

		// splash bottom matrices
		float splashRimScaleOffset = 0.0001f;
		PoseStack modelRimMatrix = getMatrixStackFromCamera(camera, tickDelta);
		modelRimMatrix.scale(widthMultiplier * multiplier.x() + splashRimScaleOffset, -heightMultiplier * multiplier.y() - splashRimScaleOffset, widthMultiplier * multiplier.z() + splashRimScaleOffset);
		modelRimMatrix.translate(0, -1.001, 0);
		PoseStack modelRimBottomMatrix = getMatrixStackFromCamera(camera, tickDelta);
		modelRimBottomMatrix.scale(widthMultiplier * multiplier.x() + splashRimScaleOffset, heightMultiplier * multiplier.y() + splashRimScaleOffset, widthMultiplier * multiplier.z() + splashRimScaleOffset);
		modelRimBottomMatrix.translate(0, 0.002, 0);

		int light = this.getLightColor(tickDelta);
		int rimLight = LightTexture.FULL_BRIGHT;
		float redAndGreenRender = Math.min(1, redAndGreen + level.getBrightness(LightLayer.BLOCK, pos) / 15f);

		MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();

		VertexConsumer modelConsumer = immediate.getBuffer(layer);
		this.waveModel.renderToBuffer(modelMatrix, modelConsumer, light, OverlayTexture.NO_OVERLAY, r, g, b, 0.9f);
		this.waveBottomModel.renderToBuffer(modelBottomMatrix, modelConsumer, light, OverlayTexture.NO_OVERLAY, r, g, b, 0.9f);

		VertexConsumer rimModelConsumer = immediate.getBuffer(rimLayer);
		this.waveRimModel.renderToBuffer(modelRimMatrix, rimModelConsumer, rimLight, OverlayTexture.NO_OVERLAY, redAndGreenRender, redAndGreenRender, blue, 1.0f);
		this.waveBottomRimModel.renderToBuffer(modelRimBottomMatrix, rimModelConsumer, rimLight, OverlayTexture.NO_OVERLAY, redAndGreenRender, redAndGreenRender, blue, 1.0f);

		immediate.endBatch();
	}

	private void drawSplash(int frame, Camera camera, float tickDelta) {
		drawSplash(frame, camera, tickDelta, new Vector3f(1, 1, 1));
	}

	private PoseStack getMatrixStackFromCamera(Camera camera, float tickDelta) {
		Vec3 cameraPos = camera.getPosition();
		float x = (float) (Mth.lerp(tickDelta, this.xo, this.x) - cameraPos.x());
		float y = (float) (Mth.lerp(tickDelta, this.yo, this.y) - cameraPos.y());
		float z = (float) (Mth.lerp(tickDelta, this.zo, this.z) - cameraPos.z());

		PoseStack matrixStack = new PoseStack();
		matrixStack.translate(x, y, z);
		return matrixStack;
	}

	@Override
	public void tick() {
		if (this.widthMultiplier == 0f) {
			this.remove();
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
				this.level.addParticle(Effective.GLOW_DROPLET, this.x + (this.random.nextGaussian() * this.widthMultiplier / 10f), this.y, this.z + (this.random.nextGaussian() * this.widthMultiplier / 10f), random.nextGaussian() / 10f * this.widthMultiplier / 2.5f, random.nextFloat() / 10f + this.heightMultiplier / 2.8f, random.nextGaussian() / 10f * this.widthMultiplier / 2.5f);
			}
		} else if (this.age == wave2Start) {
			for (int i = 0; i < this.widthMultiplier * 5f; i++) {
				this.level.addParticle(Effective.GLOW_DROPLET, this.x + (this.random.nextGaussian() * this.widthMultiplier / 10f * .5f), this.y, this.z + (this.random.nextGaussian() * this.widthMultiplier / 10f * .5f), random.nextGaussian() / 10f * this.widthMultiplier / 5f, random.nextFloat() / 10f + this.heightMultiplier / 2.2f, random.nextGaussian() / 10f * this.widthMultiplier / 5f);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		public DefaultFactory(SpriteSet spriteProvider) {
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			GlowSplashParticle instance = new GlowSplashParticle(world, x, y, z);
			if (parameters instanceof SplashParticleType splashParameters && splashParameters.initialData != null) {
				final float width = (float) splashParameters.initialData.width() * 2;
				instance.widthMultiplier = width;
				instance.heightMultiplier = (float) splashParameters.initialData.yd() * width;
				instance.wave1End = 10 + Math.round(width * 1.2f);
				instance.wave2Start = 6 + Math.round(width * 0.7f);
				instance.wave2End = 20 + Math.round(width * 2.4f);
			}
			return instance;
		}
	}
}
