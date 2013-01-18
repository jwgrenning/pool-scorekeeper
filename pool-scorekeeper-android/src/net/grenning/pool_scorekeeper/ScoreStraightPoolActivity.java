package net.grenning.pool_scorekeeper;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreStraightPoolActivity extends Activity {

	StraightPoolGameScorer scorer;
	StraightPoolScorer player1Scorer;
	StraightPoolScorer player2Scorer;

	StraightPoolScorerView player1View = new StraightPoolScorerView() {

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

		@Override
		public void makeActive() {
			setActiveById(R.id.player1Name);
		}

		@Override
		public void makeInactive() {
			setInactiveById(R.id.player1Name);
		}
	};

	StraightPoolScorerView player2View = new StraightPoolScorerView() {

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

		@Override
		public void makeActive() {
			setActiveById(R.id.player2Name);
		}

		@Override
		public void makeInactive() {
			setInactiveById(R.id.player2Name);
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

		player1Scorer = new StraightPoolScorer(player1View,
				getNumberFieldFromIntent("player1PointsToWin"));
		player2Scorer = new StraightPoolScorer(player2View,
				getNumberFieldFromIntent("player2PointsToWin"));
		scorer = new StraightPoolGameScorer(player1Scorer, player2Scorer);

		setFieldById(R.id.player2Score, 0);
		setFieldById(R.id.player2BallsThisRack, 0);
		setFieldById(R.id.player2PointsToWin,
				getNumberFieldFromIntent("player2PointsToWin"));
		setFieldById(R.id.player2ConsecutiveFouls, 0);
		setFieldById(R.id.player2TotalFouls, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_score_straight_pool, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.game_summary_button:
			showSummary();
			break;
		case R.id.email_game_summary_button:
			sendEmailSummary();
			break;
		case R.id.straight_pool_rules_button:
			showStraightPoolRules();
			break;
		case R.id.general_pool_rules_button:
			showGeneralPoolRules();
			break;

		}
		return true;
	}

	private void showSummary() {
		Toast.makeText(this, "Game summary details\nGame summary details\nGame summary details\nGame summary details\nGame summary details\nGame summary details\nGame summary details\n", Toast.LENGTH_LONG).show();	
	}

	private void sendEmailSummary() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"james@grenning.net"});
		i.putExtra(Intent.EXTRA_SUBJECT, "game summary");
		i.putExtra(Intent.EXTRA_TEXT   , "body of summary");
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}	}

	protected void setActiveById(int id) {
		TextView field = (TextView) findViewById(id);
		field.setBackgroundColor(getResources().getColor(R.color.active_player));
	}

	protected void setInactiveById(int id) {
		TextView field = (TextView) findViewById(id);
		field.setBackgroundColor(getResources().getColor(
				R.color.inactive_player));
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

	public void showGeneralPoolRules() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://www.wpa-pool.com/web/the_rules_of_play"));
		startActivity(browserIntent);
	}

	public void showGeneralPoolRules(View view) {
		showGeneralPoolRules();
	}

	public void showStraightPoolRules(View view) {
		showStraightPoolRules();
	}

	private void showStraightPoolRules() {
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
