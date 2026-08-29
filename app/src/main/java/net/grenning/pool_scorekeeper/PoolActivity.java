package net.grenning.pool_scorekeeper;

import androidx.appcompat.app.AppCompatActivity;

public abstract class PoolActivity extends AppCompatActivity {

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			ImmersiveMode.hideSystemBars(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		ImmersiveMode.hideSystemBars(this);
	}
}
