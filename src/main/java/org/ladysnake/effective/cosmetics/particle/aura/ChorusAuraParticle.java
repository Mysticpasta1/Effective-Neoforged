package org.ladysnake.effective.cosmetics.particle.aura;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import org.ladysnake.effective.core.particle.ChorusPetalParticle;

public class ChorusAuraParticle extends ChorusPetalParticle {
	public ChorusAuraParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd, spriteProvider);

		this.yd = -0.01 - random.nextFloat() / 10;
		this.xd = random.nextGaussian() / 50;
		this.zd = random.nextGaussian() / 50;

		this.setPos(this.x + TwilightLegacyFireflyParticle.getWanderingDistance(this.random), this.y + random.nextFloat() * 2d, this.z + TwilightLegacyFireflyParticle.getWanderingDistance(this.random));
	}

	public void tick() {
		if (this.age++ < this.lifetime) {
			this.alpha = Math.min(1f, this.alpha + 0.1f);
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.99D;
		this.yd *= 0.99D;
		this.zd *= 0.99D;

		this.rCol *= 0.99;
		this.gCol *= 0.98;

		if (this.age >= this.lifetime) {
//            this.red *= 0.9;
//            this.green *= 0.8;

			this.alpha = Math.max(0f, this.alpha - 0.1f);

			if (this.alpha <= 0f) {
				this.remove();
			}
		}

		this.oRoll = this.roll;
		if (this.onGround || this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER)) {
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
			return new ChorusAuraParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

}
