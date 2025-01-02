package net.rotgruengelb.your_time;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.rotgruengelb.your_time.TimerGuiOverlay.client;

class Timer {
	private static StatType<Identifier> statType;
	private static Identifier stat;
	private static int syncedTime;
	private static int syncedTimeSinceDeath;
	private static int lastSyncedTime;

	public static int getTime(StatType<Identifier> statType, Identifier stat, boolean hardcoreDeathFreeze) {
		requestStats();

		int time = getTimeStat(statType, stat);

		if (hardcoreDeathFreeze) {
			int timeSinceDeath = getTimeStat(Stats.CUSTOM, Stats.TIME_SINCE_DEATH);
			time -= timeSinceDeath;
		}
		return time;
	}

	public static int getTimeExperimental(StatType<Identifier> statType, Identifier stat, boolean hardcoreDeathFreeze) {
		if (statType != Timer.statType || stat != Timer.stat) {
			Timer.statType = statType;
			Timer.stat = stat;
			syncTime();
			return getTimeExperimental(statType, stat, hardcoreDeathFreeze);
		}

		final int currentTime = (int) (System.currentTimeMillis() / 1000) * 20;
		final int timeSinceLastSync = currentTime - lastSyncedTime;

		if (timeSinceLastSync >= 200) {
			syncTime();
			return getTimeExperimental(statType, stat, hardcoreDeathFreeze);
		}

		client.player.sendMessage(Text.of("Time: " + syncedTime + " + " + timeSinceLastSync), false);
		final int time = syncedTime + timeSinceLastSync;

		if (hardcoreDeathFreeze) {
			return time - (syncedTimeSinceDeath + timeSinceLastSync);
		}
		return time;
	}

	private static void syncTime() {
		// client.player.sendMessage(Text.of("Syncing time..."), false);
		requestStats();

		Timer.lastSyncedTime = (int) (System.currentTimeMillis() * 20 ) / 1000;

		Timer.syncedTime = client.player.getStatHandler().getStat(statType, stat);
		Timer.syncedTimeSinceDeath = client.player.getStatHandler().getStat(Stats.CUSTOM, Stats.TIME_SINCE_DEATH);
	}

	private static void requestStats() {
		ClientStatusC2SPacket packet = new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS);
		ClientPlayNetworkHandler network = client.getNetworkHandler();
		if (network != null) { network.sendPacket(packet); }
	}

	private static int getTimeStat(StatType<Identifier> statType, Identifier stat) {
		return client.player.getStatHandler().getStat(statType, stat);
	}
}
