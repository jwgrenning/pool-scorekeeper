package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class BeadRackView extends View {

	private static final long SLIDE_MS = 500;
	private static final int MAX_ANIMATED_POINTS = 15;
	private static final float SPACING_RATIO = 0.18f;

	private final Paint wirePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint beadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint overflowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final RectF beadRect = new RectF();
	private final LinearInterpolator slideInterpolator = new LinearInterpolator();

	private int score;
	private int fromScore;
	private int toScore;
	private float travel = 1f;
	private boolean allowAnimation;
	private ValueAnimator animator;

	public BeadRackView(Context context) {
		super(context);
		init();
	}

	public BeadRackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BeadRackView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		float density = getResources().getDisplayMetrics().density;
		wirePaint.setColor(getResources().getColor(R.color.bead_wire, getContext().getTheme()));
		wirePaint.setStrokeWidth(density);
		wirePaint.setStyle(Paint.Style.STROKE);
		wirePaint.setStrokeCap(Paint.Cap.ROUND);

		beadPaint.setColor(getResources().getColor(R.color.bead, getContext().getTheme()));
		beadPaint.setStyle(Paint.Style.FILL);

		strokePaint.setColor(getResources().getColor(R.color.bead_stroke, getContext().getTheme()));
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeWidth(0.6f * density);

		highlightPaint.setColor(getResources().getColor(R.color.bead_highlight, getContext().getTheme()));
		highlightPaint.setStyle(Paint.Style.FILL);

		overflowPaint.setColor(getResources().getColor(R.color.ivory, getContext().getTheme()));
		overflowPaint.setTextSize(9f * density);
		overflowPaint.setTextAlign(Paint.Align.LEFT);
		overflowPaint.setFakeBoldText(true);

		updateDescription();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		post(() -> allowAnimation = true);
	}

	@Override
	protected void onDetachedFromWindow() {
		cancelSlide();
		super.onDetachedFromWindow();
	}

	public void setScore(int score) {
		if (this.score == score && (animator == null || !animator.isRunning())) {
			return;
		}
		int from = this.score;
		cancelSlide();
		this.score = score;
		updateDescription();

		boolean canAnimate = allowAnimation && isLaidOut() && getWidth() > 0
				&& from != score
				&& Math.abs(score - from) <= MAX_ANIMATED_POINTS;
		if (!canAnimate) {
			fromScore = score;
			toScore = score;
			travel = 1f;
			invalidate();
			return;
		}

		fromScore = from;
		toScore = score;
		travel = 0f;
		animator = ValueAnimator.ofFloat(0f, 1f);
		animator.setDuration(SLIDE_MS);
		animator.setInterpolator(slideInterpolator);
		animator.addUpdateListener(animation -> {
			travel = (float) animation.getAnimatedValue();
			invalidate();
		});
		final ValueAnimator running = animator;
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (BeadRackView.this.animator != running) {
					return;
				}
				travel = 1f;
				fromScore = toScore;
				BeadRackView.this.animator = null;
				invalidate();
			}
		});
		animator.start();
	}

	private void cancelSlide() {
		if (animator != null) {
			animator.cancel();
			animator = null;
		}
	}

	private void updateDescription() {
		int overflow = BeadScore.overflowPoints(score);
		int left = BeadScore.onLeft(score);
		if (overflow > 0) {
			setContentDescription(overflow + " plus " + left + " beads");
		} else {
			setContentDescription(left + " beads");
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		RackGeometry geo = geometry();
		if (geo == null) {
			return;
		}

		canvas.drawLine(geo.wireStart, geo.cy, geo.wireEnd, geo.cy, wirePaint);

		if (travel >= 1f || fromScore == toScore) {
			drawSettled(canvas, geo, BeadScore.onLeft(score), BeadScore.overflowPoints(score));
			return;
		}

		drawTraveling(canvas, geo);
	}

	private void drawTraveling(Canvas canvas, RackGeometry geo) {
		int fromLeft = BeadScore.onLeft(fromScore);
		int toLeft = BeadScore.onLeft(toScore);
		int fromOverflow = BeadScore.overflowPoints(fromScore);
		int toOverflow = BeadScore.overflowPoints(toScore);
		boolean increasing = toScore > fromScore;

		if (fromOverflow == toOverflow) {
			drawSlide(canvas, geo, fromLeft, toLeft, fromOverflow, travel);
			return;
		}

		if (increasing && toOverflow > fromOverflow) {
			int fill = BeadScore.BEADS_PER_STRING - fromLeft;
			int rest = toLeft;
			int total = fill + rest;
			if (total <= 0) {
				drawSettled(canvas, geo, toLeft, toOverflow);
				return;
			}
			if (fill == 0) {
				float resetEnd = 0.35f;
				if (travel < resetEnd) {
					drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, 0, fromOverflow,
							travel / resetEnd);
				} else {
					drawSlide(canvas, geo, 0, rest, toOverflow,
							(travel - resetEnd) / (1f - resetEnd));
				}
				return;
			}
			float fillEnd = fill / (float) total;
			if (travel < fillEnd) {
				drawSlide(canvas, geo, fromLeft, BeadScore.BEADS_PER_STRING, fromOverflow,
						travel / fillEnd);
			} else {
				float t2 = fillEnd >= 1f ? 1f : (travel - fillEnd) / (1f - fillEnd);
				drawSlide(canvas, geo, 0, rest, toOverflow, t2);
			}
			return;
		}

		int empty = fromLeft;
		int peel = BeadScore.BEADS_PER_STRING - toLeft;
		int total = empty + peel;
		if (total <= 0) {
			drawSettled(canvas, geo, toLeft, toOverflow);
			return;
		}
		float emptyEnd = empty / (float) total;
		if (empty == 0) {
			float resetEnd = 0.35f;
			if (travel < resetEnd) {
				drawSlide(canvas, geo, 0, BeadScore.BEADS_PER_STRING, toOverflow,
						travel / resetEnd);
			} else {
				drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, toLeft, toOverflow,
						(travel - resetEnd) / (1f - resetEnd));
			}
			return;
		}
		if (travel < emptyEnd) {
			drawSlide(canvas, geo, fromLeft, 0, fromOverflow, travel / emptyEnd);
		} else {
			float t2 = emptyEnd >= 1f ? 1f : (travel - emptyEnd) / (1f - emptyEnd);
			drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, toLeft, toOverflow, t2);
		}
	}

	private void drawSettled(Canvas canvas, RackGeometry geo, int leftCount, int overflow) {
		drawOverflow(canvas, geo, overflow);
		int rightCount = BeadScore.BEADS_PER_STRING - leftCount;
		for (int i = 0; i < leftCount; i++) {
			drawBead(canvas, geo.leftX(i), geo.cy, geo.radius);
		}
		for (int i = 0; i < rightCount; i++) {
			drawBead(canvas, geo.rightX(i), geo.cy, geo.radius);
		}
	}

	private void drawSlide(Canvas canvas, RackGeometry geo, int fromLeft, int toLeft, int overflow,
			float t) {
		drawOverflow(canvas, geo, overflow);
		t = clamp01(t);
		int moving = toLeft - fromLeft;
		if (moving == 0) {
			drawSettled(canvas, geo, toLeft, overflow);
			return;
		}

		if (moving > 0) {
			int parkedRight = BeadScore.BEADS_PER_STRING - toLeft;
			for (int i = 0; i < fromLeft; i++) {
				drawBead(canvas, geo.leftX(i), geo.cy, geo.radius);
			}
			for (int i = 0; i < parkedRight; i++) {
				drawBead(canvas, geo.rightX(i), geo.cy, geo.radius);
			}
			for (int k = 0; k < moving; k++) {
				float startX = geo.rightX(parkedRight + moving - 1 - k);
				float endX = geo.leftX(fromLeft + k);
				drawBead(canvas, lerp(startX, endX, t), geo.cy, geo.radius);
			}
			return;
		}

		moving = -moving;
		int parkedRight = BeadScore.BEADS_PER_STRING - fromLeft;
		for (int i = 0; i < toLeft; i++) {
			drawBead(canvas, geo.leftX(i), geo.cy, geo.radius);
		}
		for (int i = 0; i < parkedRight; i++) {
			drawBead(canvas, geo.rightX(i), geo.cy, geo.radius);
		}
		for (int k = 0; k < moving; k++) {
			float startX = geo.leftX(fromLeft - 1 - k);
			float endX = geo.rightX(parkedRight + moving - 1 - k);
			drawBead(canvas, lerp(startX, endX, t), geo.cy, geo.radius);
		}
	}

	private void drawBead(Canvas canvas, float cx, float cy, float radius) {
		beadRect.set(cx - radius, cy - radius, cx + radius, cy + radius);
		canvas.drawOval(beadRect, beadPaint);
		canvas.drawOval(beadRect, strokePaint);
		float highlight = radius * 0.35f;
		canvas.drawCircle(cx - radius * 0.25f, cy - radius * 0.25f, highlight, highlightPaint);
	}

	private void drawOverflow(Canvas canvas, RackGeometry geo, int overflow) {
		if (overflow <= 0) {
			return;
		}
		String label = "+" + overflow;
		canvas.drawText(label, getPaddingLeft(),
				geo.cy - (overflowPaint.ascent() + overflowPaint.descent()) / 2f, overflowPaint);
	}

	private RackGeometry geometry() {
		float density = getResources().getDisplayMetrics().density;
		float padY = 1.5f * density;
		float padEnd = 4f * density;
		float cy = getHeight() / 2f;
		float overflowReserve = overflowPaint.measureText("+150") + 6f * density;
		float wireStart = getPaddingLeft() + overflowReserve;
		float wireEnd = getWidth() - getPaddingRight() - padEnd;
		float available = Math.max(0f, wireEnd - wireStart);
		float packed = BeadScore.BEADS_PER_STRING + (BeadScore.BEADS_PER_STRING - 1) * SPACING_RATIO;
		float diameter = available / packed / 2f;
		diameter = Math.min(diameter, getHeight() - 2f * padY);
		if (diameter <= 0f) {
			return null;
		}
		RackGeometry geo = new RackGeometry();
		geo.cy = cy;
		geo.wireStart = wireStart;
		geo.wireEnd = wireEnd;
		geo.radius = diameter / 2f;
		geo.step = diameter * (1f + SPACING_RATIO);
		return geo;
	}

	private static float lerp(float start, float end, float t) {
		return start + (end - start) * t;
	}

	private static float clamp01(float t) {
		if (t < 0f) {
			return 0f;
		}
		if (t > 1f) {
			return 1f;
		}
		return t;
	}

	private static final class RackGeometry {
		float wireStart;
		float wireEnd;
		float cy;
		float radius;
		float step;

		float leftX(int index) {
			return wireStart + radius + index * step;
		}

		float rightX(int indexFromRight) {
			return wireEnd - radius - indexFromRight * step;
		}
	}
}
