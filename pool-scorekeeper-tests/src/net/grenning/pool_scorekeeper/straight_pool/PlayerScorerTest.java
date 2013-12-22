package net.grenning.pool_scorekeeper.straight_pool;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class PlayerScorerTest extends PlayerScorerBase {

	@Before
	public void setUp() throws Exception {
		view = new PlayerViewSpy();
		scorer = new PlayerScorer(view, 50);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void assertStartingNumbers(int ballsNeededToWin) {
		assertPlayerScore(0);
		assertRackScore(0);
		assertBallsNeededToWin(ballsNeededToWin);
		assertConsecutiveFouls(0);
		assertTotalFouls(0);
		assertCurrentRun(0);
		assertLongestRun(0);
		assertSafesMade(0);
		assertSafesMissed(0);
		assertConsecutiveSafes(0);
	}

	@Test
	public void testCreate() {
		assertStartingNumbers(50);
	}

	@Test
	public void testRestartWithCustomBallsNeededToWin() {
		int ballsNeededToWin = 100;
		scorer.missedShot();
		scorer.foul();
		scorer.reset(ballsNeededToWin);
		assertStartingNumbers(ballsNeededToWin);
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
	public void testNewRack() {
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
	
	@Test
	public void testCurrentRun() {
		runSomeBalls(3);
		assertCurrentRun(3);
	}
	
	@Test
	public void testCurrentRunResetBySafeMade() {
		runSomeBalls(3);
		scorer.safeMade();
		assertCurrentRun(0);
	}
	
	@Test
	public void testCurrentRunResetBySafeMissed() {
		runSomeBalls(3);
		scorer.safeMissed();
		assertCurrentRun(0);
	}
	
	@Test
	public void testCurrentRunResetByFoul() {
		runSomeBalls(3);
		scorer.foul();
		assertCurrentRun(0);
	}
	
	@Test
	public void testInitialLongestRun() {
		runSomeBalls(3);
		assertLongestRun(3);
	}

	@Test
	public void testNewLongestRun() {
		runSomeBalls(3);
		scorer.missedShot();
		runSomeBalls(6);
		assertLongestRun(6);
	}

	@Test
	public void testLongestRunPreserverd() {
		runSomeBalls(3);
		scorer.missedShot();
		runSomeBalls(2);
		assertLongestRun(3);
	}

	@Test
	public void testSafeMade() {
		scorer.safeMade();
		assertSafesMade(1);
	}

	@Test
	public void testSafeMissed() {
		scorer.safeMissed();
		assertSafesMissed(1);
	}

	private void runSomeBalls(int runLength) {
		for (int i = 0; i < runLength ; i++)
			scorer.goodShot();
	}
	
	
	
	
	
	//test for required re-rack after 3 consecutive fouls

}
