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
	private final Paint bead5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint bead10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint highlight5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint highlight10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final RectF beadRect = new RectF();
	private final LinearInterpolator slideInterpolator = new LinearInterpolator();

	private int score;
	private int raceTo = 50;
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
		bead5Paint.setColor(getResources().getColor(R.color.bead_5, getContext().getTheme()));
		bead5Paint.setStyle(Paint.Style.FILL);
		bead10Paint.setColor(getResources().getColor(R.color.bead_10, getContext().getTheme()));
		bead10Paint.setStyle(Paint.Style.FILL);

		strokePaint.setColor(getResources().getColor(R.color.bead_stroke, getContext().getTheme()));
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeWidth(0.6f * density);

		highlightPaint.setColor(getResources().getColor(R.color.bead_highlight, getContext().getTheme()));
		highlightPaint.setStyle(Paint.Style.FILL);
		highlight5Paint.setColor(getResources().getColor(R.color.bead_highlight_5, getContext().getTheme()));
		highlight5Paint.setStyle(Paint.Style.FILL);
		highlight10Paint.setColor(getResources().getColor(R.color.bead_highlight_10, getContext().getTheme()));
		highlight10Paint.setStyle(Paint.Style.FILL);

		updateDescription();
	}

	public void setRaceTo(int raceTo) {
		int clamped = Math.max(0, raceTo);
		if (this.raceTo == clamped) {
			return;
		}
		this.raceTo = clamped;
		updateDescription();
		invalidate();
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
		int left = BeadScore.onLeft(score);
		int fifties = BeadScore.markersOnLeft(score);
		if (fifties > 0) {
			setContentDescription(fifties + " fifties and " + left + " beads");
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

		boolean settled = travel >= 1f || fromScore == toScore;
		int fromMarkers = BeadScore.markersOnLeft(settled ? score : fromScore);
		int toMarkers = BeadScore.markersOnLeft(settled ? score : toScore);
		drawMarkers(canvas, geo, fromMarkers, toMarkers, settled ? 1f : travel);

		canvas.drawLine(geo.wireStart, geo.cy, geo.wireEnd, geo.cy, wirePaint);

		if (settled) {
			drawSettled(canvas, geo, BeadScore.onLeft(score));
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
			drawSlide(canvas, geo, fromLeft, toLeft, travel);
			return;
		}

		if (increasing && toOverflow > fromOverflow) {
			int fill = BeadScore.BEADS_PER_STRING - fromLeft;
			int rest = toLeft;
			int total = fill + rest;
			if (total <= 0) {
				drawSettled(canvas, geo, toLeft);
				return;
			}
			if (fill == 0) {
				float resetEnd = 0.35f;
				if (travel < resetEnd) {
					drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, 0, travel / resetEnd);
				} else {
					drawSlide(canvas, geo, 0, rest, (travel - resetEnd) / (1f - resetEnd));
				}
				return;
			}
			float fillEnd = fill / (float) total;
			if (travel < fillEnd) {
				drawSlide(canvas, geo, fromLeft, BeadScore.BEADS_PER_STRING, travel / fillEnd);
			} else {
				float t2 = fillEnd >= 1f ? 1f : (travel - fillEnd) / (1f - fillEnd);
				drawSlide(canvas, geo, 0, rest, t2);
			}
			return;
		}

		int empty = fromLeft;
		int peel = BeadScore.BEADS_PER_STRING - toLeft;
		int total = empty + peel;
		if (total <= 0) {
			drawSettled(canvas, geo, toLeft);
			return;
		}
		float emptyEnd = empty / (float) total;
		if (empty == 0) {
			float resetEnd = 0.35f;
			if (travel < resetEnd) {
				drawSlide(canvas, geo, 0, BeadScore.BEADS_PER_STRING, travel / resetEnd);
			} else {
				drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, toLeft,
						(travel - resetEnd) / (1f - resetEnd));
			}
			return;
		}
		if (travel < emptyEnd) {
			drawSlide(canvas, geo, fromLeft, 0, travel / emptyEnd);
		} else {
			float t2 = emptyEnd >= 1f ? 1f : (travel - emptyEnd) / (1f - emptyEnd);
			drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, toLeft, t2);
		}
	}

	private void drawSettled(Canvas canvas, RackGeometry geo, int leftCount) {
		int rightCount = BeadScore.BEADS_PER_STRING - leftCount;
		for (int i = 0; i < leftCount; i++) {
			drawBead(canvas, geo.leftX(i), geo.cy, geo.radius, i + 1);
		}
		for (int i = 0; i < rightCount; i++) {
			drawBead(canvas, geo.rightX(i), geo.cy, geo.radius, rightBeadNumber(i));
		}
	}

	private void drawSlide(Canvas canvas, RackGeometry geo, int fromLeft, int toLeft, float t) {
		t = clamp01(t);
		int moving = toLeft - fromLeft;
		if (moving == 0) {
			drawSettled(canvas, geo, toLeft);
			return;
		}

		if (moving > 0) {
			int parkedRight = BeadScore.BEADS_PER_STRING - toLeft;
			for (int i = 0; i < fromLeft; i++) {
				drawBead(canvas, geo.leftX(i), geo.cy, geo.radius, i + 1);
			}
			for (int i = 0; i < parkedRight; i++) {
				drawBead(canvas, geo.rightX(i), geo.cy, geo.radius, rightBeadNumber(i));
			}
			for (int k = 0; k < moving; k++) {
				float startX = geo.rightX(parkedRight + moving - 1 - k);
				float endX = geo.leftX(fromLeft + k);
				drawBead(canvas, lerp(startX, endX, t), geo.cy, geo.radius, fromLeft + k + 1);
			}
			return;
		}

		moving = -moving;
		int parkedRight = BeadScore.BEADS_PER_STRING - fromLeft;
		for (int i = 0; i < toLeft; i++) {
			drawBead(canvas, geo.leftX(i), geo.cy, geo.radius, i + 1);
		}
		for (int i = 0; i < parkedRight; i++) {
			drawBead(canvas, geo.rightX(i), geo.cy, geo.radius, rightBeadNumber(i));
		}
		for (int k = 0; k < moving; k++) {
			float startX = geo.leftX(fromLeft - 1 - k);
			float endX = geo.rightX(parkedRight + moving - 1 - k);
			drawBead(canvas, lerp(startX, endX, t), geo.cy, geo.radius, fromLeft - k);
		}
	}

	private static int rightBeadNumber(int indexFromRight) {
		return BeadScore.BEADS_PER_STRING - indexFromRight;
	}

	private void drawBead(Canvas canvas, float cx, float cy, float radius, int beadNumber) {
		Paint fill = beadPaint;
		Paint highlight = highlightPaint;
		if (beadNumber % 10 == 0) {
			fill = bead10Paint;
			highlight = highlight10Paint;
		} else if (beadNumber % 5 == 0) {
			fill = bead5Paint;
			highlight = highlight5Paint;
		}
		beadRect.set(cx - radius, cy - radius, cx + radius, cy + radius);
		canvas.drawOval(beadRect, fill);
		canvas.drawOval(beadRect, strokePaint);
		float glint = radius * 0.35f;
		canvas.drawCircle(cx - radius * 0.25f, cy - radius * 0.25f, glint, highlight);
	}

	private void drawMarkers(Canvas canvas, RackGeometry geo, int fromLeft, int toLeft, float t) {
		if (geo.markerSlots <= 0) {
			return;
		}
		canvas.drawLine(geo.markerStart, geo.cy, geo.markerEnd, geo.cy, wirePaint);
		t = clamp01(t);
		fromLeft = clampCount(fromLeft, geo.markerSlots);
		toLeft = clampCount(toLeft, geo.markerSlots);
		int moving = toLeft - fromLeft;
		if (moving == 0) {
			int right = geo.markerSlots - toLeft;
			for (int i = 0; i < toLeft; i++) {
				drawBead(canvas, geo.markerLeftX(i), geo.cy, geo.markerRadius, 10 * (i + 1));
			}
			for (int i = 0; i < right; i++) {
				drawBead(canvas, geo.markerRightX(i), geo.cy, geo.markerRadius,
						10 * (geo.markerSlots - i));
			}
			return;
		}
		if (moving > 0) {
			int parkedRight = geo.markerSlots - toLeft;
			for (int i = 0; i < fromLeft; i++) {
				drawBead(canvas, geo.markerLeftX(i), geo.cy, geo.markerRadius, 10 * (i + 1));
			}
			for (int i = 0; i < parkedRight; i++) {
				drawBead(canvas, geo.markerRightX(i), geo.cy, geo.markerRadius,
						10 * (geo.markerSlots - i));
			}
			for (int k = 0; k < moving; k++) {
				float startX = geo.markerRightX(parkedRight + moving - 1 - k);
				float endX = geo.markerLeftX(fromLeft + k);
				drawBead(canvas, lerp(startX, endX, t), geo.cy, geo.markerRadius,
						10 * (fromLeft + k + 1));
			}
			return;
		}
		moving = -moving;
		int parkedRight = geo.markerSlots - fromLeft;
		for (int i = 0; i < toLeft; i++) {
			drawBead(canvas, geo.markerLeftX(i), geo.cy, geo.markerRadius, 10 * (i + 1));
		}
		for (int i = 0; i < parkedRight; i++) {
			drawBead(canvas, geo.markerRightX(i), geo.cy, geo.markerRadius,
					10 * (geo.markerSlots - i));
		}
		for (int k = 0; k < moving; k++) {
			float startX = geo.markerLeftX(fromLeft - 1 - k);
			float endX = geo.markerRightX(parkedRight + moving - 1 - k);
			drawBead(canvas, lerp(startX, endX, t), geo.cy, geo.markerRadius, 10 * (fromLeft - k));
		}
	}

	private RackGeometry geometry() {
		float density = getResources().getDisplayMetrics().density;
		float padY = 1.5f * density;
		float padEnd = 4f * density;
		float cy = getHeight() / 2f;
		int markerSlots = BeadScore.markerSlots(raceTo);
		float markerStart = getPaddingLeft();
		float markerEnd = markerStart;
		float markerRadius = 0f;
		float markerStep = 0f;
		float gap = 0f;
		if (markerSlots > 0) {
			float markerDiameter = Math.min(getHeight() - 2f * padY, 6f * density);
			markerRadius = markerDiameter / 2f;
			markerStep = markerDiameter * (1f + SPACING_RATIO);
			markerEnd = markerStart + markerDiameter + (markerSlots - 1) * markerStep;
			gap = 8f * density;
		}
		float wireStart = markerEnd + gap;
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
		geo.markerSlots = markerSlots;
		geo.markerStart = markerStart;
		geo.markerEnd = markerEnd;
		geo.markerRadius = markerRadius;
		geo.markerStep = markerStep;
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

	private static int clampCount(int count, int max) {
		if (count < 0) {
			return 0;
		}
		if (count > max) {
			return max;
		}
		return count;
	}

	private static final class RackGeometry {
		float wireStart;
		float wireEnd;
		float cy;
		float radius;
		float step;
		int markerSlots;
		float markerStart;
		float markerEnd;
		float markerRadius;
		float markerStep;

		float leftX(int index) {
			return wireStart + radius + index * step;
		}

		float rightX(int indexFromRight) {
			return wireEnd - radius - indexFromRight * step;
		}

		float markerLeftX(int index) {
			return markerStart + markerRadius + index * markerStep;
		}

		float markerRightX(int indexFromRight) {
			return markerEnd - markerRadius - indexFromRight * markerStep;
		}
	}
}
