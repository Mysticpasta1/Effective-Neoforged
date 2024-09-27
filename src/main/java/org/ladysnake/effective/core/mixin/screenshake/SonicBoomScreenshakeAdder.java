package org.ladysnake.effective.core.mixin.screenshake;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.warden.SonicBoom;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

@Mixin(SonicBoom.class)
public class SonicBoomScreenshakeAdder {
	@Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/warden/Warden;J)V", at = @At("HEAD"))
	protected void keepRunning(ServerLevel serverWorld, Warden wardenEntity, long l, CallbackInfo ci) {
		if (EffectiveConfig.sonicBoomScreenShake && !wardenEntity.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_DELAY)
			&& !wardenEntity.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {
			wardenEntity.getBrain()
				.getMemoryInternal(MemoryModuleType.ATTACK_TARGET)
				.filter(wardenEntity::canTargetEntity)
				.filter(livingEntity -> wardenEntity.closerThan(livingEntity, 15.0, 20.0))
				.ifPresent(livingEntity -> {
					ScreenshakeInstance boomScreenShake = new PositionedScreenshakeInstance(20, wardenEntity.position(), 20f, 25f, Easing.CIRC_IN_OUT).setIntensity(EffectiveConfig.screenShakeIntensity, 0.0f, 0.0f);
					ScreenshakeHandler.addScreenshake(boomScreenShake);
				});
		}
	}
}
