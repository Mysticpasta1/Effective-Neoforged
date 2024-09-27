package org.ladysnake.effective.core.mixin.water;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.world.SplashSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntitySplashMixin {
	@Shadow
	private Level level;

	@Inject(method = "doWaterSplashEffect", at = @At("TAIL"))
	protected void onSwimmingStart(CallbackInfo callbackInfo) {
		if (this.level.isClientSide && EffectiveConfig.splashes) {
			SplashSpawner.trySpawnSplash((Entity) (Object) this);
		}
	}
}
