package net.grenning.pool_scorekeeper;

import static org.junit.Assert.assertEquals;
import net.grenning.pool_scorekeeper.straight_pool.PlayerScorer;

public class StraightPoolPlayerScorerBase {

	protected StraightPoolPlayerViewSpy view;
	protected PlayerScorer scorer;

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