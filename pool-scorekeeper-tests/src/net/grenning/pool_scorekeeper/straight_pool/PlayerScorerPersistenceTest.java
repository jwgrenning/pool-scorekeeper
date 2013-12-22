package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;

import net.grenning.pool_scorekeeper.NameValueSaver;
import net.grenning.pool_scorekeeper.straight_pool.PlayerScorer;
import net.grenning.pool_scorekeeper.straight_pool.PlayerViewSpy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class PlayerScorerPersistenceTest extends PlayerScorerBase {

	private static final int INT_FIELDS_PER_PLAYER = 10;
	private static final int BOOL_FIELDS_PER_PLAYER = 1;
	private static final int NONSAVED_FIELDS_PER_PLAYER = 1;

	Writer testWriter;
	InputStream input;

	NameValueSaver saver = mock(NameValueSaver.class);
	
	@Before
	public void setUp() throws Exception {
		view = new PlayerViewSpy();
		scorer = new PlayerScorer(view, 50);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void valuesSavedByName() {
		scorer.save(saver, 1);
		verify(saver, times(1)).save("ballsNeededToWin", 1, 50);
		verify(saver, times(1)).save("score", 1, 0);
		verify(saver, times(1)).save("rackScore", 1, 0);
		verify(saver, times(1)).save("consecutiveFouls", 1, 0);
		verify(saver, times(1)).save("fouls", 1, 0);
		verify(saver, times(1)).save("breakShotComing", 1, false);
		verify(saver, times(1)).save("currentRun", 1, 0);
		verify(saver, times(1)).save("longestRun", 1, 0);
		verify(saver, times(1)).save("safesMade", 1, 0);
		verify(saver, times(1)).save("safesMissed", 1, 0);
		verify(saver, times(1)).save("consecutiveSafes", 1, 0);
		verifyNoMoreInteractions(saver);
		

	}
	@Test
	public void allValuesSaved() {
		scorer.save(saver, 1);
		verify(saver, times(INT_FIELDS_PER_PLAYER)).save(anyString(), anyInt(), anyInt());
		verify(saver, times(BOOL_FIELDS_PER_PLAYER)).save(anyString(), anyInt(), anyBoolean());
		verifyNoMoreInteractions(saver);
	}

	@Test
	public void allValuesRestored() {
		when(saver.getInt(eq("ballsNeededToWin" + 1), anyInt())).thenReturn(99);
		when(saver.getInt(eq("score" + 1), anyInt())).thenReturn(42);
		when(saver.getInt(eq("rackScore" + 1), anyInt())).thenReturn(15);
		when(saver.getInt(eq("consecutiveFouls" + 1), anyInt())).thenReturn(1);
		when(saver.getInt(eq("fouls" + 1), anyInt())).thenReturn(2);
		when(saver.getBoolean(eq("breakShotComing" + 1), anyBoolean())).thenReturn(false);
		when(saver.getInt(eq("currentRun" + 1), anyInt())).thenReturn(149);
		when(saver.getInt(eq("longestRun" + 1), anyInt())).thenReturn(201);
		when(saver.getInt(eq("safesMade" + 1), anyInt())).thenReturn(17);
		when(saver.getInt(eq("safesMissed" + 1), anyInt())).thenReturn(42);
		when(saver.getInt(eq("consecutiveSafes" + 1), anyInt())).thenReturn(3);

		scorer.restore(saver, 1);
		
		assertPlayerScore(42);
		assertRackScore(15);
		assertBallsNeededToWin(99);
		assertConsecutiveFouls(1);
		assertTotalFouls(2);
		assertCurrentRun(149);
		assertLongestRun(201);
		assertSafesMade(17);
		assertSafesMissed(42);
		assertConsecutiveSafes(3);
	}

	@Test
	public void testAllFieldsAccoutedFor() {
		int totalFieldsPerPlager = INT_FIELDS_PER_PLAYER + BOOL_FIELDS_PER_PLAYER + NONSAVED_FIELDS_PER_PLAYER;
		assertEquals(totalFieldsPerPlager, PlayerScorer.class.getDeclaredFields().length);
	}


}
