package org.ladysnake.effective.ambience;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biomes;
import org.ladysnake.effective.ambience.sound.AmbientCondition;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.utils.EffectiveUtils;

import java.util.HashSet;
import java.util.Set;

public class EffectiveAmbience implements ClientModInitializer {
	public static final Set<AmbientCondition> AMBIENT_CONDITIONS = new HashSet<>();

	@Override
	public void onInitializeClient() {
		// bees in floral biomes during the day
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_BEES, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && world.getBiome(pos).is(ConventionalBiomeTags.FLORAL) && !Effective.isNightTime(world)));

		// birds in forests during the day
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_BIRDS, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && world.getBiome(pos).is(ConventionalBiomeTags.FOREST) && !Effective.isNightTime(world)));

		// cicadas in savannas during day
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_CICADAS, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && world.getBiome(pos).is(ConventionalBiomeTags.SAVANNA) && !Effective.isNightTime(world)));

		// crickets in temperate (excluding swamps to use their dedicated cricket and frog ambience instead) and floral biomes at night
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_CRICKETS, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && ((world.getBiome(pos).is(ConventionalBiomeTags.CLIMATE_TEMPERATE) && !world.getBiome(pos).is(ConventionalBiomeTags.SWAMP)) || world.getBiome(pos).is(ConventionalBiomeTags.FLORAL)) && Effective.isNightTime(world)));

		// frogs and crickets in swamps at night
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_FROGS_AND_CRICKETS, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && (world.getBiome(pos).is(ConventionalBiomeTags.SWAMP)) && Effective.isNightTime(world)));

		// day jungle animals in jungles during the day
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_JUNGLE_DAY, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && (world.getBiome(pos).is(ConventionalBiomeTags.JUNGLE)) && !Effective.isNightTime(world)));

		// night jungle animals in jungles at night
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_JUNGLE_NIGHT, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && (world.getBiome(pos).is(ConventionalBiomeTags.JUNGLE)) && Effective.isNightTime(world)));

		// mangrove birds in mangroves during the day
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_MANGROVE_BIRDS, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && world.getBiome(pos).is(Biomes.MANGROVE_SWAMP) && !Effective.isNightTime(world)));

		// owls in forests at night
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.ANIMAL_OWLS, AmbientCondition.Type.ANIMAL,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && (world.getBiome(pos).is(ConventionalBiomeTags.FOREST)) && Effective.isNightTime(world)));

		// rustling reverbed foliage in lush caves
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.FOLIAGE_CAVE_LEAVES, AmbientCondition.Type.FOLIAGE,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && world.getBiome(pos).is(Biomes.LUSH_CAVES)));

		// rustling foliage in forests, floral biomes, swamps, jungles, wooded badlands and lush caves
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.FOLIAGE_LEAVES, AmbientCondition.Type.FOLIAGE,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && (world.getBiome(pos).is(ConventionalBiomeTags.FOREST) || world.getBiome(pos).is(ConventionalBiomeTags.FLORAL) || world.getBiome(pos).is(ConventionalBiomeTags.SWAMP) || world.getBiome(pos).is(ConventionalBiomeTags.JUNGLE) || world.getBiome(pos).is(Biomes.WOODED_BADLANDS))));

		// water dripping in dripstone caves
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WATER_DRIPSTONE_CAVES, AmbientCondition.Type.WATER,
			(world, pos, player) -> {
				if (EffectiveUtils.isInOverworld(world, pos)) {
					if (EffectiveUtils.isInCave(world, pos)) {
						BlockPos.MutableBlockPos mutable = pos.mutable();
						int startY = mutable.getY();
						for (int y = startY; y <= startY + 20; y += 5) {
							mutable.setY(y);
							if (world.getBiome(mutable).is(Biomes.DRIPSTONE_CAVES)) {
								return true;
							}
						}
						return false;
					} else return world.getBiome(pos).is(Biomes.DRIPSTONE_CAVES);
				} else return false;
			}));

		// water streams in lush caves
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WATER_LUSH_CAVES, AmbientCondition.Type.WATER,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && world.getBiome(pos).is(Biomes.LUSH_CAVES)));

		// water flowing in rivers
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WATER_RIVER, AmbientCondition.Type.WATER,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && world.getBiome(pos).is(ConventionalBiomeTags.RIVER)));

		// water waves in beaches and oceans
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WATER_WAVES, AmbientCondition.Type.WATER,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && world.getBiome(pos).is(ConventionalBiomeTags.BEACH) || world.getBiome(pos).is(ConventionalBiomeTags.OCEAN)));

		// arid wind in deserts and mesas
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WIND_ARID, AmbientCondition.Type.WIND,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && (world.getBiome(pos).is(ConventionalBiomeTags.DESERT) || world.getBiome(pos).is(ConventionalBiomeTags.MESA))));

		// cave wind in caves (excluding the deep dark to use its dedicated ambience instead)
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WIND_CAVE, AmbientCondition.Type.WIND,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && EffectiveUtils.isInCave(world, pos) && !world.getBiome(pos).is(Biomes.DEEP_DARK)));

		// cold wind in cold biomes (excluding peaks to use their dedicated wind instead) and mountain slopes
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WIND_COLD, AmbientCondition.Type.WIND,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && (world.getBiome(pos).is(ConventionalBiomeTags.CLIMATE_COLD) || world.getBiome(pos).is(ConventionalBiomeTags.MOUNTAIN_SLOPE)) && !world.getBiome(pos).is(ConventionalBiomeTags.MOUNTAIN_PEAK)));

		// deep dark ambience (classified as wind)
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WIND_DEEP_DARK, AmbientCondition.Type.WIND,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && world.getBiome(pos).is(Biomes.DEEP_DARK)));

		// end ambience (classified as wind)
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WIND_END, AmbientCondition.Type.WIND,
			(world, pos, player) -> world.getBiome(pos).is(ConventionalBiomeTags.IN_THE_END)));

		// mountain wind in peaks
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WIND_MOUNTAINS, AmbientCondition.Type.WIND,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && world.getBiome(pos).is(ConventionalBiomeTags.MOUNTAIN_PEAK)));

		// soft wind in temperate, floral, savanna, jungle, swamp and mushroom field biomes
		AMBIENT_CONDITIONS.add(new AmbientCondition(EffectiveAmbienceSounds.WIND_TEMPERATE, AmbientCondition.Type.WIND,
			(world, pos, player) -> EffectiveUtils.isInOverworld(world, pos) && !EffectiveUtils.isInCave(world, pos) && (world.getBiome(pos).is(ConventionalBiomeTags.CLIMATE_TEMPERATE) || world.getBiome(pos).is(ConventionalBiomeTags.FLORAL) || world.getBiome(pos).is(ConventionalBiomeTags.SAVANNA) || world.getBiome(pos).is(ConventionalBiomeTags.JUNGLE) || world.getBiome(pos).is(ConventionalBiomeTags.SWAMP) || world.getBiome(pos).is(ConventionalBiomeTags.MUSHROOM))));
	}
}
