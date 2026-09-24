package net.grenning.pool_scorekeeper;

import net.grenning.pool_scorekeeper.cowboy.CowboyPoolStartActivity;
import net.grenning.pool_scorekeeper.straight_pool.StartGameActivity;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class ChooseGame extends PoolActivity {

	private static final String PREFS = "pool_scorekeeper_ui";
	private static final String PIN_ASKED = "homeShortcutAsked";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_game);
		maybeOfferHomeShortcut();
	}

	public void launchStraightPoolScreen(View view) {
		startActivity(new Intent(this, StartGameActivity.class));
	}

	public void launchCowboyPoolScreen(View view) {
		startActivity(new Intent(this, CowboyPoolStartActivity.class));
	}

	private void maybeOfferHomeShortcut() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return;
		}
		if (getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(PIN_ASKED, false)) {
			return;
		}
		ShortcutManager shortcuts = getSystemService(ShortcutManager.class);
		if (shortcuts == null || !shortcuts.isRequestPinShortcutSupported()) {
			return;
		}
		Intent launch = new Intent(this, ChooseGame.class);
		launch.setAction(Intent.ACTION_MAIN);
		launch.addCategory(Intent.CATEGORY_LAUNCHER);
		ShortcutInfo pin = new ShortcutInfo.Builder(this, "launcher")
				.setShortLabel(getString(R.string.app_name))
				.setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
				.setIntent(launch)
				.build();
		getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean(PIN_ASKED, true).apply();
		shortcuts.requestPinShortcut(pin, null);
	}
}
