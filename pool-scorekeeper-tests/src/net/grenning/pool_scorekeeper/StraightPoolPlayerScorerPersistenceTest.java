package net.grenning.pool_scorekeeper;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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

	GameFieldSaver saver = mock(GameFieldSaver.class);

	@Before
	public void setUp() throws Exception {
		view = new StraightPoolPlayerViewSpy();
		scorer = new StraightPoolPlayerScorer(view, 50);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void allValuesSaved() {
		scorer.save(saver, 1);
		verify(saver, times(1)).save("ballsNeededToWin" + 1, 50);
		verify(saver, times(1)).save("score" + 1, 0);
		verify(saver, times(1)).save("rackScore" + 1, 0);
		verify(saver, times(1)).save("consecutiveFouls" + 1, 0);
		verify(saver, times(1)).save("fouls" + 1, 0);
		verify(saver, times(1)).save("breakShotComing" + 1, false);
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
		scorer.restore(saver, 1);
		
		assertPlayerScore(42);
		assertRackScore(15);
		assertBallsNeededToWin(99);
		assertConsecutiveFouls(1);
		assertTotalFouls(2);

	}

}
