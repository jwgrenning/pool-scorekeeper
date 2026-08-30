package net.grenning.pool_scorekeeper.cowboy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public final class CowboyStore {

	static final String PREFS_NAME = "cowboy_pool_game";
	static final String PLAYER_COUNT = "playerCount";
	static final String GAME_IN_PROGRESS = "gameInProgress";
	static final String BALL_COUNT = "setupBallCount";
	static final String CAROM_COUNT = "setupCaromCount";
	static final String SPECIAL_LAST_SHOT = "setupSpecialLastShot";

	private CowboyStore() {
	}

	public static SharedPreferences prefs(Context context) {
		return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}

	public static boolean hasUnfinishedGame(Context context) {
		return prefs(context).getBoolean(GAME_IN_PROGRESS, false);
	}

	public static String nameKey(int index) {
		return "setupPlayer" + (index + 1) + "Name";
	}

	public static String pointsKey(int index) {
		return "setupPlayer" + (index + 1) + "Points";
	}

	public static String ballsKey(int index) {
		return "setupPlayer" + (index + 1) + "Balls";
	}

	public static String caromsKey(int index) {
		return "setupPlayer" + (index + 1) + "Caroms";
	}

	public static String lastShotKey(int index) {
		return "setupPlayer" + (index + 1) + "LastShot";
	}

	public static int ballsOf(SharedPreferences prefs, int index) {
		String value = firstNonEmpty(prefs.getString(ballsKey(index), ""),
				prefs.getString(BALL_COUNT, ""),
				prefs.getString(pointsKey(index), ""));
		return parseBalls(value);
	}

	public static int caromsOf(SharedPreferences prefs, int index, int balls) {
		String value = firstNonEmpty(prefs.getString(caromsKey(index), ""),
				prefs.getString(CAROM_COUNT, ""));
		if (value.isEmpty()) {
			return CowboyPlayer.defaultCaroms(balls);
		}
		return parseCaroms(value);
	}

	public static boolean lastShotOf(SharedPreferences prefs, int index) {
		if (prefs.contains(lastShotKey(index))) {
			return prefs.getBoolean(lastShotKey(index), true);
		}
		if (prefs.contains(SPECIAL_LAST_SHOT)) {
			return prefs.getBoolean(SPECIAL_LAST_SHOT, true);
		}
		return true;
	}

	private static String firstNonEmpty(String... values) {
		for (String value : values) {
			if (value != null && !value.isEmpty()) {
				return value;
			}
		}
		return "";
	}

	static int parseBalls(String value) {
		try {
			int points = Integer.parseInt(value);
			return points > 0 ? Math.min(150, points) : 50;
		} catch (NumberFormatException e) {
			return 50;
		}
	}

	static int parseCaroms(String value) {
		try {
			int points = Integer.parseInt(value);
			return Math.max(0, Math.min(50, points));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static Intent scoreboardIntent(Context context, boolean resume) {
		Intent intent = new Intent(context, CowboyScoreActivity.class);
		intent.putExtra("resume", resume);
		return intent;
	}
}
