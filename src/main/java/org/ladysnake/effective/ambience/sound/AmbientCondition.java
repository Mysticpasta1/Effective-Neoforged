package org.ladysnake.effective.ambience.sound;


import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record AmbientCondition(SoundEvent event, Type type, AmbiencePredicate predicate) {
	public interface AmbiencePredicate {
		boolean shouldPlay(Level world, BlockPos pos, Player player);
	}

	public static enum Type {
		WIND, ANIMAL, FOLIAGE, WATER;
	}
}
