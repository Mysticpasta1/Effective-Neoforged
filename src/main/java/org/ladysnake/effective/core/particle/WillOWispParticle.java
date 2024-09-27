package org.ladysnake.effective.core.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.cosmetics.particle.pet.PlayerWispParticle;
import org.ladysnake.effective.cosmetics.render.entity.model.pet.WillOWispModel;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;

import java.awt.*;
import java.util.List;

public class WillOWispParticle extends Particle {
	public final ResourceLocation texture;
	final Model model;
	final RenderType layer;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	public float speedModifier;
	protected float gotoRed;
	protected float gotoGreen;
	protected float gotoBlue;
	protected double xTarget;
	protected double yTarget;
	protected double zTarget;
	protected int targetChangeCooldown = 0;
	protected int timeInSolid = -1;

	protected WillOWispParticle(ClientLevel world, double x, double y, double z, ResourceLocation texture, float red, float green, float blue, float gotoRed, float gotoGreen, float gotoBlue) {
		super(world, x, y, z);
		this.texture = texture;
		this.model = new WillOWispModel(Minecraft.getInstance().getEntityModels().bakeLayer(WillOWispModel.MODEL_LAYER));
		this.layer = RenderType.entityTranslucent(texture);
		this.gravity = 0.0F;
		this.lifetime = 600 + random.nextInt(600);
		speedModifier = 0.1f + Math.max(0, random.nextFloat() - 0.1f);

		this.rCol = red;
		this.gCol = green;
		this.bCol = blue;

		this.gotoRed = gotoRed;
		this.gotoBlue = gotoBlue;
		this.gotoGreen = gotoGreen;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).is(net.minecraft.tags.BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
			this.level.addParticle(ParticleTypes.SOUL, this.x + random.nextGaussian() / 10, this.y + random.nextGaussian() / 10, this.z + random.nextGaussian() / 10, random.nextGaussian() / 20, random.nextGaussian() / 20, random.nextGaussian() / 20);
		} else {
			float x = (float) (Mth.lerp(tickDelta, this.xo, this.x));
			float y = (float) (Mth.lerp(tickDelta, this.yo, this.y));
			float z = (float) (Mth.lerp(tickDelta, this.zo, this.z));

			for (int i = 0; i < 2; i++) {
				WorldParticleBuilder.create(Effective.WISP)
					.enableForcedSpawn()
					.setSpinData(SpinParticleData.create((float) (this.level.random.nextGaussian() / 5f)).build())
					.setScaleData(
						GenericParticleData.create(this instanceof PlayerWispParticle ? 0.16f : 0.25f, 0f)
							.setEasing(Easing.CIRC_OUT)
							.build()
					)
					.setTransparencyData(GenericParticleData.create(1f).build())
					.setColorData(
						ColorParticleData.create(new Color(this.rCol, this.gCol, this.bCol), new Color(this.gotoRed, this.gotoGreen, this.gotoBlue))
							.setEasing(Easing.CIRC_OUT)
							.build()
					)
					.setMotion(0, 0.066f, 0)
					.enableNoClip()
					.setLifetime(40)
					.spawn(this.level, x + random.nextGaussian() / 20f, y + random.nextGaussian() / 20f, z + random.nextGaussian() / 20f);
			}

			WorldParticleBuilder.create(Effective.WISP)
				.enableForcedSpawn()
				.setSpinData(SpinParticleData.create((float) (this.level.random.nextGaussian() / 5f)).build())
				.setScaleData(GenericParticleData.create(this instanceof PlayerWispParticle ? 0.10f : 0.15f).build())
				.setTransparencyData(GenericParticleData.create(0.2f, 0f).build())
				.setColorData(ColorParticleData.create(new Color(0xFFFFFF), new Color(0xFFFFFF)).build())
				.setMotion(0, 0.066f, 0)
				.enableNoClip()
				.setLifetime(3)
				.spawn(this.level, x, y, z);
		}
	}

	@Override
	public void tick() {
		if (this.xo == this.x && this.yo == this.y && this.zo == this.z) {
			this.selectBlockTarget();
		}

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		BlockPos bp = BlockPos.containing(this.x, this.y, this.z);

		if (this.age++ >= this.lifetime) {
			for (int i = 0; i < 50; i++) {
				WorldParticleBuilder.create(Effective.WISP)
					.enableForcedSpawn()
					.setSpinData(SpinParticleData.create((float) (this.level.random.nextGaussian() / 5f)).build())
					.setScaleData(GenericParticleData.create(0.25f, 0f).setEasing(Easing.CIRC_OUT).build())
					.setTransparencyData(GenericParticleData.create(1f).build())
					.setColorData(
						ColorParticleData.create(new Color(this.rCol, this.gCol, this.bCol), new Color(this.gotoRed, this.gotoGreen, this.gotoBlue))
							.setEasing(Easing.CIRC_OUT)
							.build()
					)
					.setMotion(
						new Vector3f((float) (random.nextGaussian() / 10f), (float) (random.nextGaussian() / 10f), (float) (random.nextGaussian() / 10f))
					)
					.enableNoClip()
					.setLifetime(20)
					.repeat(this.level, x, y, z, 3);
				this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SOUL_SAND.defaultBlockState()), this.x + random.nextGaussian() / 10, this.y + random.nextGaussian() / 10, this.z + random.nextGaussian() / 10, random.nextGaussian() / 20, random.nextGaussian() / 20, random.nextGaussian() / 20);
			}

			this.level.playLocalSound(bp.getX(), bp.getY(), bp.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.AMBIENT, 1.0f, 1.5f, true);
			this.level.playLocalSound(bp.getX(), bp.getY(), bp.getZ(), SoundEvents.SOUL_SAND_BREAK, SoundSource.AMBIENT, 1.0f, 1.0f, true);
			this.remove();
		}

		this.targetChangeCooldown -= (new Vec3(x, y, z).distanceToSqr(xo, yo, zo) < 0.0125) ? 10 : 1;

		if ((this.level.getGameTime() % 20 == 0) && ((xTarget == 0 && yTarget == 0 && zTarget == 0) || new Vec3(x, y, z).distanceToSqr(xTarget, yTarget, zTarget) < 9 || targetChangeCooldown <= 0)) {
			selectBlockTarget();
		}

		Vec3 targetVector = new Vec3(this.xTarget - this.x, this.yTarget - this.y, this.zTarget - this.z);
		double length = targetVector.length();
		targetVector = targetVector.scale(speedModifier / length);

		xd = (0.9) * xd + (0.1) * targetVector.x;
		yd = (0.9) * yd + (0.1) * targetVector.y;
		zd = (0.9) * zd + (0.1) * targetVector.z;

		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		Vec3 vec3d = new Vec3(xd, yd, zd);
		float f = (float) Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
		this.yaw = (float) (Mth.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
		this.pitch = (float) (Mth.atan2(vec3d.y, f) * 57.2957763671875D);

		if (!BlockPos.containing(x, y, z).equals(this.getTargetPosition())) {
			this.move(xd, yd, zd);
		}

		if (random.nextInt(20) == 0) {
			this.level.playLocalSound(bp.getX(), bp.getY(), bp.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.AMBIENT, 1.0f, 1.5f, true);
		}

		BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
		if (!this.level.getBlockState(pos).isAir()) {
			if (timeInSolid > -1) {
				timeInSolid += 1;
			}
		} else {
			timeInSolid = 0;
		}

		if (timeInSolid > 25) {
			this.remove();
		}
	}

	@Override
	public void move(double dx, double dy, double dz) {
		double d = dx;
		double e = dy;
		if (this.hasPhysics && !this.level.getBlockState(BlockPos.containing(this.x + dx, this.y + dy, this.z + dz)).is(BlockTags.SOUL_FIRE_BASE_BLOCKS) && (dx != 0.0D || dy != 0.0D || dz != 0.0D)) {
			Vec3 vec3d = Entity.collideBoundingBox(null, new Vec3(dx, dy, dz), this.getBoundingBox(), this.level, List.of());

			dx = vec3d.x;
			dy = vec3d.y;
			dz = vec3d.z;
		}

		if (dx != 0.0D || dy != 0.0D || dz != 0.0D) {
			this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
			this.setLocationFromBoundingbox();
		}

		this.onGround = dy != dy && e < 0.0D && !this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).is(BlockTags.SOUL_FIRE_BASE_BLOCKS);
		if (d != dx) {
			this.xd = 0.0D;
		}

		if (dz != dz) {
			this.zd = 0.0D;
		}
	}

	public BlockPos getTargetPosition() {
		return BlockPos.containing(this.xTarget, this.yTarget + 0.5, this.zTarget);
	}

	private void selectBlockTarget() {
		// Behaviour
		this.xTarget = this.x + random.nextGaussian() * 10;
		this.yTarget = this.y + random.nextGaussian() * 10;
		this.zTarget = this.z + random.nextGaussian() * 10;

		BlockPos targetPos = BlockPos.containing(this.xTarget, this.yTarget, this.zTarget);
		if (this.level.getBlockState(targetPos).isCollisionShapeFullBlock(level, targetPos) && !this.level.getBlockState(targetPos).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
			targetChangeCooldown = 0;
			return;
		}

		speedModifier = 0.1f + Math.max(0, random.nextFloat() - 0.1f);
		targetChangeCooldown = random.nextInt() % (int) (100 / this.speedModifier);
	}


	public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {
		private final ResourceLocation texture;
		private final float red;
		private final float green;
		private final float blue;
		private final float toRed;
		private final float toGreen;
		private final float toBlue;

		public DefaultFactory(SpriteSet spriteProvider, ResourceLocation texture, float red, float green, float blue, float toRed, float toGreen, float toBlue) {
			this.texture = texture;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.toRed = toRed;
			this.toGreen = toGreen;
			this.toBlue = toBlue;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
			return new WillOWispParticle(world, x, y, z, this.texture, this.red, this.green, this.blue, this.toRed, this.toGreen, this.toBlue);
		}
	}
}
