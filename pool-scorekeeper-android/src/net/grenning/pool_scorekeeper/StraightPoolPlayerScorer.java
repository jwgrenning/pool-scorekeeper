package net.grenning.pool_scorekeeper;

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
		else
		{
			consecutiveFouls++;
		}

		score--;
		ballsNeededToWin++;
		fouls++;
		if (consecutiveFouls == 3)
			score -= 15;
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

	public void save(NameValueSaver saver, int playerNumber) {
		saver.save("ballsNeededToWin" + playerNumber, ballsNeededToWin);
		saver.save("score" + playerNumber, score);
		saver.save("rackScore" + playerNumber, rackScore);
		saver.save("consecutiveFouls" + playerNumber, consecutiveFouls);
		saver.save("fouls" + playerNumber, fouls);
		saver.save("breakShotComing" + playerNumber, breakShotComing);
	}

	public void restore(NameValueSaver saver, int playerNumber) {
		ballsNeededToWin = saver.getInt("ballsNeededToWin" + playerNumber, ballsNeededToWin);
		score = saver.getInt("score" + playerNumber, score);
		rackScore = saver.getInt("rackScore" + playerNumber, rackScore);
		consecutiveFouls = saver.getInt("consecutiveFouls" + playerNumber, consecutiveFouls);
		fouls = saver.getInt("fouls" + playerNumber, fouls);
		breakShotComing = saver.getBoolean("breakShotComing" + playerNumber, breakShotComing);
		updateView(view);
	}

	public boolean wins() {
		return ballsNeededToWin == 0;
	}

}
