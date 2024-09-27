package org.ladysnake.effective.ambience.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import org.ladysnake.effective.ambience.EffectiveAmbience;
import org.ladysnake.effective.ambience.sound.AmbientCondition;
import org.ladysnake.effective.ambience.sound.BiomeAmbientLoop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class AmbienceClientWorldMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "tick", at = @At(value = "HEAD"))
	private void effective$playAmbience(CallbackInfo ci) {
		LocalPlayer clientPlayerEntity = minecraft.player;
		if (clientPlayerEntity != null) {
			for (AmbientCondition ambientCondition : EffectiveAmbience.AMBIENT_CONDITIONS) {
				if (ambientCondition.predicate().shouldPlay(minecraft.level, minecraft.player.blockPosition(), minecraft.player)) {
					boolean allow = true;
					for (TickableSoundInstance tickingSound : minecraft.getSoundManager().soundEngine.tickingSounds) {
						if (tickingSound.getLocation().equals(ambientCondition.event().getLocation())) {
							allow = false;
							break;
						}
					}
					if (allow) {
						minecraft.getSoundManager().play(new BiomeAmbientLoop(clientPlayerEntity, ambientCondition.event(), ambientCondition));
					}
				}
			}
		}
	}
}
