package org.ladysnake.effective.core.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.core.particle.FireflyParticle;
import org.ladysnake.effective.core.settings.SpawnSettings;
import org.ladysnake.effective.core.settings.data.FireflySpawnSetting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ParticleSpawningClientWorldMixin extends Level {
	@Shadow
	@Final
	private LevelRenderer levelRenderer;

	protected ParticleSpawningClientWorldMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
	}

	@Inject(method = "doAnimateTick", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getAmbientParticle()Ljava/util/Optional;")),
		at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", ordinal = 0, shift = At.Shift.AFTER))
	private void randomBlockDisplayTick(int centerX, int centerY, int centerZ, int radius, RandomSource random, Block block, BlockPos.MutableBlockPos blockPos, CallbackInfo ci) {
		BlockPos.MutableBlockPos pos = blockPos.offset(Mth.floor(this.random.nextGaussian() * 50), Mth.floor(this.random.nextGaussian() * 10), Mth.floor(this.random.nextGaussian() * 50)).mutable();
		BlockPos.MutableBlockPos pos2 = pos.mutable();
		Holder<Biome> biome = this.getBiome(pos);

		// FIREFLIES
		if (EffectiveConfig.fireflyDensity > 0) {
			FireflySpawnSetting fireflySpawnSetting = SpawnSettings.FIREFLIES.get(biome.unwrapKey().get());
			if (fireflySpawnSetting != null) {
				if (random.nextFloat() * 250f <= fireflySpawnSetting.spawnChance() * EffectiveConfig.fireflyDensity && pos.getY() > this.getSeaLevel()) {
					for (int y = this.getSeaLevel(); y <= this.getSeaLevel() * 2; y++) {
						pos.setY(y);
						pos2.setY(y-1);
						boolean canSpawnFirefly = FireflyParticle.canFlyThroughBlock(this, pos, this.getBlockState(pos)) && !FireflyParticle.canFlyThroughBlock(this, pos2, this.getBlockState(pos2));

						if (canSpawnFirefly) {
							WorldParticleBuilder.create(Effective.FIREFLY)
								.enableForcedSpawn()
								.setColorData(ColorParticleData.create(fireflySpawnSetting.color(), fireflySpawnSetting.color()).build())
								.setScaleData(GenericParticleData.create(0.05f + random.nextFloat() * 0.10f).build())
								.setLifetime(ThreadLocalRandom.current().nextInt(40, 120))
								.setRenderType(LodestoneWorldParticleRenderType.ADDITIVE)
								.spawn(this, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat() * 5f, pos.getZ() + random.nextFloat());
							break;
						}
					}
				}
			}
		}

		pos = blockPos.offset(Mth.floor(this.random.nextGaussian() * 50), Mth.floor(this.random.nextGaussian() * 25), Mth.floor(this.random.nextGaussian() * 50)).mutable();

		// WILL O' WISP
		if (EffectiveConfig.willOWispDensity > 0) {
			if (biome.is(Biomes.SOUL_SAND_VALLEY)) {
				if (random.nextFloat() * 100f <= 0.01f * EffectiveConfig.willOWispDensity) {
					if (this.getBlockState(pos).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
						this.addParticle(Effective.WILL_O_WISP, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
					}
				}
			}
		}

		// EYES IN THE DARK
		if ((EffectiveConfig.eyesInTheDark == EffectiveConfig.EyesInTheDarkOptions.ALWAYS || (EffectiveConfig.eyesInTheDark == EffectiveConfig.EyesInTheDarkOptions.HALLOWEEN && LocalDate.now().getMonth() == Month.OCTOBER))
			&& random.nextFloat() <= 0.00002f) {
			this.addParticle(Effective.EYES, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
		}
	}
}
