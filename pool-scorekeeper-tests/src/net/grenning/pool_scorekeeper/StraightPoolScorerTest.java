package net.grenning.pool_scorekeeper;

import static org.junit.Assert.*;

import net.grenning.pool_scorekeeper.StraightPoolScorer;
import net.grenning.pool_scorekeeper.StraightPoolScorerViewSpy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class StraightPoolScorerTest {

	StraightPoolScorerViewSpy view;
	StraightPoolScorer scorer;

	@Before
	public void setUp() throws Exception {
		view = new StraightPoolScorerViewSpy();
		scorer = new StraightPoolScorer(view, 50);
	}

	@After
	public void tearDown() throws Exception {
	}

	private void assertTotalFouls(int fouls) {
		assertEquals(fouls, view.player1TotalFouls);
	}
	
	private void assertConsecutiveFouls(int fouls) {
		assertEquals(fouls, view.player1ConsecutiveFouls);
	}
	
	private void assertBallsNeededToWin(int balls) {
		assertEquals(balls, view.player1NeededToWin);
	}
	
	private void assertRackScore(int score) {
		assertEquals(score, view.player1RackScore);
	}
	
	private void assertPlayerScore(int score) {
		assertEquals(score, view.player1Score);
	}
	
	@Test
	public void testCreate() {
		assertPlayerScore(0);
		assertRackScore(0);
		assertBallsNeededToWin(50);
		assertConsecutiveFouls(0);
		assertTotalFouls(0);
	}

	@Test
	public void missedFirstShot() {
		scorer.missedShot();
		assertPlayerScore(0);
		assertRackScore(0);
		assertBallsNeededToWin(50);
		assertConsecutiveFouls(0);
		assertTotalFouls(0);
	}

	@Test
	public void testFirstBallShot() {
		scorer.goodShot();
		assertPlayerScore(1);
		assertRackScore(1);
		assertBallsNeededToWin(49);
		assertConsecutiveFouls(0);
		assertTotalFouls(0);
	}

	@Test
	public void testMissThenFoul() {
		scorer.missedShot();
		scorer.foul();
		assertPlayerScore(-1);
		assertRackScore(0);
		assertBallsNeededToWin(51);
		assertConsecutiveFouls(1);
		assertTotalFouls(1);
	}

	@Test
	public void testFoulShot() {
		scorer.goodShot();
		scorer.goodShot();
		scorer.foul();
		assertPlayerScore(1);
		assertRackScore(2);
		assertBallsNeededToWin(49);
		assertConsecutiveFouls(1);
		assertTotalFouls(1);
	}

	@Test
	public void testFoulOnBreak() {
		scorer.yourBreak();
		scorer.foul();
		assertPlayerScore(-2);
		assertRackScore(0);
		assertBallsNeededToWin(52);
		assertConsecutiveFouls(1);
		assertTotalFouls(1);
	}

	@Test
	public void testFoulOnBreakAndSecondShot() {
		scorer.yourBreak();
		scorer.foul();
		scorer.foul();
		assertPlayerScore(-3);
		assertRackScore(0);
		assertBallsNeededToWin(53);
		assertConsecutiveFouls(2);
		assertTotalFouls(2);
	}

	@Test
	public void testBreakMissFoul() {
		scorer.yourBreak();
		scorer.missedShot();
		scorer.foul();
		assertPlayerScore(-1);
		assertRackScore(0);
		assertBallsNeededToWin(51);
		assertConsecutiveFouls(1);
		assertTotalFouls(1);
	}

	@Test
	public void testBreakGoodShotFoul() {
		scorer.yourBreak();
		scorer.goodShot();
		scorer.foul();
		assertPlayerScore(0);
		assertRackScore(1);
		assertBallsNeededToWin(50);
		assertConsecutiveFouls(1);
		assertTotalFouls(1);
	}

}
