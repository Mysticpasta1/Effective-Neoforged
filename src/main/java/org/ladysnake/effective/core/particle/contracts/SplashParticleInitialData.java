package org.ladysnake.effective.core.particle.contracts;

public record SplashParticleInitialData(double width, double yd) {
	public SplashParticleInitialData(double width, double yd) {
		this.width = width;
		this.yd = Math.abs(yd);
	}
}
