package net.grenning.pool_scorekeeper;

import net.grenning.pool_scorekeeper.cowboy.CowboyPoolStartActivity;
import net.grenning.pool_scorekeeper.straight_pool.StartGameActivity;
import net.grenning.pool_scorekeeper.straight_pool.StraightPoolStore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseGame extends PoolActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_game);
		if (savedInstanceState == null && StraightPoolStore.hasUnfinishedGame(this)) {
			startActivity(StraightPoolStore.scoreboardIntent(this, true));
		}
	}

	public void launchStraightPoolScreen(View view) {
		startActivity(new Intent(this, StartGameActivity.class));
	}

	public void launchCowboyPoolScreen(View view) {
		startActivity(new Intent(this, CowboyPoolStartActivity.class));
	}
}
