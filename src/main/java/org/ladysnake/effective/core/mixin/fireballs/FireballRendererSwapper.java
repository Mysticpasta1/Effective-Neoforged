package org.ladysnake.effective.core.mixin.fireballs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
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

@Mixin(ThrownItemRenderer.class)
public class FireballRendererSwapper<T extends Entity & ItemSupplier> {
	@Inject(method = "Lnet/minecraft/client/renderer/entity/ThrownItemRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
	public void render(T entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
		if (EffectiveConfig.improvedFireballs && entity instanceof Fireball fireballEntity) {
			float x = (float) (Mth.lerp(tickDelta, fireballEntity.xOld, fireballEntity.getX()));
			float y = (float) (Mth.lerp(tickDelta, fireballEntity.yOld, fireballEntity.getY()));
			float z = (float) (Mth.lerp(tickDelta, fireballEntity.zOld, fireballEntity.getZ()));
			float scale = 1f;
			if (entity instanceof SmallFireball) {
				scale = 0.3f;
			} else if (entity instanceof LargeFireball) {
				scale = 0.8f;
			}

			for (int i = 0; i < 2; i++) {
				WorldParticleBuilder.create(Effective.FLAME)
					.enableForcedSpawn()
					.setSpinData(SpinParticleData.create((float) (fireballEntity.level().random.nextGaussian() / 5f)).build())
					.setScaleData(GenericParticleData.create(scale, 0f).setEasing(Easing.CIRC_OUT).build())
					.setTransparencyData(GenericParticleData.create(1f).build())
					.setColorData(
						ColorParticleData.create(new Color(0xFF3C00), new Color(0xFFCB00))
							.setEasing(Easing.CIRC_OUT)
							.build()
					)
					.enableNoClip()
					.setLifetime(20)
					.spawn(fireballEntity.level(), x + fireballEntity.level().random.nextGaussian() / 20f, y + (fireballEntity.getBbHeight() / 2f) + fireballEntity.level().random.nextGaussian() / 20f, z + fireballEntity.level().random.nextGaussian() / 20f);
			}

			ci.cancel();
		}
	}
}
