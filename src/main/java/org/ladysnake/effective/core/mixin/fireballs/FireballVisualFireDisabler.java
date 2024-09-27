package org.ladysnake.effective.core.mixin.fireballs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Fireball;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class FireballVisualFireDisabler {
	@Inject(method = "displayFireAnimation", at = @At("HEAD"), cancellable = true)
	protected void doesRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
		if (EffectiveConfig.improvedFireballs && (Object) this instanceof Fireball) {
			cir.setReturnValue(false);
		}
	}
}
