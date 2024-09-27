package org.ladysnake.effective.cosmetics.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.logging.Level;

public record IlluminationData(SimpleParticleType illuminationType,
							   BiPredicate<Level, BlockPos> locationSpawnPredicate,
							   Supplier<Float> chanceSupplier) {

	public boolean shouldAddParticle(RandomSource random) {
		float chance = chanceSupplier.get();
		if (chance <= 0f) return false;
		float density = 1f;
		return random.nextFloat() <= chance * density;
	}
}
