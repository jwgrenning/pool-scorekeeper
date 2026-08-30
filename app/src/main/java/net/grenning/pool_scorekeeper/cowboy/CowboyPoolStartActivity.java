package net.grenning.pool_scorekeeper.cowboy;

import net.grenning.pool_scorekeeper.PoolActivity;
import net.grenning.pool_scorekeeper.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class CowboyPoolStartActivity extends PoolActivity {

	private static final int[] NAME_IDS = {
			R.id.player1Name, R.id.player2Name, R.id.player3Name, R.id.player4Name
	};
	private static final int[] BALL_IDS = {
			R.id.player1Balls, R.id.player2Balls, R.id.player3Balls, R.id.player4Balls
	};
	private static final int[] CAROM_IDS = {
			R.id.player1Caroms, R.id.player2Caroms, R.id.player3Caroms, R.id.player4Caroms
	};
	private static final int[] LAST_SHOT_IDS = {
			R.id.player1LastShot, R.id.player2LastShot,
			R.id.player3LastShot, R.id.player4LastShot
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
		selectAllOnFocus(BALL_IDS);
		selectAllOnFocus(CAROM_IDS);
		wireBallCaromDefaults();
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

	public void resetFifty(View view) {
		resetRaces(50);
	}

	public void resetHundred(View view) {
		resetRaces(100);
	}

	private void resetRaces(int balls) {
		String ballText = Integer.toString(balls);
		for (int i = 0; i < BALL_IDS.length; i++) {
			((EditText) findViewById(BALL_IDS[i])).setText(ballText);
		}
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
			int balls = CowboyStore.ballsOf(prefs, i);
			((EditText) findViewById(BALL_IDS[i])).setText(Integer.toString(balls));
			int caroms = CowboyStore.caromsOf(prefs, i, balls);
			((EditText) findViewById(CAROM_IDS[i])).setText(Integer.toString(caroms));
			((CheckBox) findViewById(LAST_SHOT_IDS[i])).setChecked(CowboyStore.lastShotOf(prefs, i));
		}
	}

	private void saveSetup() {
		SharedPreferences.Editor editor = CowboyStore.prefs(this).edit();
		editor.putInt(CowboyStore.PLAYER_COUNT, playerCount());
		for (int i = 0; i < 4; i++) {
			editor.putString(CowboyStore.nameKey(i), textOf(NAME_IDS[i]));
			editor.putString(CowboyStore.ballsKey(i), textOf(BALL_IDS[i]));
			editor.putString(CowboyStore.caromsKey(i), textOf(CAROM_IDS[i]));
			editor.putBoolean(CowboyStore.lastShotKey(i),
					((CheckBox) findViewById(LAST_SHOT_IDS[i])).isChecked());
		}
		editor.apply();
	}

	private void wireBallCaromDefaults() {
		for (int i = 0; i < 4; i++) {
			EditText balls = findViewById(BALL_IDS[i]);
			EditText caroms = findViewById(CAROM_IDS[i]);
			balls.addTextChangedListener(new AfterChange() {
				@Override
				public void afterTextChanged(Editable s) {
					caroms.setText(Integer.toString(
							CowboyPlayer.defaultCaroms(CowboyStore.parseBalls(s.toString()))));
				}
			});
		}
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

	private abstract static class AfterChange implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}
}
