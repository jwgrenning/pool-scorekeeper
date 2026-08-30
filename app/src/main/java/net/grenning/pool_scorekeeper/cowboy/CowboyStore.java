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

	public static Intent scoreboardIntent(Context context, boolean resume) {
		Intent intent = new Intent(context, CowboyScoreActivity.class);
		intent.putExtra("resume", resume);
		return intent;
	}
}
