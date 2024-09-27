package org.ladysnake.effective.core.particle.types;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.effective.core.particle.AllayTwinkleParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;

public class AllayTwinkleParticleType extends LodestoneWorldParticleType {
	public static class Factory implements ParticleProvider<WorldParticleOptions> {
		private final SpriteSet sprite;

		public Factory(SpriteSet sprite) {
			this.sprite = sprite;
		}


		@Nullable
		@Override
		public Particle createParticle(WorldParticleOptions data, ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
			return new AllayTwinkleParticle(world, data, (ParticleEngine.MutableSpriteSet) sprite, x, y, z, mx, my, mz);
		}
	}
}
