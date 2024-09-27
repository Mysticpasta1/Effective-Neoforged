package org.ladysnake.effective.cosmetics.render.entity.model.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;

public class CrownModel extends OverheadModel {
	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(ResourceLocation.tryBuild(EffectiveCosmetics.MODID, "crown"), "main");

	public CrownModel(EntityRendererProvider.Context ctx) {
		super(ctx, MODEL_LAYER);

		ModelPart crown = this.head.getChild("crown");
		ModelPart south_r1 = crown.getChild("south_r1");
		ModelPart east_r1 = crown.getChild("east_r1");
		ModelPart north_r1 = crown.getChild("north_r1");
		ModelPart west_r1 = crown.getChild("west_r1");
		setRotationAngle(east_r1, -0.2618F, 1.5708F, 0.0F);
		setRotationAngle(north_r1, -0.2618F, 3.1416F, 0.0F);
		setRotationAngle(west_r1, -0.2618F, -1.5708F, 0.0F);
		setRotationAngle(south_r1, -0.2618F, 0.0F, 0.0F);
	}

	public static LayerDefinition getLayerDefinition() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition modelPartData1 = modelPartData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 7).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-4.0f)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition modelPartData2 = modelPartData1.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -13.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5f)), PartPose.offset(0.0F, -4.0F, 0.0F));
		modelPartData2.addOrReplaceChild("west_r1", CubeListBuilder.create().texOffs(7, 39).addBox(-4.0F, -8.0F, 3.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, -7.0F, 0.0F));
		modelPartData2.addOrReplaceChild("north_r1", CubeListBuilder.create().texOffs(7, 15).addBox(-4.0F, -8.0F, 3.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, -7.0F, 0.0F));
		modelPartData2.addOrReplaceChild("east_r1", CubeListBuilder.create().texOffs(7, 23).addBox(-4.0F, -8.0F, 3.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, -7.0F, 0.0F));
		modelPartData2.addOrReplaceChild("south_r1", CubeListBuilder.create().texOffs(7, 31).addBox(-4.0F, -8.0F, 3.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, -7.0F, 0.0F));
		return LayerDefinition.create(modelData, 32, 48);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart bone, float x, float y, float z) {
		bone.xRot = x;
		bone.yRot = y;
		bone.zRot = z;
	}
}
