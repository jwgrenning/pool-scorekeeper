package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.assertTrue;
import net.grenning.pool_scorekeeper.straight_pool.GameScorer;
import net.grenning.pool_scorekeeper.straight_pool.PlayerScorer;

public class GameScorerTestBase {

	protected PlayerViewSpy player1Spy = new PlayerViewSpy();
	protected PlayerViewSpy player2Spy = new PlayerViewSpy();
	PlayerScorer player1Scorer = new PlayerScorer(
				player1Spy, 50);
	PlayerScorer player2Scorer = new PlayerScorer(
				player2Spy, 50);
	protected GameViewSpy gameViewSpy = new GameViewSpy();
	protected GameScorer game = new GameScorer(gameViewSpy,
				player1Scorer, player2Scorer);

	public GameScorerTestBase() {
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

	protected void playerMakesSafe() {
			game.playerMakesSafe();
	}

	protected void playerMissesSafe() {
			game.playerMissesSafe();
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