package net.grenning.pool_scorekeeper;

import static org.junit.Assert.assertTrue;
import net.grenning.pool_scorekeeper.straight_pool.GameScorer;
import net.grenning.pool_scorekeeper.straight_pool.PlayerScorer;

public class StraightPoolGameScorerTestBase {

	protected StraightPoolPlayerViewSpy player1Spy = new StraightPoolPlayerViewSpy();
	protected StraightPoolPlayerViewSpy player2Spy = new StraightPoolPlayerViewSpy();
	PlayerScorer player1Scorer = new PlayerScorer(
				player1Spy, 50);
	PlayerScorer player2Scorer = new PlayerScorer(
				player2Spy, 50);
	protected StraightPoolViewSpy gameViewSpy = new StraightPoolViewSpy();
	protected GameScorer game = new GameScorer(gameViewSpy,
				player1Scorer, player2Scorer);

	public StraightPoolGameScorerTestBase() {
		super();
	}

	protected void playerMissesSomeShots(int numberOfShots) {
		for (int i = 0; i < numberOfShots; i++)
			game.playerMissesShot();
	}

	protected void playerMakesSomeShots(int numberOfShots) {
		for (int i = 0; i < numberOfShots; i++)
			game.playerMakesShot();
	}

	protected void assertPlayerOneActive() {
		assertTrue(player1Spy.playerIsActive);
		assertTrue(player2Spy.playerIsInactive);
	}

	protected void assertPlayerTwoActive() {
		assertTrue(player1Spy.playerIsInactive);
		assertTrue(player2Spy.playerIsActive);
	}

}