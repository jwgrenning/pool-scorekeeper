package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.*;

import net.grenning.pool_scorekeeper.NameValueSaver;

import org.junit.After;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class GameScorerPersistenceTest extends
		GameScorerTestBase {

	private static final int INT_FIELDS_PER_PLAYER = 10;
	private static final int BOOL_FIELDS_PER_PLAYER = 1;
	private static final int NONSAVED_FIELDS_PER_PLAYER = 1;
	private static final int STRING_FIELDS_PER_PLAYER = 1;
	
	NameValueSaver saver = mock(NameValueSaver.class);
	
	
	@After
	public void verifyNoUncheckedInterations() {
	}
	
	@Test
	public void testInitialGameSave() {
		game.save(saver);
		verify(saver, times(1)).save("ballsOnTheTable", 15);
		verify(saver, times(1)).save("currentPlayerNumber", 0);
		verify(saver, times(1)).save("inning", 1);
		verify(saver, times(1)).save(eq("inningRecord"), eq(1), eq(""));
		verify(saver, times(INT_FIELDS_PER_PLAYER)).save(anyString(), eq(1), anyInt());
		verify(saver, times(BOOL_FIELDS_PER_PLAYER)).save(anyString(), eq(1), anyBoolean());
		verify(saver, times(1)).save(eq("inningRecord"), eq(2), eq(""));
		verify(saver, times(INT_FIELDS_PER_PLAYER)).save(anyString(), eq(2), anyInt());
		verify(saver, times(BOOL_FIELDS_PER_PLAYER)).save(anyString(), eq(2), anyBoolean());
		verifyNoMoreInteractions(saver);		
	}

	@Test
	public void testRestore() {
		when(saver.getInt(eq("ballsOnTheTable"), anyInt())).thenReturn(99);
		when(saver.getInt(eq("currentPlayerNumber"), anyInt())).thenReturn(0);
		when(saver.getInt(eq("inning"), eq(1))).thenReturn(13);
		when(saver.getString(eq("inningRecord"), eq(1), eq(""))).thenReturn("the inning records");
		game.populateFromPersistence(saver);
		verify(saver, times(1)).getInt("currentPlayerNumber", 0);
		verify(saver, times(1)).getInt("ballsOnTheTable", 15);
		verify(saver, times(1)).getInt("inning", 1);
		verify(saver, times(2*INT_FIELDS_PER_PLAYER+3)).getInt(anyString(), anyInt());
		verify(saver, times(2*BOOL_FIELDS_PER_PLAYER)).getBoolean(anyString(), anyBoolean());
		assertPlayerOneActive();
		assertEquals(99, gameViewSpy.ballsOnTheTable);
		assertEquals(13, gameViewSpy.inning);
		int totalFieldsPerPlager = INT_FIELDS_PER_PLAYER + BOOL_FIELDS_PER_PLAYER + STRING_FIELDS_PER_PLAYER + NONSAVED_FIELDS_PER_PLAYER;
		assertEquals(totalFieldsPerPlager, PlayerScorer.class.getDeclaredFields().length);
	}

	@Test
	public void testAllFieldsAccoutedFor() {
		int totalFieldsPerPlager = INT_FIELDS_PER_PLAYER + BOOL_FIELDS_PER_PLAYER + STRING_FIELDS_PER_PLAYER + NONSAVED_FIELDS_PER_PLAYER;
		assertEquals(totalFieldsPerPlager, PlayerScorer.class.getDeclaredFields().length);
	}

	/*
	 * game view, racks, runs, innings multiple balls in one shot 14:1 re rack
	 * Undo Move rules into the options menu Add settings, default players,
	 * points
	 */

}
