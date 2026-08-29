package net.grenning.pool_scorekeeper.straight_pool;

import java.util.ArrayDeque;
import java.util.Deque;

import net.grenning.pool_scorekeeper.MapNameValueSaver;
import net.grenning.pool_scorekeeper.NameValueSaver;

public class GameScorer {

	private static final String BALLS_ON_THE_TABLE = "ballsOnTheTable";
	private static final String CURRENT_PLAYER_NUMBER = "currentPlayerNumber";
	private static final String INNING = "inning";
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
		updateView();
	}

	private void updateView() {
		gameView.ballsOnTheTable(ballsOnTheTable);
		gameView.inning(inning);
		updateActivePlayer();
		if (playerScorer[0].wins()) {
			gameView.theWinnerIs(1);
		} else if (playerScorer[1].wins()) {
			gameView.theWinnerIs(2);
		} else {
			gameView.noWinner();
		}
	}

	private void checkpoint() {
		MapNameValueSaver snapshot = new MapNameValueSaver();
		save(snapshot);
		history.push(snapshot);
		while (history.size() > MAX_UNDO) {
			history.removeLast();
		}
	}

	public boolean undo() {
		if (history.isEmpty()) {
			return false;
		}
		restoreState(history.pop());
		return true;
	}

	public boolean canUndo() {
		return !history.isEmpty();
	}

	public void foul() {
		checkpoint();
		currentPlayerScorer.foul();
		switchPlayers();
	}

	public void playerMakesShot() {
		if (ballsOnTheTable == 0) {
			gameView.suggestRerack();
		} else {
			checkpoint();
			currentPlayerScorer.goodShot();
			oneLessBallOnTheTable();
			if (currentPlayerScorer.wins()) {
				gameView.theWinnerIs(currentPlayerNumber+1);
				gameView.gameOverApplause();
			}
		}
	}

	public void playerMissesShot() {
		checkpoint();
		currentPlayerScorer.missedShot();
		switchPlayers();
	}

	private void switchPlayers() {
		currentPlayerNumber ^= 1;
		currentPlayerScorer = playerScorer[currentPlayerNumber];
		if (currentPlayerNumber == 0)
			inning++;
		updateView();
	}

	private void updateActivePlayer() {
		playerScorer[currentPlayerNumber].makeActive();
		playerScorer[currentPlayerNumber ^ 1].makeInactive();
	}

	public void newRack() {
		checkpoint();
		ballsOnTheTable = 15;
		player1Scorer.newRack();
		player2Scorer.newRack();
		updateView();
	}

	private void oneLessBallOnTheTable() {
		ballsOnTheTable--;
		if (ballsOnTheTable == 1)
			gameView.oneBallOnTheTable();
		else
			gameView.ballsOnTheTable(ballsOnTheTable);
		if (ballsOnTheTable <= 1)
			gameView.suggestRerack();
	}

	public void save(NameValueSaver saver) {
		saver.save(CURRENT_PLAYER_NUMBER, currentPlayerNumber);
		saver.save(BALLS_ON_THE_TABLE, ballsOnTheTable);
		saver.save(INNING, inning);
		playerScorer[0].save(saver, 1);
		playerScorer[1].save(saver, 2);
	}

	public void populateFromPersistence(NameValueSaver saver) {
		history.clear();
		restoreState(saver);
	}

	private void restoreState(NameValueSaver saver) {
		currentPlayerNumber = saver.getInt(CURRENT_PLAYER_NUMBER,
				currentPlayerNumber);
		ballsOnTheTable = saver.getInt(BALLS_ON_THE_TABLE, 15);
		inning = saver.getInt(INNING, 1);
		playerScorer[0].restore(saver, 1);
		playerScorer[1].restore(saver, 2);
		currentPlayerScorer = playerScorer[currentPlayerNumber];
		updateView();
	}

	public void playerMakesSafe() {
		checkpoint();
		currentPlayerScorer.safeMade();
		switchPlayers();
	}

	public void playerMissesSafe() {
		checkpoint();
		currentPlayerScorer.safeMissed();
		switchPlayers();
	}

	public void reportSummary(GameView gameView, PlayerView player1, PlayerView player2) {
		gameView.ballsOnTheTable(ballsOnTheTable);
		gameView.inning(inning);
		playerScorer[0].reportSummary(player1);
		playerScorer[1].reportSummary(player2);
	}
}
