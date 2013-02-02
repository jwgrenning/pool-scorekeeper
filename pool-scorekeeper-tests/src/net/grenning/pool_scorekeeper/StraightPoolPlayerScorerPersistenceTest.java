package net.grenning.pool_scorekeeper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import net.grenning.pool_scorekeeper.StraightPoolPlayerScorer;
import net.grenning.pool_scorekeeper.StraightPoolPlayerViewSpy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class StraightPoolPlayerScorerPersistenceTest extends StraightPoolPlayerScorerBase {

	Writer testWriter;
	InputStream input;

	@Before
	public void setUp() throws Exception {
		view = new StraightPoolPlayerViewSpy();
		scorer = new StraightPoolPlayerScorer(view, 50);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRestoreAtBeginningOfGame() throws IOException {
		scorer.save(1);
		scorer.goodShot();
		scorer.restore(1);
		assertBallsNeededToWin(50);
		assertPlayerScore(0);
		assertRackScore(0);
		assertConsecutiveFouls(0);
		assertTotalFouls(0);
	}

	@Test
	public void testRestoreYourBreak() throws IOException {
		scorer.yourBreak();
		scorer.save(1);
		scorer.goodShot();
		scorer.restore(1);
		scorer.foul();
		assertPlayerScore(-2);
	}

	@Test
	public void testRestorebadBreak() throws IOException {
		scorer.yourBreak();
		scorer.foul();
		scorer.save(2);
		scorer.goodShot();
		scorer.foul();
		scorer.restore(2);
		assertPlayerScore(-2);
	}

	@Test
	public void testRestore2Shots() throws IOException {
		scorer.goodShot();
		scorer.goodShot();
		scorer.save(1);
		scorer.restore(1);
		assertPlayerScore(2);
		assertRackScore(2);
		assertBallsNeededToWin(48);
		assertConsecutiveFouls(0);
		assertTotalFouls(0);
	}

}
