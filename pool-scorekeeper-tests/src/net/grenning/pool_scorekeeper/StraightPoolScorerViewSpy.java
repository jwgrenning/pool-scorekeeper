package net.grenning.pool_scorekeeper;

public class StraightPoolScorerViewSpy implements StraightPoolScorerView {

	public int player1Score;
	public int player2Score = 0;
	public int player1RackScore = 0;
	public int player1NeededToWin = 0;
	public int player1ConsecutiveFouls;
	public int player1TotalFouls;
	public boolean playerIsActive = false;
	public boolean playerIsInactive = false;

	@Override
	public void score(int i) {
		player1Score = i;
	}

	@Override
	public void ballsNeededToWin(int ballsNeededToWin) {
		this.player1NeededToWin = ballsNeededToWin;
	}

	@Override
	public void rackScore(int player1RackScore) {
		this.player1RackScore = player1RackScore;
	}

	@Override
	public void consecutiveFouls(int consecutiveFouls) {
		player1ConsecutiveFouls = consecutiveFouls;
	}

	@Override
	public void fouls(int fouls) {
		player1TotalFouls = fouls;
	}

	@Override
	public void makeActive() {
		playerIsActive = true;
	}

	@Override
	public void makeInactive() {
		playerIsInactive = true;
	}

}
