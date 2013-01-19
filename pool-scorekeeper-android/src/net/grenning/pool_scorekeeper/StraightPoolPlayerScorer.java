package net.grenning.pool_scorekeeper;

public class StraightPoolPlayerScorer {
	StraightPoolPlayerView view;
	private int ballsNeededToWin = 0;
	private int score = 0;
	private int rackScore = 0;
	private int consecutiveFouls = 0;
	private int fouls = 0;
	private boolean breakShotComing = false;
	public StraightPoolPlayerScorer(StraightPoolPlayerView view, int ballsNeededToWin) {
		this.view = view;
		this.ballsNeededToWin = ballsNeededToWin;
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
		if (breakShotComing)
		{
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
		breakShotComing  = true;
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

}
