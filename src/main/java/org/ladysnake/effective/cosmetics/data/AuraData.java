package org.ladysnake.effective.cosmetics.data;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

import java.util.function.Supplier;

public record AuraData(SimpleParticleType particle, Supplier<AuraSettings> auraSettingsSupplier) {

	public boolean shouldAddParticle(RandomSource random, int age) {

		AuraSettings settings = auraSettingsSupplier().get();
		if (settings.spawnRate() == 0) return false;
		float rand = random.nextFloat();
		return rand <= settings.spawnRate() && (age % settings.delay() == 0);
	}
}
