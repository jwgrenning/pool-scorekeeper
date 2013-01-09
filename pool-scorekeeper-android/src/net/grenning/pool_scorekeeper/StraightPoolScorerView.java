package net.grenning.pool_scorekeeper;

public interface StraightPoolScorerView {

	void score(int i);

	void ballsNeededToWin(int ballsNeededToWin);

	void rackScore(int player1RackScore);

	void consecutiveFouls(int consecutiveFouls);

	void fouls(int fouls);

	void makeActive();

	void makeInactive();

}
