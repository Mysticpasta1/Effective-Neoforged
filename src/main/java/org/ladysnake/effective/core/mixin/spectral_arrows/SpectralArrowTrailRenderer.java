package org.ladysnake.effective.core.mixin.spectral_arrows;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.particle.contracts.ColoredParticleInitialData;
import org.ladysnake.effective.core.utils.PositionTrackedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;

import java.awt.*;
import java.util.List;

@Mixin(ArrowRenderer.class)
public abstract class SpectralArrowTrailRenderer<T extends AbstractArrow> extends EntityRenderer<T> {
	private static final RenderType TRAIL_TYPE = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createCachedToken(Effective.id("textures/vfx/light_trail.png")));

	protected SpectralArrowTrailRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	public RenderType getTrailRenderType() {
		return TRAIL_TYPE;
	}

	// spectral arrow trail and twinkle
	@Inject(method = "render", at = @At("TAIL"))
	public void render(T entity, float entityYaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, CallbackInfo ci) {
		// new render
		if (EffectiveConfig.spectralArrowTrails != EffectiveConfig.TrailOptions.NONE && entity instanceof SpectralArrow spectralArrowEntity && !spectralArrowEntity.isInvisible()) {
			ColoredParticleInitialData data = new ColoredParticleInitialData(0xFFFF77);

			// trail
			if (EffectiveConfig.spectralArrowTrails == EffectiveConfig.TrailOptions.BOTH || EffectiveConfig.spectralArrowTrails == EffectiveConfig.TrailOptions.TWINKLE) {
				matrixStack.pushPose();
				List<TrailPoint> positions = ((PositionTrackedEntity) spectralArrowEntity).getPastPositions();
				VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setRenderType(getTrailRenderType());

				float size = 0.15f;
				float alpha = 1f;

				float x = (float) Mth.lerp(tickDelta, spectralArrowEntity.xo, spectralArrowEntity.getX());
				float y = (float) Mth.lerp(tickDelta, spectralArrowEntity.yo, spectralArrowEntity.getY());
				float z = (float) Mth.lerp(tickDelta, spectralArrowEntity.zo, spectralArrowEntity.getZ());

				matrixStack.translate(-x, -y, -z);
				builder.setColor(new Color(data.color))
					.setAlpha(alpha)
					.renderTrail(matrixStack,
						positions,
						f -> Mth.sqrt(f) * size,
						f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (alpha * f) - 0.1f)))
					)
					.renderTrail(matrixStack,
						positions,
						f -> (Mth.sqrt(f) * size) / 1.5f,
						f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (((alpha * f) / 1.5f) - 0.1f))))
					);

				matrixStack.popPose();
			}

			// twinkles
			if (EffectiveConfig.spectralArrowTrails == EffectiveConfig.TrailOptions.BOTH || EffectiveConfig.spectralArrowTrails == EffectiveConfig.TrailOptions.TWINKLE) {
				if ((spectralArrowEntity.level().getRandom().nextInt(100) + 1) <= 5 && !Minecraft.getInstance().isPaused()) {
					float spreadDivider = 4f;
					WorldParticleBuilder.create(Effective.ALLAY_TWINKLE)
						.enableForcedSpawn()
						.setColorData(ColorParticleData.create(new Color(data.color), new Color(data.color)).build())
						.setTransparencyData(GenericParticleData.create(0.9f).build())
						.setScaleData(GenericParticleData.create(0.06f).build())
						.setLifetime(15)
						.setMotion(0, 0.05f, 0)
						.spawn(spectralArrowEntity.level(), spectralArrowEntity.getLightProbePosition(Minecraft.getInstance().getFrameTime()).x + spectralArrowEntity.level().getRandom().nextGaussian() / spreadDivider, spectralArrowEntity.getLightProbePosition(Minecraft.getInstance().getFrameTime()).y - 0.2f + spectralArrowEntity.level().getRandom().nextGaussian() / spreadDivider, spectralArrowEntity.getLightProbePosition(Minecraft.getInstance().getFrameTime()).z + spectralArrowEntity.level().getRandom().nextGaussian() / spreadDivider);
				}
			}
		}
	}

}
