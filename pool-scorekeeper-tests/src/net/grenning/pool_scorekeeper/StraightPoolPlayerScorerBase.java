package net.grenning.pool_scorekeeper;

import static org.junit.Assert.assertEquals;
import net.grenning.pool_scorekeeper.StraightPoolPlayerScorer;

public class StraightPoolPlayerScorerBase {

	protected StraightPoolPlayerViewSpy view;
	protected StraightPoolPlayerScorer scorer;

	public StraightPoolPlayerScorerBase() {
		super();
	}

	protected void assertTotalFouls(int fouls) {
		assertEquals(fouls, view.totalFouls);
	}

	protected void assertConsecutiveFouls(int fouls) {
		assertEquals(fouls, view.consecutiveFouls);
	}

	protected void assertBallsNeededToWin(int balls) {
		assertEquals(balls, view.pointsNeededToWin);
	}

	protected void assertRackScore(int score) {
		assertEquals(score, view.rackScore);
	}

	protected void assertPlayerScore(int score) {
		assertEquals(score, view.score);
	}

}