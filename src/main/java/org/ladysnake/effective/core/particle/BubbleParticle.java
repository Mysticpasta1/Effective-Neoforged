package org.ladysnake.effective.core.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import team.lodestar.lodestone.systems.particle.world.FrameSetParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;

public class BubbleParticle extends FrameSetParticle {
	public BubbleParticle(ClientLevel world, WorldParticleOptions data, ParticleEngine.MutableSpriteSet spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, data, spriteSet, x, y, z, xd, yd, zd);
	}

	@Override
	public void tick() {
		super.tick();

		if (!level.isWaterAt(BlockPos.containing(this.x, this.y, this.z))) {
			this.remove();
		}
	}
}
