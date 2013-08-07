package net.grenning.pool_scorekeeper;

import net.grenning.pool_scorekeeper.StraightPoolPlayerView;

public class StraightPoolPlayerViewSpy implements StraightPoolPlayerView {

	public int score;
	public int rackScore = 0;
	public int pointsNeededToWin = 0;
	public int consecutiveFouls;
	public int totalFouls;
	public boolean playerIsActive = false;
	public boolean playerIsInactive = false;

	@Override
	public void score(int i) {
		score = i;
	}

	@Override
	public void ballsNeededToWin(int ballsNeededToWin) {
		this.pointsNeededToWin = ballsNeededToWin;
	}

	@Override
	public void rackScore(int player1RackScore) {
		this.rackScore = player1RackScore;
	}

	@Override
	public void consecutiveFouls(int fouls) {
		consecutiveFouls = fouls;
	}

	@Override
	public void fouls(int fouls) {
		totalFouls = fouls;
	}

	@Override
	public void makeActive() {
		playerIsActive = true;
		playerIsInactive = false;
	}

	@Override
	public void makeInactive() {
		playerIsInactive = true;
		playerIsActive = false;
	}

}
