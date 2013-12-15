package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.straight_pool.GameView;

public class GameViewSpy implements GameView {

	public int ballsOnTheTable = -1;
	public boolean onlyOneBallLeft = false;;
	public int reRackSuggestedCount = 0;
	public int gameOverApplause = 0;
	public int inning = -1;
	public int winningPlayer = -1;

	@Override
	public void inning(int inning) {
		this.inning = inning;
	}

	@Override
	public void ballsOnTheTable(int balls) {
		onlyOneBallLeft = false;
		ballsOnTheTable = balls;
	}

	public boolean reRackSuggested() {
		return reRackSuggestedCount != 0;
	}

	@Override
	public void suggestRerack() {
		reRackSuggestedCount++;
	}

	@Override
	public void gameOverApplause() {
		gameOverApplause++;
	}

	@Override
	public void oneBallOnTheTable() {
		onlyOneBallLeft = true;
		ballsOnTheTable = 1;
	}

	@Override
	public void theWinnerIs(int playerNumber) {
		winningPlayer = playerNumber;
	}

}
