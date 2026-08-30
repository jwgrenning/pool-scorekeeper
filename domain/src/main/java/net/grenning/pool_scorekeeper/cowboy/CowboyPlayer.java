package net.grenning.pool_scorekeeper.cowboy;

import net.grenning.pool_scorekeeper.NameValueSaver;

public class CowboyPlayer {

	public String name = "";
	public int ballCount = 50;
	public int caromCount = 5;
	public boolean specialLastShot = true;
	public int score;
	public int inning;

	public static int defaultCaroms(int ballCount) {
		return Math.max(0, Math.max(0, ballCount) / 10);
	}

	public void normalize() {
		if (ballCount < 0) {
			ballCount = 0;
		}
		if (caromCount < 0) {
			caromCount = 0;
		}
		if (caromCount > ballCount) {
			caromCount = ballCount;
		}
		if (raceTo() <= 0) {
			ballCount = 50;
			caromCount = defaultCaroms(50);
			specialLastShot = true;
		}
	}

	public int mixedLimit() {
		return Math.max(0, ballCount - caromCount);
	}

	public int caromLimit() {
		return ballCount;
	}

	public int raceTo() {
		return ballCount + (specialLastShot ? 1 : 0);
	}

	public int total() {
		return score + inning;
	}

	public boolean hasWon() {
		return score >= raceTo();
	}

	public void save(NameValueSaver saver, int index) {
		saver.save("name", index, name);
		saver.save("ballCount", index, ballCount);
		saver.save("caromCount", index, caromCount);
		saver.save("specialLastShot", index, specialLastShot);
		saver.save("raceTo", index, raceTo());
		saver.save("score", index, score);
		saver.save("inning", index, inning);
	}

	public void restore(NameValueSaver saver, int index) {
		name = saver.getString("name", index, name);
		int savedBalls = saver.getInt("ballCount", index, -1);
		if (savedBalls < 0) {
			int oldRace = saver.getInt("raceTo", index, 50);
			if (oldRace <= 0) {
				oldRace = 50;
			}
			ballCount = oldRace;
			caromCount = defaultCaroms(oldRace);
			specialLastShot = true;
		} else {
			ballCount = savedBalls;
			caromCount = saver.getInt("caromCount", index, defaultCaroms(ballCount));
			specialLastShot = saver.getBoolean("specialLastShot", index, true);
		}
		score = saver.getInt("score", index, score);
		inning = saver.getInt("inning", index, inning);
		normalize();
	}
}
