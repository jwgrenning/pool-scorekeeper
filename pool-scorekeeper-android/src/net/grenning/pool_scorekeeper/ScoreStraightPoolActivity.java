package net.grenning.pool_scorekeeper;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ScoreStraightPoolActivity extends Activity {

	StraightPoolGameScorer scorer;
	StraightPoolScorer player1Scorer;
	StraightPoolScorer player2Scorer;
	
	StraightPoolScorerView player1View = new  StraightPoolScorerView() {

		@Override
		public void score(int i) {
			setFieldById(R.id.player1Score, i);
		}

		@Override
		public void ballsNeededToWin(int ballsNeededToWin) {
			setFieldById(R.id.player1PointsToWin, ballsNeededToWin);
		}

		@Override
		public void rackScore(int player1RackScore) {
			setFieldById(R.id.player1BallsThisRack, player1RackScore);
		}
		
		@Override
		public void fouls(int count) {
			setFieldById(R.id.player1TotalFouls, count);
		}

		@Override
		public void consecutiveFouls(int count) {
			setFieldById(R.id.player1ConsecutiveFouls, count);
		}		
	};
	

	StraightPoolScorerView player2View = new  StraightPoolScorerView() {

		@Override
		public void score(int i) {
			setFieldById(R.id.player2Score, i);
		}

		@Override
		public void ballsNeededToWin(int ballsNeededToWin) {
			setFieldById(R.id.player2PointsToWin, ballsNeededToWin);
		}

		@Override
		public void rackScore(int rackScore) {
			setFieldById(R.id.player2BallsThisRack, rackScore);
		}
		
		@Override
		public void fouls(int count) {
			setFieldById(R.id.player2TotalFouls, count);
		}

		@Override
		public void consecutiveFouls(int count) {
			setFieldById(R.id.player2ConsecutiveFouls, count);
		}		
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_score_straight_pool);

		setPlayerName("player1Name", R.id.player1Name,
				R.string.default_player1Name);
		setPlayerName("player2Name", R.id.player2Name,
				R.string.default_player2Name);
				
		player1Scorer = new StraightPoolScorer(player1View, getNumberFieldFromIntent("player1PointsToWin"));
		player2Scorer = new StraightPoolScorer(player2View, getNumberFieldFromIntent("player2PointsToWin"));
		scorer = new StraightPoolGameScorer(player1Scorer, player2Scorer);
		
		setFieldById(R.id.player2Score, 0);
		setFieldById(R.id.player2BallsThisRack, 0);
		setFieldById(R.id.player2PointsToWin, getNumberFieldFromIntent("player2PointsToWin"));
		setFieldById(R.id.player2ConsecutiveFouls, 0);
		setFieldById(R.id.player2TotalFouls, 0);
	}

	private void setFieldById(int id, int value) {
		TextView field = (TextView) findViewById(id);
		field.setText(Integer.valueOf(value).toString());
	}

	private void setPlayerName(String player, int playerTextId,
			int defaultPlayer) {
		String p1 = getIntent().getStringExtra(player);
		EditText playerText = (EditText) findViewById(playerTextId);
		if (p1.equals(""))
			playerText.setText(defaultPlayer);
		else
			playerText.setText(p1);
	}

	private int getNumberFieldFromIntent(String name) {
		String number = getIntent().getStringExtra(name);
		return Integer.valueOf(number);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_score_straight_pool, menu);
		return true;
	}

	public void showGeneralPoolRules(View view) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://www.wpa-pool.com/web/the_rules_of_play"));
		startActivity(browserIntent);
	}

	public void showStraightPoolRules(View view) {
		Intent browserIntent = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("http://www.wpa-pool.com/web/index.asp?id=119&pagetype=rules"));
		startActivity(browserIntent);
	}

	public void shotMadeButtonClicked(View view) {
		scorer.playerMakesShot();
	}

	public void missedShotButtonClicked(View view) {
		scorer.playerMissesShot();
	}

	public void foulButtonClicked(View view) {
		scorer.foul();
	}
	
	public void newRackButtonClicked(View view) {
		scorer.newRack();
	}

}
