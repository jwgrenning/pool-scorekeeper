package net.grenning.pool_scorekeeper.cowboy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CowboyGameTest {

	private CowboyGame game;
	private CowboyPlayer p1;
	private CowboyPlayer p2;

	@Before
	public void setUp() {
		p1 = player("A", 50);
		p2 = player("B", 50);
		game = new CowboyGame(p1, p2);
	}

	@Test
	public void mixedLimitIsNinetyPercent() {
		assertEquals(45, CowboyGame.mixedLimit(50));
		assertEquals(5, CowboyGame.caromStretch(50));
	}

	@Test
	public void pocketingAddsInningPointsNotBanked() {
		assertEquals(CowboyGame.Result.CONTINUE, game.pocket(5));
		assertEquals(0, p1.score);
		assertEquals(5, p1.inning);
		assertEquals(0, game.currentIndex());
	}

	@Test
	public void missBanksTheInningAndSwitches() {
		game.pocket(5);
		game.pocket(3);
		assertEquals(CowboyGame.Result.TURN_OVER, game.miss());
		assertEquals(8, p1.score);
		assertEquals(0, p1.inning);
		assertEquals(1, game.currentIndex());
	}

	@Test
	public void foulDropsInningPoints() {
		game.pocket(5);
		game.pocket(5);
		assertEquals(CowboyGame.Result.FOUL, game.foul());
		assertEquals(0, p1.score);
		assertEquals(0, p1.inning);
		assertEquals(1, game.currentIndex());
	}

	@Test
	public void twoBallCaromScoresOne() {
		assertEquals(CowboyGame.Result.CONTINUE, game.caromTwo());
		assertEquals(1, p1.inning);
	}

	@Test
	public void threeBallCaromScoresTwo() {
		assertEquals(CowboyGame.Result.CONTINUE, game.caromThree());
		assertEquals(2, p1.inning);
	}

	@Test
	public void cannotPassMixedLimitOnAMixedShot() {
		p1.inning = 43;
		assertEquals(CowboyGame.Result.FOUL, game.pocket(5));
		assertEquals(0, p1.inning);
		assertEquals(1, game.currentIndex());
	}

	@Test
	public void canLandExactlyOnMixedLimit() {
		p1.inning = 42;
		assertEquals(CowboyGame.Result.CONTINUE, game.pocket(3));
		assertEquals(45, p1.total());
		assertEquals(CowboyGame.Phase.CAROMS, game.phase());
	}

	@Test
	public void pocketingDuringCaromPhaseIsAFoul() {
		p1.score = 45;
		assertEquals(CowboyGame.Phase.CAROMS, game.phase());
		assertEquals(CowboyGame.Result.FOUL, game.pocket(1));
		assertEquals(1, game.currentIndex());
	}

	@Test
	public void caromsAreAllowedAfterMixedLimit() {
		p1.score = 45;
		assertEquals(CowboyGame.Result.CONTINUE, game.caromTwo());
		assertEquals(46, p1.total());
	}

	@Test
	public void lastPointMustBeTheWinShot() {
		p1.score = 49;
		assertEquals(CowboyGame.Phase.WIN, game.phase());
		assertEquals(CowboyGame.Result.FOUL, game.caromTwo());
		assertEquals(1, game.currentIndex());
	}

	@Test
	public void winShotAtFortyNineWins() {
		p1.score = 49;
		assertEquals(CowboyGame.Result.WIN, game.winShot());
		assertEquals(50, p1.score);
		assertTrue(p1.hasWon());
	}

	@Test
	public void threePlayersRotate() {
		CowboyPlayer p3 = player("C", 50);
		game = new CowboyGame(p1, p2, p3);
		game.miss();
		game.miss();
		assertEquals(2, game.currentIndex());
		game.miss();
		assertEquals(0, game.currentIndex());
		assertEquals(2, game.inningNumber());
	}

	@Test
	public void undoRestoresInningPoints() {
		game.pocket(5);
		game.miss();
		assertTrue(game.undo());
		assertEquals(0, p1.score);
		assertEquals(5, p1.inning);
		assertEquals(0, game.currentIndex());
	}

	@Test
	public void fourPlayersAllowed() {
		game = new CowboyGame(p1, p2, player("C", 50), player("D", 75));
		assertEquals(4, game.playerCount());
		assertEquals(75, game.player(3).raceTo);
	}

	private static CowboyPlayer player(String name, int raceTo) {
		CowboyPlayer player = new CowboyPlayer();
		player.name = name;
		player.raceTo = raceTo;
		return player;
	}
}
