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

	public enum FoulKind {
		NONE,
		CALLED,
		POCKET_DURING_CAROMS,
		PASSED_MIXED_LIMIT,
		PASSED_CAROM_LIMIT,
		NOT_WIN_SHOT
	}

	private static final int MAX_UNDO = 100;

	private CowboyPlayer[] players;
	private int current;
	private int inning = 1;
	private FoulKind lastFoul = FoulKind.NONE;
	private int lastFoulPoints;
	private final Deque<MapNameValueSaver> history = new ArrayDeque<MapNameValueSaver>();
	private final Deque<MapNameValueSaver> redoHistory = new ArrayDeque<MapNameValueSaver>();

	public CowboyGame(CowboyPlayer... players) {
		if (players == null || players.length < 2 || players.length > 4) {
			throw new IllegalArgumentException("Cowboy pool is 2 to 4 players");
		}
		this.players = players;
		for (CowboyPlayer player : players) {
			player.normalize();
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

	public FoulKind lastFoul() {
		return lastFoul;
	}

	public int lastFoulPoints() {
		return lastFoulPoints;
	}

	public Phase phase() {
		return phaseOf(currentPlayer());
	}

	public Phase phaseOf(CowboyPlayer player) {
		int total = player.total();
		if (player.specialLastShot && total >= player.caromLimit()) {
			return Phase.WIN;
		}
		if (total >= player.mixedLimit()) {
			return Phase.CAROMS;
		}
		return Phase.MIXED;
	}

	public Result pocket(int ball) {
		if (ball != 1 && ball != 3 && ball != 5) {
			return Result.IGNORED;
		}
		return combo(ball == 1, ball == 3, ball == 5, 0);
	}

	public Result caromTwo() {
		return combo(false, false, false, 1);
	}

	public Result caromThree() {
		return combo(false, false, false, 2);
	}

	public static int comboPoints(boolean ball1, boolean ball3, boolean ball5, int caroms) {
		if (caroms < 0 || caroms > 3) {
			return 0;
		}
		int pockets = 0;
		if (ball1) {
			pockets += 1;
		}
		if (ball3) {
			pockets += 3;
		}
		if (ball5) {
			pockets += 5;
		}
		int caromPoints = pockets == 9 ? Math.min(caroms, 2) : caroms;
		return pockets + caromPoints;
	}

	public Result combo(boolean ball1, boolean ball3, boolean ball5, int caroms) {
		int points = comboPoints(ball1, ball3, ball5, caroms);
		if (points <= 0) {
			return Result.IGNORED;
		}
		int pockets = (ball1 ? 1 : 0) + (ball3 ? 3 : 0) + (ball5 ? 5 : 0);
		return scorePoints(points, pockets == 0);
	}

	public Result winShot() {
		if (isOver()) {
			return Result.IGNORED;
		}
		checkpoint();
		CowboyPlayer player = currentPlayer();
		if (!player.specialLastShot || player.total() != player.caromLimit()) {
			return foulAfterCheckpoint(FoulKind.NOT_WIN_SHOT, 0);
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
		return foulAfterCheckpoint(FoulKind.CALLED, 0);
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
			return foulAfterCheckpoint(FoulKind.NOT_WIN_SHOT, 0);
		}
		if (phase == Phase.CAROMS && !carom) {
			return foulAfterCheckpoint(FoulKind.POCKET_DURING_CAROMS, 0);
		}
		int after = player.total() + points;
		if (phase == Phase.MIXED && after > player.mixedLimit()) {
			return foulAfterCheckpoint(FoulKind.PASSED_MIXED_LIMIT, player.mixedLimit());
		}
		int caromCap = player.specialLastShot ? player.caromLimit() : player.raceTo();
		if (phase == Phase.CAROMS && after > caromCap) {
			return foulAfterCheckpoint(player.specialLastShot
					? FoulKind.NOT_WIN_SHOT : FoulKind.PASSED_CAROM_LIMIT, caromCap);
		}
		if (!player.specialLastShot && after >= player.raceTo()) {
			player.inning += points;
			player.score += player.inning;
			player.inning = 0;
			return Result.WIN;
		}
		player.inning += points;
		return Result.CONTINUE;
	}

	private Result foulAfterCheckpoint(FoulKind kind, int points) {
		lastFoul = kind;
		lastFoulPoints = points;
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
