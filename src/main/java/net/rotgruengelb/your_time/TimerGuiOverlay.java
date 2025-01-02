package net.rotgruengelb.your_time;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.rotgruengelb.nixienaut.util.StringUtils;
import net.rotgruengelb.your_time.config.ModConfigModel;

import static net.rotgruengelb.your_time.Your_Time.CONFIG;

public class TimerGuiOverlay {
	public static final MinecraftClient client = MinecraftClient.getInstance();

	private static boolean isDebugHudEnabled() {
		try {
			DebugHud debugHud = (DebugHud) client.getClass()
					.getMethod("getDebugHud")
					.invoke(client);
			return (boolean) debugHud.getClass()
					.getMethod("shouldShowDebugHud")
					.invoke(debugHud);
		} catch (Exception e) {
			try {
				return client.options.debugEnabled;
			} catch (Exception e2) {
				Your_Time.LOGGER.error("Failed to check if debug hud is enabled using modern method and legacy.");
				Your_Time.LOGGER.error("\tModern method exception... ", e);
				Your_Time.LOGGER.error("\tLegacy method exception... ", e2);
			}
		}
		return false;
	}

	public static void renderOverlay(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		renderOverlay(drawContext, renderTickCounter);
	}

	public static void renderOverlay(DrawContext drawContext, float delta) {
		renderOverlay(drawContext);
	}

	public static void renderOverlay(DrawContext drawContext) {
		if (!CONFIG.enabled()) { return; }
		if (isDebugHudEnabled()) { return; }
		if (client.currentScreen instanceof StatsScreen) { return; }

		final ClientPlayerEntity player = client.player;
		if (player == null) { return; }

		Window window = client.getWindow();
		TextRenderer textRenderer = client.textRenderer;
		MatrixStack stack = drawContext.getMatrices();
		stack.push();

		final String[] strings = getTimerText(CONFIG.timeType().statType, CONFIG.timeType().stat);

		final ModConfigModel.Position position = CONFIG.position();

		final boolean useCustomPosition = position == ModConfigModel.Position.CUSTOM;
		final int x = useCustomPosition ? CONFIG.customPosition.x() : position.x;
		final int y = useCustomPosition ? CONFIG.customPosition.y() : position.y;

		int yOffset = 0;
		if (y < 0) {
			yOffset = -10 * (strings.length - 1);
		}
		for (final String string : strings) {
			int stringX = x;
			if (x < 0) {
				stringX = window.getScaledWidth() + x - textRenderer.getWidth(string);
			}
			int stringY = y;
			if (y < 0) {
				stringY = window.getScaledHeight() + (y + yOffset);
			} else {
				stringY += yOffset;
			}
			yOffset += 10;

			drawContext.drawText(textRenderer, string, stringX, stringY, Integer.parseInt(CONFIG.colorHex(), 16), CONFIG.textShadow());
		}

		stack.pop();
	}

	private static boolean shouldHardcoreDeathTimeFreeze() {
		if (CONFIG.timeType().stat != Stats.TOTAL_WORLD_TIME) { return false; }
		if (!CONFIG.freezeOnHardcoreDeath()) { return false; }

		assert client.player != null;
		if (client.player.getStatHandler()
				.getStat(Stats.CUSTOM, Stats.DEATHS) == 0) { return false; }
		return client.player.clientWorld.getLevelProperties()
				.isHardcore();
	}

	private static String[] getTimerText(StatType<Identifier> statType, Identifier stat) {
		int time = Timer.getTime(statType, stat, shouldHardcoreDeathTimeFreeze());

		return StringUtils.formatTime(time, CONFIG.format())
				.split("\\\\n");
	}
}
