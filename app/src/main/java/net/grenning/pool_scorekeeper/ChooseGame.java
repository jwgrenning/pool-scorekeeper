package net.grenning.pool_scorekeeper;

import net.grenning.pool_scorekeeper.cowboy.CowboyPoolStartActivity;
import net.grenning.pool_scorekeeper.straight_pool.StartGameActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseGame extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_game);
	}

	public void launchStraightPoolScreen(View view) {
		startActivity(new Intent(this, StartGameActivity.class));
	}

	public void launchCowboyPoolScreen(View view) {
		startActivity(new Intent(this, CowboyPoolStartActivity.class));
	}
}
