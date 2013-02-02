package net.grenning.pool_scorekeeper;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

public class StraightPoolGameScorerPersistenceTest extends StraightPoolGameScorerTestBase {

	Writer testWriter;
	InputStream input;
	
	@Test
	public void testGameStateSavesAndRestoresBallsOnTable() throws IOException {
		playerMakesSomeShots(3);
		game.save();
		playerMakesSomeShots(4);
		game.restore();

		assertEquals(12, gameViewSpy.ballsOnTheTable);
	}

	@Test
	public void testGameStateSavesAndRestoresCurrentPlayer() throws IOException {
		assertPlayerOneActive();
		game.save();
		game.playerMissesShot();
		assertPlayerTwoActive();
		game.restore();
		assertPlayerOneActive();
	}

	/*
	 * game view, racks, runs, innings multiple balls in one shot 14:1 re rack
	 * Undo Move rules into the options menu Add settings, default players,
	 * points
	 */

}

