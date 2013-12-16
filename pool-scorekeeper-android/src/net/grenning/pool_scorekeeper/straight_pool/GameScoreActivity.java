package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.AndroidGameFieldSaver;
import net.grenning.pool_scorekeeper.NameValueSaver;
import net.grenning.pool_scorekeeper.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameScoreActivity extends Activity {
	GameScorer scorer;
	PlayerScorer player1Scorer;
	PlayerScorer player2Scorer;
	SharedPreferences prefs;
	private NameValueSaver gameSaver;
	
	GameView gameView = new GameView() {
		
		@Override
		public void inning(int inning) {
			String count = Integer.toString(inning);
			String message = getString(R.string.inning_title) + count;
			setFieldById(R.id.inning, message);
		}

		@Override
		public void suggestRerack() {
			showRerackSuggestion();
		}
		
		@Override
		public void oneBallOnTheTable() {
			String message = getString(R.string.one_ball_left_on_the_table);
			setFieldById(R.id.ballsOnTheTable, message);
		}
		@Override
		public void ballsOnTheTable(int balls) {
			String count = Integer.toString(balls);
			String message = count + " " + getString(R.string.balls_left_on_the_table);
			setFieldById(R.id.ballsOnTheTable, message);
		}

		@Override
		public void gameOverApplause() {
			MediaPlayer mp = MediaPlayer.create(getApplicationContext(), net.grenning.pool_scorekeeper.R.raw.applause);	
			mp.start();
		}

		@Override
		public void theWinnerIs(int playerNumber) {
			setBlinkingByPlayerNumber(playerNumber);
		}

	};
	PlayerView player1View = new PlayerView() {

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

		@Override
		public void currentRun(int count) {
			setFieldById(R.id.player1CurrentRun, count);			
		}

		@Override
		public void longestRun(int count) {
			setFieldById(R.id.player1LongestRun, count);			
		}

		@Override
		public void safesMade(int count) {
			setFieldById(R.id.player1SafesMade, count);			
		}

		@Override
		public void safesMissed(int count) {
			setFieldById(R.id.player1SafesMissed, count);			
		}

		@Override
		public void consecutiveSafes(int count) {
		}
	};

	PlayerView player2View = new PlayerView() {

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

		@Override
		public void currentRun(int count) {
			setFieldById(R.id.player2CurrentRun, count);			
		}

		@Override
		public void longestRun(int count) {
			setFieldById(R.id.player2LongestRun, count);			
		}

		@Override
		public void safesMade(int count) {
			setFieldById(R.id.player2SafesMade, count);			
		}

		@Override
		public void safesMissed(int count) {
			setFieldById(R.id.player2SafesMissed, count);			
		}

		@Override
		public void consecutiveSafes(int count) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(this.getClass().getName(), ".onCreate()");
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_score_straight_pool_2);

		setPlayerName("player1Name", R.id.player1Name,
				R.string.default_player1Name);
		setPlayerName("player2Name", R.id.player2Name,
				R.string.default_player2Name);

		player1Scorer = new PlayerScorer(player1View,
				getNumberFieldFromIntent("player1PointsToWin"));
		player2Scorer = new PlayerScorer(player2View,
				getNumberFieldFromIntent("player2PointsToWin"));
		
		scorer = new GameScorer(gameView, player1Scorer, player2Scorer);

		setFieldById(R.id.player2Score, 0);
		setFieldById(R.id.player2BallsThisRack, 0);
		setFieldById(R.id.player2PointsToWin,
				getNumberFieldFromIntent("player2PointsToWin"));
		setFieldById(R.id.player2ConsecutiveFouls, 0);
		setFieldById(R.id.player2TotalFouls, 0);
	}
	
	@Override
	protected void onStart() {
		Log.d(this.getClass().getName(), ".onStart()");
		super.onStart();
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		gameSaver = new AndroidGameFieldSaver(prefs);
		if (getBooleanFieldFromIntent("resume"))
			scorer.populateFromPersistence(gameSaver);
		scorer.save(gameSaver);
		scorer.populateFromPersistence(gameSaver);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(this.getClass().getName(), ".onPause()");
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		gameSaver = new AndroidGameFieldSaver(prefs);
		scorer.save(gameSaver);
		scorer.populateFromPersistence(gameSaver);
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
		Toast.makeText(this, "Game summary details\n", Toast.LENGTH_LONG).show();	
	}

	private void showRerackSuggestion() {
		Toast.makeText(this, "Maybe you should rerack", Toast.LENGTH_LONG).show();	
	}

	private void sendEmailSummary() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"James"});
		i.putExtra(Intent.EXTRA_SUBJECT, "game summary subject");
		i.putExtra(Intent.EXTRA_TEXT   , "body of game summary");
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}	}

	protected void setBlinkingByPlayerNumber(int id) {
		TextView field;
		if (id == 1) 
			field = (TextView) findViewById(R.id.player1Name);
		else
			field = (TextView) findViewById(R.id.player2Name);
			
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(100); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		field.startAnimation(anim);
	}

	protected void setActiveById(int id) {
		TextView field = (TextView) findViewById(id);
		field.setBackgroundColor(getResources().getColor(R.color.active_player));
	}

	protected void setInactiveById(int id) {
		TextView field = (TextView) findViewById(id);
		field.setBackgroundColor(getResources().getColor(
				R.color.inactive_player));
	}

	private void setFieldById(int id, String value) {
		TextView field = (TextView) findViewById(id);
		field.setText(value);
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

	private boolean getBooleanFieldFromIntent(String name) {
		return getIntent().getBooleanExtra(name, false);
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

	public void safeMadeButtonClicked(View view) {
		scorer.playerMakesSafe();
	}

	public void missedSafeButtonClicked(View view) {
		scorer.playerMissesSafe();
	}

	public void foulButtonClicked(View view) {
		scorer.foul();
	}

	public void newRackButtonClicked(View view) {
		scorer.newRack();
	}

}
