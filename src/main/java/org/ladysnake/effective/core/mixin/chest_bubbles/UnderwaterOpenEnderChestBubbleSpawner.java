package org.ladysnake.effective.core.mixin.chest_bubbles;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.joml.Vector3f;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.utils.LinearForcedMotionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;

import java.awt.*;

@Mixin(EnderChestBlockEntity.class)
public class UnderwaterOpenEnderChestBubbleSpawner<T extends BlockEntity & LidBlockEntity> {
	@Inject(method = "lidAnimateTick", at = @At("TAIL"))
	private static void clientTick(Level world, BlockPos pos, BlockState state, EnderChestBlockEntity blockEntity, CallbackInfo ci) {
		boolean bl = world != null;

		if (EffectiveConfig.underwaterOpenChestBubbles && bl && world.random.nextInt(2) == 0) {
			BlockState blockState = blockEntity.getBlockState();
			ChestType chestType = blockState.hasProperty(ChestBlock.TYPE) ? blockState.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
			Direction facing = blockState.hasProperty(ChestBlock.FACING) ? blockState.getValue(ChestBlock.FACING) : Direction.NORTH;
			Block block = blockState.getBlock();
			if (block instanceof AbstractChestBlock<?> && world.isWaterAt(blockEntity.getBlockPos()) && world.isWaterAt(blockEntity.getBlockPos().relative(Direction.UP, 1))) {
				AbstractChestBlock<?> abstractChestBlock = (AbstractChestBlock) block;
				boolean doubleChest = chestType != ChestType.SINGLE;

				DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> propertySource;
				propertySource = abstractChestBlock.combine(blockState, world, blockEntity.getBlockPos(), true);

				float openFactor = propertySource.apply(ChestBlock.opennessCombiner(blockEntity)).get(1.0f);

				if (openFactor > 0) {
					if (doubleChest) {
						if (chestType == ChestType.LEFT) {
							float xOffset = 0f;
							float zOffset = 0f;
							float xOffsetRand = 0f;
							float zOffsetRand = 0f;

							if (facing == Direction.NORTH) {
								xOffset = 1f;
								zOffset = .5f;
								xOffsetRand = (world.random.nextFloat() - world.random.nextFloat()) * .8f;
								zOffsetRand = (world.random.nextFloat() - world.random.nextFloat()) * .3f;
							} else if (facing == Direction.SOUTH) {
								xOffset = 0f;
								zOffset = .5f;
								xOffsetRand = (world.random.nextFloat() - world.random.nextFloat()) * .8f;
								zOffsetRand = (world.random.nextFloat() - world.random.nextFloat()) * .3f;
							} else if (facing == Direction.EAST) {
								xOffset = .5f;
								zOffset = 1f;
								xOffsetRand = (world.random.nextFloat() - world.random.nextFloat()) * .3f;
								zOffsetRand = (world.random.nextFloat() - world.random.nextFloat()) * .8f;
							} else if (facing == Direction.WEST) {
								xOffset = .5f;
								zOffset = 0f;
								xOffsetRand = (world.random.nextFloat() - world.random.nextFloat()) * .3f;
								zOffsetRand = (world.random.nextFloat() - world.random.nextFloat()) * .8f;
							}

							for (int i = 0; i < 1 + world.random.nextInt(3); i++) {
								spawnBubble(world, blockEntity.getBlockPos().getX() + xOffset + xOffsetRand, blockEntity.getBlockPos().getY() + .5f, blockEntity.getBlockPos().getZ() + zOffset + zOffsetRand, block == Blocks.ENDER_CHEST);
							}

							if (openFactor <= .6f) {
								spawnClosingBubble(world, blockEntity.getBlockPos().getX() + xOffset, blockEntity.getBlockPos().getY() + .5f, blockEntity.getBlockPos().getZ() + zOffset, facing, true, block == Blocks.ENDER_CHEST);
							}
						}
					} else {
						for (int i = 0; i < 1 + world.random.nextInt(3); i++) {
							spawnBubble(world, blockEntity.getBlockPos().getX() + .5f + (world.random.nextFloat() - world.random.nextFloat()) * .3f, blockEntity.getBlockPos().getY() + .5f, blockEntity.getBlockPos().getZ() + .5f + (world.random.nextFloat() - world.random.nextFloat()) * .3f, block == Blocks.ENDER_CHEST);
						}

						if (openFactor <= .6f) {
							spawnClosingBubble(world, blockEntity.getBlockPos().getX() + .5f, blockEntity.getBlockPos().getY() + .5f, blockEntity.getBlockPos().getZ() + .5f, facing, false, block == Blocks.ENDER_CHEST);
						}
					}
				}
			}
		}
	}

