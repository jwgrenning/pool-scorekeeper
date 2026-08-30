package net.grenning.pool_scorekeeper.cowboy;

import java.util.ArrayDeque;
import java.util.Deque;

import net.grenning.pool_scorekeeper.MapNameValueSaver;
import net.grenning.pool_scorekeeper.NameValueSaver;

public class CowboyGame {

	public enum Result {
		CONTINUE, TURN_OVER, FOUL, WIN, IGNORED
	}

	public enum Phase {
		MIXED, CAROMS, WIN
	}

	private static final int MAX_UNDO = 100;

	private CowboyPlayer[] players;
	private int current;
	private int inning = 1;
	private final Deque<MapNameValueSaver> history = new ArrayDeque<MapNameValueSaver>();
	private final Deque<MapNameValueSaver> redoHistory = new ArrayDeque<MapNameValueSaver>();

	public CowboyGame(CowboyPlayer... players) {
		if (players == null || players.length < 2 || players.length > 4) {
			throw new IllegalArgumentException("Cowboy pool is 2 to 4 players");
		}
		this.players = players;
		for (CowboyPlayer player : players) {
			if (player.raceTo <= 0) {
				player.raceTo = 50;
			}
		}
	}

	public int playerCount() {
		return players.length;
	}

	public CowboyPlayer player(int index) {
		return players[index];
	}

	public int currentIndex() {
		return current;
	}

	public CowboyPlayer currentPlayer() {
		return players[current];
	}

	public int inningNumber() {
		return inning;
	}

	public static int caromStretch(int raceTo) {
		return Math.max(1, raceTo / 10);
	}

	public static int mixedLimit(int raceTo) {
		return Math.max(0, raceTo - caromStretch(raceTo));
	}

	public Phase phase() {
		return phaseOf(currentPlayer());
	}

	public Phase phaseOf(CowboyPlayer player) {
		int total = player.total();
		if (total >= player.raceTo) {
			return Phase.WIN;
		}
		if (total == player.raceTo - 1) {
			return Phase.WIN;
		}
		if (total >= mixedLimit(player.raceTo)) {
			return Phase.CAROMS;
		}
		return Phase.MIXED;
	}

	public Result pocket(int ball) {
		if (ball != 1 && ball != 3 && ball != 5) {
			return Result.IGNORED;
		}
		return scorePoints(ball, false);
	}

	public Result caromTwo() {
		return scorePoints(1, true);
	}

	public Result caromThree() {
		return scorePoints(2, true);
	}

	public Result winShot() {
		if (isOver()) {
			return Result.IGNORED;
		}
		checkpoint();
		CowboyPlayer player = currentPlayer();
		if (player.total() != player.raceTo - 1) {
			return foulAfterCheckpoint();
		}
		player.inning += 1;
		player.score += player.inning;
		player.inning = 0;
		return Result.WIN;
	}

	public Result miss() {
		if (isOver()) {
			return Result.IGNORED;
		}
		checkpoint();
		CowboyPlayer player = currentPlayer();
		player.score += player.inning;
		player.inning = 0;
		if (player.hasWon()) {
			return Result.WIN;
		}
		advance();
		return Result.TURN_OVER;
	}

	public Result foul() {
		if (isOver()) {
			return Result.IGNORED;
		}
		checkpoint();
		return foulAfterCheckpoint();
	}

	public boolean isOver() {
		for (CowboyPlayer player : players) {
			if (player.hasWon()) {
				return true;
			}
		}
		return false;
	}

	public int winnerIndex() {
		for (int i = 0; i < players.length; i++) {
			if (players[i].hasWon()) {
				return i;
			}
		}
		return -1;
	}

	public boolean canUndo() {
		return !history.isEmpty();
	}

	public boolean canRedo() {
		return !redoHistory.isEmpty();
	}

	public boolean undo() {
		if (history.isEmpty()) {
			return false;
		}
		pushCapped(redoHistory);
		restoreState(history.pop());
		return true;
	}

	public boolean redo() {
		if (redoHistory.isEmpty()) {
			return false;
		}
		pushCapped(history);
		restoreState(redoHistory.pop());
		return true;
	}

	public void save(NameValueSaver saver) {
		saveState(saver);
		saveStack(saver, history, "undoCount", "undo");
		saveStack(saver, redoHistory, "redoCount", "redo");
	}

	public void restore(NameValueSaver saver) {
		restoreState(saver);
		loadStack(saver, history, "undoCount", "undo");
		loadStack(saver, redoHistory, "redoCount", "redo");
	}

	private Result scorePoints(int points, boolean carom) {
		if (isOver()) {
			return Result.IGNORED;
		}
		checkpoint();
		CowboyPlayer player = currentPlayer();
		Phase phase = phaseOf(player);
		if (phase == Phase.WIN) {
			return foulAfterCheckpoint();
		}
		if (phase == Phase.CAROMS && !carom) {
			return foulAfterCheckpoint();
		}
		int mixed = mixedLimit(player.raceTo);
		int after = player.total() + points;
		if (phase == Phase.MIXED && after > mixed) {
			return foulAfterCheckpoint();
		}
		if (phase == Phase.CAROMS && after > player.raceTo - 1) {
			return foulAfterCheckpoint();
		}
		player.inning += points;
		return Result.CONTINUE;
	}

	private Result foulAfterCheckpoint() {
		currentPlayer().inning = 0;
		advance();
		return Result.FOUL;
	}

	private void advance() {
		int previous = current;
		current = (current + 1) % players.length;
		if (current < previous) {
			inning++;
		}
	}

	private void checkpoint() {
		pushCapped(history);
		redoHistory.clear();
	}

	private void pushCapped(Deque<MapNameValueSaver> stack) {
		MapNameValueSaver snapshot = new MapNameValueSaver();
		saveState(snapshot);
		stack.push(snapshot);
		while (stack.size() > MAX_UNDO) {
			stack.removeLast();
		}
	}

	private void saveStack(NameValueSaver saver, Deque<MapNameValueSaver> stack,
			String countKey, String prefix) {
		saver.save(countKey, stack.size());
		int index = 0;
		for (MapNameValueSaver snapshot : stack) {
			saver.save(prefix + index, snapshot.encode());
			index++;
		}
	}

	private void loadStack(NameValueSaver saver, Deque<MapNameValueSaver> stack,
			String countKey, String prefix) {
		stack.clear();
		int count = saver.getInt(countKey, 0);
		for (int index = 0; index < count; index++) {
			stack.addLast(MapNameValueSaver.decode(saver.getString(prefix + index, "")));
		}
	}

	private void saveState(NameValueSaver saver) {
		saver.save("playerCount", players.length);
		saver.save("current", current);
		saver.save("inning", inning);
		for (int i = 0; i < players.length; i++) {
			players[i].save(saver, i);
		}
	}

	private void restoreState(NameValueSaver saver) {
		int count = saver.getInt("playerCount", players.length);
		if (count != players.length) {
			CowboyPlayer[] next = new CowboyPlayer[count];
			for (int i = 0; i < count; i++) {
				next[i] = i < players.length ? players[i] : new CowboyPlayer();
			}
			players = next;
		}
		current = saver.getInt("current", 0);
		inning = saver.getInt("inning", 1);
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				players[i] = new CowboyPlayer();
			}
			players[i].restore(saver, i);
		}
	}
}
