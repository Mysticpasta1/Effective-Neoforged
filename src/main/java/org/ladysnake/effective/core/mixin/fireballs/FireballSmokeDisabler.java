package org.ladysnake.effective.core.mixin.fireballs;

import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractHurtingProjectile.class)
public class FireballSmokeDisabler {
//	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;ZDDDDDD)V", ordinal = 1)) TODO: Fix later. For some reason this particular method is not decompling correclty for me. Waterpicker
//	private void effective$disableSmokeParticles(Level world, ParticleOptions particleEffect, double x, double y, double z, double velX, double velY, double velZ, Operation<Void> voidOperation) {
//		if ((!EffectiveConfig.improvedFireballs && (Object) this instanceof Fireball) || (!EffectiveConfig.improvedDragonFireballsAndBreath && (Object) this instanceof DragonFireball)) {
//			voidOperation.call(world, particleEffect, x, y, z, velX, velY, velZ);
//		}
//	}
}
