package net.grenning.pool_scorekeeper;

import java.util.prefs.Preferences;

public class StraightPoolPlayerScorer {

	StraightPoolPlayerView view;
	private int ballsNeededToWin = 0;
	private int score = 0;
	private int rackScore = 0;
	private int consecutiveFouls = 0;
	private int fouls = 0;
	private boolean breakShotComing = false;

	public StraightPoolPlayerScorer(StraightPoolPlayerView view,
			int ballsNeededToWin) {
		this.view = view;
		reset(ballsNeededToWin);
	}

	public void reset(int ballsNeededToWin) {
		this.ballsNeededToWin = ballsNeededToWin;
		score = 0;
		rackScore = 0;
		consecutiveFouls = 0;
		fouls = 0;
		breakShotComing = false;
		updateView(view);
	}

	private void updateView(StraightPoolPlayerView view) {
		view.score(score);
		view.rackScore(rackScore);
		view.ballsNeededToWin(ballsNeededToWin);
		view.consecutiveFouls(consecutiveFouls);
		view.fouls(fouls);
	}

	public void goodShot() {
		score++;
		rackScore++;
		ballsNeededToWin--;
		breakShotComing = false;
		consecutiveFouls = 0;
		updateView(view);
	}

	public void foul() {
		if (breakShotComing) {
			breakShotComing = false;
			ballsNeededToWin++;
			score--;
		}

		score--;
		ballsNeededToWin++;
		consecutiveFouls++;
		fouls++;
		updateView(view);
	}

	public void missedShot() {
		breakShotComing = false;
		consecutiveFouls = 0;
		updateView(view);
	}

	public void yourBreak() {
		breakShotComing = true;
	}

	public void newRack() {
		rackScore = 0;
		updateView(view);
	}

	public void makeActive() {
		view.makeActive();
	}

	public void makeInactive() {
		view.makeInactive();
	}

	public void save(int playerNumber) {
		Preferences gameState;
		gameState = Preferences.userRoot().node(this.getClass().getName() + playerNumber);
		gameState.putInt("ballsNeededToWin", ballsNeededToWin);
		gameState.putInt("score", score);
		gameState.putInt("rackScore", rackScore);
		gameState.putInt("consecutiveFouls", consecutiveFouls);
		gameState.putInt("fouls", fouls);
		gameState.putBoolean("breakShotComing", breakShotComing);


	}

	public void restore(int playerNumber) {
		Preferences gameState;
		gameState = Preferences.userRoot().node(this.getClass().getName() + playerNumber);
		ballsNeededToWin = gameState.getInt("ballsNeededToWin", 999);
		score = gameState.getInt("score", score);
		rackScore = gameState.getInt("rackScore", rackScore);
		consecutiveFouls = gameState.getInt("consecutiveFouls", consecutiveFouls);
		fouls = gameState.getInt("fouls", fouls);
		breakShotComing = gameState.getBoolean("breakShotComing", breakShotComing);
		updateView(view);
	}

}
