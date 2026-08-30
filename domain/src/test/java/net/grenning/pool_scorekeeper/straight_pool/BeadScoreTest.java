package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BeadScoreTest {

	@Test
	public void zeroAndNegativeLeaveAllBeadsOnTheRight() {
		assertEquals(0, BeadScore.onesOnLeft(0, 50));
		assertEquals(0, BeadScore.onesOnLeft(-2, 150));
		assertEquals(0, BeadScore.markersOnLeft(-2, 150));
	}

	@Test
	public void eachPointMovesOneBeadLeft() {
		assertEquals(1, BeadScore.onesOnLeft(1, 50));
		assertEquals(14, BeadScore.onesOnLeft(14, 150));
		assertEquals(0, BeadScore.markersOnLeft(14, 150));
	}

	@Test
	public void fiftyFillsOnesWhenRaceIsFifty() {
		assertEquals(50, BeadScore.onesOnLeft(50, 50));
		assertEquals(0, BeadScore.markersOnLeft(50, 50));
		assertEquals(0, BeadScore.markerSlots(50));
	}

	@Test
	public void fiftyCarriesToAMarkerWhenRaceIsLonger() {
		assertEquals(0, BeadScore.onesOnLeft(50, 150));
		assertEquals(1, BeadScore.markersOnLeft(50, 150));
		assertEquals(1, BeadScore.onesOnLeft(51, 150));
		assertEquals(1, BeadScore.markersOnLeft(51, 150));
	}

	@Test
	public void oneHundredFillsTwoFifties() {
		assertEquals(0, BeadScore.onesOnLeft(100, 150));
		assertEquals(2, BeadScore.markersOnLeft(100, 150));
		assertEquals(50, BeadScore.onesOnLeft(100, 100));
		assertEquals(1, BeadScore.markersOnLeft(100, 100));
	}

	@Test
	public void oneFiftyFillsLastOnesString() {
		assertEquals(50, BeadScore.onesOnLeft(150, 150));
		assertEquals(2, BeadScore.markersOnLeft(150, 150));
	}

	@Test
	public void raceOverFiftyHasOneFiftiesMarker() {
		assertEquals(1, BeadScore.markerSlots(75));
		assertEquals(1, BeadScore.markerSlots(100));
	}

	@Test
	public void raceOverOneHundredHasTwoFiftiesMarkers() {
		assertEquals(2, BeadScore.markerSlots(101));
		assertEquals(2, BeadScore.markerSlots(150));
	}

	@Test
	public void shorterRaceSpotsTheDifference() {
		assertEquals(0, BeadScore.spot(150, 150));
		assertEquals(100, BeadScore.spot(50, 150));
		assertEquals(50, BeadScore.spot(100, 150));
	}

	@Test
	public void spottedHundredLooksAlreadyShot() {
		int visual = BeadScore.visualScore(0, 100);
		assertEquals(0, BeadScore.onesOnLeft(visual, 150));
		assertEquals(2, BeadScore.markersOnLeft(visual, 150));
	}

	@Test
	public void spottedFiftyLeavesOnesToPlay() {
		int visual = BeadScore.visualScore(0, 50);
		assertEquals(0, BeadScore.onesOnLeft(visual, 150));
		assertEquals(1, BeadScore.markersOnLeft(visual, 150));
	}

	@Test
	public void scoringAfterASpotMovesOnes() {
		int visual = BeadScore.visualScore(7, 100);
		assertEquals(7, BeadScore.onesOnLeft(visual, 150));
		assertEquals(2, BeadScore.markersOnLeft(visual, 150));
	}
}
