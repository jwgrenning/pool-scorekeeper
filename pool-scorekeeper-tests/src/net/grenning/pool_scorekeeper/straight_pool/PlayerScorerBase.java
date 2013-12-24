package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.assertEquals;
import net.grenning.pool_scorekeeper.straight_pool.PlayerScorer;

public class PlayerScorerBase {

	protected PlayerViewSpy view;
	protected PlayerScorer scorer;

	public PlayerScorerBase() {
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

	protected void assertConsecutiveSafes(int safes) {
		assertEquals(safes, view.consecutiveSafes);		
	}

	protected void assertInningReport(String report) {
		assertEquals(report, view.inningRecord);		
	}

	protected void assertSafesMissed(int safes) {
		assertEquals(safes, view.safesMissed);		
	}

	protected void assertSafesMade(int safes) {
		assertEquals(safes, view.safesMade);		
	}

	protected void assertLongestRun(int run) {
		assertEquals(run, view.longestRun);				
	}

	protected void assertCurrentRun(int run) {
		assertEquals(run, view.currentRun);				
	}
	}