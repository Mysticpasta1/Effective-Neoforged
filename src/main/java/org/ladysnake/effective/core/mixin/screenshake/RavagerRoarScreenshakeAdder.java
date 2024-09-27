package org.ladysnake.effective.core.mixin.screenshake;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
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

@Mixin(Ravager.class)
public class RavagerRoarScreenshakeAdder extends Monster {
	protected RavagerRoarScreenshakeAdder(EntityType<? extends Monster> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "roar", at = @At("HEAD"))
	public void roar(CallbackInfo ci) {
		if (EffectiveConfig.ravagerScreenShake) {
			ScreenshakeInstance roarScreenShake = new PositionedScreenshakeInstance(10, this.position(), 20f, 25f, Easing.CIRC_IN_OUT).setIntensity(0.0f, EffectiveConfig.screenShakeIntensity, 0.0f);
			ScreenshakeHandler.addScreenshake(roarScreenShake);
		}
	}
}
