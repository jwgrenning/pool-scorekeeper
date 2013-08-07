package net.grenning.pool_scorekeeper;

import net.grenning.pool_scorekeeper.StraightPoolView;

public class StraightPoolViewSpy implements StraightPoolView {

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
