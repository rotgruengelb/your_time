package net.rotgruengelb.your_time.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.StatType;
import net.minecraft.text.Text;
import net.rotgruengelb.nixienaut.format.TimeFormatting;
import net.rotgruengelb.your_time.Your_Time;
import net.rotgruengelb.your_time.config.ModConfigModel;

public class GuiEventForOverlay {
	private static final MinecraftClient client = MinecraftClient.getInstance();

	public static void renderOverlay(DrawContext drawContext, float tickDelta) {
		if (!Your_Time.CONFIG.enabled()) { return; }
		if (client.getDebugHud().shouldShowDebugHud()) { return; }
		if (client.currentScreen instanceof StatsScreen) { return; }

		Window window = client.getWindow();
		TextRenderer textRenderer = client.textRenderer;
		MatrixStack stack = drawContext.getMatrices();
		stack.push();

		final String[] strings = requestString(Your_Time.CONFIG.timeType().statType, Your_Time.CONFIG.timeType().stat).split("\\\\n");

		final ModConfigModel.Position position = Your_Time.CONFIG.position();

		final boolean useCustomPosition = position == ModConfigModel.Position.CUSTOM;
		final int x = useCustomPosition ? Your_Time.CONFIG.customPosition.x() : position.x;
		final int y = useCustomPosition ? Your_Time.CONFIG.customPosition.y() : position.y;

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

			drawText(textRenderer, drawContext, string, stringX, stringY, Integer.parseInt(Your_Time.CONFIG.colorHex(), 16), true);
		}

		stack.pop();
	}

	private static void drawText(TextRenderer textRenderer, DrawContext drawContext, String string, int x, int y, int color, boolean shouldDrawShadow) {
		drawContext.drawText(textRenderer, Text.literal(string), x, y, color, shouldDrawShadow);
	}

	private static <T> String requestString(StatType<T> statType, T stat) {
		ClientStatusC2SPacket packet = new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS);
		ClientPlayNetworkHandler network = client.getNetworkHandler();
		if (network != null) {
			network.sendPacket(packet);
		}
		return TimeFormatting.formatTime(client.player.getStatHandler()
				.getStat(statType, stat), Your_Time.CONFIG.format());
	}
}
