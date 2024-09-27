package org.ladysnake.effective.core.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ParryScreen extends net.minecraft.client.gui.screens.Screen {
	public ParryScreen() {
		super(Component.literal(""));
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		context.fillGradient(0, 0, this.width, this.height, 0xAAFFFFFF, 0xAAFFFFFF);

		super.render(context, mouseX, mouseY, delta);
	}

}
