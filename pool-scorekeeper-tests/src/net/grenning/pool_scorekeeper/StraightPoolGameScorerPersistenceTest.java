package net.grenning.pool_scorekeeper;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class StraightPoolGameScorerPersistenceTest extends
		StraightPoolGameScorerTestBase {

	GameFieldSaver saver = mock(GameFieldSaver.class);
	
	@After
	public void verifyNoUncheckedInterations() {
	}
	
	@Test
	public void testInitialGameSave() {
		game.save(saver);
		verify(saver, times(1)).save("ballsOnTheTable", 15);
		verify(saver, times(1)).save("currentPlayerNumber", 0);
		verifyNoMoreInteractions(saver);		
	}

	@Test
	public void testRestore() {
		when(saver.getInt(eq("ballsOnTheTable"), anyInt())).thenReturn(99);
		when(saver.getInt(eq("currentPlayerNumber"), anyInt())).thenReturn(0);
		game.restore(saver);
		assertPlayerOneActive();
		assertEquals(99, gameViewSpy.ballsOnTheTable);
	}

//	@Test
//	public void testGameStateSavesAndRestoresCurrentPlayer() {
//		assertPlayerOneActive();
//		game.save();
//		game.playerMissesShot();
//		assertPlayerTwoActive();
//		game.restore();
//		assertPlayerOneActive();
//	}
//
//	@Test
//	public void testRestoresBothPlayers() {
//		game.save();
//		game.playerMakesShot();
//		game.playerMissesShot();
//		game.restore();
//		assertEquals(50, player1Spy.pointsNeededToWin);
//		assertEquals(0, player1Spy.score);
//		assertEquals(0, player1Spy.rackScore);
//		assertEquals(0, player1Spy.totalFouls);
//		assertEquals(50, player2Spy.pointsNeededToWin);
//		assertEquals(0, player2Spy.score);
//		assertEquals(0, player2Spy.rackScore);
//		assertEquals(0, player2Spy.totalFouls);
//	}

	/*
	 * game view, racks, runs, innings multiple balls in one shot 14:1 re rack
	 * Undo Move rules into the options menu Add settings, default players,
	 * points
	 */

}
