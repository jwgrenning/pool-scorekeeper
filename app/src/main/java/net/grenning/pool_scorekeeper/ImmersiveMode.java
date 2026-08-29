package net.grenning.pool_scorekeeper;

import android.app.Activity;
import android.view.Window;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

final class ImmersiveMode {

	private ImmersiveMode() {
	}

	static void hideSystemBars(Activity activity) {
		Window window = activity.getWindow();
		WindowCompat.setDecorFitsSystemWindows(window, false);
		WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
				window, window.getDecorView());
		controller.hide(WindowInsetsCompat.Type.systemBars());
		controller.setSystemBarsBehavior(
				WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
	}
}
