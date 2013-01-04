package net.grenning.pool_scorekeeper;

public class StraightPoolGameScorer {
	
	StraightPoolScorer player1Scorer;
	StraightPoolScorer player2Scorer;
	StraightPoolScorer currentPlayer;

	public StraightPoolGameScorer(StraightPoolScorer player1Scorer,
			StraightPoolScorer player2Scorer) {
		super();
		this.player1Scorer = player1Scorer;
		this.player2Scorer = player2Scorer;
		currentPlayer = player1Scorer;
		currentPlayer.yourBreak();
	}

	public int ballsOnTheTable() {
		// TODO Auto-generated method stub
		return 15;
	}

	public void foul() {
		currentPlayer.foul();		
		playerMissesShot();
	}

	public void playerMakesShot() {
		currentPlayer.goodShot();
	}

	public void playerMissesShot() {
		if (currentPlayer == player1Scorer)
			currentPlayer = player2Scorer;
		else
			currentPlayer = player1Scorer;
	}

}
