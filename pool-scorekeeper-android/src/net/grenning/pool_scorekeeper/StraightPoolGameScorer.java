package net.grenning.pool_scorekeeper;

import java.util.prefs.Preferences;

public class StraightPoolGameScorer {

	StraightPoolPlayerScorer player1Scorer;
	StraightPoolPlayerScorer player2Scorer;
	StraightPoolPlayerScorer playerScorer[] = new StraightPoolPlayerScorer[2];
	int currentPlayerNumber = 0;
	StraightPoolPlayerScorer currentPlayerScorer;
	StraightPoolView gameView;
	int ballsOnTheTable = 15;

	public StraightPoolGameScorer(StraightPoolView gameView,
			StraightPoolPlayerScorer player1Scorer,
			StraightPoolPlayerScorer player2Scorer) {
		super();
		this.gameView = gameView;
		gameView.ballsOnTheTable(ballsOnTheTable);

		playerScorer[0] = player1Scorer;
		playerScorer[1] = player2Scorer;
		player1Scorer.makeActive();
		player2Scorer.makeInactive();
		currentPlayerScorer = player1Scorer;
		currentPlayerScorer.yourBreak();

		this.player1Scorer = player1Scorer;
		this.player2Scorer = player2Scorer;
	}

	public void foul() {
		currentPlayerScorer.foul();
		switchPlayers();
	}

	public void playerMakesShot() {
		currentPlayerScorer.goodShot();
		oneLessBallOnTheTable();
	}

	public void playerMissesShot() {
		currentPlayerScorer.missedShot();
		switchPlayers();
	}

	private void switchPlayers() {
		currentPlayerNumber ^= 1;
		currentPlayerScorer = playerScorer[currentPlayerNumber];
		updateActivePlayer();
	}

	private void updateActivePlayer() {
		playerScorer[currentPlayerNumber].makeActive();
		playerScorer[currentPlayerNumber^1].makeInactive();
	}

	public void newRack() {
		ballsOnTheTable = 15;
		gameView.ballsOnTheTable(ballsOnTheTable);
		player1Scorer.newRack();
		player2Scorer.newRack();
	}

	private void oneLessBallOnTheTable() {
		ballsOnTheTable--;
		gameView.ballsOnTheTable(ballsOnTheTable);
		if (ballsOnTheTable <= 1)
			gameView.suggestRerack();
	}

	public void save() {
		Preferences gameState;
		gameState = Preferences.userRoot().node(this.getClass().getName());
		gameState.putInt("currentPlayerNumber", currentPlayerNumber);
		gameState.putInt("ballsOnTheTable", ballsOnTheTable);
	}

	public void restore() {
		Preferences gameState;
		gameState = Preferences.userRoot().node(this.getClass().getName());
		currentPlayerNumber = gameState.getInt("currentPlayerNumber", currentPlayerNumber);
		ballsOnTheTable = gameState.getInt("ballsOnTheTable", ballsOnTheTable);
		gameView.ballsOnTheTable(ballsOnTheTable);
		updateActivePlayer();
	}
}
