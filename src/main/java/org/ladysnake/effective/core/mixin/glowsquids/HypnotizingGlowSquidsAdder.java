package org.ladysnake.effective.core.mixin.glowsquids;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SquidRenderer;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.animal.Squid;
import org.ladysnake.effective.core.world.RenderedHypnotizingEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SquidRenderer.class)
public class HypnotizingGlowSquidsAdder {
	// add glow squid to entities hypnotizing the client
	@Inject(method = "setupRotations(Lnet/minecraft/world/entity/animal/Squid;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("TAIL"))
	protected void setupTransforms(Squid squidEntity, PoseStack matrixStack, float f, float g, float h, CallbackInfo callbackInfo) {
		if (squidEntity instanceof GlowSquid glowSquid && glowSquid.getDarkTicksRemaining() <= 0f && Math.sqrt(Minecraft.getInstance().player.position().distanceToSqr(squidEntity.position())) < 20.0) {
			RenderedHypnotizingEntities.GLOWSQUIDS.add(glowSquid);
		}
	}
}
