package net.grenning.pool_scorekeeper.cowboy;

import net.grenning.pool_scorekeeper.NameValueSaver;

public class CowboyPlayer {

	public String name = "";
	public int raceTo = 50;
	public int score;
	public int inning;

	public int total() {
		return score + inning;
	}

	public boolean hasWon() {
		return score >= raceTo;
	}

	public void save(NameValueSaver saver, int index) {
		saver.save("name", index, name);
		saver.save("raceTo", index, raceTo);
		saver.save("score", index, score);
		saver.save("inning", index, inning);
	}

	public void restore(NameValueSaver saver, int index) {
		name = saver.getString("name", index, name);
		raceTo = saver.getInt("raceTo", index, raceTo);
		if (raceTo <= 0) {
			raceTo = 50;
		}
		score = saver.getInt("score", index, score);
		inning = saver.getInt("inning", index, inning);
	}
}
