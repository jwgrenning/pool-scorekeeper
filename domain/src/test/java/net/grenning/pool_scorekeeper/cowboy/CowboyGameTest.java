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
	public void caromsDefaultToTenPercentOfBalls() {
		assertEquals(5, CowboyPlayer.defaultCaroms(50));
		assertEquals(10, CowboyPlayer.defaultCaroms(100));
		assertEquals(0, CowboyPlayer.defaultCaroms(9));
		assertEquals(50, p1.ballCount);
		assertEquals(5, p1.caromCount);
		assertTrue(p1.specialLastShot);
		assertEquals(51, p1.raceTo());
		assertEquals(45, p1.mixedLimit());
		assertEquals(50, p1.caromLimit());
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
	public void comboPocketsAndCaromsOnOneStroke() {
		assertEquals(CowboyGame.Result.CONTINUE, game.combo(true, true, false, 1));
		assertEquals(0, p1.score);
		assertEquals(5, p1.inning);
		assertEquals(0, game.currentIndex());
	}

	@Test
	public void comboPointsCapsLuckyShotAtEleven() {
		assertEquals(11, CowboyGame.comboPoints(true, true, true, 2));
		assertEquals(11, CowboyGame.comboPoints(true, true, true, 3));
		assertEquals(9, CowboyGame.comboPoints(true, true, true, 0));
		assertEquals(3, CowboyGame.comboPoints(false, false, false, 3));
		assertEquals(0, CowboyGame.comboPoints(false, false, false, 0));
	}

	@Test
	public void luckyShotIsOneThreeFiveAndTwoCaroms() {
		assertEquals(CowboyGame.Result.CONTINUE, game.combo(true, true, true, 2));
		assertEquals(11, p1.inning);
	}

	@Test
	public void luckyShotCannotAddAThirdCarom() {
		assertEquals(CowboyGame.Result.CONTINUE, game.combo(true, true, true, 3));
		assertEquals(11, p1.inning);
	}

	@Test
	public void threeCaromsScoreThreeWhenBallsAreMissed() {
		assertEquals(CowboyGame.Result.CONTINUE, game.combo(false, false, false, 3));
		assertEquals(3, p1.inning);
	}

	@Test
	public void emptyComboIsIgnored() {
		assertEquals(CowboyGame.Result.IGNORED, game.combo(false, false, false, 0));
		assertEquals(0, p1.inning);
		assertFalse(game.canUndo());
	}

	@Test
	public void comboThatPassesMixedLimitIsAFoul() {
		p1.inning = 41;
		assertEquals(CowboyGame.Result.FOUL, game.combo(true, false, true, 0));
		assertEquals(0, p1.inning);
		assertEquals(1, game.currentIndex());
	}

	@Test
	public void comboWithAPocketDuringCaromPhaseIsAFoul() {
		p1.score = 45;
		assertEquals(CowboyGame.Phase.CAROMS, game.phase());
		assertEquals(CowboyGame.Result.FOUL, game.combo(true, false, false, 2));
		assertEquals(1, game.currentIndex());
	}

	@Test
	public void caromComboIsAllowedAfterMixedLimit() {
		p1.score = 45;
		assertEquals(CowboyGame.Result.CONTINUE, game.combo(false, false, false, 2));
		assertEquals(47, p1.total());
	}

	@Test
	public void undoRestoresAWholeCombo() {
		game.combo(true, true, true, 2);
		assertTrue(game.undo());
		assertEquals(0, p1.inning);
		assertEquals(0, game.currentIndex());
	}

	@Test
	public void cannotPassMixedLimitOnAMixedShot() {
		p1.inning = 41;
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
		p1.score = 50;
		assertEquals(CowboyGame.Phase.WIN, game.phase());
		assertEquals(CowboyGame.Result.FOUL, game.caromTwo());
		assertEquals(1, game.currentIndex());
	}

	@Test
	public void winShotAfterCaromsWins() {
		p1.score = 50;
		assertEquals(CowboyGame.Result.WIN, game.winShot());
		assertEquals(51, p1.score);
		assertTrue(p1.hasWon());
	}

	@Test
	public void lastShotIsPerPlayer() {
		p1.specialLastShot = true;
		p2.specialLastShot = false;
		p1.score = 50;
		p2.score = 49;
		assertEquals(CowboyGame.Phase.WIN, game.phaseOf(p1));
		assertEquals(CowboyGame.Phase.CAROMS, game.phaseOf(p2));
		assertEquals(51, p1.raceTo());
		assertEquals(50, p2.raceTo());
	}

	@Test
	public void withoutSpecialLastShotACaromCanWin() {
		p1.specialLastShot = false;
		p2.specialLastShot = false;
		p1.score = 49;
		assertEquals(CowboyGame.Phase.CAROMS, game.phase());
		assertEquals(CowboyGame.Result.WIN, game.caromTwo());
		assertEquals(50, p1.score);
		assertTrue(p1.hasWon());
	}

	@Test
	public void editableCaromCountIsNotTenPercent() {
		p1.ballCount = 50;
		p1.caromCount = 2;
		p1.specialLastShot = true;
		assertEquals(48, p1.mixedLimit());
		p1.score = 48;
		assertEquals(CowboyGame.Phase.CAROMS, game.phase());
		assertEquals(CowboyGame.Result.CONTINUE, game.caromTwo());
		assertEquals(49, p1.total());
		assertEquals(CowboyGame.Result.CONTINUE, game.caromTwo());
		assertEquals(CowboyGame.Phase.WIN, game.phase());
		assertEquals(51, p1.raceTo());
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
	public void lastTenthIsCaromsForEachPlayersOwnRace() {
		p1.ballCount = 50;
		p1.caromCount = 5;
		p2.ballCount = 100;
		p2.caromCount = 10;
		p1.score = 45;
		assertEquals(CowboyGame.Phase.CAROMS, game.phaseOf(p1));
		assertEquals(CowboyGame.Result.FOUL, game.pocket(1));
		assertEquals(1, game.currentIndex());
		p2.score = 85;
		assertEquals(CowboyGame.Phase.MIXED, game.phaseOf(p2));
		assertEquals(CowboyGame.Result.CONTINUE, game.pocket(5));
		assertEquals(90, p2.total());
		assertEquals(CowboyGame.Phase.CAROMS, game.phaseOf(p2));
		assertEquals(CowboyGame.Result.FOUL, game.pocket(1));
	}

	@Test
	public void fourPlayersAllowed() {
		game = new CowboyGame(p1, p2, player("C", 50), player("D", 75));
		assertEquals(4, game.playerCount());
		assertEquals(75, game.player(3).ballCount);
		assertEquals(7, game.player(3).caromCount);
		assertEquals(76, game.player(3).raceTo());
	}

	@Test
	public void foulButtonIsACalledFoul() {
		assertEquals(CowboyGame.Result.FOUL, game.foul());
		assertEquals(CowboyGame.FoulKind.CALLED, game.lastFoul());
	}

	@Test
	public void pocketInCaromsRecordsPocketFoul() {
		p1.score = 45;
		assertEquals(CowboyGame.Result.FOUL, game.pocket(1));
		assertEquals(CowboyGame.FoulKind.POCKET_DURING_CAROMS, game.lastFoul());
	}

	@Test
	public void passingMixedLimitRecordsThatFoul() {
		p1.inning = 41;
		assertEquals(CowboyGame.Result.FOUL, game.pocket(5));
		assertEquals(CowboyGame.FoulKind.PASSED_MIXED_LIMIT, game.lastFoul());
		assertEquals(45, game.lastFoulPoints());
	}

	@Test
	public void scoringTheLastPointAsACaromIsNotTheWinShot() {
		p1.score = 50;
		assertEquals(CowboyGame.Result.FOUL, game.caromTwo());
		assertEquals(CowboyGame.FoulKind.NOT_WIN_SHOT, game.lastFoul());
	}

	@Test
	public void restoreMigratesOldRaceTo() {
		net.grenning.pool_scorekeeper.MapNameValueSaver saver =
				new net.grenning.pool_scorekeeper.MapNameValueSaver();
		saver.save("playerCount", 2);
		saver.save("current", 0);
		saver.save("inning", 1);
		saver.save("name", 0, "A");
		saver.save("raceTo", 0, 50);
		saver.save("score", 0, 0);
		saver.save("inning", 0, 0);
		saver.save("name", 1, "B");
		saver.save("raceTo", 1, 100);
		saver.save("score", 1, 0);
		saver.save("inning", 1, 0);
		game.restore(saver);
		assertEquals(50, p1.ballCount);
		assertEquals(5, p1.caromCount);
		assertTrue(p1.specialLastShot);
		assertEquals(51, p1.raceTo());
		assertEquals(45, p1.mixedLimit());
		assertEquals(100, p2.ballCount);
		assertEquals(10, p2.caromCount);
		assertEquals(101, p2.raceTo());
	}

	private static CowboyPlayer player(String name, int ballCount) {
		CowboyPlayer player = new CowboyPlayer();
		player.name = name;
		player.ballCount = ballCount;
		player.caromCount = CowboyPlayer.defaultCaroms(ballCount);
		player.specialLastShot = true;
		return player;
	}
}
