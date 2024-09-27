package org.ladysnake.effective.core.mixin.allays;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.allay.Allay;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.particle.contracts.ColoredParticleInitialData;
import org.ladysnake.effective.core.utils.EffectiveUtils;
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

@Mixin(net.minecraft.client.renderer.entity.LivingEntityRenderer.class)
public abstract class AllayTrailRenderer<T extends net.minecraft.world.entity.LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
	private static final RenderType TRAIL_TYPE = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createCachedToken(Effective.id("textures/vfx/light_trail.png")));

	protected AllayTrailRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	public RenderType getTrailRenderType() {
		return TRAIL_TYPE;
	}

	// allay trail and twinkle
	@Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("TAIL"))
	public void render(T livingEntity, float entityYaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, CallbackInfo ci) {
		// new render
		if (EffectiveConfig.allayTrails != EffectiveConfig.TrailOptions.NONE && livingEntity instanceof Allay allayEntity && !allayEntity.isInvisible()) {
			ColoredParticleInitialData data = new ColoredParticleInitialData(allayEntity.getUUID().hashCode() % 2 == 0 && EffectiveConfig.goldenAllays ? 0xFFC200 : 0x22CFFF);

			// trail
			if (EffectiveConfig.allayTrails == EffectiveConfig.TrailOptions.BOTH || EffectiveConfig.allayTrails == EffectiveConfig.TrailOptions.TRAIL) {
				matrixStack.pushPose();
				List<TrailPoint> positions = ((PositionTrackedEntity) allayEntity).getPastPositions();
				VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setRenderType(getTrailRenderType());

				float size = 0.2f;
				float alpha = 1f;

				float x = (float) Mth.lerp(tickDelta, allayEntity.xo, allayEntity.getX());
				float y = (float) Mth.lerp(tickDelta, allayEntity.yo, allayEntity.getY());
				float z = (float) Mth.lerp(tickDelta, allayEntity.zo, allayEntity.getZ());

				matrixStack.translate(-x, -y, -z);
				builder.setColor(new Color(data.color))
					.setAlpha(alpha)
					.renderTrail(matrixStack,
						positions,
						f -> Mth.sqrt(f) * size,
						f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (alpha * f) - 0.1f)))
					)
					.setAlpha(alpha)
					.renderTrail(matrixStack,
						positions,
						f -> (Mth.sqrt(f) * size) / 1.5f,
						f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (((alpha * f) / 1.5f) - 0.1f))))
					);

				matrixStack.popPose();
			}

			// twinkles
			if (EffectiveConfig.allayTrails == EffectiveConfig.TrailOptions.BOTH || EffectiveConfig.allayTrails == EffectiveConfig.TrailOptions.TWINKLE) {
				if ((allayEntity.getRandom().nextInt(100) + 1) <= 5 && EffectiveUtils.isGoingFast(allayEntity) && !Minecraft.getInstance().isPaused()) {
					float spreadDivider = 4f;
					WorldParticleBuilder.create(Effective.ALLAY_TWINKLE)
						.enableForcedSpawn()
						.setColorData(ColorParticleData.create(new Color(data.color), new Color(data.color)).build())
						.setTransparencyData(GenericParticleData.create(0.9f).build())
						.setScaleData(GenericParticleData.create(0.12f).build())
						.setLifetime(15)
						.setMotion(0, 0.05f, 0)
						.spawn(allayEntity.level(), allayEntity.getLightProbePosition(Minecraft.getInstance().getFrameTime()).x + allayEntity.getRandom().nextGaussian() / spreadDivider, allayEntity.getLightProbePosition(Minecraft.getInstance().getFrameTime()).y - 0.2f + allayEntity.getRandom().nextGaussian() / spreadDivider, allayEntity.getLightProbePosition(Minecraft.getInstance().getFrameTime()).z + allayEntity.getRandom().nextGaussian() / spreadDivider);
				}
			}
		}
	}

}
