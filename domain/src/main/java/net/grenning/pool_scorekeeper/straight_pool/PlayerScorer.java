package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.NameValueSaver;

public class PlayerScorer {

	private PlayerView view;
	private int ballsNeededToWin = -1;
	private int score = -1;
	private int rackScore = -1;
	private int consecutiveFouls = -1;
	private int fouls = -1;
	private boolean breakShotComing = false;
	private int currentRun = -1;
	private int longestRun = -1;
	private int safesMade = -1;
	private int safesMissed = -1;
	private int consecutiveSafes = -1;
	private String inningRecord = "";
	private int tableTimeMillis = 0;
	private int turnCount = 0;
	private int shotCount = 0;
	private int raceTo = 0;

	public PlayerScorer(PlayerView view,
			int ballsNeededToWin) {
		this.view = view;
		reset(ballsNeededToWin);
	}

	public void reset(int ballsNeededToWin) {
		this.raceTo = ballsNeededToWin;
		this.ballsNeededToWin = ballsNeededToWin;
		score = 0;
		rackScore = 0;
		consecutiveFouls = 0;
		fouls = 0;
		breakShotComing = false;
		currentRun = 0;
		longestRun = 0;
		safesMade = 0;
		safesMissed = 0;
		consecutiveSafes = 0;
		inningRecord = "";
		tableTimeMillis = 0;
		turnCount = 0;
		shotCount = 0;
		updateView(view);
	}

	public boolean adjustRaceTo(int newRace) {
		if (newRace <= 0 || newRace == raceTo) {
			return false;
		}
		ballsNeededToWin += newRace - raceTo;
		raceTo = newRace;
		if (ballsNeededToWin < 0) {
			ballsNeededToWin = 0;
		}
		updateView(view);
		return true;
	}

	private void updateView(PlayerView view) {
		view.score(score);
		view.rackScore(rackScore);
		view.ballsNeededToWin(ballsNeededToWin);
		view.consecutiveFouls(consecutiveFouls);
		view.fouls(fouls);
		view.currentRun(currentRun);
		view.longestRun(longestRun);
		view.safesMade(safesMade);
		view.safesMissed(safesMissed);
		view.consecutiveSafes(consecutiveSafes);
		view.inningRecord(inningRecord);
		view.tableTimeMillis(tableTimeMillis);
		view.turnCount(turnCount);
		view.shotCount(shotCount);
	}

	public void goodShot() {
		score++;
		rackScore++;
		ballsNeededToWin--;
		breakShotComing = false;
		consecutiveFouls = 0;
		currentRun++;
		if (currentRun > longestRun)
			longestRun = currentRun;
		updateView(view);
	}

	public boolean foul() {
		updateInningRecord("F");
		boolean threeFoulPenalty = false;
		if (breakShotComing) {
			breakShotComing = false;
			ballsNeededToWin++;
			score--;
		} else {
			consecutiveFouls++;
		}

		score--;
		ballsNeededToWin++;
		fouls++;
		currentRun = 0;
		if (consecutiveFouls == 3) {
			score -= 15;
			ballsNeededToWin += 15;
			consecutiveFouls = 0;
			threeFoulPenalty = true;
		}
		updateView(view);
		return threeFoulPenalty;
	}

	public void missedShot() {
		updateInningRecord(" ");
		breakShotComing = false;
		consecutiveFouls = 0;
		currentRun = 0;
		updateView(view);
	}

	private void updateInningRecord(String terminator) {
		inningRecord += String.format(" %2d%s", currentRun, terminator);
	}

	public void yourBreak() {
		breakShotComing = true;
	}

	public void newRack() {
		rackScore = 0;
		updateView(view);
	}

	public void makeActive() {
		view.makeActive();
	}

	public void makeInactive() {
		view.makeInactive();
		view.turnStartedAt(0);
	}

	public void showTurnStartedAt(long epochMillis) {
		view.turnStartedAt(epochMillis);
	}

	public void recordShot() {
		shotCount++;
		updateView(view);
	}

	public void addCompletedTurn(int elapsedMillis) {
		tableTimeMillis += elapsedMillis;
		turnCount++;
		updateView(view);
	}

	public void save(NameValueSaver saver, int playerNumber) {
		saver.save("ballsNeededToWin", playerNumber, ballsNeededToWin);
		saver.save("score", playerNumber, score);
		saver.save("rackScore", playerNumber, rackScore);
		saver.save("consecutiveFouls", playerNumber, consecutiveFouls);
		saver.save("fouls", playerNumber, fouls);
		saver.save("breakShotComing", playerNumber, breakShotComing);
		saver.save("currentRun", playerNumber, currentRun);
		saver.save("longestRun", playerNumber, longestRun);
		saver.save("safesMade", playerNumber, safesMade);
		saver.save("safesMissed", playerNumber, safesMissed);
		saver.save("consecutiveSafes", playerNumber, consecutiveSafes);
		saver.save("inningRecord", playerNumber, inningRecord);
		saver.save("tableTimeMillis", playerNumber, tableTimeMillis);
		saver.save("turnCount", playerNumber, turnCount);
		saver.save("shotCount", playerNumber, shotCount);
		saver.save("raceTo", playerNumber, raceTo);
	}

	public void restore(NameValueSaver saver, int playerNumber) {
		ballsNeededToWin = saver.getInt("ballsNeededToWin" + playerNumber, ballsNeededToWin);
		score = saver.getInt("score" + playerNumber, score);
		rackScore = saver.getInt("rackScore" + playerNumber, rackScore);
		consecutiveFouls = saver.getInt("consecutiveFouls" + playerNumber, consecutiveFouls);
		fouls = saver.getInt("fouls" + playerNumber, fouls);
		breakShotComing = saver.getBoolean("breakShotComing" + playerNumber, breakShotComing);
		currentRun = saver.getInt("currentRun" + playerNumber, 0);
		longestRun = saver.getInt("longestRun"+ playerNumber, 0);
		safesMade = saver.getInt("safesMade" + playerNumber, 0);
		safesMissed = saver.getInt("safesMissed" + playerNumber, 0);
		consecutiveSafes = saver.getInt("consecutiveSafes" + playerNumber, 0);
		inningRecord = saver.getString("inningRecord" + playerNumber, "");
		tableTimeMillis = saver.getInt("tableTimeMillis" + playerNumber, 0);
		turnCount = saver.getInt("turnCount" + playerNumber, 0);
		shotCount = saver.getInt("shotCount" + playerNumber, 0);
		raceTo = saver.getInt("raceTo" + playerNumber, -1);
		if (raceTo < 0) {
			raceTo = ballsNeededToWin + Math.max(score, 0);
		}
		updateView(view);
	}

	public int raceTo() {
		return raceTo;
	}

	public boolean wins() {
		return ballsNeededToWin == 0;
	}

	public void safeMade() {
		updateInningRecord("S");
		safesMade++;
		currentRun = 0;
		consecutiveFouls = 0;
		breakShotComing = false;
		updateView(view);
	}

	public void safeMissed() {
		updateInningRecord("s");
		safesMissed++;
		currentRun = 0;
		consecutiveFouls = 0;
		breakShotComing = false;
		updateView(view);
	}

	public void reportSummary(PlayerView player) {
		reportSummary(player, 0, 0);
	}

	public void reportSummary(PlayerView player, int extraTableTimeMillis, int extraTurns) {
		updateView(player);
		player.tableTimeMillis(tableTimeMillis + extraTableTimeMillis);
		player.turnCount(turnCount + extraTurns);
		player.shotCount(shotCount);
	}

}
