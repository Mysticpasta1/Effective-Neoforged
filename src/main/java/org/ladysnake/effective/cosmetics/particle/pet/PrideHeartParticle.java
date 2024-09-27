package org.ladysnake.effective.cosmetics.particle.pet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.effective.cosmetics.render.entity.model.pet.PrideHeartModel;

public class PrideHeartParticle extends PlayerLanternParticle {
	protected PrideHeartParticle(ClientLevel world, double x, double y, double z, ResourceLocation texture, float red, float green, float blue) {
		super(world, x, y, z, texture, red, green, blue);

		this.model = new PrideHeartModel(Minecraft.getInstance().getEntityModels().bakeLayer(PrideHeartModel.MODEL_LAYER));
	}

	@Override
	public void tick() {
		super.tick();
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final ResourceLocation texture;
		private final float red;
		private final float green;
		private final float blue;

		public DefaultFactory(SpriteSet spriteProvider, ResourceLocation texture, float red, float green, float blue) {
			this.texture = texture;
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new PrideHeartParticle(world, x, y, z, this.texture, this.red, this.green, this.blue);
		}
	}
}
