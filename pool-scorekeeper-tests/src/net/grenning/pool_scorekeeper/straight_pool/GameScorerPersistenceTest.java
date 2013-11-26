package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.*;

import net.grenning.pool_scorekeeper.NameValueSaver;

import org.junit.After;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class GameScorerPersistenceTest extends
		GameScorerTestBase {

	NameValueSaver saver = mock(NameValueSaver.class);
	
	@After
	public void verifyNoUncheckedInterations() {
	}
	
	@Test
	public void testInitialGameSave() {
		game.save(saver);
		verify(saver, times(1)).save("ballsOnTheTable", 15);
		verify(saver, times(1)).save("currentPlayerNumber", 0);
		verify(saver, times(5)).save(anyString(), eq(1), anyInt());
		verify(saver, times(1)).save(anyString(), eq(1), anyBoolean());
		verify(saver, times(5)).save(anyString(), eq(2), anyInt());
		verify(saver, times(1)).save(anyString(), eq(2), anyBoolean());
		verifyNoMoreInteractions(saver);		
	}

	@Test
	public void testRestore() {
		when(saver.getInt(eq("ballsOnTheTable"), anyInt())).thenReturn(99);
		when(saver.getInt(eq("currentPlayerNumber"), anyInt())).thenReturn(0);
		game.populateFromPersistence(saver);
		verify(saver, times(2*6)).getInt(anyString(), anyInt());
		verify(saver, times(2*1)).getBoolean(anyString(), anyBoolean());
		assertPlayerOneActive();
		assertEquals(99, gameViewSpy.ballsOnTheTable);
	}

	/*
	 * game view, racks, runs, innings multiple balls in one shot 14:1 re rack
	 * Undo Move rules into the options menu Add settings, default players,
	 * points
	 */

}
