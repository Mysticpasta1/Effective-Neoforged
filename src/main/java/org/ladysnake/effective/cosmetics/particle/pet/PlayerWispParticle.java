package org.ladysnake.effective.cosmetics.particle.pet;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.effective.core.particle.WillOWispParticle;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.data.PlayerCosmeticData;

import java.util.Objects;

public class PlayerWispParticle extends WillOWispParticle {
	protected Player owner;

	protected PlayerWispParticle(ClientLevel world, double x, double y, double z, ResourceLocation texture, float red, float green, float blue, float redEvolution, float greenEvolution, float blueEvolution) {
		super(world, x, y, z, texture, red, green, blue, redEvolution, greenEvolution, blueEvolution);

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.lifetime = 35;
		this.owner = world.getNearestPlayer(TargetingConditions.forNonCombat().range(1D), this.x, this.y, this.z);

		if (this.owner == null) {
			this.remove();
		}

		if (owner != null && owner.getUUID() != null && EffectiveCosmetics.getCosmeticData(owner) != null) {
			PlayerCosmeticData data = Objects.requireNonNull(EffectiveCosmetics.getCosmeticData(owner));
			this.rCol = data.getColor1Red() / 255f;
			this.gCol = data.getColor1Green() / 255f;
			this.bCol = data.getColor1Blue() / 255f;
			this.gotoRed = data.getColor2Red() / 255f;
			this.gotoGreen = data.getColor2Green() / 255f;
			this.gotoBlue = data.getColor2Blue() / 255f;
		} else {
			this.remove();
		}
		this.alpha = 0;
	}

	@Override
	public void tick() {
		if (this.age > 10) {
			this.alpha = 1f;
		} else {
			this.alpha = 0;
		}

		if (owner != null) {
			this.xo = this.x;
			this.yo = this.y;
			this.zo = this.z;

			// die if old enough
			if (this.age++ >= this.lifetime) {
				this.remove();
			}

			this.setPos(owner.getX() + Math.cos(owner.yBodyRot / 50) * 0.5, owner.getY() + owner.getBbHeight() + 0.5f + Math.sin(owner.tickCount / 12f) / 12f, owner.getZ() - Math.cos(owner.yBodyRot / 50) * 0.5);

			this.pitch = -owner.getXRot();
			this.prevPitch = -owner.xRotO;
			this.yaw = -owner.getYRot();
			this.prevYaw = -owner.yRotO;
		} else {
			this.remove();
		}
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final ResourceLocation texture;
		private final float red;
		private final float green;
		private final float blue;
		private final float redEvolution;
		private final float greenEvolution;
		private final float blueEvolution;

		public DefaultFactory(SpriteSet spriteProvider, ResourceLocation texture, float red, float green, float blue, float redEvolution, float greenEvolution, float blueEvolution) {
			this.texture = texture;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.redEvolution = redEvolution;
			this.greenEvolution = greenEvolution;
			this.blueEvolution = blueEvolution;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new PlayerWispParticle(world, x, y, z, this.texture, this.red, this.green, this.blue, this.redEvolution, this.greenEvolution, this.blueEvolution);
		}
	}
}
