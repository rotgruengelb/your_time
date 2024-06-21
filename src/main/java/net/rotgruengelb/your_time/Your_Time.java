package net.rotgruengelb.your_time;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.rotgruengelb.your_time.config.ModConfig;
import net.rotgruengelb.your_time.events.GuiEventForOverlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Your_Time implements ClientModInitializer {
	public static final String MOD_ID = "your_time";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ModConfig CONFIG = ModConfig.createAndLoad();

	@Override
	public void onInitializeClient() {
		registerEvents();
	}

	private void registerEvents() {
		HudRenderCallback.EVENT.register(GuiEventForOverlay::renderOverlay);
	}
}