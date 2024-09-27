package org.ladysnake.effective.core.mixin.fireballs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingPhase;
import net.minecraft.world.level.Level;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;

import java.awt.*;

@Mixin(DragonLandingPhase.class)
public class LandingFlamingRendererSwapper {
	@WrapOperation(method = "doClientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private void effective$swapDragonBreathParticles(Level world, ParticleOptions parameters, double x, double y, double z, double xd, double yd, double zd, Operation<Void> voidOperation) {
		if (EffectiveConfig.improvedDragonFireballsAndBreath) {
			WorldParticleBuilder.create(Effective.DRAGON_BREATH)
				.enableForcedSpawn()
				.setSpinData(SpinParticleData.create((float) (world.random.nextGaussian() / 5f)).build())
				.setScaleData(GenericParticleData.create(1f, 0f).setEasing(Easing.CIRC_OUT).build())
				.setTransparencyData(GenericParticleData.create(0.2f).build())
				.setColorData(
					ColorParticleData.create(new Color(0xD21EFF), new Color(0x7800FF))
						.setEasing(Easing.CIRC_OUT)
						.build()
				)
				.enableNoClip()
				.setLifetime(40)
				.setMotion((float) xd, (float) yd + 0.05f, (float) zd)
				.spawn(world, x, y, z);
		} else {
			voidOperation.call(world, parameters, x, y, z, xd, yd, zd);
		}
	}
}
