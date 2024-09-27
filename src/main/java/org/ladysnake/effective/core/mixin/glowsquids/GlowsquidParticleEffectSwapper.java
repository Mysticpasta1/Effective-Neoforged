package org.ladysnake.effective.core.mixin.glowsquids;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.level.Level;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;

import java.awt.*;

@Mixin(GlowSquid.class)
public class GlowsquidParticleEffectSwapper extends Squid {
	public GlowsquidParticleEffectSwapper(EntityType<? extends Squid> entityType, Level world) {
		super(entityType, world);
	}

	@WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private void effective$swapGlowsquidParticles(Level world, ParticleOptions particleEffect, double x, double y, double z, double velX, double velY, double velZ, Operation<Void> voidOperation) {
		if (EffectiveConfig.improvedGlowSquidParticles) {
			if (random.nextInt(5) == 0) {
				float spreadDivider = 1.2f;

				WorldParticleBuilder.create(Effective.ALLAY_TWINKLE)
					.enableForcedSpawn()
					.setColorData(ColorParticleData.create(new Color(0x00FFAA), new Color(0x51FFFF)).build())
					.setScaleData(GenericParticleData.create(0.01f, .2f + random.nextFloat() / 10f).setEasing(Easing.SINE_OUT).build())
					.setLifetime(40)
					.spawn(this.level(), this.getLightProbePosition(Minecraft.getInstance().getFrameTime()).x + this.getRandom().nextGaussian() / spreadDivider, this.getLightProbePosition(Minecraft.getInstance().getFrameTime()).y - 0.2f + this.getRandom().nextGaussian() / spreadDivider, this.getLightProbePosition(Minecraft.getInstance().getFrameTime()).z + this.getRandom().nextGaussian() / spreadDivider);
			}
		} else {
			voidOperation.call(world, particleEffect, x, y, z, velX, velY, velZ);
		}
	}
}
