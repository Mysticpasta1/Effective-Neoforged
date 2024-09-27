package org.ladysnake.effective.core.sound;


import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class DistancedSoundInstance extends SimpleSoundInstance implements TickableSoundInstance {
	private static final RandomSource RANDOM = RandomSource.create();
	private final float maxDistance;

	public DistancedSoundInstance(SoundEvent soundEvent, SoundSource soundCategory, float pitch, BlockPos blockPos, float maxDistance) {
		super(soundEvent, soundCategory, 0.0f, pitch, RANDOM, blockPos);
		this.maxDistance = maxDistance;
		this.looping = false;
	}

	public static DistancedSoundInstance ambient(SoundEvent soundEvent, float pitch, BlockPos blockPos, float maxDistance) {
		return new DistancedSoundInstance(soundEvent, SoundSource.AMBIENT, pitch, blockPos, maxDistance);
	}

	@Override
	public Attenuation getAttenuation() {
		return Attenuation.NONE;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public void tick() {
		if (Minecraft.getInstance().player != null) {
			float distance = Mth.sqrt((float) Minecraft.getInstance().player.position().distanceToSqr(this.x, this.y, this.z));
			this.volume = Mth.clampedLerp(0f, 1.0f, 1.0f - distance / this.maxDistance);
		}
	}
}
