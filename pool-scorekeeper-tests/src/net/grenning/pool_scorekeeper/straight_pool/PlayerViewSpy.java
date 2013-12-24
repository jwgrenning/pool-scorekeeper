package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.straight_pool.PlayerView;

public class PlayerViewSpy implements PlayerView {

	public int score = -1;
	public int rackScore = -1;
	public int pointsNeededToWin = 0;
	public int consecutiveFouls = -1;;
	public int totalFouls = -1;
	public boolean playerIsActive = false;
	public boolean playerIsInactive = false;
	public int longestRun = -1;
	public int currentRun = -1;
	public int safesMade = -1;;
	public int safesMissed = -1;
	public int consecutiveSafes = -1;
	public String inningRecord = "---";

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

	@Override
	public void longestRun(int count) {
		longestRun = count;
		
	}

	@Override
	public void currentRun(int count) {
		currentRun = count;	
	}

	@Override
	public void safesMade(int count) {
		safesMade = count;
	}

	@Override
	public void safesMissed(int count) {
		safesMissed = count;		
	}

	@Override
	public void consecutiveSafes(int count) {
		consecutiveSafes = count;
	}

	@Override
	public void inningRecord(String record) {
		inningRecord  = record;
	}

}
