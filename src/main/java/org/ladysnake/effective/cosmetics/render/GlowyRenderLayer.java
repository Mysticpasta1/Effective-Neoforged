package org.ladysnake.effective.cosmetics.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GlowyRenderLayer extends RenderType {
	public GlowyRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.Mode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
		super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
	}

	public static RenderType get(ResourceLocation texture) {
		CompositeState multiPhaseParameters = CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).setTransparencyState(TransparencyStateShard.TRANSLUCENT_TRANSPARENCY).setCullState(CullStateShard.NO_CULL).setLightmapState(RenderStateShard.LIGHTMAP).setOverlayState(RenderStateShard.NO_OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).setShaderState(ShaderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER).createCompositeState(true);
		return RenderType.create("crown", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, multiPhaseParameters);
	}
}
