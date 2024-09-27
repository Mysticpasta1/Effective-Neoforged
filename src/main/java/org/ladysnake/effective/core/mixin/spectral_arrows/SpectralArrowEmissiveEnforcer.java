package org.ladysnake.effective.core.mixin.spectral_arrows;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.SpectralArrow;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class SpectralArrowEmissiveEnforcer<T extends Entity> {
	@Inject(method = "getBlockLightLevel", at = @At("RETURN"), cancellable = true)
	protected void getBlockLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (EffectiveConfig.spectralArrowTrails != EffectiveConfig.TrailOptions.NONE && entity instanceof SpectralArrow spectralArrowEntity) {
			cir.setReturnValue(15);
		}
	}
}
