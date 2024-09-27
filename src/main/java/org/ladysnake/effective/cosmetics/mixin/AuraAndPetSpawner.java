package org.ladysnake.effective.cosmetics.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.data.AuraData;
import org.ladysnake.effective.cosmetics.data.PlayerCosmeticData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class AuraAndPetSpawner extends LivingEntity {
	protected AuraAndPetSpawner(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void tick(CallbackInfo callbackInfo) {
		PlayerCosmeticData cosmeticData = EffectiveCosmetics.getCosmeticData((Player) (Object) this);

		// if player has cosmetics
		if (cosmeticData != null) {
			// player aura
			String playerAura = cosmeticData.getAura();
			if (EffectiveConfig.shouldDisplayCosmetics() && playerAura != null && EffectiveCosmetics.AURAS_DATA.containsKey(playerAura)) {
				// do not render in first person or if the player is invisible
				// noinspection ConstantConditions
				if (((EffectiveConfig.cosmetics == EffectiveConfig.CosmeticsOptions.FIRST_PERSON || Minecraft.getInstance().gameRenderer.getMainCamera().isDetached()) || Minecraft.getInstance().player != (Object) this) && !this.isInvisible()) {
					if (EffectiveCosmetics.AURAS_DATA.containsKey(playerAura)) {
						AuraData aura = EffectiveCosmetics.AURAS_DATA.get(playerAura);
						if (EffectiveCosmetics.AURAS_DATA.get(playerAura).shouldAddParticle(this.random, this.tickCount)) {
							level().addParticle(aura.particle(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
						}
					}
				}
			}

			// player pet
			String playerPet = cosmeticData.getPet();
			if (EffectiveConfig.shouldDisplayCosmetics() && playerPet != null && EffectiveCosmetics.PETS_DATA.containsKey(playerPet)) {
				// do not render in first person or if the player is invisible
				//noinspection ConstantConditions
				if (((EffectiveConfig.cosmetics == EffectiveConfig.CosmeticsOptions.FIRST_PERSON || Minecraft.getInstance().gameRenderer.getMainCamera().isDetached()) || Minecraft.getInstance().player != (Object) this) && !this.isInvisible()) {
					if (EffectiveCosmetics.PETS_DATA.containsKey(playerPet)) {
						SimpleParticleType overhead = EffectiveCosmetics.PETS_DATA.get(playerPet);
						if (this.tickCount % 20 == 0) {
							level().addParticle(overhead, this.getX() + Math.cos(this.yBodyRot / 50) * 0.5, this.getY() + this.getBbHeight() + 0.5f + Math.sin(this.tickCount / 12f) / 12f, this.getZ() - Math.cos(this.yBodyRot / 50) * 0.5, 0, 0, 0);
						}
					}
				}
			}
		}
	}
}
