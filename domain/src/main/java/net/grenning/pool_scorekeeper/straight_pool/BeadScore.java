package net.grenning.pool_scorekeeper.straight_pool;

public final class BeadScore {

	public static final int BEADS_PER_STRING = 50;

	private BeadScore() {
	}

	public static int onLeft(int score) {
		if (score <= 0) {
			return 0;
		}
		int remainder = score % BEADS_PER_STRING;
		return remainder == 0 ? BEADS_PER_STRING : remainder;
	}

	public static int completedStrings(int score) {
		if (score <= 0) {
			return 0;
		}
		return (score - 1) / BEADS_PER_STRING;
	}

	public static int overflowPoints(int score) {
		return completedStrings(score) * BEADS_PER_STRING;
	}
}
