package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.grenning.pool_scorekeeper.MapNameValueSaver;

public class GameScorerUndoTest extends GameScorerTestBase {

	@Test
	public void testEmptyUndoDoesNothing() {
		assertFalse(game.canUndo());
		assertFalse(game.undo());
		assertEquals(15, gameViewSpy.ballsOnTheTable);
		assertEquals(0, player1Spy.score);
		assertEquals(0, player2Spy.score);
		assertPlayerOneActive();
	}

	@Test
	public void testUndoMadeShot() {
		game.playerMakesShot();
		assertEquals(14, gameViewSpy.ballsOnTheTable);
		assertEquals(1, player1Spy.score);
		assertEquals(1, player1Spy.currentRun);
		assertEquals(49, player1Spy.pointsNeededToWin);
		assertTrue(game.canUndo());

		assertTrue(game.undo());
		assertEquals(15, gameViewSpy.ballsOnTheTable);
		assertEquals(0, player1Spy.score);
		assertEquals(0, player1Spy.currentRun);
		assertEquals(50, player1Spy.pointsNeededToWin);
		assertFalse(game.canUndo());
		assertPlayerOneActive();
	}

	@Test
	public void testUndoMissRestoresActivePlayerAndInning() {
		game.playerMissesShot();
		assertPlayerTwoActive();
		assertEquals(1, gameViewSpy.inning);

		game.undo();
		assertPlayerOneActive();
		assertEquals(1, gameViewSpy.inning);
		assertEquals(15, gameViewSpy.ballsOnTheTable);
	}

	@Test
	public void testUndoBreakFoul() {
		game.foul();
		assertEquals(-2, player1Spy.score);
		assertEquals(0, player1Spy.consecutiveFouls);
		assertPlayerTwoActive();

		game.undo();
		assertEquals(0, player1Spy.score);
		assertEquals(0, player1Spy.consecutiveFouls);
		assertEquals(50, player1Spy.pointsNeededToWin);
		assertPlayerOneActive();
	}

	@Test
	public void testUndoThirdConsecutiveFoul() {
		game.playerMissesShot();
		game.foul();
		game.playerMissesShot();
		game.foul();
		game.playerMissesShot();
		game.foul();
		assertEquals(-18, player2Spy.score);
		assertEquals(0, player2Spy.consecutiveFouls);
		assertPlayerTwoActive();
		assertEquals(15, gameViewSpy.ballsOnTheTable);

		game.undo();
		assertEquals(-2, player2Spy.score);
		assertEquals(2, player2Spy.consecutiveFouls);
		assertPlayerTwoActive();
	}

	@Test
	public void testUndoNewRack() {
		playerMakesSomeShots(14);
		assertEquals(14, player1Spy.score);
		assertEquals(14, player1Spy.rackScore);
		assertEquals(1, gameViewSpy.ballsOnTheTable);

		game.newRack();
		assertEquals(0, player1Spy.rackScore);
		assertEquals(15, gameViewSpy.ballsOnTheTable);

		game.undo();
		assertEquals(14, player1Spy.score);
		assertEquals(14, player1Spy.rackScore);
		assertEquals(1, gameViewSpy.ballsOnTheTable);
	}

	@Test
	public void testUndoTwiceReturnsToStart() {
		game.playerMakesShot();
		game.playerMakesShot();
		assertEquals(2, player1Spy.score);
		assertEquals(13, gameViewSpy.ballsOnTheTable);

		game.undo();
		game.undo();
		assertEquals(0, player1Spy.score);
		assertEquals(15, gameViewSpy.ballsOnTheTable);
		assertFalse(game.canUndo());
	}

	@Test
	public void testUndoWinningShot() {
		player1Scorer.reset(2);
		playerMakesSomeShots(2);
		assertTrue(player1Scorer.wins());
		assertEquals(1, gameViewSpy.winningPlayer);
		assertEquals(1, gameViewSpy.gameOverApplause);

		game.undo();
		assertFalse(player1Scorer.wins());
		assertEquals(-1, gameViewSpy.winningPlayer);
		assertEquals(1, player1Spy.score);
	}

