package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.AndroidGameFieldSaver;
import net.grenning.pool_scorekeeper.PoolActivity;
import net.grenning.pool_scorekeeper.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class GameScoreActivity extends PoolActivity {

	GameScorer scorer;
	PlayerScorer player1Scorer;
	PlayerScorer player2Scorer;
	private AndroidGameFieldSaver gameSaver;
	private boolean restoreOnStart;
	private Animation winnerAnimation;
	private androidx.appcompat.app.AlertDialog rackDialog;
	private final Runnable hideShotMadeHintRunnable = this::hideShotMadeHint;

	GameView gameView = new GameView() {

		@Override
		public void inning(int inning) {
			MaterialToolbar toolbar = findViewById(R.id.scoreToolbar);
			toolbar.setTitle(getString(R.string.inning_title) + inning);
		}

		@Override
		public void suggestRerack() {
			showRerackSuggestion();
		}

		@Override
		public void oneBallOnTheTable() {
			setFieldById(R.id.ballsOnTheTable, getString(R.string.one_ball_left_on_the_table));
		}

		@Override
		public void ballsOnTheTable(int balls) {
			String message = balls + " " + getString(R.string.balls_left_on_the_table);
			setFieldById(R.id.ballsOnTheTable, message);
		}

		@Override
		public void gameOverApplause() {
			MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.applause);
			if (mp != null) {
				mp.setOnCompletionListener(MediaPlayer::release);
				mp.start();
			}
		}

		@Override
		public void theWinnerIs(int playerNumber) {
			setBlinkingByPlayerNumber(playerNumber);
		}

		@Override
		public void noWinner() {
			stopWinnerAnimation();
		}
	};

	PlayerView player1View = new PlayerView() {
		@Override
		public void score(int i) {
			setScoreAndBeads(R.id.player1Score, R.id.player1Beads, i);
		}

		@Override
		public void ballsNeededToWin(int ballsNeededToWin) {
			setFieldById(R.id.player1PointsToWin, ballsNeededToWin);
		}

		@Override
		public void rackScore(int player1RackScore) {
			setFieldById(R.id.player1BallsThisRack, player1RackScore);
		}

		@Override
		public void fouls(int count) {
			setFieldById(R.id.player1TotalFouls, count);
		}

		@Override
		public void consecutiveFouls(int count) {
			setFieldById(R.id.player1ConsecutiveFouls, count);
		}

		@Override
		public void makeActive() {
			setActiveById(R.id.player1Card);
		}

		@Override
		public void makeInactive() {
			setInactiveById(R.id.player1Card);
		}

		@Override
		public void currentRun(int count) {
			setFieldById(R.id.player1CurrentRun, count);
		}

		@Override
		public void longestRun(int count) {
			setFieldById(R.id.player1LongestRun, count);
		}

		@Override
		public void safesMade(int count) {
			setFieldById(R.id.player1SafesMade, count);
		}

		@Override
		public void safesMissed(int count) {
		}

		@Override
		public void consecutiveSafes(int count) {
		}

		@Override
		public void inningRecord(String string) {
		}

		@Override
		public void turnStartedAt(long epochMillis) {
			setTurnClock(R.id.player1TurnClock, epochMillis);
		}

		@Override
		public void tableTimeMillis(int millis) {
		}

		@Override
		public void turnCount(int count) {
		}

		@Override
		public void shotCount(int count) {
		}
	};

	PlayerView player2View = new PlayerView() {
		@Override
		public void score(int i) {
			setScoreAndBeads(R.id.player2Score, R.id.player2Beads, i);
		}

		@Override
		public void ballsNeededToWin(int ballsNeededToWin) {
			setFieldById(R.id.player2PointsToWin, ballsNeededToWin);
		}

		@Override
		public void rackScore(int rackScore) {
			setFieldById(R.id.player2BallsThisRack, rackScore);
		}

		@Override
		public void fouls(int count) {
			setFieldById(R.id.player2TotalFouls, count);
		}

		@Override
		public void consecutiveFouls(int count) {
			setFieldById(R.id.player2ConsecutiveFouls, count);
		}

		@Override
		public void makeActive() {
			setActiveById(R.id.player2Card);
		}

		@Override
		public void makeInactive() {
			setInactiveById(R.id.player2Card);
		}

		@Override
		public void currentRun(int count) {
			setFieldById(R.id.player2CurrentRun, count);
		}

		@Override
		public void longestRun(int count) {
			setFieldById(R.id.player2LongestRun, count);
		}

		@Override
		public void safesMade(int count) {
			setFieldById(R.id.player2SafesMade, count);
		}

		@Override
		public void safesMissed(int count) {
		}

		@Override
		public void consecutiveSafes(int count) {
		}

		@Override
		public void inningRecord(String string) {
		}

		@Override
		public void turnStartedAt(long epochMillis) {
			setTurnClock(R.id.player2TurnClock, epochMillis);
		}

		@Override
		public void tableTimeMillis(int millis) {
		}

		@Override
		public void turnCount(int count) {
		}

		@Override
		public void shotCount(int count) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_score_straight_pool);

		MaterialToolbar toolbar = findViewById(R.id.scoreToolbar);
		setSupportActionBar(toolbar);

		setPlayerName("player1Name", R.id.player1Name, R.string.default_player1Name);
		setPlayerName("player2Name", R.id.player2Name, R.string.default_player2Name);

		player1Scorer = new PlayerScorer(player1View, getNumberFieldFromIntent("player1PointsToWin"));
		player2Scorer = new PlayerScorer(player2View, getNumberFieldFromIntent("player2PointsToWin"));
		scorer = new GameScorer(gameView, player1Scorer, player2Scorer);

		restoreOnStart = getBooleanFieldFromIntent("resume") || savedInstanceState != null;
		findViewById(R.id.shotMadeButton).setOnLongClickListener(view -> {
			vibrate(view);
			hideShotMadeHint();
			showBallsPottedPicker();
			return true;
		});
		if (!restoreOnStart) {
			View hint = findViewById(R.id.shotMadeHint);
			hint.setOnClickListener(v -> hideShotMadeHint());
			hint.post(this::showShotMadeHint);
		}
		linkPlayerStatScrolls();
		refreshUndoButton();
		refreshGameOverChrome();
	}

	private void linkPlayerStatScrolls() {
		ScrollView player1Scroll = findViewById(R.id.player1Scroll);
		ScrollView player2Scroll = findViewById(R.id.player2Scroll);
		View.OnScrollChangeListener sync = (view, scrollX, scrollY, oldX, oldY) -> {
			ScrollView other = view.getId() == R.id.player1Scroll ? player2Scroll : player1Scroll;
			if (other.getScrollX() != scrollX || other.getScrollY() != scrollY) {
				other.scrollTo(scrollX, scrollY);
			}
		};
		player1Scroll.setOnScrollChangeListener(sync);
		player2Scroll.setOnScrollChangeListener(sync);
	}

	@Override
	protected void onDestroy() {
		View hint = findViewById(R.id.shotMadeHint);
		if (hint != null) {
			hint.removeCallbacks(hideShotMadeHintRunnable);
			hint.animate().cancel();
		}
		if (rackDialog != null) {
			rackDialog.setOnDismissListener(null);
			rackDialog.dismiss();
			rackDialog = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences prefs = StraightPoolStore.prefs(this);
		gameSaver = new AndroidGameFieldSaver(prefs);
		if (restoreOnStart) {
			scorer.populateFromPersistence(gameSaver);
			scorer.adjustRaces(
					getNumberFieldFromIntent("player1PointsToWin"),
					getNumberFieldFromIntent("player2PointsToWin"));
		}
		persistGame();
		refreshUndoButton();
		refreshGameOverChrome();
	}

	@Override
	protected void onPause() {
		super.onPause();
		persistGame();
	}

	private void persistGame() {
		if (scorer == null) {
			return;
		}
		if (gameSaver == null) {
			gameSaver = new AndroidGameFieldSaver(StraightPoolStore.prefs(this));
		}
		scorer.save(gameSaver);
		gameSaver.save(StraightPoolStore.PLAYER1_NAME, textOf(R.id.player1Name));
		gameSaver.save(StraightPoolStore.PLAYER2_NAME, textOf(R.id.player2Name));
		gameSaver.save(StraightPoolStore.GAME_IN_PROGRESS,
				!player1Scorer.wins() && !player2Scorer.wins());
		gameSaver.persist();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_score_straight_pool, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.straight_pool_rules_button) {
			showStraightPoolRules();
			return true;
		}
		if (id == R.id.general_pool_rules_button) {
			showGeneralPoolRules();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showRerackSuggestion() {
		if (isFinishing() || isDestroyed() || isGameOver()) {
			return;
		}
		if (rackDialog != null && rackDialog.isShowing()) {
			return;
		}

		int remaining = scorer.ballsOnTheTable();
		int message = remaining <= 0 ? R.string.rack_prompt_none : R.string.rack_prompt_one;
		rackDialog = new MaterialAlertDialogBuilder(this)
				.setTitle(R.string.rack_prompt_title)
				.setMessage(message)
				.setPositiveButton(R.string.rack_yes, (dialog, which) -> {
					scorer.newRack();
					afterScoreChange();
				})
				.setNegativeButton(R.string.rack_not_now, null)
				.setOnDismissListener(dialog -> rackDialog = null)
				.show();
	}

	private void showShotMadeHint() {
		if (isFinishing() || isDestroyed() || isGameOver()) {
			return;
		}
		View hint = findViewById(R.id.shotMadeHint);
		if (hint == null) {
			return;
		}
		hint.animate().cancel();
		hint.setVisibility(View.VISIBLE);
		hint.setAlpha(0f);
		hint.animate().alpha(1f).setDuration(250).start();
		hint.removeCallbacks(hideShotMadeHintRunnable);
		hint.postDelayed(hideShotMadeHintRunnable, 6500);
	}

	private void hideShotMadeHint() {
		View hint = findViewById(R.id.shotMadeHint);
		if (hint == null || hint.getVisibility() != View.VISIBLE) {
			return;
		}
		hint.removeCallbacks(hideShotMadeHintRunnable);
		hint.animate().cancel();
		hint.animate().alpha(0f).setDuration(200).withEndAction(() -> {
			if (!isDestroyed()) {
				hint.setVisibility(View.GONE);
				hint.setAlpha(1f);
			}
		}).start();
	}

	private void showGameSummary() {
		String player1Name = textOf(R.id.player1Name);
		String player2Name = textOf(R.id.player2Name);
		SummaryEmail summary = new SummaryEmail(scorer, player1Name, player2Name);
		startActivity(GameSummaryActivity.intent(this, summary.subject(this), summary.body(this)));
	}

	public void newGameButtonClicked(View view) {
		vibrate(view);
		persistGame();
		Intent intent = new Intent(this, StartGameActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public void gameSummaryButtonClicked(View view) {
		vibrate(view);
		showGameSummary();
	}

	private boolean isGameOver() {
		return player1Scorer != null && player2Scorer != null
				&& (player1Scorer.wins() || player2Scorer.wins());
	}

	private void refreshGameOverChrome() {
		View playing = findViewById(R.id.playingActions);
		View over = findViewById(R.id.gameOverActions);
		boolean gameOver = isGameOver();
		if (playing != null) {
			playing.setVisibility(gameOver ? View.GONE : View.VISIBLE);
		}
		if (over != null) {
			over.setVisibility(gameOver ? View.VISIBLE : View.GONE);
		}
		if (gameOver) {
			hideShotMadeHint();
			if (rackDialog != null && rackDialog.isShowing()) {
				rackDialog.dismiss();
			}
		}
	}

	private void stopWinnerAnimation() {
		TextView p1 = findViewById(R.id.player1Name);
		TextView p2 = findViewById(R.id.player2Name);
		p1.clearAnimation();
		p2.clearAnimation();
		winnerAnimation = null;
	}

	protected void setBlinkingByPlayerNumber(int playerNumber) {
		stopWinnerAnimation();
		TextView field = findViewById(playerNumber == 1 ? R.id.player1Name : R.id.player2Name);
		winnerAnimation = new AlphaAnimation(0.0f, 1.0f);
		winnerAnimation.setDuration(100);
		winnerAnimation.setStartOffset(20);
		winnerAnimation.setRepeatMode(Animation.REVERSE);
		winnerAnimation.setRepeatCount(Animation.INFINITE);
		field.startAnimation(winnerAnimation);
	}

	protected void setActiveById(int id) {
		MaterialCardView card = findViewById(id);
		int stroke = Math.round(4 * getResources().getDisplayMetrics().density);
		card.setStrokeWidth(stroke);
		card.setStrokeColor(getResources().getColor(R.color.active_player, getTheme()));
	}

	protected void setInactiveById(int id) {
		MaterialCardView card = findViewById(id);
		card.setStrokeWidth(0);
	}

	private void setScoreAndBeads(int scoreId, int beadId, int score) {
		setFieldById(scoreId, score);
		BeadRackView beads = findViewById(beadId);
		if (beads != null) {
			beads.setScore(score);
		}
	}

	private void setFieldById(int id, String value) {
		((TextView) findViewById(id)).setText(value);
	}

	private void setFieldById(int id, int value) {
		((TextView) findViewById(id)).setText(Integer.toString(value));
	}

	private void setTurnClock(int id, long epochMillis) {
		TextView field = findViewById(id);
		if (epochMillis <= 0) {
			field.setText("");
		} else {
			field.setText(android.text.format.DateFormat.getTimeFormat(this)
					.format(new java.util.Date(epochMillis)));
		}
	}

	private void setPlayerName(String player, int playerTextId, int defaultPlayer) {
		String name = getIntent().getStringExtra(player);
		if (name == null || name.equals("")) {
			String key = "player1Name".equals(player)
					? StraightPoolStore.PLAYER1_NAME
					: StraightPoolStore.PLAYER2_NAME;
			name = StraightPoolStore.prefs(this).getString(key, "");
		}
		TextView playerText = findViewById(playerTextId);
		if (name == null || name.equals("")) {
			playerText.setText(defaultPlayer);
		} else {
			playerText.setText(name);
		}
	}

	private String textOf(int id) {
		return ((TextView) findViewById(id)).getText().toString();
	}

	private int getNumberFieldFromIntent(String name) {
		String number = getIntent().getStringExtra(name);
		if (number == null || number.equals("")) {
			String key = "player1PointsToWin".equals(name)
					? StraightPoolStore.PLAYER1_POINTS
					: StraightPoolStore.PLAYER2_POINTS;
			number = StraightPoolStore.prefs(this).getString(key, "");
		}
		if (number == null || number.equals("")) {
			return 50;
		}
		return Integer.parseInt(number);
	}

	private boolean getBooleanFieldFromIntent(String name) {
		return getIntent().getBooleanExtra(name, false);
	}

	public void showGeneralPoolRules() {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://www.wpa-pool.com/web/the_rules_of_play")));
	}

	private void showStraightPoolRules() {
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse("https://www.wpa-pool.com/web/index.asp?id=119&pagetype=rules")));
	}

	private void vibrate(View view) {
		view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
	}

	private void refreshUndoButton() {
		if (scorer == null) {
			return;
		}
		View undo = findViewById(R.id.undoButton);
		if (undo != null) {
			undo.setEnabled(scorer.canUndo());
		}
		View redo = findViewById(R.id.redoButton);
		if (redo != null) {
			redo.setEnabled(scorer.canRedo());
		}
	}

	public void shotMadeButtonClicked(View view) {
		vibrate(view);
		hideShotMadeHint();
		scorer.playerMakesShot();
		afterScoreChange();
	}

	private void showBallsPottedPicker() {
		int max = scorer.ballsOnTheTable();
		if (max <= 0) {
			scorer.playerMakesShot();
			afterScoreChange();
			return;
		}

		int columns = 5;
		GridLayout grid = new GridLayout(this);
		grid.setColumnCount(columns);
		int padding = Math.round(16 * getResources().getDisplayMetrics().density);
		grid.setPadding(padding, padding, padding, padding);

		MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
		builder.setTitle(R.string.balls_potted_title);
		androidx.appcompat.app.AlertDialog dialog = builder.create();

		for (int n = 1; n <= max; n++) {
			final int count = n;
			MaterialButton button = new MaterialButton(this);
			button.setText(Integer.toString(count));
			button.setOnClickListener(v -> {
				scorer.playerMakesShots(count);
				afterScoreChange();
				dialog.dismiss();
			});
			GridLayout.LayoutParams params = new GridLayout.LayoutParams();
			params.width = 0;
			params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
			params.setMargins(padding / 4, padding / 4, padding / 4, padding / 4);
			button.setLayoutParams(params);
			button.setMinHeight(Math.round(56 * getResources().getDisplayMetrics().density));
			grid.addView(button);
		}

		dialog.setView(grid);
		dialog.show();
	}

	public void missedShotButtonClicked(View view) {
		vibrate(view);
		scorer.playerMissesShot();
		afterScoreChange();
	}

	public void safeMadeButtonClicked(View view) {
		vibrate(view);
		scorer.playerMakesSafe();
		afterScoreChange();
	}

	public void foulButtonClicked(View view) {
		vibrate(view);
		scorer.foul();
		afterScoreChange();
	}

	public void undoButtonClicked(View view) {
		vibrate(view);
		scorer.undo();
		afterScoreChange();
	}

	public void redoButtonClicked(View view) {
		vibrate(view);
		scorer.redo();
		afterScoreChange();
	}

	private void afterScoreChange() {
		persistGame();
		refreshUndoButton();
		refreshGameOverChrome();
	}
}
