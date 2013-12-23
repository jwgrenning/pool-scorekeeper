package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.NameValueSaver;

public class PlayerScorer {

	PlayerView view;
	private int ballsNeededToWin = -1;
	private int score = -1;
	private int rackScore = -1;
	private int consecutiveFouls = -1;
	private int fouls = -1;
	private boolean breakShotComing = false;
	private int currentRun = -1;
	private int longestRun = -1;
	private int safesMade = -1;
	private int safesMissed = -1;
	private int consecutiveSafes = -1;

	public PlayerScorer(PlayerView view,
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
		currentRun = 0;
		longestRun = 0;
		safesMade = 0;
		safesMissed = 0;
		consecutiveSafes = 0;
		updateView(view);
	}

	private void updateView(PlayerView view) {
		view.score(score);
		view.rackScore(rackScore);
		view.ballsNeededToWin(ballsNeededToWin);
		view.consecutiveFouls(consecutiveFouls);
		view.fouls(fouls);
		view.currentRun(currentRun);
		view.longestRun(longestRun);
		view.safesMade(safesMade);
		view.safesMissed(safesMissed);
		view.consecutiveSafes(consecutiveSafes);
	}

	public void goodShot() {
		score++;
		rackScore++;
		ballsNeededToWin--;
		breakShotComing = false;
		consecutiveFouls = 0;
		currentRun++;
		if (currentRun > longestRun)
			longestRun = currentRun;
		updateView(view);
	}

	public void foul() {
		if (breakShotComing) {
			breakShotComing = false;
			ballsNeededToWin++;
			score--;
		}
		else
		{
			consecutiveFouls++;
		}

		score--;
		ballsNeededToWin++;
		fouls++;
		currentRun = 0;
		if (consecutiveFouls == 3)
			score -= 15;
		updateView(view);
	}

	public void missedShot() {
		breakShotComing = false;
		consecutiveFouls = 0;
		currentRun = 0;
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

	public void save(NameValueSaver saver, int playerNumber) {
		saver.save("ballsNeededToWin", playerNumber, ballsNeededToWin);
		saver.save("score", playerNumber, score);
		saver.save("rackScore", playerNumber, rackScore);
		saver.save("consecutiveFouls", playerNumber, consecutiveFouls);
		saver.save("fouls", playerNumber, fouls);
		saver.save("breakShotComing", playerNumber, breakShotComing);
		saver.save("currentRun", playerNumber, currentRun);
		saver.save("longestRun", playerNumber, longestRun);
		saver.save("safesMade", playerNumber, safesMade);
		saver.save("safesMissed", playerNumber, safesMissed);
		saver.save("consecutiveSafes", playerNumber, consecutiveSafes);
	}

	public void restore(NameValueSaver saver, int playerNumber) {
		ballsNeededToWin = saver.getInt("ballsNeededToWin" + playerNumber, ballsNeededToWin);
		score = saver.getInt("score" + playerNumber, score);
		rackScore = saver.getInt("rackScore" + playerNumber, rackScore);
		consecutiveFouls = saver.getInt("consecutiveFouls" + playerNumber, consecutiveFouls);
		fouls = saver.getInt("fouls" + playerNumber, fouls);
		breakShotComing = saver.getBoolean("breakShotComing" + playerNumber, breakShotComing);
		currentRun = saver.getInt("currentRun" + playerNumber, 0);
		longestRun = saver.getInt("longestRun"+ playerNumber, 0);
		safesMade = saver.getInt("safesMade" + playerNumber, 0);
		safesMissed = saver.getInt("safesMissed" + playerNumber, 0);
		consecutiveSafes = saver.getInt("consecutiveSafes" + playerNumber, 0);
		updateView(view);
	}

	public boolean wins() {
		return ballsNeededToWin == 0;
	}

	public void safeMade() {
		safesMade++;
		currentRun = 0;
		updateView(view);		
	}

	public void safeMissed() {
		safesMissed++;
		currentRun = 0;
		updateView(view);		
	}

	public void reportSummary(PlayerView player) {
		updateView(player);
	}

}
