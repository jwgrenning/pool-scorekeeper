package net.grenning.pool_scorekeeper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Scanner;

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

	public boolean save(Writer writer) {
		try {
			writeInt(writer, currentPlayerNumber);
			writeInt(writer, ballsOnTheTable);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private void writeInt(Writer writer, int value) throws IOException {
		writer.write(String.valueOf(value));
		writer.write(" ");
	}

	public boolean restore(InputStream savedScore) {
		Scanner s = new Scanner(savedScore);
		currentPlayerNumber = s.nextInt();
		ballsOnTheTable = s.nextInt();
		gameView.ballsOnTheTable(ballsOnTheTable);
		updateActivePlayer();
		return true;
	}

}
