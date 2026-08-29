package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GameScorerTimingTest extends GameScorerTestBase {

	@Test
	public void testActivePlayerShowsTurnStartTime() {
		game.startTurnAt(1_000_000L);
		assertEquals(1_000_000L, player1Spy.turnStartedAt);
		assertEquals(0, player2Spy.turnStartedAt);
	}

	@Test
	public void testSwitchingTurnsMovesTheClock() {
		game.startTurnAt(1_000L);
		game.setNowMillis(5_000L);
		game.playerMissesShot();
		assertEquals(0, player1Spy.turnStartedAt);
		assertEquals(5_000L, player2Spy.turnStartedAt);
	}

	@Test
	public void testTableTimeAndShotCounts() {
		game.startTurnAt(0);
		game.playerMakesShot();
		game.setNowMillis(10_000);
		game.playerMissesShot();

		game.reportSummary(gameViewSpy, player1Spy, player2Spy);

		assertEquals(10_000, player1Spy.tableTimeMillis);
		assertEquals(1, player1Spy.turnCount);
		assertEquals(2, player1Spy.shotCount);
		assertTrue(player2Spy.turnCount >= 1);
		assertEquals(0, player2Spy.shotCount);
	}
}
