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
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.render.GlowyRenderLayer;

public class WillOWispModel extends Model {
	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(ResourceLocation.tryBuild(EffectiveCosmetics.MODID, "will_o_wisp"), "main");

	private final ModelPart skull;

	public WillOWispModel(ModelPart root) {
		super(GlowyRenderLayer::get);
		this.skull = root.getChild("skull");
	}

	public static LayerDefinition getLayerDefinition() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("skull", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F)
				.texOffs(0, 16)
				.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.25F)),
			PartPose.offset(0.0F, 16.0F, 0.0F)
		);
		return LayerDefinition.create(modelData, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		skull.render(matrixStack, buffer, packedLight, packedOverlay);
	}
}
