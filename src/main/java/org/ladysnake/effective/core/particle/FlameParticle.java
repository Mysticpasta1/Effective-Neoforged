package org.ladysnake.effective.core.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;

public class FlameParticle extends LodestoneWorldParticle {
	public FlameParticle(ClientLevel world, WorldParticleOptions data, ParticleEngine.MutableSpriteSet spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, data, spriteSet, x, y, z, xd, yd, zd);
		this.setSprite(this.spriteSet.sprites.get(random.nextInt(3)));
	}
}
