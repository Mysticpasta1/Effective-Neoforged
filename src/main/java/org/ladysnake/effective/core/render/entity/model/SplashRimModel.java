package org.ladysnake.effective.core.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import org.ladysnake.effective.core.Effective;

public class SplashRimModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Effective.id("splash_rim"), "main");
	private final ModelPart splash;

	public SplashRimModel(ModelPart root) {
		this.splash = root.getChild("splash_rim");
	}

	public static LayerDefinition getLayerDefinition() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("splash_rim", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -16.0F, -6.0F, 12.0F, 16.0F, 12.0F), PartPose.offset(0.0F, 16.0F, 0.0F));

		return LayerDefinition.create(modelData, 48, 28);
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		splash.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}
