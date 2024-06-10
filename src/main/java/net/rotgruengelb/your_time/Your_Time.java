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
		LOGGER.warn("!!!!	Fabric API 0.100.0+1.20.6 and owo-lib 0.12.8+1.20.5 are incompatible and may result in crashes.		!!!!");
		LOGGER.warn("!!!!	Use earlier (or later) versions of Fabric API (or owo-lib) like FAPI 0.99.4+1.20.6. 				!!!!");
		LOGGER.warn("!!!!	If you are on different versions of either mods this might not apply. 								!!!!");
		registerEvents();
	}

	private void registerEvents() {

		HudRenderCallback.EVENT.register(GuiEventForOverlay::renderOverlay);
	}
}