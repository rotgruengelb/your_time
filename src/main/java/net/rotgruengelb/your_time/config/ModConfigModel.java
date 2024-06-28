package net.rotgruengelb.your_time.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.RegexConstraint;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

@Modmenu(modId = "your_time")
@Config(name = "your_time", wrapperName = "ModConfig")
public class ModConfigModel {

	public boolean enabled = true;
	public TimeType timeType = TimeType.TOTAL;
	public String format = "%tH%:%m%:%s%";
	@RegexConstraint("^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$") public String colorHex = "ffffff";
	public boolean textShadow = true;
	public Position position = Position.TOP_LEFT;
	@Nest public CustomPosition customPosition = new CustomPosition();
	public boolean freezeOnHardcoreDeath = false;

	public enum TimeType {
		TOTAL(Stats.CUSTOM, Stats.TOTAL_WORLD_TIME),
		SESSION(Stats.CUSTOM, Stats.PLAY_TIME),
		SINCE_DEATH(Stats.CUSTOM, Stats.TIME_SINCE_DEATH);

		public final StatType<Identifier> statType;
		public final Identifier stat;

		TimeType(StatType<Identifier> statType, Identifier stat) {
			this.statType = statType;
			this.stat = stat;
		}
	}

	public enum Position {
		TOP_LEFT(10, 10),
		TOP_RIGHT(-10, 10),
		BOTTOM_LEFT(10, -16),
		BOTTOM_RIGHT(-10, -16),
		CUSTOM(0, 0);

		public final int x;
		public final int y;

		Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static class CustomPosition {
		public int x = 0;
		public int y = 0;
	}
}