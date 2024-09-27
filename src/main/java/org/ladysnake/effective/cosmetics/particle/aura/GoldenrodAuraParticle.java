package org.ladysnake.effective.cosmetics.particle.aura;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import org.ladysnake.effective.core.particle.ChorusPetalParticle;

public class GoldenrodAuraParticle extends ChorusPetalParticle {
	private int elevation = 0;

	public GoldenrodAuraParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
		super(world, x, y, z, xd, yd, zd, spriteProvider);

		this.yd = 0;
		this.xd = 0;
		this.zd = 0;
		this.quadSize = 0.9f;


		this.setPos(this.x + get_rando(), this.y + random.nextFloat() + 0.5 * 1.5d, this.z + get_rando());
	}

	public double get_rando() {
		double rando = (random.nextFloat() - 0.5) * 1.4;
		if (rando < 0.3 && rando > 0) {
			rando += 0.3;
		} else if (rando < 0 && rando > -0.3) {
			rando -= 0.3;
		}
		return rando;
	}

	public void tick() {
		this.age += 2;
		if (this.age < this.lifetime) {
			this.alpha = Math.min(1f, this.alpha + 0.1f);
		}
		this.quadSize *= 0.9;

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.move(this.xd, this.yd, this.zd);
		if (this.yd == 0) {
			int temp_rand = random.nextInt(15);
			if (temp_rand == 0 && elevation < 1) {
				this.yd = 0.3;
				elevation += 1;
			} else if (temp_rand == 1 && elevation > -2) {
				this.yd = -0.3;
				elevation -= 1;
			}
		} else if (Math.abs(this.yd) > 0.08) {
			this.yd *= 0.5;
		} else {
			this.yd = 0;
		}

		this.bCol *= 0.96;
		this.gCol *= 0.98;

		if (this.age >= this.lifetime) {
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

		this.roll = 0;

	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public DefaultFactory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(SimpleParticleType SimpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
			return new GoldenrodAuraParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}

}
