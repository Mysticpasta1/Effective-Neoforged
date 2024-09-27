/// Made with Model Converter by Globox_Z
/// Generate all required imports
/// Made with Blockbench 3.8.4
/// Exported for Minecraft version 1.15
/// Paste this class into your mod and generate all required imports
package org.ladysnake.effective.cosmetics.render.entity.model.pet;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.render.GlowyRenderLayer;

public class LanternModel extends Model {
	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(ResourceLocation.tryBuild(EffectiveCosmetics.MODID, "lantern"), "main");

	private final ModelPart lantern;

	public LanternModel(ModelPart root) {
		super(GlowyRenderLayer::get);
		this.lantern = root.getChild("lantern");
	}

	public static LayerDefinition getLayerDefinition() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("lantern", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 7.0F, 6.0F)
				.texOffs(0, 13)
				.addBox(-2.0F, -5.0F, -2.0F, 4.0F, 2.0F, 4.0F)
				.texOffs(16, 13)
				.addBox(-2.5F, -8.0F, 0.0F, 5.0F, 4.0F, 0.0F),
			PartPose.offset(0.0F, 16.0F, 0.0F)
		);
		return LayerDefinition.create(modelData, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		lantern.render(matrixStack, buffer, packedLight, packedOverlay);
	}
}
