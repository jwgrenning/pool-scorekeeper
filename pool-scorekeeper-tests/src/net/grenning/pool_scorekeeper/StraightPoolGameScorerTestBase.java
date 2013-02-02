package net.grenning.pool_scorekeeper;

import static org.junit.Assert.assertTrue;

public class StraightPoolGameScorerTestBase {

	protected StraightPoolPlayerViewSpy player1Spy = new StraightPoolPlayerViewSpy();
	protected StraightPoolPlayerViewSpy player2Spy = new StraightPoolPlayerViewSpy();
	StraightPoolPlayerScorer player1Scorer = new StraightPoolPlayerScorer(
				player1Spy, 50);
	StraightPoolPlayerScorer player2Scorer = new StraightPoolPlayerScorer(
				player2Spy, 50);
	protected StraightPoolViewSpy gameViewSpy = new StraightPoolViewSpy();
	protected StraightPoolGameScorer game = new StraightPoolGameScorer(gameViewSpy,
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