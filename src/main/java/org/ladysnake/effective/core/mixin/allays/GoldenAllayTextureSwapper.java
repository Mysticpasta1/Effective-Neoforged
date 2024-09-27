package org.ladysnake.effective.core.mixin.allays;

import net.minecraft.client.renderer.entity.AllayRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.allay.Allay;
import org.ladysnake.effective.core.Effective;
import org.ladysnake.effective.core.EffectiveConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AllayRenderer.class)
public class GoldenAllayTextureSwapper {
	private static final ResourceLocation GOLDEN_TEXTURE = Effective.id("textures/entity/golden_allay.png");

	@Shadow
	@Final
	private static ResourceLocation ALLAY_TEXTURE;

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/allay/Allay;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
	public void getTexture(Allay allayEntity, CallbackInfoReturnable<ResourceLocation> cir) {
		cir.setReturnValue(allayEntity.getUUID().hashCode() % 2 == 0 && EffectiveConfig.goldenAllays ? GOLDEN_TEXTURE : ALLAY_TEXTURE);
	}
}
