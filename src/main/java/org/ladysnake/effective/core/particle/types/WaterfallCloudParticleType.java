package org.ladysnake.effective.core.particle.types;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import org.ladysnake.effective.core.particle.WaterfallCloudParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;

@Environment(EnvType.CLIENT)
public class WaterfallCloudParticleType extends LodestoneWorldParticleType {
	public record Factory(SpriteSet sprite) implements ParticleProvider<WorldParticleOptions> {
		@Override
		public Particle createParticle(WorldParticleOptions data, ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
			return new WaterfallCloudParticle(world, data, (ParticleEngine.MutableSpriteSet) sprite, x, y, z, mx, my, mz);
		}
	}
}
