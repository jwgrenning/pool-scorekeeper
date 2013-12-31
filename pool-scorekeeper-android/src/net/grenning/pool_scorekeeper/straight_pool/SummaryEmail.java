package net.grenning.pool_scorekeeper.straight_pool;

import android.app.Activity;
import net.grenning.pool_scorekeeper.R;

public class SummaryEmail {
	private static final String gameCountFormat = "%s %d\n";
	private static final String playerCountFormat = "     %s %d\n";
	GameScorer scorer;
	
	class GamesStats implements GameView {
		
		public int winningPlayer = -1;
		public int inning = -1;
		public boolean isOver = false;
		public int ballsOnTheTable = -1;

		@Override
		public void theWinnerIs(int playerNumber) {
			winningPlayer  = playerNumber;
		}
		
		@Override
		public void suggestRerack() {
		}
		
		@Override
		public void oneBallOnTheTable() {
		}
		
		@Override
		public void inning(int inning) {
			this.inning = inning;
		}
		
		@Override
		public void gameOverApplause() {
			isOver = true;
		}
		
		@Override
		public void ballsOnTheTable(int balls) {
			ballsOnTheTable = balls;
		}
	};

	class PlayerStats implements PlayerView {
		
		public int score = -1;
		public int safesMissed = -1;
		public int safesMade = -1;
		public int longestRun = -1;
		public int totalFouls = -1;
		public int consecutiveSafes = -1;
		public int consecutiveFouls = -1;
		public int ballsNeededToWin = -1;
		public String inningRecord = "---";

		@Override
		public void score(int score) {
			this.score = score;
		}
		
		@Override
		public void safesMissed(int count) {
			safesMissed = count;
		}
		
		@Override
		public void safesMade(int count) {
			safesMade = count;
		}
		
		@Override
		public void rackScore(int player1RackScore) {
		}
		
		@Override
		public void makeInactive() {
		}
		
		@Override
		public void makeActive() {
		}
		
		@Override
		public void longestRun(int count) {
			longestRun = count;
		}
		
		@Override
		public void fouls(int fouls) {
			totalFouls = fouls;
		}
		
		@Override
		public void currentRun(int count) {
		}
		
		@Override
		public void consecutiveSafes(int count) {
			consecutiveSafes = count;
		}
		
		@Override
		public void consecutiveFouls(int count) {
			consecutiveFouls = count;
		}
		
		@Override
		public void ballsNeededToWin(int count) {
			ballsNeededToWin = count;
		}

		@Override
		public void inningRecord(String record) {
			inningRecord = record;
		}
	};
	
	GamesStats game = new GamesStats();
	PlayerStats player1 = new PlayerStats();
	PlayerStats player2 = new PlayerStats();
	private String player1Name;
	private String player2Name;

	public SummaryEmail(GameScorer scorer, String player1Name, String player2Name) {
		super();
		this.scorer = scorer;
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		scorer.reportSummary(game, player1, player2);
	}

	String subject(Activity activity) {
		String subject = String.format("%s vs %s ", player1Name, player2Name);
//		if (game.isOver)
//			subject += "we have a winner"; 
//		else
//			subject += "in progess"; 
					
		return subject;
	}

	public String body(Activity activity) {
		String body = heading();
		
		body += String.format(gameCountFormat, activity.getResources().getString(R.string.balls_left_on_the_table), game.ballsOnTheTable);
		body += String.format(gameCountFormat, activity.getResources().getString(R.string.inning_title), game.inning);
		
		body += String.format("\n%s\n", player1Name);
		body += playerSummary(player1, activity);
		body += String.format("\n%s\n", player2Name);
		body += playerSummary(player2, activity);

		return body;
	}
	
	private String heading()
	{
		String heading = String.format("%s vs %s\n", player1Name, player2Name);
		String winner = "??";
		if (game.winningPlayer == 1)
			winner = player2Name;
		else if (game.winningPlayer == 2)
			winner = player2Name;
		
//		if (game.isOver)
//			heading += String.format("The winner is %s\n", winner); 
//		else
//			heading += "Game is in progesss\n"; 
		
		return heading;
	}
	
	private String playerSummary(PlayerStats player, Activity activity)
	{
		String body = "";
		body += String.format(playerCountFormat, activity.getResources().getString(R.string.total_points), player.score);
		body += String.format(playerCountFormat, activity.getResources().getString(R.string.points_to_win), player.ballsNeededToWin);
		body += String.format(playerCountFormat, activity.getResources().getString(R.string.longest_run), player.longestRun);
		body += String.format(playerCountFormat, activity.getResources().getString(R.string.safe_made), player.safesMade);
		body += String.format(playerCountFormat, activity.getResources().getString(R.string.total_fouls), player.totalFouls);
		body += String.format(playerCountFormat, activity.getResources().getString(R.string.consecutive_safes_made), player.consecutiveSafes);
		body += String.format(playerCountFormat, activity.getResources().getString(R.string.consecutive_fouls), player.consecutiveFouls);

		
		body +=  activity.getResources().getString(R.string.inning_title) + player.inningRecord;
		
		return body;
		
	}

}
