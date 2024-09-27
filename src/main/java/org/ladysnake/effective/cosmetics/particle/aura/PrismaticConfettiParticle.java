package org.ladysnake.effective.cosmetics.particle.aura;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.data.PlayerCosmeticData;

import java.util.Objects;

public class PrismaticConfettiParticle extends ConfettiParticle {
	public PrismaticConfettiParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd, spriteProvider);

		Player owner = world.getNearestPlayer(TargetingConditions.forNonCombat().range(1D), this.x, this.y, this.z);

		if (owner != null && owner.getUUID() != null && EffectiveCosmetics.getCosmeticData(owner) != null) {
			PlayerCosmeticData data = Objects.requireNonNull(EffectiveCosmetics.getCosmeticData(owner));
			this.rCol = data.getColor1Red() / 255f;
			this.gCol = data.getColor1Green() / 255f;
			this.bCol = data.getColor1Blue() / 255f;
		} else {
			this.remove();
		}
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new PrismaticConfettiParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

}
