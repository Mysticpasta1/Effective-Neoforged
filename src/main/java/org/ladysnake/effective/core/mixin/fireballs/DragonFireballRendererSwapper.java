package org.ladysnake.effective.core.mixin.fireballs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.DragonFireballRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.DragonFireball;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;

import java.awt.*;

@Mixin(DragonFireballRenderer.class)
public class DragonFireballRendererSwapper {
	@Inject(method = "render(Lnet/minecraft/world/entity/projectile/DragonFireball;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
	public void render(DragonFireball dragonFireballEntity, float f, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, CallbackInfo ci) {
		if (EffectiveConfig.improvedDragonFireballsAndBreath) {
			float x = (float) (Mth.lerp(tickDelta, dragonFireballEntity.xOld, dragonFireballEntity.getX()));
			float y = (float) (Mth.lerp(tickDelta, dragonFireballEntity.yOld, dragonFireballEntity.getY()));
			float z = (float) (Mth.lerp(tickDelta, dragonFireballEntity.zOld, dragonFireballEntity.getZ()));
			float scale = 1f;

			for (int i = 0; i < 2; i++) {
				WorldParticleBuilder.create(Effective.DRAGON_BREATH)
					.enableForcedSpawn()
					.setSpinData(SpinParticleData.create((float) (dragonFireballEntity.level().random.nextGaussian() / 5f)).build())
					.setScaleData(GenericParticleData.create(scale, 0f).setEasing(Easing.CIRC_OUT).build())
					.setTransparencyData(GenericParticleData.create(1f).build())
					.setColorData(
						ColorParticleData.create(new Color(0xD21EFF), new Color(0x7800FF))
							.setEasing(Easing.CIRC_OUT)
							.build()
					)
					.enableNoClip()
					.setLifetime(20)
					.spawn(dragonFireballEntity.level(), x + dragonFireballEntity.level().random.nextGaussian() / 20f, y + (dragonFireballEntity.getBbHeight() / 2f) + dragonFireballEntity.level().random.nextGaussian() / 20f, z + dragonFireballEntity.level().random.nextGaussian() / 20f);
			}

			ci.cancel();
		}
	}
}
