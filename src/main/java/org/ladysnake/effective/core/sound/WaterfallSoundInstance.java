package org.ladysnake.effective.core.sound;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.ladysnake.effective.core.EffectiveConfig;

public class WaterfallSoundInstance extends DistancedSoundInstance {
	public WaterfallSoundInstance(SoundEvent soundEvent, SoundSource soundCategory, float pitch, BlockPos blockPos, float maxDistance) {
		super(soundEvent, soundCategory, pitch, blockPos, maxDistance);
	}

	public static WaterfallSoundInstance ambient(SoundEvent soundEvent, float pitch, BlockPos blockPos, float maxDistance) {
		return new WaterfallSoundInstance(soundEvent, SoundSource.AMBIENT, pitch, blockPos, maxDistance);
	}

	@Override
	public void tick() {
		super.tick();
		final float volumeAdjustor = (EffectiveConfig.cascadeSoundsVolume / 100.f) * 2.5f;
		this.volume = Mth.clampedLerp(0f, volumeAdjustor, this.volume);
	}
}
