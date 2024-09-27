package org.ladysnake.effective.core.mixin.glowsquids;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.effective.core.Effective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class JebGlowSquidRainbowRenderer {
	@Nullable
	@Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true)
	protected void getRenderLayer(LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<RenderType> cir) {
		if (entity instanceof GlowSquid) {
			RenderType baseLayer = cir.getReturnValue();
			if (entity.hasCustomName() && "jeb_".equals(entity.getName().getString())) {
				cir.setReturnValue(baseLayer == null ? null : Effective.RAINBOW_SHADER.getRenderLayer(baseLayer));
			}
		}
	}
}
