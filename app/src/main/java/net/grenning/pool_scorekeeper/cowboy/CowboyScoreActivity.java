package net.grenning.pool_scorekeeper.cowboy;

import net.grenning.pool_scorekeeper.AndroidGameFieldSaver;
import net.grenning.pool_scorekeeper.PoolActivity;
import net.grenning.pool_scorekeeper.R;
import net.grenning.pool_scorekeeper.straight_pool.BeadRackView;
import net.grenning.pool_scorekeeper.straight_pool.BeadScore;
import net.grenning.pool_scorekeeper.straight_pool.GameSummaryActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CowboyScoreActivity extends PoolActivity {

	private static final int[] ROW_IDS = {
			R.id.player1Row, R.id.player2Row, R.id.player3Row, R.id.player4Row
	};
	private static final int[] NAME_IDS = {
			R.id.player1Name, R.id.player2Name, R.id.player3Name, R.id.player4Name
	};
	private static final int[] SCORE_IDS = {
			R.id.player1Score, R.id.player2Score, R.id.player3Score, R.id.player4Score
	};
	private static final int[] BEAD_IDS = {
			R.id.player1Beads, R.id.player2Beads, R.id.player3Beads, R.id.player4Beads
	};

	private CowboyGame game;
	private AndroidGameFieldSaver gameSaver;
	private boolean restoreOnStart;
	private androidx.appcompat.app.AlertDialog winDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_cowboy_score);

		MaterialToolbar toolbar = findViewById(R.id.cowboyToolbar);
		setSupportActionBar(toolbar);

		SharedPreferences prefs = CowboyStore.prefs(this);
		int count = Math.min(4, Math.max(2, prefs.getInt(CowboyStore.PLAYER_COUNT, 2)));
		CowboyPlayer[] players = new CowboyPlayer[count];
		for (int i = 0; i < count; i++) {
			players[i] = new CowboyPlayer();
			players[i].name = prefs.getString(CowboyStore.nameKey(i), getString(defaultName(i)));
			players[i].ballCount = CowboyStore.ballsOf(prefs, i);
			players[i].caromCount = CowboyStore.caromsOf(prefs, i, players[i].ballCount);
			players[i].specialLastShot = CowboyStore.lastShotOf(prefs, i);
		}
		game = new CowboyGame(players);
		restoreOnStart = getIntent().getBooleanExtra("resume", false) || savedInstanceState != null;

		findViewById(R.id.caromButton).setOnLongClickListener(view -> {
			vibrate(view);
			if (game.phase() == CowboyGame.Phase.WIN) {
				showWinShotReminder();
			} else {
				apply(game.caromThree());
			}
			return true;
		});
		refreshBoard();
	}

	@Override
	protected void onStart() {
		super.onStart();
		gameSaver = new AndroidGameFieldSaver(CowboyStore.prefs(this));
		if (restoreOnStart) {
			game.restore(gameSaver);
		}
		persistGame();
		refreshBoard();
	}

	@Override
	protected void onPause() {
		super.onPause();
		persistGame();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_cowboy_score, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.cowboy_rules_button) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://www.pool-table-rules.com/bcacowboy.php")));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void ball1Clicked(View view) {
		vibrate(view);
		apply(game.pocket(1));
	}

	public void ball3Clicked(View view) {
		vibrate(view);
		apply(game.pocket(3));
	}

	public void ball5Clicked(View view) {
		vibrate(view);
		apply(game.pocket(5));
	}

	public void caromClicked(View view) {
		vibrate(view);
		if (game.phase() == CowboyGame.Phase.WIN) {
			showWinShotReminder();
		} else {
			apply(game.caromTwo());
		}
	}

	private void showWinShotReminder() {
		if (isFinishing() || isDestroyed()) {
			return;
		}
		if (winDialog != null && winDialog.isShowing()) {
			return;
		}
		winDialog = new MaterialAlertDialogBuilder(this)
				.setTitle(R.string.cowboy_win_shot_title)
				.setMessage(R.string.cowboy_win_shot_rule)
				.setPositiveButton(R.string.cowboy_win_shot_made, (dialog, which) -> apply(game.winShot()))
				.setNegativeButton(android.R.string.cancel, null)
				.setOnDismissListener(dialog -> winDialog = null)
				.show();
	}

	public void multiClicked(View view) {
		vibrate(view);
		if (game.phase() == CowboyGame.Phase.WIN) {
			return;
		}
		showComboDialog();
	}

	private void showComboDialog() {
		View body = LayoutInflater.from(this).inflate(R.layout.dialog_cowboy_combo, null);
		MaterialButtonToggleGroup balls = body.findViewById(R.id.comboBalls);
		MaterialButtonToggleGroup caroms = body.findViewById(R.id.comboCaroms);
		MaterialButton ball1 = body.findViewById(R.id.comboBall1);
		MaterialButton ball3 = body.findViewById(R.id.comboBall3);
		MaterialButton ball5 = body.findViewById(R.id.comboBall5);
		TextView total = body.findViewById(R.id.comboTotal);
		MaterialButton enter = body.findViewById(R.id.comboEnter);
		boolean pocketsAllowed = game.phase() == CowboyGame.Phase.MIXED;
		ball1.setEnabled(pocketsAllowed);
		ball3.setEnabled(pocketsAllowed);
		ball5.setEnabled(pocketsAllowed);
		balls.setEnabled(pocketsAllowed);

		androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
				.setTitle(R.string.cowboy_combo_title)
				.setView(body)
				.create();

		Runnable refresh = () -> {
			int points = CowboyGame.comboPoints(
					pocketsAllowed && ball1.isChecked(),
					pocketsAllowed && ball3.isChecked(),
					pocketsAllowed && ball5.isChecked(),
					checkedCaroms(caroms));
			if (points > 0) {
				total.setText(getString(R.string.cowboy_combo_total, points));
			} else {
				total.setText("");
			}
			enter.setEnabled(points > 0);
		};
		balls.addOnButtonCheckedListener((group, id, checked) -> refresh.run());
		caroms.addOnButtonCheckedListener((group, id, checked) -> refresh.run());
		enter.setOnClickListener(v -> {
			boolean one = pocketsAllowed && ball1.isChecked();
			boolean three = pocketsAllowed && ball3.isChecked();
			boolean five = pocketsAllowed && ball5.isChecked();
			dialog.dismiss();
			apply(game.combo(one, three, five, checkedCaroms(caroms)));
		});
		refresh.run();
		dialog.show();
	}

	public void missClicked(View view) {
		vibrate(view);
		apply(game.miss());
	}

	public void foulClicked(View view) {
		vibrate(view);
		apply(game.foul());
	}

	public void undoClicked(View view) {
		vibrate(view);
		game.undo();
		afterChange();
	}

	public void redoClicked(View view) {
		vibrate(view);
		game.redo();
		afterChange();
	}

	public void newGameClicked(View view) {
		startActivity(new Intent(this, CowboyPoolStartActivity.class));
		finish();
	}

	public void gameSummaryClicked(View view) {
		startActivity(GameSummaryActivity.intent(this, cowboySubject(), cowboyBody()));
	}

	private void apply(CowboyGame.Result result) {
		if (result == CowboyGame.Result.WIN) {
			playWinApplause();
		}
		afterChange();
	}

	private void afterChange() {
		persistGame();
		refreshBoard();
	}

	private void persistGame() {
		if (gameSaver == null) {
			gameSaver = new AndroidGameFieldSaver(CowboyStore.prefs(this));
		}
		game.save(gameSaver);
		gameSaver.save(CowboyStore.GAME_IN_PROGRESS, !game.isOver());
		gameSaver.save(CowboyStore.PLAYER_COUNT, game.playerCount());
		for (int i = 0; i < game.playerCount(); i++) {
			CowboyPlayer player = game.player(i);
			gameSaver.save(CowboyStore.nameKey(i), player.name);
			gameSaver.save(CowboyStore.ballsKey(i), Integer.toString(player.ballCount));
			gameSaver.save(CowboyStore.caromsKey(i), Integer.toString(player.caromCount));
			gameSaver.save(CowboyStore.lastShotKey(i), player.specialLastShot);
		}
		gameSaver.persist();
	}

	private void refreshBoard() {
		int maxRace = 50;
		for (int i = 0; i < game.playerCount(); i++) {
			maxRace = Math.max(maxRace, game.player(i).caromLimit());
		}
		for (int i = 0; i < 4; i++) {
			boolean present = i < game.playerCount();
			findViewById(ROW_IDS[i]).setVisibility(present ? View.VISIBLE : View.GONE);
			if (!present) {
				continue;
			}
			CowboyPlayer player = game.player(i);
			TextView name = findViewById(NAME_IDS[i]);
			name.setText(player.name);
			boolean active = i == game.currentIndex() && !game.isOver();
			name.setTextColor(getResources().getColor(
					active ? R.color.active_player : R.color.ivory, getTheme()));
			TextView score = findViewById(SCORE_IDS[i]);
			if (player.inning > 0) {
				score.setText(player.score + "+" + player.inning);
			} else {
				score.setText(Integer.toString(player.score));
			}
			BeadRackView beads = findViewById(BEAD_IDS[i]);
			int normal = player.caromLimit();
			int shownScore = Math.min(player.score, normal);
			int shownPending = Math.min(player.inning, Math.max(0, normal - shownScore));
			beads.setRack(maxRace, BeadScore.spot(normal, maxRace), player.specialLastShot);
			beads.setBankedAndPending(shownScore, shownPending);
			beads.setWinBeadScored(player.specialLastShot && player.score >= player.raceTo());
		}

		MaterialToolbar toolbar = findViewById(R.id.cowboyToolbar);
		toolbar.setTitle(getString(R.string.inning_title) + game.inningNumber());
		TextView phase = findViewById(R.id.cowboyPhase);
		phase.setText(phaseLabel());

		MaterialButton carom = findViewById(R.id.caromButton);
		if (carom != null) {
			carom.setText(game.phase() == CowboyGame.Phase.WIN
					? R.string.cowboy_win_shot : R.string.cowboy_carom);
		}

		boolean over = game.isOver();
		View multi = findViewById(R.id.multiButton);
		if (multi != null) {
			multi.setEnabled(!over && game.phase() != CowboyGame.Phase.WIN);
		}
		findViewById(R.id.playingActions).setVisibility(over ? View.GONE : View.VISIBLE);
		findViewById(R.id.gameOverActions).setVisibility(over ? View.VISIBLE : View.GONE);
		boolean canUndo = game.canUndo();
		boolean canRedo = game.canRedo();
		View undo = findViewById(R.id.undoButton);
		View redo = findViewById(R.id.redoButton);
		if (undo != null) {
			undo.setEnabled(canUndo);
		}
		if (redo != null) {
			redo.setEnabled(canRedo);
		}
		View undoOver = findViewById(R.id.undoOverButton);
		View redoOver = findViewById(R.id.redoOverButton);
		if (undoOver != null) {
			undoOver.setEnabled(canUndo);
		}
		if (redoOver != null) {
			redoOver.setEnabled(canRedo);
		}
	}

	private static int checkedCaroms(MaterialButtonToggleGroup caroms) {
		int caromId = caroms.getCheckedButtonId();
		if (caromId == R.id.comboCarom1) {
			return 1;
		}
		if (caromId == R.id.comboCarom2) {
			return 2;
		}
		if (caromId == R.id.comboCarom3) {
			return 3;
		}
		return 0;
	}

	private String phaseLabel() {
		CowboyGame.Phase phase = game.phase();
		if (phase == CowboyGame.Phase.WIN) {
			return getString(R.string.cowboy_phase_win);
		}
		if (phase == CowboyGame.Phase.CAROMS) {
			return getString(R.string.cowboy_phase_caroms);
		}
		return getString(R.string.cowboy_phase_mixed);
	}

	private String cowboySubject() {
		StringBuilder subject = new StringBuilder();
		for (int i = 0; i < game.playerCount(); i++) {
			if (i > 0) {
				subject.append(" vs ");
			}
			subject.append(game.player(i).name);
		}
		return subject.toString();
	}

	private String cowboyBody() {
		StringBuilder body = new StringBuilder(cowboySubject()).append('\n');
		for (int i = 0; i < game.playerCount(); i++) {
			CowboyPlayer player = game.player(i);
			body.append(player.name).append(' ').append(player.score)
					.append('/').append(player.raceTo());
			if (player.hasWon()) {
				body.append(" *");
			}
			body.append('\n');
		}
		return body.toString();
	}

	private int defaultName(int index) {
		if (index == 1) {
			return R.string.default_player2Name;
		}
		if (index == 2) {
			return R.string.default_player3Name;
		}
		if (index == 3) {
			return R.string.default_player4Name;
		}
		return R.string.default_player1Name;
	}

	private void vibrate(View view) {
		view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
	}
}
