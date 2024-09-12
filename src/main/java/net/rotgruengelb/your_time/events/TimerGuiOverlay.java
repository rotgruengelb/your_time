package net.rotgruengelb.your_time.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.rotgruengelb.nixienaut.util.StringUtils;
import net.rotgruengelb.your_time.config.ModConfigModel;

import static net.rotgruengelb.your_time.Your_Time.CONFIG;

public class TimerGuiOverlay {
	private static final MinecraftClient client = MinecraftClient.getInstance();

	public static void renderOverlay(DrawContext drawContext, float delta) {
		renderOverlay(drawContext);
	}

	public static void renderOverlay(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		renderOverlay(drawContext);
	}

	public static void renderOverlay(DrawContext drawContext) {
		if (!CONFIG.enabled()) { return; }
		if (client.getDebugHud().shouldShowDebugHud()) { return; }
		if (client.currentScreen instanceof StatsScreen) { return; }

		final ClientPlayerEntity player = client.player;
		if (player == null) { return; }

		Window window = client.getWindow();
		TextRenderer textRenderer = client.textRenderer;
		MatrixStack stack = drawContext.getMatrices();
		stack.push();

		final String[] strings = requestTimerString(CONFIG.timeType().statType, CONFIG.timeType().stat);

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

	private static boolean shouldUseHardcoreFreezeTime(ClientPlayerEntity player) {
		if (CONFIG.timeType().stat != Stats.TOTAL_WORLD_TIME) { return false; }
		if (!CONFIG.freezeOnHardcoreDeath()) { return false; }
		if (player.getStatHandler().getStat(Stats.CUSTOM, Stats.DEATHS) == 0) { return false; }
		return player.clientWorld.getLevelProperties().isHardcore();
	}

	private static <T> String[] requestTimerString(StatType<T> statType, T stat) {
		ClientStatusC2SPacket packet = new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS);
		ClientPlayNetworkHandler network = client.getNetworkHandler();
		if (network != null) { network.sendPacket(packet); }
		int time = client.player.getStatHandler().getStat(statType, stat);
		if (shouldUseHardcoreFreezeTime(client.player)) {
			time = time - client.player.getStatHandler().getStat(Stats.CUSTOM, Stats.TIME_SINCE_DEATH);
		}
		return StringUtils.formatTime(time, CONFIG.format()).split("\\\\n");
	}
}
