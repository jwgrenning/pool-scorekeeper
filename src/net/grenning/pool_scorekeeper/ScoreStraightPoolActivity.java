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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_score_straight_pool);
		
		setPlayerName("player1Name", (EditText)findViewById(R.id.player1Name));
		setPlayerName("player2Name", (EditText)findViewById(R.id.player2Name));
	}
	
	private void setPlayerName(String player, EditText playerText) {
		String p1 = getIntent().getStringExtra(player);
		if (p1.equals(""))
			playerText.setText(R.string.default_player1Name);
		else
			playerText.setText(p1);
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
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
    			Uri.parse("http://www.wpa-pool.com/web/index.asp?id=119&pagetype=rules"));
    	startActivity(browserIntent);
    }

}
