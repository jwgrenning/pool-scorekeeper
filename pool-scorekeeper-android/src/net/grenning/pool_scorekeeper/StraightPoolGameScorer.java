package net.grenning.pool_scorekeeper;

public class StraightPoolGameScorer {
	
	StraightPoolScorer player1Scorer;
	StraightPoolScorer player2Scorer;
	StraightPoolScorer currentPlayer;

	public StraightPoolGameScorer(StraightPoolScorer player1Scorer,
			StraightPoolScorer player2Scorer) {
		super();
		this.player1Scorer = player1Scorer;
		this.player1Scorer.makeActive();
		this.player2Scorer = player2Scorer;
		this.player2Scorer.makeInactive();
		currentPlayer = player1Scorer;
		currentPlayer.yourBreak();
	}

	public int ballsOnTheTable() {
		return 15;
	}

	public void foul() {
		currentPlayer.foul();		
		switchPlayers();
	}

	public void playerMakesShot() {
		currentPlayer.goodShot();
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
		player1Scorer.newRack();
		player2Scorer.newRack();	
	}

}
