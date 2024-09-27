package org.ladysnake.effective.core.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.util.Mth;
import team.lodestar.lodestone.systems.particle.world.FrameSetParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;

public class AllayTwinkleParticle extends FrameSetParticle {
	public AllayTwinkleParticle(ClientLevel world, WorldParticleOptions data, ParticleEngine.MutableSpriteSet spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, data, spriteSet, x, y, z, xd, yd, zd);
		addFrames(0, 5);
		setLifetime(frameSet.size() * 3);
	}

	@Override
	public void tick() {
		super.tick();
		if (age < frameSet.size() * 3) {
			this.setSprite(this.spriteSet.sprites.get(frameSet.get(Mth.floor(age / 3f))));
		}
	}
}
