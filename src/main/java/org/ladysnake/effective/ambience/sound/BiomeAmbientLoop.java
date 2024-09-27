package org.ladysnake.effective.ambience.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.ladysnake.effective.core.EffectiveConfig;

public class BiomeAmbientLoop extends AbstractTickableSoundInstance {
	private static final int TRANSITION_TIME = 100;
	private final LocalPlayer player;
	private int transitionTimer;
	private final AmbientCondition ambientConditions;

	public BiomeAmbientLoop(LocalPlayer player, SoundEvent ambientSound, AmbientCondition ambientConditions) {
		super(ambientSound, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
		this.player = player;
		this.looping = true;
		this.delay = 0;
		this.volume = 0.001F;
		this.relative = true;
		this.ambientConditions = ambientConditions;
	}

	@Override
	public void tick() {
		final float windVolume = EffectiveConfig.windAmbienceVolume / 100f;
		final float waterVolume = EffectiveConfig.waterAmbienceVolume / 100f;
		final float foliageVolume = EffectiveConfig.foliageAmbienceVolume / 100f;
		final float animalVolume = EffectiveConfig.animalAmbienceVolume / 100f;

		final float volumeAdjustor = switch (this.ambientConditions.type()) {
			case WIND -> windVolume;
			case ANIMAL -> animalVolume;
			case FOLIAGE -> foliageVolume;
			case WATER -> waterVolume;
		};

		ClientLevel world = Minecraft.getInstance().level;
		if (world != null && !this.player.isRemoved() && !this.player.isUnderWater() && this.transitionTimer >= 0 && volumeAdjustor > 0) {
			this.transitionTimer = Math.min(this.transitionTimer + (this.ambientConditions.predicate().shouldPlay(this.player.level(), this.player.blockPosition(), this.player) ? 1 : -1), TRANSITION_TIME);
			this.volume = Mth.clamp((float) this.transitionTimer / (float) TRANSITION_TIME, 0.0F, volumeAdjustor);
		} else {
			this.stop();
		}
	}
}
