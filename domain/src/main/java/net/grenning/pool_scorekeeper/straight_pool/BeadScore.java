package net.grenning.pool_scorekeeper.straight_pool;

public final class BeadScore {

	public static final int BEADS_PER_STRING = 50;

	private BeadScore() {
	}

	public static int spot(int raceTo, int maxRace) {
		return Math.max(0, maxRace - Math.max(0, raceTo));
	}

	public static int visualScore(int score, int spot) {
		return score + spot;
	}

	public static int markerSlots(int maxRace) {
		if (maxRace <= BEADS_PER_STRING) {
			return 0;
		}
		return Math.min(2, (maxRace - 1) / BEADS_PER_STRING);
	}

	public static int markersOnLeft(int visualScore, int maxRace) {
		if (visualScore <= 0) {
			return 0;
		}
		int slots = markerSlots(maxRace);
		if (slots == 0) {
			return 0;
		}
		return Math.min(slots, visualScore / BEADS_PER_STRING);
	}

	public static int onesOnLeft(int visualScore, int maxRace) {
		if (visualScore <= 0) {
			return 0;
		}
		int slots = markerSlots(maxRace);
		if (slots == 0) {
			int remainder = visualScore % BEADS_PER_STRING;
			return remainder == 0 ? BEADS_PER_STRING : remainder;
		}
		int lastStringStart = slots * BEADS_PER_STRING;
		if (visualScore >= lastStringStart + BEADS_PER_STRING) {
			return BEADS_PER_STRING;
		}
		return visualScore % BEADS_PER_STRING;
	}

	public static int onLeft(int score) {
		return onesOnLeft(score, BEADS_PER_STRING);
	}

	public static int completedStrings(int score) {
		return markersOnLeft(score, 150);
	}

	public static int overflowPoints(int score) {
		return completedStrings(score) * BEADS_PER_STRING;
	}

	public static int markersOnLeft(int score) {
		return markersOnLeft(score, 150);
	}
}
