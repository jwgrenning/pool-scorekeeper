package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameScrorerSummaryTest extends GameScorerTestBase {

	@Test
	public void testGameSummary() {
		game.reportSummary(gameViewSpy, player1Spy, player2Spy);
		assertEquals(15, gameViewSpy.ballsOnTheTable);
		assertEquals(1, gameViewSpy.inning);
		assertEquals(0, player1Spy.score);
		assertEquals(0, player2Spy.score);
	}

}
