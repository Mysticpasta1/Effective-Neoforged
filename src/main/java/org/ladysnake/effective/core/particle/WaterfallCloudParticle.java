package org.ladysnake.effective.core.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import team.lodestar.lodestone.systems.particle.world.FrameSetParticle;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;

public class WaterfallCloudParticle extends FrameSetParticle {
	public WaterfallCloudParticle(ClientLevel world, WorldParticleOptions data, ParticleEngine.MutableSpriteSet spriteSet, double x, double y, double z, double xd, double yd, double zd) {
		super(world, data, spriteSet, x, y, z, xd, yd, zd);

		this.setSprite(this.spriteSet.get(world.random));
	}

	@Override
	public void tick() {
		super.tick();

		this.setSpriteFromAge(this.spriteSet);

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.onGround || (this.age > 10 && this.level.getBlockState(BlockPos.containing(this.x, this.y + this.yd, this.z)).getBlock() == Blocks.WATER)) {
			this.xd *= 0.5f;
			this.yd *= 0.5f;
			this.zd *= 0.5f;
		}

		if (this.level.getBlockState(BlockPos.containing(this.x, this.y + this.yd, this.z)).getBlock() == Blocks.WATER && this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
			this.xd *= 0.9;
			this.yd *= 0.9;
			this.zd *= 0.9;
		}

		this.xd *= 0.95f;
		this.yd -= 0.02f;
		this.zd *= 0.95f;

		this.move(xd, yd, zd);
	}
}
