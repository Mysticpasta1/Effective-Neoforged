package org.ladysnake.effective.cosmetics.render.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.effective.core.EffectiveConfig;
import org.ladysnake.effective.cosmetics.EffectiveCosmetics;
import org.ladysnake.effective.cosmetics.data.PlayerCosmeticData;
import org.ladysnake.effective.cosmetics.render.GlowyRenderLayer;
import org.ladysnake.effective.cosmetics.render.entity.model.hat.OverheadModel;

import java.util.Map;
import java.util.stream.Collectors;

public class OverheadFeatureRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	private final Map<String, ResolvedOverheadData> models;

	public OverheadFeatureRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> featureRendererContext, EntityRendererProvider.Context loader) {
		super(featureRendererContext);
		this.models = EffectiveCosmetics.OVERHEADS_DATA.entrySet().stream()
			.collect(Collectors.toMap(Map.Entry::getKey, data -> new ResolvedOverheadData(data.getValue().getTexture(), data.getValue().createModel(loader))));
	}



	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		PlayerCosmeticData cosmeticData = EffectiveCosmetics.getCosmeticData(entity);
		if (EffectiveConfig.shouldDisplayCosmetics() && cosmeticData != null && !entity.isInvisible()) {
			String playerOverhead = cosmeticData.getOverhead();
			if (playerOverhead != null) {
				ResolvedOverheadData resolvedOverheadData = this.models.get(playerOverhead);
				if (resolvedOverheadData != null) {
					ResourceLocation texture = resolvedOverheadData.texture();
					OverheadModel model = resolvedOverheadData.model();

					model.head.x = this.getParentModel().head.x;
					model.head.y = this.getParentModel().head.y;
					model.head.xRot = this.getParentModel().head.xRot;
					model.head.yRot = this.getParentModel().head.yRot;
					model.renderToBuffer(matrices, vertexConsumers.getBuffer(GlowyRenderLayer.get(texture)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
				}
			}
		}
	}

	private record ResolvedOverheadData(ResourceLocation texture, OverheadModel model) {
	}
}
