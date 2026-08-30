package net.grenning.pool_scorekeeper.straight_pool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BeadScoreTest {

	@Test
	public void zeroAndNegativeLeaveAllBeadsOnTheRight() {
		assertEquals(0, BeadScore.onLeft(0));
		assertEquals(0, BeadScore.onLeft(-2));
		assertEquals(0, BeadScore.completedStrings(0));
		assertEquals(0, BeadScore.overflowPoints(-2));
	}

	@Test
	public void eachPointMovesOneBeadLeft() {
		assertEquals(1, BeadScore.onLeft(1));
		assertEquals(14, BeadScore.onLeft(14));
		assertEquals(0, BeadScore.completedStrings(14));
	}

	@Test
	public void fiftyFillsTheString() {
		assertEquals(50, BeadScore.onLeft(50));
		assertEquals(0, BeadScore.completedStrings(50));
		assertEquals(0, BeadScore.overflowPoints(50));
	}

	@Test
	public void fiftyOneReusesTheString() {
		assertEquals(1, BeadScore.onLeft(51));
		assertEquals(1, BeadScore.completedStrings(51));
		assertEquals(50, BeadScore.overflowPoints(51));
	}

	@Test
	public void oneHundredFillsTheSecondString() {
		assertEquals(50, BeadScore.onLeft(100));
		assertEquals(1, BeadScore.completedStrings(100));
		assertEquals(50, BeadScore.overflowPoints(100));
	}

	@Test
	public void oneFiftyFillsTheThirdString() {
		assertEquals(50, BeadScore.onLeft(150));
		assertEquals(2, BeadScore.completedStrings(150));
		assertEquals(100, BeadScore.overflowPoints(150));
	}

	@Test
	public void raceOfFiftyHasNoFiftiesMarkers() {
		assertEquals(0, BeadScore.markerSlots(50));
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
	public void fiftiesMarkersStayRightUntilAStringCompletes() {
		assertEquals(0, BeadScore.markersOnLeft(50));
		assertEquals(1, BeadScore.markersOnLeft(51));
		assertEquals(1, BeadScore.markersOnLeft(100));
		assertEquals(2, BeadScore.markersOnLeft(101));
		assertEquals(2, BeadScore.markersOnLeft(150));
	}
}
