package net.grenning.pool_scorekeeper.cowboy;

import net.grenning.pool_scorekeeper.PoolActivity;
import net.grenning.pool_scorekeeper.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class CowboyPoolStartActivity extends PoolActivity {

	private static final int[] NAME_IDS = {
			R.id.player1Name, R.id.player2Name, R.id.player3Name, R.id.player4Name
	};
	private static final int[] POINTS_IDS = {
			R.id.player1PointsToWin, R.id.player2PointsToWin,
			R.id.player3PointsToWin, R.id.player4PointsToWin
	};
	private static final int[] ROW_IDS = {
			R.id.player1Row, R.id.player2Row, R.id.player3Row, R.id.player4Row
	};
	private static final int[] NAME_DEFAULTS = {
			R.string.default_player1Name, R.string.default_player2Name,
			R.string.default_player3Name, R.string.default_player4Name
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cowboy_pool_start);
		loadSetup();
		MaterialButtonToggleGroup group = findViewById(R.id.playerCountGroup);
		group.addOnButtonCheckedListener((g, id, checked) -> {
			if (checked) {
				showPlayerRows(playerCount());
			}
		});
		showPlayerRows(playerCount());
		selectAllOnFocus(NAME_IDS);
		selectAllOnFocus(POINTS_IDS);
		findViewById(R.id.resumeCowboyButton).setEnabled(CowboyStore.hasUnfinishedGame(this));
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveSetup();
	}

	public void startCowboy(View view) {
		saveSetup();
		startActivity(CowboyStore.scoreboardIntent(this, false));
	}

	public void resumeCowboy(View view) {
		saveSetup();
		startActivity(CowboyStore.scoreboardIntent(this, true));
	}

	private int playerCount() {
		MaterialButtonToggleGroup group = findViewById(R.id.playerCountGroup);
		int id = group.getCheckedButtonId();
		if (id == R.id.players3) {
			return 3;
		}
		if (id == R.id.players4) {
			return 4;
		}
		return 2;
	}

	private void showPlayerRows(int count) {
		for (int i = 0; i < ROW_IDS.length; i++) {
			findViewById(ROW_IDS[i]).setVisibility(i < count ? View.VISIBLE : View.GONE);
		}
	}

	private void loadSetup() {
		SharedPreferences prefs = CowboyStore.prefs(this);
		int count = prefs.getInt(CowboyStore.PLAYER_COUNT, 2);
		MaterialButtonToggleGroup group = findViewById(R.id.playerCountGroup);
		if (count == 3) {
			group.check(R.id.players3);
		} else if (count == 4) {
			group.check(R.id.players4);
		} else {
			group.check(R.id.players2);
		}
		for (int i = 0; i < 4; i++) {
			setText(NAME_IDS[i], prefs.getString(CowboyStore.nameKey(i), ""), NAME_DEFAULTS[i]);
			setText(POINTS_IDS[i], prefs.getString(CowboyStore.pointsKey(i), ""),
					R.string.cowboy_default_points_to_win);
		}
	}

	private void saveSetup() {
		SharedPreferences.Editor editor = CowboyStore.prefs(this).edit();
		editor.putInt(CowboyStore.PLAYER_COUNT, playerCount());
		for (int i = 0; i < 4; i++) {
			editor.putString(CowboyStore.nameKey(i), textOf(NAME_IDS[i]));
			editor.putString(CowboyStore.pointsKey(i), textOf(POINTS_IDS[i]));
		}
		editor.apply();
	}

	private void selectAllOnFocus(int... ids) {
		for (int id : ids) {
			EditText field = findViewById(id);
			field.setSelectAllOnFocus(true);
			field.setOnFocusChangeListener((view, hasFocus) -> {
				if (hasFocus) {
					view.post(() -> ((EditText) view).selectAll());
				}
			});
		}
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
}
