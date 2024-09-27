package org.ladysnake.effective.core.mixin.screenshake;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingAttackingPhase;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

@Mixin(DragonSittingAttackingPhase.class)
public abstract class EnderDragonRoarScreenshakeAdder extends AbstractDragonSittingPhase {
	public EnderDragonRoarScreenshakeAdder(EnderDragon enderDragonEntity) {
		super(enderDragonEntity);
	}

	@Inject(method = "doClientTick", at = @At("HEAD"))
	public void clientTick(CallbackInfo ci) {
		if (EffectiveConfig.dragonScreenShake) {
			ScreenshakeInstance roarScreenShake = new PositionedScreenshakeInstance(60, this.dragon.position(), 20f, 25f, Easing.CIRC_IN_OUT).setIntensity(0.0f, EffectiveConfig.screenShakeIntensity, 0.0f);
			ScreenshakeHandler.addScreenshake(roarScreenShake);
		}
	}
}
