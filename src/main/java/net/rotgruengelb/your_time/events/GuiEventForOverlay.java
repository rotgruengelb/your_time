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

		String string = requestString(Your_Time.CONFIG.timeType().statType, Your_Time.CONFIG.timeType().stat);
		ModConfigModel.Position position = Your_Time.CONFIG.position();
		int x = position.x;
		int y = position.y;
		if (position == ModConfigModel.Position.CUSTOM) {
			x = Your_Time.CONFIG.customPosition.x();
			y = Your_Time.CONFIG.customPosition.y();
		}
		if (x < 0) { x = window.getScaledWidth() + position.x - textRenderer.getWidth(string); }
		if (y < 0) { y = window.getScaledHeight() + position.y; }

		drawText(textRenderer, drawContext, string, x, y, Integer.parseInt(Your_Time.CONFIG.colorHex(), 16), true);

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
