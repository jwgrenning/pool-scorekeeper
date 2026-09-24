package net.grenning.pool_scorekeeper;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.google.android.material.button.MaterialButton;

/**
 * MaterialButton that keeps a custom {@code android:background} (glossy pool balls).
 * MaterialButton normally replaces XML backgrounds with its own shape drawable.
 */
public class PoolBallButton extends MaterialButton {

	private Drawable ballBackground;
	private boolean applyingBallBackground;

	public PoolBallButton(Context context) {
		super(context);
		init(context, null);
	}

	public PoolBallButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public PoolBallButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.background });
			ballBackground = a.getDrawable(0);
			a.recycle();
		}
		applyBallBackground();
	}

	private void applyBallBackground() {
		if (ballBackground == null) {
			return;
		}
		if (ballBackground.getConstantState() != null) {
			ballBackground = ballBackground.getConstantState().newDrawable().mutate();
		} else {
			ballBackground = ballBackground.mutate();
		}
		applyingBallBackground = true;
		setSupportBackgroundTintList(null);
		super.setBackgroundDrawable(ballBackground);
		applyingBallBackground = false;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		applyBallBackground();
	}

	@Override
	public void setSupportBackgroundTintList(ColorStateList tint) {
		if (ballBackground != null) {
			super.setSupportBackgroundTintList(null);
			return;
		}
		super.setSupportBackgroundTintList(tint);
	}

	@Override
	public void setBackgroundDrawable(Drawable background) {
		if (ballBackground != null && !applyingBallBackground) {
			// Ignore MaterialButton replacing our glossy ball.
			return;
		}
		super.setBackgroundDrawable(background);
	}
}
