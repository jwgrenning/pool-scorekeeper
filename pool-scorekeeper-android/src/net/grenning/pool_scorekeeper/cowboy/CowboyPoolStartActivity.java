package net.grenning.pool_scorekeeper.cowboy;

import net.grenning.pool_scorekeeper.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TabHost;

public class CowboyPoolStartActivity extends Activity {

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// // SharedPreferences preferences =
	// //
	// PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	// SharedPreferences preferences = PreferenceManager
	// .getDefaultSharedPreferences(getApplicationContext());
	// SharedPreferences.Editor editor = preferences.edit();
	// editor.putInt("junk", 142);
	// editor.putString("Name", "Harneet");
	// editor.commit();
	// int value = preferences.getInt("junk", 42);
	// Log.d(this.getClass().getName(), "getString("
	// + "junk" + ", " + 42 + ") = " + value);
	// editor.commit();
	// String svalue = preferences.getString("Name", "default name");
	// Log.d(this.getClass().getName(), "getString(" + "Name ) = " + svalue);
	// setContentView(R.layout.activity_cowboy_pool_start);
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_cowboy_pool_start);

		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("score");
//		spec.setContent(R.layout.activity_score_straight_pool_2);
		spec.setContent(R.id.tab1);
		spec.setIndicator("Analog Clock");
		tabs.addTab(spec);
		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.digitalClock1);
		spec.setIndicator("Digital Clock");
		tabs.addTab(spec);
		tabs.setCurrentTab(0);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cowboy_pool_start, menu);
		return true;
	}

}
