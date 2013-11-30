package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameScorerTest extends GameScorerTestBase {

	@Test
	public void testGameStartsAllBallsOnTheTable() {
		assertEquals(15, gameViewSpy.ballsOnTheTable);
	}

	@Test
	public void testShotsMadeReduceBallsOnTable() {
		game.playerMakesShot();
		assertEquals(14, gameViewSpy.ballsOnTheTable);
	}

	@Test
	public void testShotsMissedDontReduceBallsOnTable() {
		game.playerMissesShot();
		assertEquals(15, gameViewSpy.ballsOnTheTable);
	}

	@Test
	public void testFoulsDontReduceBallsOnTable() {
		game.foul();
		assertEquals(15, gameViewSpy.ballsOnTheTable);
	}

	@Test
	public void testGamePlayer1BreaksThenFouls() {
		game.foul();
		assertEquals(-2, player1Spy.score);
	}

	@Test
	public void testGamePlayer1BreaksThenFoulsPlayer2Scores() {
		game.foul();
		game.playerMakesShot();
		assertEquals(-2, player1Spy.score);
		assertEquals(1, player2Spy.score);
	}

	@Test
	public void testGamePlayer1BreaksAndHitsSomeBallsIn() {
		playerMakesSomeShots(3);
		assertEquals(3, player1Spy.score);
	}

	@Test
	public void testGamePlayer1BreaksMissesPlayer2HitsSomeBallsIn() {
		game.playerMissesShot();
		playerMakesSomeShots(2);
		assertEquals(2, player2Spy.score);
	}

	@Test
	public void testP1MissP2MissP1MakesShot() {
		playerMissesSomeShots(2);
		game.playerMakesShot();
		assertEquals(1, player1Spy.score);
		assertEquals(0, player2Spy.score);
	}

	@Test
	public void testNewRack() {
		playerMakesSomeShots(14);
		assertEquals(14, player1Spy.score);
		game.newRack();
		assertEquals(14, player1Spy.score);
		assertEquals(0, player1Spy.rackScore);
		assertEquals(0, player2Spy.rackScore);
		assertEquals(15, gameViewSpy.ballsOnTheTable);
	}

	@Test
	public void testGameStartsWithPlayerOneActive() {
		assertPlayerOneActive();
	}

	@Test
	public void testPlayerTwoActiveAfterPlayerOneMisses() {
		game.playerMissesShot();
		assertPlayerTwoActive();
	}

	@Test
	public void testPlayerOneActiveAfterPlayerTwoMisses() {
		game.playerMissesShot();
		game.playerMissesShot();
		assertPlayerOneActive();
	}

	@Test
	public void testConsecutiveFoulsReset() {
		game.foul();
		game.foul();
		game.foul();
		game.foul();
		assertEquals(1, player1Spy.consecutiveFouls);
		assertEquals(2, player2Spy.consecutiveFouls);
		game.playerMissesShot();
		assertEquals(0, player1Spy.consecutiveFouls);
	}

	@Test
	public void testSuggestsRerackWhenOneBallLeft() {
		playerMakesSomeShots(14);
		assertEquals(1, gameViewSpy.reRackSuggestedCount);
	}

	@Test
	public void testSuggestsRerackWhenNoBallsLeft() {
		playerMakesSomeShots(15);
		assertEquals(2, gameViewSpy.reRackSuggestedCount);
	}

	@Test
	public void testDoesNotSuggestsRerackWithMoreThanOneBallLeft() {
		playerMakesSomeShots(13);
		assertEquals(0, gameViewSpy.reRackSuggestedCount);
	}

	@Test
	public void testWinningGetsApplause() {
		playerMakesSomeShots(50);
		assertEquals(1, gameViewSpy.gameOverApplause);
	}

	/*
	 * game view, racks, runs, innings multiple balls in one shot 14:1 re rack
	 * Undo Move rules into the options menu Add settings, default players,
	 * points
	 */

}