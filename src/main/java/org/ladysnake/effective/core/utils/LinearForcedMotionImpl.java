package org.ladysnake.effective.core.utils;

import net.minecraft.util.Mth;
import org.joml.Vector3f;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle;

import java.util.function.Consumer;


/**
 * Lodestar removed setForcedMotion and motion coefficient.
 * Here is my implementation of it or rather a copy paste from LodestoneLib 1.19.2
 * It makes a particle travel from one point to another and can be applied using
 * {@link WorldParticleBuilder#addTickActor(Consumer)} (Consumer)}
 *
 * @author SzczurekYT
 */
public class LinearForcedMotionImpl implements Consumer<LodestoneWorldParticle> {

	private final Vector3f start;
	private final Vector3f end;
	private final float coefficient;

	public LinearForcedMotionImpl(Vector3f start, Vector3f end, float coefficient) {
		this.start = start;
		this.end = end;
		this.coefficient = coefficient;
	}

	@Override
	public void accept(LodestoneWorldParticle particle) {
		float motionAge = getCurve(particle, coefficient);
		float xd = Mth.lerp(motionAge, start.x(), end.x());
		float yd = Mth.lerp(motionAge, start.y(), end.y());
		float zd = Mth.lerp(motionAge, start.z(), end.z());
		particle.setParticleSpeed(xd, yd, zd);
	}

	public float getCurve(LodestoneWorldParticle particle, float multiplier) {
		return Mth.clamp((particle.getAge() * multiplier) / (float) particle.getLifetime(), 0, 1);
	}

}
