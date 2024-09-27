package org.ladysnake.effective.core.mixin.spectral_arrows;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.utils.PositionTrackedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.List;

@Mixin(SpectralArrow.class)
public abstract class SpectralArrowPositionTrackerAndParticleRemover extends AbstractArrow implements PositionTrackedEntity {
	@Unique
	public final TrailPointBuilder trailPointBuilder = TrailPointBuilder.create(20);

	protected SpectralArrowPositionTrackerAndParticleRemover(EntityType<? extends AbstractArrow> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (EffectiveConfig.spectralArrowTrails != EffectiveConfig.TrailOptions.NONE) {
			Vec3 position = this.getEyePosition(Minecraft.getInstance().getFrameTime()).add(0, -.1f, 0f);
			trailPointBuilder.addTrailPoint(position);
			trailPointBuilder.tickTrailPoints();
		}
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	public void tick(Level world, ParticleOptions parameters, double x, double y, double z, double xd, double yd, double zd, Operation<Void> voidOperation) {
		if (EffectiveConfig.spectralArrowTrails == EffectiveConfig.TrailOptions.NONE) {
			voidOperation.call(world, parameters, x, y, z, xd, yd, zd);
		}
	}

	@Override
	public List<TrailPoint> getPastPositions() {
		return trailPointBuilder.getTrailPoints();
	}
}