	@Test
	public void testShotWithZeroBallsDoesNotCreateUndoEntry() {
		playerMakesSomeShots(15);
		assertEquals(0, gameViewSpy.ballsOnTheTable);
		assertTrue(game.canUndo());

		game.playerMakesShot();
		assertEquals(0, gameViewSpy.ballsOnTheTable);

		game.undo();
		assertEquals(1, gameViewSpy.ballsOnTheTable);
		assertEquals(14, player1Spy.score);
	}

	@Test
	public void testUndoRestoresLongestRunAndInningRecord() {
		playerMakesSomeShots(3);
		game.playerMissesShot();
		assertEquals(3, player1Spy.longestRun);
		assertEquals("  3 ", player1Spy.inningRecord);

		game.undo();
		assertEquals(3, player1Spy.longestRun);
		assertEquals("", player1Spy.inningRecord);
		assertEquals(3, player1Spy.currentRun);
		assertPlayerOneActive();

		game.undo();
		assertEquals(2, player1Spy.longestRun);
		assertEquals(2, player1Spy.currentRun);
	}

	@Test
	public void testPopulateFromPersistenceRestoresUndoHistory() {
		game.playerMakesShot();
		game.playerMakesShot();
		MapNameValueSaver saver = new MapNameValueSaver();
		game.save(saver);

		PlayerViewSpy restoredPlayer1 = new PlayerViewSpy();
		PlayerViewSpy restoredPlayer2 = new PlayerViewSpy();
		GameViewSpy restoredGameView = new GameViewSpy();
		GameScorer restored = new GameScorer(restoredGameView,
				new PlayerScorer(restoredPlayer1, 50),
				new PlayerScorer(restoredPlayer2, 50));
		restored.populateFromPersistence(saver);

		assertEquals(13, restoredGameView.ballsOnTheTable);
		assertEquals(2, restoredPlayer1.score);
		assertTrue(restored.canUndo());

		assertTrue(restored.undo());
		assertEquals(14, restoredGameView.ballsOnTheTable);
		assertEquals(1, restoredPlayer1.score);
		assertTrue(restored.undo());
		assertEquals(15, restoredGameView.ballsOnTheTable);
		assertEquals(0, restoredPlayer1.score);
		assertFalse(restored.canUndo());
	}

	@Test
	public void testRedoRestoresUndoneShot() {
		game.playerMakesShot();
		assertEquals(14, gameViewSpy.ballsOnTheTable);
		game.undo();
		assertEquals(15, gameViewSpy.ballsOnTheTable);
		assertTrue(game.canRedo());
		assertFalse(game.canUndo());

		assertTrue(game.redo());
		assertEquals(14, gameViewSpy.ballsOnTheTable);
		assertEquals(1, player1Spy.score);
		assertFalse(game.canRedo());
		assertTrue(game.canUndo());
	}

	@Test
	public void testNewActionClearsRedo() {
		game.playerMakesShot();
		game.undo();
		assertTrue(game.canRedo());
		game.playerMissesShot();
		assertFalse(game.canRedo());
	}

	@Test
	public void testPopulateFromPersistenceRestoresRedoHistory() {
		game.playerMakesShot();
		game.undo();
		MapNameValueSaver saver = new MapNameValueSaver();
		game.save(saver);

		PlayerViewSpy restoredPlayer1 = new PlayerViewSpy();
		PlayerViewSpy restoredPlayer2 = new PlayerViewSpy();
		GameViewSpy restoredGameView = new GameViewSpy();
		GameScorer restored = new GameScorer(restoredGameView,
				new PlayerScorer(restoredPlayer1, 50),
				new PlayerScorer(restoredPlayer2, 50));
		restored.populateFromPersistence(saver);

		assertEquals(15, restoredGameView.ballsOnTheTable);
		assertTrue(restored.canRedo());
		assertTrue(restored.redo());
		assertEquals(14, restoredGameView.ballsOnTheTable);
		assertEquals(1, restoredPlayer1.score);
	}

	@Test
	public void testUndoSafe() {
		game.playerMakesSafe();
		assertEquals(1, player1Spy.safesMade);
		assertPlayerTwoActive();

		game.undo();
		assertEquals(0, player1Spy.safesMade);
		assertPlayerOneActive();
	}
}
