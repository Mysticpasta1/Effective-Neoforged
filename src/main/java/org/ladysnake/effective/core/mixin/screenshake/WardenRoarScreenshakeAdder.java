package org.ladysnake.effective.core.mixin.screenshake;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

@Mixin(Warden.class)
public class WardenRoarScreenshakeAdder extends Monster {
	public ScreenshakeInstance roarScreenShake;
	public int ticksSinceAnimationStart = 0;

	protected WardenRoarScreenshakeAdder(EntityType<? extends Monster> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (EffectiveConfig.wardenScreenShake && this.getPose().equals(Pose.ROARING)) {
			ticksSinceAnimationStart++;
			if (roarScreenShake == null) {
				if (ticksSinceAnimationStart >= 20) {
					roarScreenShake = new PositionedScreenshakeInstance(70, this.position(), 20f, 25f, Easing.CIRC_IN_OUT).setIntensity(0.0f, EffectiveConfig.screenShakeIntensity, 0.0f);
					ScreenshakeHandler.addScreenshake(roarScreenShake);
				}
			}
		} else {
			roarScreenShake = null;
			ticksSinceAnimationStart = 0;
		}
	}


}
