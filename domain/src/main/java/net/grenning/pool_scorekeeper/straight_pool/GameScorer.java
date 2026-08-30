package net.grenning.pool_scorekeeper.straight_pool;

import java.util.ArrayDeque;
import java.util.Deque;

import net.grenning.pool_scorekeeper.MapNameValueSaver;
import net.grenning.pool_scorekeeper.NameValueSaver;

public class GameScorer {

	private static final String BALLS_ON_THE_TABLE = "ballsOnTheTable";
	private static final String CURRENT_PLAYER_NUMBER = "currentPlayerNumber";
	private static final String INNING = "inning";
	private static final String UNDO_COUNT = "undoCount";
	private static final String UNDO_PREFIX = "undo";
	private static final String REDO_COUNT = "redoCount";
	private static final String REDO_PREFIX = "redo";
	private static final String TURN_STARTED_AT = "turnStartedAt";
	private static final String TURN_ELAPSED_MILLIS = "turnElapsedMillis";
	private static final int MAX_UNDO = 100;

	PlayerScorer player1Scorer;
	PlayerScorer player2Scorer;
	PlayerScorer playerScorer[] = new PlayerScorer[2];
	PlayerScorer currentPlayerScorer;
	GameView gameView;
	int currentPlayerNumber = 0;
	int ballsOnTheTable = 15;
	int inning = 1;
	private final Deque<MapNameValueSaver> history = new ArrayDeque<MapNameValueSaver>();
	private final Deque<MapNameValueSaver> redoHistory = new ArrayDeque<MapNameValueSaver>();
	private long nowMillis = -1;
	private long turnStartedAtMillis = 0;
	private int turnElapsedMillis = 0;
	private long turnRunningSinceMillis = 0;
	private boolean clockRunning = false;
	
	public GameScorer(GameView gameView, PlayerScorer player1Scorer,
			PlayerScorer player2Scorer) {
		super();
		this.gameView = gameView;

		playerScorer[0] = player1Scorer;
		playerScorer[1] = player2Scorer;
		player1Scorer.makeActive();
		player2Scorer.makeInactive();
		currentPlayerScorer = player1Scorer;
		currentPlayerScorer.yourBreak();

		this.player1Scorer = player1Scorer;
		this.player2Scorer = player2Scorer;
		startTurn();
		updateView();
	}

	public void setNowMillis(long millis) {
		nowMillis = millis;
	}

	public void startTurnAt(long millis) {
		nowMillis = millis;
		startTurn();
		updateView();
	}

	private long now() {
		if (nowMillis >= 0) {
			return nowMillis;
		}
		return System.currentTimeMillis();
	}

