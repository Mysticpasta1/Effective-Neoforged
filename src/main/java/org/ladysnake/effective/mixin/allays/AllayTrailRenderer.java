package org.ladysnake.effective.mixin.allays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.AllayRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.allay.Allay;
import org.joml.Vector4f;
import org.ladysnake.effective.Effective;
import org.ladysnake.effective.EffectiveConfig;
import org.ladysnake.effective.EffectiveUtils;
import org.ladysnake.effective.particle.contracts.ColoredParticleInitialData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.setup.LodestoneRenderLayers;
import team.lodestar.lodestone.systems.rendering.PositionTrackedEntity;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;

import java.awt.*;
import java.util.ArrayList;

import static team.lodestar.lodestone.handlers.RenderHandler.DELAYED_RENDER;

@Mixin(AllayRenderer.class)
public abstract class AllayTrailRenderer<M extends EntityModel<Allay>> extends EntityRenderer<Allay> {
	private static final Identifier LIGHT_TRAIL = new Identifier(Effective.MODID, "textures/vfx/light_trail.png");
	private static final RenderLayer LIGHT_TYPE = LodestoneRenderLayers.ADDITIVE_TEXTURE.apply(LIGHT_TRAIL);

	protected AllayTrailRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public void render(Allay allayEntity, float entityYaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
		super.render(allayEntity, entityYaw, tickDelta, matrixStack, vertexConsumerProvider, light);
		// new render
		if (EffectiveConfig.allayTrails != EffectiveConfig.TrailOptions.NONE && !allayEntity.isInvisible()) {
			ColoredParticleInitialData data = new ColoredParticleInitialData(allayEntity.getUuid().hashCode() % 2 == 0 && EffectiveConfig.goldenAllays ? 0xFFC200 : 0x22CFFF);

			// trail
			if (EffectiveConfig.allayTrails == EffectiveConfig.TrailOptions.BOTH || EffectiveConfig.allayTrails == EffectiveConfig.TrailOptions.TRAIL) {
				matrixStack.push();
				ArrayList<Vec3d> positions = new ArrayList<>(((PositionTrackedEntity) allayEntity).getPastPositions());
				VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();

				float size = 0.2f;
				float alpha = 1f;

				float x = (float) MathHelper.lerp(tickDelta, allayEntity.prevX, allayEntity.getX());
				float y = (float) MathHelper.lerp(tickDelta, allayEntity.prevY, allayEntity.getY());
				float z = (float) MathHelper.lerp(tickDelta, allayEntity.prevZ, allayEntity.getZ());

				builder.setColor(new Color(data.color)).setOffset(-x, -y, -z)
					.setAlpha(alpha)
					.renderTrail(
						DELAYED_RENDER.getBuffer(LIGHT_TYPE),
						matrixStack,
						positions.stream()
							.map(p -> new Vector4f((float) p.x, (float) p.y, (float) p.z, 1))
							.toList(),
						f -> MathHelper.sqrt(f) * size,
						f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (alpha * f) - 0.1f)))
					)
					.renderTrail(
						DELAYED_RENDER.getBuffer(LIGHT_TYPE),
						matrixStack,
						positions.stream()
							.map(p -> new Vector4f((float) p.x, (float) p.y, (float) p.z, 1))
							.toList(),
						f -> (MathHelper.sqrt(f) * size) / 1.5f,
						f -> builder.setAlpha((float) Math.cbrt(Math.max(0, (((alpha * f) / 1.5f) - 0.1f))))
					);

				matrixStack.pop();
			}

			// twinkles
			if (EffectiveConfig.allayTrails == EffectiveConfig.TrailOptions.BOTH || EffectiveConfig.allayTrails == EffectiveConfig.TrailOptions.TWINKLE) {
				var probe = allayEntity.getLightProbePosition(Minecraft.getInstance().getFrameTime());

				if ((allayEntity.getRandom().nextInt(100) + 1) <= 5 && EffectiveUtils.isGoingFast(allayEntity) && !Minecraft.getInstance().isPaused()) {
					float spreadDivider = 4f;
					WorldParticleBuilder.create(Effective.ALLAY_TWINKLE)
						.setColorData(ColorParticleData.create(new Color(data.color), new Color(data.color)).build())
						.setTransparencyData(GenericParticleData.create(0.9f).build())
						.setScaleData(GenericParticleData.create(0.12f).build())
						.setLifetime(15)
						.setMotion(0, 0.05f, 0)
						.spawn(allayEntity.level(), probe.add(allayEntity.getRandom().nextGaussian() / spreadDivider, -0.2f + allayEntity.getRandom().nextGaussian() / spreadDivider, allayEntity.getRandom().nextGaussian() / spreadDivider));
				}
			}
		}
	}

}
