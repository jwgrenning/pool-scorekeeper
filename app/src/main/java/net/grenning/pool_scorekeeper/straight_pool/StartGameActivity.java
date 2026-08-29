package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import net.grenning.pool_scorekeeper.PoolActivity;

public class StartGameActivity extends PoolActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_straight_pool_start);
	}

	private void addToIntent(Intent intent, String name, int id) {
		EditText text = findViewById(id);
		intent.putExtra(name, text.getText().toString());
	}

	public void startStraightPool(View view) {
		playStraightPool(false);
	}

	public void resumeStraightPool(View view) {
		playStraightPool(true);
	}

	private void playStraightPool(boolean resume) {
		Intent intent = new Intent(this, GameScoreActivity.class);
		addToIntent(intent, "player1Name", R.id.player1Name);
		addToIntent(intent, "player2Name", R.id.player2Name);
		addToIntent(intent, "player1PointsToWin", R.id.player1PointsToWin);
		addToIntent(intent, "player2PointsToWin", R.id.player2PointsToWin);
		intent.putExtra("resume", resume);
		startActivity(intent);
	}

	private void swapText(int id1, int id2) {
		EditText text1 = findViewById(id1);
		EditText text2 = findViewById(id2);
		String s1 = text1.getText().toString();
		String s2 = text2.getText().toString();
		text1.setText(s2);
		text2.setText(s1);
	}

	public void swapPlayers(View view) {
		swapText(R.id.player1Name, R.id.player2Name);
		swapText(R.id.player1PointsToWin, R.id.player2PointsToWin);
	}
}
