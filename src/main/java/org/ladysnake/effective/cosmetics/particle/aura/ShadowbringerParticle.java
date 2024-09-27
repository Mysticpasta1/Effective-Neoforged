package org.ladysnake.effective.cosmetics.particle.aura;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluids;
import org.ladysnake.effective.core.particle.ChorusPetalParticle;

public class ShadowbringerParticle extends ChorusPetalParticle {

	private final SpriteSet spriteProvider;
	private final float randEffect = random.nextFloat() + 0.5F;
	boolean negateX = random.nextBoolean(), negateZ = random.nextBoolean();

	public ShadowbringerParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd, spriteProvider);

		this.lifetime = 40 + random.nextInt(40);
		this.yd = (0.2 + random.nextFloat()) / 10;
		this.xd = negateX ? -random.nextGaussian() / 50 : random.nextGaussian() / 50;
		this.zd = negateZ ? -random.nextGaussian() / 50 : random.nextGaussian() / 50;
		this.quadSize = (float) (quadSize + (random.nextGaussian() / 12.0));
		this.spriteProvider = spriteProvider;

		this.setSprite(spriteProvider.get(0, 3));
		this.alpha = 0;


		this.setPos(this.x + TwilightLegacyFireflyParticle.getWanderingDistance(this.random), this.y + random.nextFloat() * 1.5, this.z + TwilightLegacyFireflyParticle.getWanderingDistance(this.random));
	}

	public void tick() {
		if (this.age++ < this.lifetime) {
			this.alpha = Math.min(1f, this.alpha + 0.045f);
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		xd = xd * 0.85 + (negateX ? -(Math.sin(age / (2.0 + randEffect)) / 20.0) : Math.sin(age / 3.0) / 20.0);
		zd = zd * 0.85 + (negateZ ? -(Math.sin(age / (2.0 + randEffect)) / 20.0) : Math.sin(age / 3.0) / 20.0);

		this.move(this.xd, this.yd, this.zd);

		if (age > 0 && lifetime > 0) {
			float agePercent = (float) ((float) age / lifetime * 1.5);
			this.setSprite(spriteProvider.get(Math.min(3, (int) (agePercent * 4)), 3));
		}

		if (this.age >= this.lifetime) {
			this.alpha = Math.max(0f, this.alpha - 0.015f);

			if (this.alpha <= 0f) {
				this.remove();
			}
		}

		this.oRoll = this.roll;
		if (this.onGround || this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).getType() != Fluids.EMPTY) {
			this.yd *= 0.95;
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
			return new ShadowbringerParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
