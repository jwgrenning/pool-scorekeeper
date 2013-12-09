package net.grenning.pool_scorekeeper.straight_pool;


public interface PlayerView {

	void score(int i);
	
	void ballsNeededToWin(int ballsNeededToWin);

	void rackScore(int player1RackScore);

	void currentRun(int count);

	void longestRun(int count);
	
	void safesMade(int count);

	void safesMissed(int count);

	void consecutiveSafes(int count);

	void consecutiveFouls(int consecutiveFouls);

	void fouls(int fouls);

	void makeActive();

	void makeInactive();

}
