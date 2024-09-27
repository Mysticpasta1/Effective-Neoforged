package org.ladysnake.effective.core.mixin.feedbacking;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class FeedbackingParryFireballEffect {
	@Shadow
	public abstract boolean isLocalPlayer();

	@Inject(method = "attack", at = @At("HEAD"))
	public void attack(Entity target, CallbackInfo ci) {
		if (EffectiveConfig.ultrakill && this.isLocalPlayer() && target instanceof AbstractHurtingProjectile) {
			Minecraft.getInstance().player.playNotifySound(Effective.PARRY, SoundSource.PLAYERS, 1.0f, 1.0f);

			Effective.freezeFrames = 5;
		}
	}
}
