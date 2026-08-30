package net.grenning.pool_scorekeeper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

public abstract class PoolActivity extends AppCompatActivity {

	private static final long APPLAUSE_HOLD_MS = 2000;
	private static final long APPLAUSE_FADE_MS = 500;

	private MediaPlayer applause;
	private ValueAnimator applauseFade;

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

	protected void playWinApplause() {
		stopApplause();
		MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.applause);
		if (mp == null) {
			return;
		}
		applause = mp;
		mp.setOnCompletionListener(player -> {
			if (applause == player) {
				stopApplause();
			}
		});
		mp.start();
		ValueAnimator fade = ValueAnimator.ofFloat(1f, 0f);
		fade.setStartDelay(APPLAUSE_HOLD_MS);
		fade.setDuration(APPLAUSE_FADE_MS);
		fade.addUpdateListener(animation -> {
			if (applause != mp) {
				return;
			}
			float volume = (float) animation.getAnimatedValue();
			try {
				mp.setVolume(volume, volume);
			} catch (IllegalStateException ignored) {
			}
		});
		fade.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (applause == mp) {
					stopApplause();
				}
			}
		});
		applauseFade = fade;
		fade.start();
	}

	private void stopApplause() {
		MediaPlayer mp = applause;
		applause = null;
		ValueAnimator fade = applauseFade;
		applauseFade = null;
		if (fade != null) {
			fade.cancel();
		}
		if (mp != null) {
			try {
				mp.setOnCompletionListener(null);
				if (mp.isPlaying()) {
					mp.stop();
				}
			} catch (IllegalStateException ignored) {
			}
			mp.release();
		}
	}

	@Override
	protected void onDestroy() {
		stopApplause();
		super.onDestroy();
	}
}
