package org.ladysnake.effective.cosmetics.particle.aura;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.concurrent.ThreadLocalRandom;

public class PrismarineAuraParticle extends PrismarineCrystalParticle {
	public PrismarineAuraParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd, spriteProvider);

		this.setPos(this.x + TwilightLegacyFireflyParticle.getWanderingDistance(this.random), this.y + random.nextFloat() * 2d, this.z + TwilightLegacyFireflyParticle.getWanderingDistance(this.random));

		this.lifetime = ThreadLocalRandom.current().nextInt(100, 400);
	}

	public void tick() {
		if (this.age++ < this.lifetime) {
			this.alpha = Math.min(1f, this.alpha + 0.1f);
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.move(this.xd, this.yd, this.zd);

		if (this.age >= this.lifetime) {
			this.alpha = Math.max(0f, this.alpha - 0.1f);

			if (this.alpha <= 0f) {
				this.remove();
			}
		}

		this.rCol = 0.8f + (float) Math.sin(this.age / 10f) * 0.2f;
//        this.blue = 0.9f + (float) Math.cos(this.age/10f) * 0.1f;

		this.oRoll = this.roll;
		if (this.onGround) {
			this.xd = 0;
			this.yd = 0;
			this.zd = 0;
		}

		if (this.yd != 0) {
			this.roll += Math.PI * Math.sin(rotationFactor * this.age) / 2;
		}
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new PrismarineAuraParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

}
