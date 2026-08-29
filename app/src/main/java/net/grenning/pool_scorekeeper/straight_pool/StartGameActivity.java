package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.PoolActivity;
import net.grenning.pool_scorekeeper.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class StartGameActivity extends PoolActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_straight_pool_start);
		loadSetup();
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveSetup();
	}

	public void startStraightPool(View view) {
		saveSetup();
		startActivity(StraightPoolStore.scoreboardIntent(this, false));
	}

	public void resumeStraightPool(View view) {
		saveSetup();
		startActivity(StraightPoolStore.scoreboardIntent(this, true));
	}

	public void swapPlayers(View view) {
		swapText(R.id.player1Name, R.id.player2Name);
		swapText(R.id.player1PointsToWin, R.id.player2PointsToWin);
	}

	private void loadSetup() {
		SharedPreferences prefs = StraightPoolStore.prefs(this);
		setText(R.id.player1Name, prefs.getString(StraightPoolStore.PLAYER1_NAME, ""),
				R.string.default_player1Name);
		setText(R.id.player2Name, prefs.getString(StraightPoolStore.PLAYER2_NAME, ""),
				R.string.default_player2Name);
		setText(R.id.player1PointsToWin, prefs.getString(StraightPoolStore.PLAYER1_POINTS, ""),
				R.string.straight_pool_default_points_to_win);
		setText(R.id.player2PointsToWin, prefs.getString(StraightPoolStore.PLAYER2_POINTS, ""),
				R.string.straight_pool_default_points_to_win);
	}

	private void saveSetup() {
		StraightPoolStore.saveSetup(this,
				textOf(R.id.player1Name),
				textOf(R.id.player2Name),
				textOf(R.id.player1PointsToWin),
				textOf(R.id.player2PointsToWin));
	}

	private void setText(int id, String value, int defaultValue) {
		EditText field = findViewById(id);
		if (value == null || value.isEmpty()) {
			field.setText(defaultValue);
		} else {
			field.setText(value);
		}
	}

	private String textOf(int id) {
		return ((EditText) findViewById(id)).getText().toString();
	}

	private void swapText(int id1, int id2) {
		EditText text1 = findViewById(id1);
		EditText text2 = findViewById(id2);
		String s1 = text1.getText().toString();
		String s2 = text2.getText().toString();
		text1.setText(s2);
		text2.setText(s1);
	}
}