	private static void spawnBubble(Level world, float x, float y, float z, boolean endChest) {
		float bubbleSize = .05f + world.random.nextFloat() * .05f;
		WorldParticleBuilder.create(Effective.BUBBLE)
			.enableForcedSpawn()
//			.setLightLevel(endChest ? LightmapTextureManager.MAX_LIGHT_COORDINATE : -1)
			.setScaleData(GenericParticleData.create(bubbleSize).build())
			.setTransparencyData(GenericParticleData.create(1f).build())
			.enableNoClip()
			.setLifetime(60 + world.random.nextInt(60))
			.setMotion(0f, bubbleSize, 0f)
			.setRenderType(ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT)
			.setColorData(ColorParticleData.create(new Color(endChest ? 0x00FF90 : 0xFFFFFF), new Color(endChest ? 0x00FF90 : 0xFFFFFF)).build())
			.spawn(world, x, y, z);
	}

	private static void spawnClosingBubble(Level world, float x, float y, float z, Direction direction, boolean doubleChest, boolean endChest) {
		for (int i = 0; i < (doubleChest ? 10 : 5); i++) {
			float velX = .5f;
			float velZ = .5f;
			if (direction == Direction.NORTH) {
				velX = (world.random.nextFloat() - world.random.nextFloat()) / (doubleChest ? 2.5f : 5f);
				velZ = -.05f - (world.random.nextFloat() / 5f);
			} else if (direction == Direction.SOUTH) {
				velX = (world.random.nextFloat() - world.random.nextFloat()) / (doubleChest ? 2.5f : 5f);
				velZ = .05f + (world.random.nextFloat() / 5f);
			} else if (direction == Direction.EAST) {
				velX = .05f + (world.random.nextFloat() / 5f);
				velZ = (world.random.nextFloat() - world.random.nextFloat()) / (doubleChest ? 2.5f : 5f);
			} else if (direction == Direction.WEST) {
				velX = -.05f - (world.random.nextFloat() / 5f);
				velZ = (world.random.nextFloat() - world.random.nextFloat()) / (doubleChest ? 2.5f : 5f);
			}
			WorldParticleBuilder.create(Effective.BUBBLE)
				.enableForcedSpawn()
//				.setLightLevel(endChest ? LightmapTextureManager.MAX_LIGHT_COORDINATE : -1)
				.setScaleData(GenericParticleData.create(.05f + world.random.nextFloat() * .05f).build())
				.setTransparencyData(GenericParticleData.create(1f).build())
				.enableNoClip()
				.setLifetime(60 + world.random.nextInt(60))
				.addTickActor(new LinearForcedMotionImpl(
					new Vector3f(velX, .1f - (world.random.nextFloat() * .1f), velZ),
					new Vector3f(0f, .1f, 0f),
					10f
				))
				.setRenderType(ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT)
				.setColorData(ColorParticleData.create(new Color(endChest ? 0x00FF90 : 0xFFFFFF), new Color(endChest ? 0x00FF90 : 0xFFFFFF)).build())
				.spawn(world, x, y, z);
		}
	}
}
