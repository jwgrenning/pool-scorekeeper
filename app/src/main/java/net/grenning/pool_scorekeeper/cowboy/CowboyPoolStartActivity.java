package net.grenning.pool_scorekeeper.cowboy;

import net.grenning.pool_scorekeeper.PoolActivity;
import net.grenning.pool_scorekeeper.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class CowboyPoolStartActivity extends PoolActivity {

	private static final int[] NAME_IDS = {
			R.id.player1Name, R.id.player2Name, R.id.player3Name, R.id.player4Name
	};
	private static final int[] ROW_IDS = {
			R.id.player1Row, R.id.player2Row, R.id.player3Row, R.id.player4Row
	};
	private static final int[] NAME_DEFAULTS = {
			R.string.default_player1Name, R.string.default_player2Name,
			R.string.default_player3Name, R.string.default_player4Name
	};

	private boolean caromsEdited;
	private boolean updatingCaroms;

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
		selectAllOnFocus(R.id.cowboyBallCount, R.id.cowboyCaromCount);
		wireBallCaromDefault();
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
		}
		String balls = prefs.getString(CowboyStore.BALL_COUNT, "");
		if (balls == null || balls.isEmpty()) {
			balls = prefs.getString(CowboyStore.pointsKey(0), "");
		}
		setText(R.id.cowboyBallCount, balls, R.string.cowboy_default_points_to_win);
		int ballCount = parseBalls(textOf(R.id.cowboyBallCount));
		int defaultCaroms = CowboyPlayer.defaultCaroms(ballCount);
		String caroms = prefs.getString(CowboyStore.CAROM_COUNT, "");
		if (caroms == null || caroms.isEmpty()) {
			caroms = Integer.toString(defaultCaroms);
		}
		((EditText) findViewById(R.id.cowboyCaromCount)).setText(caroms);
		caromsEdited = parseCaroms(caroms) != defaultCaroms;
		MaterialButtonToggleGroup lastShot = findViewById(R.id.lastShotGroup);
		boolean special = prefs.getBoolean(CowboyStore.SPECIAL_LAST_SHOT, true);
		lastShot.check(special ? R.id.lastShotYes : R.id.lastShotNo);
	}

	private void saveSetup() {
		SharedPreferences.Editor editor = CowboyStore.prefs(this).edit();
		editor.putInt(CowboyStore.PLAYER_COUNT, playerCount());
		for (int i = 0; i < 4; i++) {
			editor.putString(CowboyStore.nameKey(i), textOf(NAME_IDS[i]));
		}
		editor.putString(CowboyStore.BALL_COUNT, textOf(R.id.cowboyBallCount));
		editor.putString(CowboyStore.CAROM_COUNT, textOf(R.id.cowboyCaromCount));
		editor.putBoolean(CowboyStore.SPECIAL_LAST_SHOT,
				((MaterialButtonToggleGroup) findViewById(R.id.lastShotGroup))
						.getCheckedButtonId() != R.id.lastShotNo);
		editor.apply();
	}

	private void wireBallCaromDefault() {
		EditText balls = findViewById(R.id.cowboyBallCount);
		EditText caroms = findViewById(R.id.cowboyCaromCount);
		balls.addTextChangedListener(new AfterChange() {
			@Override
			public void afterTextChanged(Editable s) {
				if (caromsEdited) {
					return;
				}
				updatingCaroms = true;
				caroms.setText(Integer.toString(CowboyPlayer.defaultCaroms(parseBalls(s.toString()))));
				updatingCaroms = false;
			}
		});
		caroms.addTextChangedListener(new AfterChange() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!updatingCaroms) {
					caromsEdited = true;
				}
			}
		});
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

	private static int parseBalls(String value) {
		try {
			int points = Integer.parseInt(value);
			return points > 0 ? Math.min(150, points) : 50;
		} catch (NumberFormatException e) {
			return 50;
		}
	}

	private static int parseCaroms(String value) {
		try {
			int points = Integer.parseInt(value);
			return Math.max(0, Math.min(50, points));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private abstract static class AfterChange implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}
}
