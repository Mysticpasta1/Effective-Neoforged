package org.ladysnake.effective.cosmetics.data;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.render.entity.model.hat.OverheadModel;

import java.util.function.Function;

public class OverheadData {
	private final Function<EntityRendererProvider.Context, OverheadModel> model;
	private final ResourceLocation texture;

	public OverheadData(Function<EntityRendererProvider.Context, OverheadModel> model, String textureName) {
		this.model = model;
		this.texture = ResourceLocation.tryBuild(EffectiveCosmetics.MODID, "textures/entity/" + textureName + ".png");
	}

	public OverheadModel createModel(EntityRendererProvider.Context ctx) {
		return model.apply(ctx);
	}

	public ResourceLocation getTexture() {
		return texture;
	}
}