	private void updateView() {
		gameView.ballsOnTheTable(ballsOnTheTable);
		gameView.inning(inning);
		updateActivePlayer();
		playerScorer[currentPlayerNumber].showTurnStartedAt(turnStartedAtMillis);
		playerScorer[currentPlayerNumber ^ 1].showTurnStartedAt(0);
		if (playerScorer[0].wins()) {
			gameView.theWinnerIs(1);
		} else if (playerScorer[1].wins()) {
			gameView.theWinnerIs(2);
		} else {
			gameView.noWinner();
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

	public boolean canUndo() {
		return !history.isEmpty();
	}

	public boolean canRedo() {
		return !redoHistory.isEmpty();
	}

	public void foul() {
		checkpoint();
		currentPlayerScorer.recordShot();
		if (currentPlayerScorer.foul()) {
			applyThreeFoulPenalty();
			return;
		}
		switchPlayers();
	}

	public int ballsOnTheTable() {
		return ballsOnTheTable;
	}

	public void playerMakesShot() {
		playerMakesShots(1);
	}

	public void playerMakesShots(int balls) {
		if (balls <= 0) {
			return;
		}
		if (ballsOnTheTable == 0) {
			maybeSuggestRerack();
			return;
		}
		checkpoint();
		currentPlayerScorer.recordShot();
		int remaining = Math.min(balls, ballsOnTheTable);
		for (int i = 0; i < remaining; i++) {
			currentPlayerScorer.goodShot();
			oneLessBallOnTheTable();
			if (currentPlayerScorer.wins()) {
				gameView.theWinnerIs(currentPlayerNumber + 1);
				gameView.gameOverApplause();
				return;
			}
		}
		maybeSuggestRerack();
	}

	public boolean isOver() {
		return playerScorer[0].wins() || playerScorer[1].wins();
	}

	private void maybeSuggestRerack() {
		if (isOver()) {
			return;
		}
		if (ballsOnTheTable <= 1) {
			gameView.suggestRerack();
		}
	}

	public void playerMissesShot() {
		checkpoint();
		currentPlayerScorer.recordShot();
		currentPlayerScorer.missedShot();
		switchPlayers();
	}

	private void switchPlayers() {
		endTurn();
		currentPlayerNumber ^= 1;
		currentPlayerScorer = playerScorer[currentPlayerNumber];
		if (currentPlayerNumber == 0)
			inning++;
		startTurn();
		updateView();
	}

	private void startTurn() {
		turnStartedAtMillis = now();
		turnElapsedMillis = 0;
		turnRunningSinceMillis = now();
		clockRunning = true;
	}

	private void endTurn() {
		captureElapsed();
		currentPlayerScorer.addCompletedTurn(turnElapsedMillis);
	}

	private void captureElapsed() {
		if (clockRunning) {
			turnElapsedMillis += (int) (now() - turnRunningSinceMillis);
			turnRunningSinceMillis = now();
		}
	}

	private void updateActivePlayer() {
		playerScorer[currentPlayerNumber].makeActive();
		playerScorer[currentPlayerNumber ^ 1].makeInactive();
	}

	public void newRack() {
		checkpoint();
		resetRack();
		updateView();
	}

	private void applyThreeFoulPenalty() {
		endTurn();
		resetRack();
		currentPlayerScorer.yourBreak();
		startTurn();
		updateView();
		gameView.threeFoulPenalty(currentPlayerNumber + 1);
	}

	private void resetRack() {
		ballsOnTheTable = 15;
		player1Scorer.newRack();
		player2Scorer.newRack();
	}

	private void oneLessBallOnTheTable() {
		ballsOnTheTable--;
		if (ballsOnTheTable == 1)
			gameView.oneBallOnTheTable();
		else
			gameView.ballsOnTheTable(ballsOnTheTable);
	}

	public void save(NameValueSaver saver) {
		saveState(saver);
		saveStack(saver, history, UNDO_COUNT, UNDO_PREFIX);
		saveStack(saver, redoHistory, REDO_COUNT, REDO_PREFIX);
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

	private void saveState(NameValueSaver saver) {
		captureElapsed();
		saver.save(CURRENT_PLAYER_NUMBER, currentPlayerNumber);
		saver.save(BALLS_ON_THE_TABLE, ballsOnTheTable);
		saver.save(INNING, inning);
		saver.save(TURN_STARTED_AT, Long.toString(turnStartedAtMillis));
		saver.save(TURN_ELAPSED_MILLIS, turnElapsedMillis);
		playerScorer[0].save(saver, 1);
		playerScorer[1].save(saver, 2);
	}

	public void populateFromPersistence(NameValueSaver saver) {
		restoreState(saver);
		loadStack(saver, history, UNDO_COUNT, UNDO_PREFIX);
		loadStack(saver, redoHistory, REDO_COUNT, REDO_PREFIX);
	}

	public void adjustRaces(int player1Race, int player2Race) {
		boolean changed = playerScorer[0].adjustRaceTo(player1Race)
				| playerScorer[1].adjustRaceTo(player2Race);
		if (changed) {
			history.clear();
			redoHistory.clear();
		}
		updateView();
	}

	private void loadStack(NameValueSaver saver, Deque<MapNameValueSaver> stack,
			String countKey, String prefix) {
		stack.clear();
		int count = saver.getInt(countKey, 0);
		for (int index = 0; index < count; index++) {
			stack.addLast(MapNameValueSaver.decode(saver.getString(prefix + index, "")));
		}
	}

	private void restoreState(NameValueSaver saver) {
		currentPlayerNumber = saver.getInt(CURRENT_PLAYER_NUMBER,
				currentPlayerNumber);
		ballsOnTheTable = saver.getInt(BALLS_ON_THE_TABLE, 15);
		inning = saver.getInt(INNING, 1);
		playerScorer[0].restore(saver, 1);
		playerScorer[1].restore(saver, 2);
		currentPlayerScorer = playerScorer[currentPlayerNumber];
		try {
			turnStartedAtMillis = Long.parseLong(saver.getString(TURN_STARTED_AT, "0"));
		} catch (NumberFormatException e) {
			turnStartedAtMillis = 0;
		}
		turnElapsedMillis = saver.getInt(TURN_ELAPSED_MILLIS, 0);
		clockRunning = true;
		turnRunningSinceMillis = now();
		updateView();
	}

	public void playerMakesSafe() {
		checkpoint();
		currentPlayerScorer.recordShot();
		currentPlayerScorer.safeMade();
		switchPlayers();
	}

	public void playerMissesSafe() {
		checkpoint();
		currentPlayerScorer.recordShot();
		currentPlayerScorer.safeMissed();
		switchPlayers();
	}

	public void reportSummary(GameView gameView, PlayerView player1, PlayerView player2) {
		captureElapsed();
		gameView.ballsOnTheTable(ballsOnTheTable);
		gameView.inning(inning);
		int extraTime0 = currentPlayerNumber == 0 ? turnElapsedMillis : 0;
		int extraTurns0 = currentPlayerNumber == 0 ? 1 : 0;
		int extraTime1 = currentPlayerNumber == 1 ? turnElapsedMillis : 0;
		int extraTurns1 = currentPlayerNumber == 1 ? 1 : 0;
		playerScorer[0].reportSummary(player1, extraTime0, extraTurns0);
		playerScorer[1].reportSummary(player2, extraTime1, extraTurns1);
	}
}
