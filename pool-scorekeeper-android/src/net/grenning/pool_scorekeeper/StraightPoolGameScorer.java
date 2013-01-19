package net.grenning.pool_scorekeeper;

public class StraightPoolGameScorer {

	StraightPoolPlayerScorer player1Scorer;
	StraightPoolPlayerScorer player2Scorer;
	StraightPoolPlayerScorer currentPlayer;
	StraightPoolView gameView;
	int ballsOnTheTable = 15;

	public StraightPoolGameScorer(StraightPoolView gameView,
			StraightPoolPlayerScorer player1Scorer,
			StraightPoolPlayerScorer player2Scorer) {
		super();
		this.gameView = gameView;
		gameView.ballsOnTheTable(ballsOnTheTable);

		this.player1Scorer = player1Scorer;
		this.player1Scorer.makeActive();

		this.player2Scorer = player2Scorer;
		this.player2Scorer.makeInactive();

		currentPlayer = player1Scorer;
		currentPlayer.yourBreak();
	}

	public void foul() {
		currentPlayer.foul();
		switchPlayers();
	}

	public void playerMakesShot() {
		currentPlayer.goodShot();
		oneLessBallOnTheTable();
	}

	public void playerMissesShot() {
		currentPlayer.missedShot();
		switchPlayers();
	}

	private void switchPlayers() {

		currentPlayer.makeInactive();
		if (currentPlayer == player1Scorer)
			currentPlayer = player2Scorer;
		else
			currentPlayer = player1Scorer;
		currentPlayer.makeActive();
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

}
