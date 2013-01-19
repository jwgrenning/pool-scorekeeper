package net.grenning.pool_scorekeeper;

public class StraightPoolViewSpy implements StraightPoolView {

	public int ballsOnTheTable;
	public int reRackSuggestedCount = 0;

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

}
