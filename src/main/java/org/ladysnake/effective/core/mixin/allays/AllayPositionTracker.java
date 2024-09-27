package org.ladysnake.effective.core.mixin.allays;


import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
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

@Mixin(Allay.class)
public class AllayPositionTracker extends PathfinderMob implements PositionTrackedEntity {
	@Unique
	public final TrailPointBuilder trailPointBuilder = TrailPointBuilder.create(16);

	protected AllayPositionTracker(net.minecraft.world.entity.EntityType<? extends PathfinderMob> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (EffectiveConfig.allayTrails != EffectiveConfig.TrailOptions.NONE) {
			trailPointBuilder.addTrailPoint(this.position().add(0, .2, 0));
			trailPointBuilder.tickTrailPoints();
		}
	}

	@Override
	public List<TrailPoint> getPastPositions() {
		return trailPointBuilder.getTrailPoints();
	}
}
