package org.ladysnake.effective.cosmetics.render.entity.model.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.ladysnake.effective.cosmetics.render.GlowyRenderLayer;

public abstract class OverheadModel extends Model {
	public final net.minecraft.client.model.geom.ModelPart head;

	public OverheadModel(EntityRendererProvider.Context ctx, ModelLayerLocation entityModelLayer) {
		super(GlowyRenderLayer::get);
		this.head = ctx.bakeLayer(entityModelLayer).getChild("head");
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.head.render(matrices, vertices, light, overlay);
	}
}
