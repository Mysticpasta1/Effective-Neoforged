package org.ladysnake.effective.core.mixin.water;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.ladysnake.effective.core.world.WaterfallCloudGenerators;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlockRenderer.class)
public class WaterfallGeneratorAdder {
	@Inject(method = "tesselate", at = @At("HEAD"))
	public void effective$generateWaterfall(BlockAndTintGetter level, BlockPos pos, VertexConsumer consumer, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
		WaterfallCloudGenerators.addGenerator(fluidState, pos.mutable());
	}
}
