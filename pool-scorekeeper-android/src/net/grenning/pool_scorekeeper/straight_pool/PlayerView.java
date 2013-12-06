package net.grenning.pool_scorekeeper.straight_pool;


public interface PlayerView {

	void score(int i);
	
//	void longestRun(int count);

	void ballsNeededToWin(int ballsNeededToWin);

	void rackScore(int player1RackScore);

	void consecutiveFouls(int consecutiveFouls);

	void fouls(int fouls);

	void makeActive();

	void makeInactive();

}
