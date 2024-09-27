package org.ladysnake.effective.core.mixin.water.integration.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import org.ladysnake.effective.core.world.WaterfallCloudGenerators;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public class SodiumWaterfallGeneratorAdder {
	@Inject(method = "render", at = @At("HEAD"), remap = false)
	public void effective$generateWaterfall(WorldSlice world, FluidState fluidState, BlockPos pos, BlockPos offset, ChunkBuildBuffers buffers, CallbackInfo ci) {
		WaterfallCloudGenerators.addGenerator(fluidState, pos.mutable());
	}
}
