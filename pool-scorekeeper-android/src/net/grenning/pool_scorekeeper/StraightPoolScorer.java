package net.grenning.pool_scorekeeper;

public class StraightPoolScorer {
	StraightPoolScorerView view;
	private int ballsNeededToWin = 0;
	private int score = 0;
	private int rackScore = 0;
	private int consecutiveFouls = 0;
	private int fouls = 0;
	private boolean breakShotComing = false;
	public StraightPoolScorer(StraightPoolScorerView view, int ballsNeededToWin) {
		this.view = view;
		this.ballsNeededToWin = ballsNeededToWin;
		updateView(view);
	}
	
	private void updateView(StraightPoolScorerView view) {
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
		updateView(view);
	}

	public void foul() {
		if (breakShotComing)
		{
			ballsNeededToWin++;			
			score--;
			breakShotComing = false;
		}
		
		score--;
		ballsNeededToWin++;
		consecutiveFouls++;
		fouls++;
		updateView(view);
	}

	public void missedShot() {
		breakShotComing = false;
	}

	public void yourBreak() {
		breakShotComing  = true;
	}

}