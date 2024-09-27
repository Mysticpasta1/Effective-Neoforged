package org.ladysnake.effective.core.particle.types;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import org.ladysnake.effective.core.particle.contracts.SplashParticleInitialData;

@Environment(EnvType.CLIENT)
public class SplashParticleType extends SimpleParticleType {
	public SplashParticleInitialData initialData;

	public SplashParticleType(boolean alwaysShow) {
		super(alwaysShow);
	}

	public ParticleOptions setData(SplashParticleInitialData target) {
		this.initialData = target;
		return this;
	}
}
