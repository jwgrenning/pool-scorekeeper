package net.grenning.pool_scorekeeper.cowboy;

import net.grenning.pool_scorekeeper.R;
import net.grenning.pool_scorekeeper.R.layout;
import net.grenning.pool_scorekeeper.R.menu;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class CowboyPoolStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// SharedPreferences preferences =
		// PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("junk", 142);
		editor.putString("Name", "Harneet");
		editor.commit();
		int value = preferences.getInt("junk", 42);
		Log.d(this.getClass().getName(), "getString("
				+ "junk" + ", " + 42 + ") = " + value);
		editor.commit();
		String svalue = preferences.getString("Name", "default name");
		Log.d(this.getClass().getName(), "getString(" + "Name ) = " + svalue);
		setContentView(R.layout.activity_cowboy_pool_start);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_cowboy_pool_start, menu);
		return true;
	}

}
