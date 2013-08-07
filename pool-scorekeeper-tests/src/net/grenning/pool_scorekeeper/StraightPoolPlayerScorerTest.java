package net.grenning.pool_scorekeeper;


import static org.junit.Assert.assertEquals;
import net.grenning.pool_scorekeeper.StraightPoolPlayerScorer;
import net.grenning.pool_scorekeeper.StraightPoolPlayerViewSpy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class StraightPoolPlayerScorerTest extends StraightPoolPlayerScorerBase {

	@Before
	public void setUp() throws Exception {
		view = new StraightPoolPlayerViewSpy();
		scorer = new StraightPoolPlayerScorer(view, 50);
	}

	@After
	public void tearDown() throws Exception {
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
	public void testRestart() {
		scorer.missedShot();
		scorer.foul();
		scorer.reset(100);
		assertPlayerScore(0);
		assertRackScore(0);
		assertBallsNeededToWin(100);
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
		assertConsecutiveFouls(0);
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
		assertConsecutiveFouls(1);
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

	@Test
	public void trestNewRack() {
		scorer.yourBreak();
		scorer.goodShot();
		scorer.missedShot();
		assertPlayerScore(1);
		assertRackScore(1);
		scorer.newRack();
		assertPlayerScore(1);
		assertRackScore(0);
		
	}
	
	@Test
	public void testPlayFoulStreakResets() {
		scorer.missedShot();
		scorer.foul();
		scorer.foul();
		scorer.missedShot();
		assertConsecutiveFouls(0);
	}
	
	@Test
	public void testThreeConsequtiveFoulsCost15Points() {
		scorer.missedShot();
		scorer.foul();
		scorer.foul();
		scorer.foul();
		assertEquals(-18, view.score);
		assertEquals(3, view.consecutiveFouls);
	}

	@Test
	public void testFirstFouldIsNotCountedForThreeConsequtiveFouls() {
		scorer.yourBreak();
		scorer.foul();
		scorer.foul();
		scorer.foul();
		assertEquals(-4, view.score);
		assertEquals(2, view.consecutiveFouls);
	}
	
	//test for required re-rack after 3 consecutive fouls

}
