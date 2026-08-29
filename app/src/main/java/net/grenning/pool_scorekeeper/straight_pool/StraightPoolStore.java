package net.grenning.pool_scorekeeper.straight_pool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public final class StraightPoolStore {

	static final String PREFS_NAME = "straight_pool_game";
	static final String PLAYER1_NAME = "setupPlayer1Name";
	static final String PLAYER2_NAME = "setupPlayer2Name";
	static final String PLAYER1_POINTS = "setupPlayer1Points";
	static final String PLAYER2_POINTS = "setupPlayer2Points";
	static final String GAME_IN_PROGRESS = "gameInProgress";

	private StraightPoolStore() {
	}

	public static SharedPreferences prefs(Context context) {
		return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}

	public static boolean hasUnfinishedGame(Context context) {
		return prefs(context).getBoolean(GAME_IN_PROGRESS, false);
	}

	public static void saveSetup(Context context, String player1Name, String player2Name,
			String player1Points, String player2Points) {
		prefs(context).edit()
				.putString(PLAYER1_NAME, player1Name)
				.putString(PLAYER2_NAME, player2Name)
				.putString(PLAYER1_POINTS, player1Points)
				.putString(PLAYER2_POINTS, player2Points)
				.apply();
	}

	public static Intent scoreboardIntent(Context context, boolean resume) {
		SharedPreferences prefs = prefs(context);
		Intent intent = new Intent(context, GameScoreActivity.class);
		intent.putExtra("player1Name", prefs.getString(PLAYER1_NAME, ""));
		intent.putExtra("player2Name", prefs.getString(PLAYER2_NAME, ""));
		intent.putExtra("player1PointsToWin", prefs.getString(PLAYER1_POINTS, "50"));
		intent.putExtra("player2PointsToWin", prefs.getString(PLAYER2_POINTS, "50"));
		intent.putExtra("resume", resume);
		return intent;
	}
}
