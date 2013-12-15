package net.grenning.pool_scorekeeper.straight_pool;


public interface GameView {

	public void inning(int inning);

	public void ballsOnTheTable(int balls);

	public void oneBallOnTheTable();

	public void suggestRerack();
	
	public void gameOverApplause();

	public void theWinnerIs(int playerNumber);

}
