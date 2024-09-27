package org.ladysnake.effective.core.world;

import net.minecraft.world.entity.GlowSquid;

import java.util.HashSet;
import java.util.Set;

public class RenderedHypnotizingEntities {
	public static Set<GlowSquid> GLOWSQUIDS = new HashSet<>();
	public static double lookIntensity = 0f;
	public static double lookIntensityGoal = 0f;
	public static int lockedIntensityTimer = 0;
}
