package net.grenning.pool_scorekeeper;

public class StraightPoolGameScorer {

	private static final String BALLS_ON_THE_TABLE = "ballsOnTheTable";
	private static final String CURRENT_PLAYER_NUMBER = "currentPlayerNumber";
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
		if (currentPlayerScorer.wins())
			gameView.gameOverApplause();
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

	public void save(GameFieldSaver saver) {
		saver.save(CURRENT_PLAYER_NUMBER, currentPlayerNumber);
		saver.save(BALLS_ON_THE_TABLE, ballsOnTheTable);
		playerScorer[0].save(saver, 1);
		playerScorer[1].save(saver, 2);
	}

	public void restore(GameFieldSaver saver) {
		currentPlayerNumber = saver.getInt(CURRENT_PLAYER_NUMBER, currentPlayerNumber);
		ballsOnTheTable = saver.getInt(BALLS_ON_THE_TABLE, 49);
		gameView.ballsOnTheTable(ballsOnTheTable);
		playerScorer[0].restore(saver, 1);
		playerScorer[1].restore(saver, 2);
		updateActivePlayer();
	}
}
