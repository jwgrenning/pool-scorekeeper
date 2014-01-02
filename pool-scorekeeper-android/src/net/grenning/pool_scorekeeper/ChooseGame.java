package net.grenning.pool_scorekeeper;

import net.grenning.pool_scorekeeper.cowboy.CowboyPoolStartActivity;
import net.grenning.pool_scorekeeper.straight_pool.StartGameActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

public class ChooseGame extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_choose_game);        
    }
    
    public void launchStraightPoolScreen(View view) {
    	Intent i = new Intent( this, StartGameActivity.class );
    	startActivity( i );    }

    public void launchCowboyPoolScreen(View view) {
    	Intent i = new Intent( this, CowboyPoolStartActivity.class );
    	startActivity( i );    }
}
