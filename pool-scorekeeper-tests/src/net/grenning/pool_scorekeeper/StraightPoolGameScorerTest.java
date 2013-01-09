package net.grenning.pool_scorekeeper;

import static org.junit.Assert.*;

import org.junit.Test;

public class StraightPoolGameScorerTest {

	StraightPoolScorerViewSpy player1Spy = new StraightPoolScorerViewSpy();
	StraightPoolScorerViewSpy player2Spy = new StraightPoolScorerViewSpy();
	StraightPoolScorer player1Scorer = new StraightPoolScorer(player1Spy, 50);
	StraightPoolScorer player2Scorer = new StraightPoolScorer(player2Spy, 50);
	StraightPoolGameScorer game = new StraightPoolGameScorer(player1Scorer, player2Scorer);
	
	@Test
	public void testGameStartsAllBallsOnTheTable() {
		assertEquals(15, game.ballsOnTheTable());
	}

	@Test
	public void testGamePlayer1BreaksThenFouls() {
		game.foul();
		assertEquals(-2, player1Spy.player1Score);
	}

	@Test
	public void testGamePlayer1BreaksThenFoulsPlayer2Scores() {
		game.foul();
		game.playerMakesShot();
		assertEquals(-2, player1Spy.player1Score);
		assertEquals(1, player2Spy.player1Score);
	}

	@Test
	public void testGamePlayer1BreaksAndHitsSomeBallsIn() {
		playerMakesSomeShots(3);
		assertEquals(3, player1Spy.player1Score);
	}

	@Test
	public void testGamePlayer1BreaksMissesPlayer2HitsSomeBallsIn() {
		game.playerMissesShot();
		playerMakesSomeShots(2);
		assertEquals(2, player2Spy.player1Score);
	}

	@Test
	public void testP1MissP2MissP1MakesShot() {
		playerMissesSomeShots(2);
		game.playerMakesShot();
		assertEquals(1, player1Spy.player1Score);
		assertEquals(0, player2Spy.player1Score);
	}
	
	@Test
	public void testNewRack() {
		playerMakesSomeShots(14);
		assertEquals(14, player1Spy.player1Score);
		game.newRack();
		assertEquals(14, player1Spy.player1Score);
		assertEquals(0, player1Spy.player1RackScore);
		assertEquals(0, player2Spy.player1RackScore);		
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

	private void playerMissesSomeShots(int numberOfShots) {
		for (int i = 0; i< numberOfShots; i++)
			game.playerMissesShot();
	}
	
	private void playerMakesSomeShots(int numberOfShots) {
		for (int i = 0; i< numberOfShots; i++)
			game.playerMakesShot();
	}
	
	private void assertPlayerOneActive() {
		assertTrue(player1Spy.playerIsActive);
		assertTrue(player2Spy.playerIsInactive);
	}
	
	private void assertPlayerTwoActive() {
		assertTrue(player1Spy.playerIsInactive);
		assertTrue(player2Spy.playerIsActive);
	}
	
	/*
	 * game view, racks, runs, innings
	 * multiple balls in one shot
	 * 14:1 re rack
	 * Undo
	 * Move rules into the options menu
	 * Add settings, default players, points
	 * 
	 */

}