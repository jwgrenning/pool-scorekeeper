package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.straight_pool.GameView;

public class ViewSpy implements GameView {

	public int ballsOnTheTable;
	public int reRackSuggestedCount = 0;
	public int gameOverApplause = 0;

	@Override
	public void ballsOnTheTable(int balls) {
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

}
