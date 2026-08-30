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
	private static final int MAX_ANIMATED_POINTS = 50;
	private static final float SPACING_RATIO = 0.18f;

	private enum PendingMotion {
		NONE, BANK, DUMP
	}

	private final Paint wirePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint beadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint bead5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint bead10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint highlight5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint highlight10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint winBeadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint winHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint separatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final RectF beadRect = new RectF();
	private final LinearInterpolator slideInterpolator = new LinearInterpolator();

	private int score;
	private int pending;
	private int maxRace = 50;
	private int spot;
	private boolean winBead;
	private boolean winBeadScored;
	private int fromScore;
	private int toScore;
	private float travel = 1f;
	private boolean allowAnimation;
	private ValueAnimator animator;
	private PendingMotion pendingMotion = PendingMotion.NONE;
	private int motionBanked;
	private int motionPending;

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
		winBeadPaint.setColor(getResources().getColor(R.color.bead_win, getContext().getTheme()));
		winBeadPaint.setStyle(Paint.Style.FILL);
		winHighlightPaint.setColor(getResources().getColor(R.color.bead_win_highlight, getContext().getTheme()));
		winHighlightPaint.setStyle(Paint.Style.FILL);

		separatorPaint.setColor(getResources().getColor(R.color.bead_wire, getContext().getTheme()));
		separatorPaint.setStrokeWidth(2f * density);
		separatorPaint.setStyle(Paint.Style.STROKE);
		separatorPaint.setStrokeCap(Paint.Cap.ROUND);

		updateDescription();
	}

	public void setRack(int maxRace, int spot) {
		setRack(maxRace, spot, false);
	}

	public void setRack(int maxRace, int spot, boolean winBead) {
		int nextMax = Math.max(0, maxRace);
		int nextSpot = Math.max(0, spot);
		if (this.maxRace == nextMax && this.spot == nextSpot && this.winBead == winBead) {
			return;
		}
		this.maxRace = nextMax;
		this.spot = nextSpot;
		this.winBead = winBead;
		updateDescription();
		invalidate();
	}

	public void setWinBeadScored(boolean scored) {
		if (winBeadScored == scored) {
			return;
		}
		boolean animate = scored && allowAnimation && isLaidOut() && getWidth() > 0;
		winBeadScored = scored;
		updateDescription();
		if (animate && (animator == null || !animator.isRunning())) {
			fromScore = score;
			toScore = score;
			startTravelAnimator();
			return;
		}
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
		setBankedAndPending(score, 0);
	}

	public void setBankedAndPending(int banked, int pending) {
		int newPending = Math.max(0, pending);
		int oldBanked = this.score;
		int oldPending = this.pending;
		PendingMotion motion = PendingMotion.NONE;
		if (allowAnimation && isLaidOut() && getWidth() > 0 && oldPending > 0 && newPending == 0) {
			if (banked == oldBanked + oldPending) {
				motion = PendingMotion.BANK;
			} else if (banked == oldBanked) {
				motion = PendingMotion.DUMP;
			}
		}
		this.pending = newPending;
		if (motion != PendingMotion.NONE) {
			cancelSlide();
			this.score = banked;
			updateDescription();
			pendingMotion = motion;
			motionBanked = oldBanked;
			motionPending = oldPending;
			fromScore = oldBanked;
			toScore = banked;
			startTravelAnimator();
			return;
		}
		if (this.score == banked && (animator == null || !animator.isRunning())) {
			invalidate();
			return;
		}
		int from = this.score;
		cancelSlide();
		this.score = banked;
		updateDescription();

		boolean canAnimate = allowAnimation && isLaidOut() && getWidth() > 0
				&& from != banked
				&& Math.abs(banked - from) <= MAX_ANIMATED_POINTS;
		if (!canAnimate) {
			fromScore = banked;
			toScore = banked;
			travel = 1f;
			invalidate();
			return;
		}

		fromScore = from;
		toScore = banked;
		startTravelAnimator();
	}

	private void startTravelAnimator() {
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
				pendingMotion = PendingMotion.NONE;
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
		pendingMotion = PendingMotion.NONE;
	}

	private int visual(int actualScore) {
		return BeadScore.visualScore(actualScore, spot);
	}

	private void updateDescription() {
		int visual = visual(score);
		int left = BeadScore.onesOnLeft(visual, maxRace);
		int fifties = BeadScore.markersOnLeft(visual, maxRace);
		String description;
		if (fifties > 0) {
			description = fifties + " fifties and " + left + " beads";
		} else {
			description = left + " beads";
		}
		if (winBead) {
			description += winBeadScored ? ", cue-ball shot made" : ", cue-ball shot remaining";
		}
		setContentDescription(description);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		RackGeometry geo = geometry();
		if (geo == null) {
			return;
		}

		drawSeparator(canvas, geo);

		if (pendingMotion != PendingMotion.NONE && travel < 1f) {
			int visual = visual(motionBanked);
			drawMarkers(canvas, geo, BeadScore.markersOnLeft(visual, maxRace),
					BeadScore.markersOnLeft(visual, maxRace), 1f);
			canvas.drawLine(geo.wireStart, geo.cy, geo.wireEnd, geo.cy, wirePaint);
			drawPendingMotion(canvas, geo);
			drawWinBead(canvas, geo);
			return;
		}

		boolean settled = pending > 0 || travel >= 1f || fromScore == toScore;
		if (settled) {
			int visual = visual(score);
			int leftOnes = BeadScore.onesOnLeft(visual, maxRace);
			drawMarkers(canvas, geo, BeadScore.markersOnLeft(visual, maxRace),
					BeadScore.markersOnLeft(visual, maxRace), 1f);
			canvas.drawLine(geo.wireStart, geo.cy, geo.wireEnd, geo.cy, wirePaint);
			drawSettled(canvas, geo, leftOnes);
			drawWinBead(canvas, geo);
			return;
		}

		drawTraveling(canvas, geo);
		drawWinBead(canvas, geo);
	}

	private void drawTraveling(Canvas canvas, RackGeometry geo) {
		int fromV = visual(fromScore);
		int toV = visual(toScore);
		int fromOnes = BeadScore.onesOnLeft(fromV, maxRace);
		int toOnes = BeadScore.onesOnLeft(toV, maxRace);
		int fromM = BeadScore.markersOnLeft(fromV, maxRace);
		int toM = BeadScore.markersOnLeft(toV, maxRace);
		boolean increasing = toV > fromV;

		canvas.drawLine(geo.wireStart, geo.cy, geo.wireEnd, geo.cy, wirePaint);

		if (fromM == toM) {
			drawMarkers(canvas, geo, fromM, toM, 1f);
			drawSlide(canvas, geo, fromOnes, toOnes, travel);
			return;
		}

		if (increasing && toM > fromM) {
			int fill = fromOnes == 0 ? 0 : BeadScore.BEADS_PER_STRING - fromOnes;
			int rest = toOnes;
			if (fill == 0) {
				if (travel < 0.4f) {
					drawMarkers(canvas, geo, fromM, fromM, 1f);
					drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, 0, travel / 0.4f);
				} else {
					float t2 = (travel - 0.4f) / 0.6f;
					drawMarkers(canvas, geo, fromM, toM, t2);
					drawSlide(canvas, geo, 0, rest, t2);
				}
				return;
			}
			float fillEnd = 0.45f;
			if (travel < fillEnd) {
				drawMarkers(canvas, geo, fromM, fromM, 1f);
				drawSlide(canvas, geo, fromOnes, BeadScore.BEADS_PER_STRING, travel / fillEnd);
			} else {
				float t2 = (travel - fillEnd) / (1f - fillEnd);
				drawMarkers(canvas, geo, fromM, toM, t2);
				drawSlide(canvas, geo, 0, rest, t2);
			}
			return;
		}

		if (fromOnes == 0) {
			if (travel < 0.6f) {
				float t1 = travel / 0.6f;
				drawMarkers(canvas, geo, fromM, toM, t1);
				drawSlide(canvas, geo, 0, BeadScore.BEADS_PER_STRING, t1);
			} else {
				float t2 = (travel - 0.6f) / 0.4f;
				drawMarkers(canvas, geo, toM, toM, 1f);
				drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, toOnes, t2);
			}
			return;
		}
		if (travel < 0.55f) {
			float t1 = travel / 0.55f;
			drawMarkers(canvas, geo, fromM, toM, t1);
			drawSlide(canvas, geo, fromOnes, 0, t1);
		} else {
			float t2 = (travel - 0.55f) / 0.45f;
			drawMarkers(canvas, geo, toM, toM, 1f);
			drawSlide(canvas, geo, BeadScore.BEADS_PER_STRING, toOnes, t2);
		}
	}

	private void drawSeparator(Canvas canvas, RackGeometry geo) {
		if (geo.markerSlots <= 0) {
			return;
		}
		float peg = geo.markerRadius > 0f ? geo.markerRadius : geo.radius * 0.35f;
		float top = geo.cy - peg * 2.2f;
		float bottom = geo.cy + peg * 2.2f;
		canvas.drawLine(geo.separatorX, top, geo.separatorX, bottom, separatorPaint);
		canvas.drawCircle(geo.separatorX, geo.cy, peg, bead10Paint);
		canvas.drawCircle(geo.separatorX, geo.cy, peg, strokePaint);
	}

	private void drawSettled(Canvas canvas, RackGeometry geo, int leftCount) {
		int midCount = Math.min(pending, Math.max(0, BeadScore.BEADS_PER_STRING - leftCount));
		int rightCount = BeadScore.BEADS_PER_STRING - leftCount - midCount;
		for (int i = 0; i < leftCount; i++) {
			drawBead(canvas, geo.leftX(i), geo.cy, geo.radius, i + 1);
		}
		for (int i = 0; i < rightCount; i++) {
			drawBead(canvas, geo.rightX(i), geo.cy, geo.radius, rightBeadNumber(i));
		}
		for (int i = 0; i < midCount; i++) {
			drawBead(canvas, pendingX(geo, leftCount, midCount, rightCount, i), geo.cy, geo.radius,
					leftCount + i + 1);
		}
	}

	private void drawPendingMotion(Canvas canvas, RackGeometry geo) {
		int leftCount = BeadScore.onesOnLeft(visual(motionBanked), maxRace);
		int midCount = Math.min(motionPending, Math.max(0, BeadScore.BEADS_PER_STRING - leftCount));
		int rightCount = BeadScore.BEADS_PER_STRING - leftCount - midCount;
		float t = clamp01(travel);
		for (int i = 0; i < leftCount; i++) {
			drawBead(canvas, geo.leftX(i), geo.cy, geo.radius, i + 1);
		}
		for (int i = 0; i < rightCount; i++) {
			drawBead(canvas, geo.rightX(i), geo.cy, geo.radius, rightBeadNumber(i));
		}
		for (int k = 0; k < midCount; k++) {
			float startX = pendingX(geo, leftCount, midCount, rightCount, k);
			float endX;
			if (pendingMotion == PendingMotion.BANK) {
				endX = geo.leftX(leftCount + k);
			} else {
				endX = geo.rightX(rightCount + (midCount - 1 - k));
			}
			drawBead(canvas, lerp(startX, endX, t), geo.cy, geo.radius, leftCount + k + 1);
		}
	}

	private float pendingX(RackGeometry geo, int leftCount, int midCount, int rightCount, int index) {
		float gapStart = leftCount == 0 ? geo.wireStart + geo.radius
				: geo.leftX(leftCount - 1) + geo.step;
		float gapEnd = rightCount == 0 ? geo.wireEnd - geo.radius
				: geo.rightX(rightCount - 1) - geo.step;
		float width = midCount <= 1 ? 0f : (midCount - 1) * geo.step;
		float start = (gapStart + gapEnd - width) / 2f;
		return start + index * geo.step;
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

	private void drawWinBead(Canvas canvas, RackGeometry geo) {
		if (!geo.winBead) {
			return;
		}
		canvas.drawLine(geo.winWireStart, geo.cy, geo.winWireEnd, geo.cy, wirePaint);
		float t = 0f;
		if (winBeadScored) {
			boolean running = animator != null && animator.isRunning();
			t = running ? clamp01(travel) : 1f;
		}
		float cx = lerp(geo.winRightX, geo.winLeftX, t);
		beadRect.set(cx - geo.radius, geo.cy - geo.radius, cx + geo.radius, geo.cy + geo.radius);
		canvas.drawOval(beadRect, winBeadPaint);
		canvas.drawOval(beadRect, strokePaint);
		float glint = geo.radius * 0.35f;
		canvas.drawCircle(cx - geo.radius * 0.25f, geo.cy - geo.radius * 0.25f, glint, winHighlightPaint);
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
		int markerSlots = BeadScore.markerSlots(maxRace);
		float left = getPaddingLeft();
		float maxBead = Math.max(0f, getHeight() - 2f * padY);
		float winReserve = 0f;
		if (winBead) {
			winReserve = Math.max(maxBead, 12f * density) + 10f * density;
		}
		float right = getWidth() - getPaddingRight() - padEnd - winReserve;
		float available = Math.max(0f, right - left);
		float onesPacked = BeadScore.BEADS_PER_STRING
				+ (BeadScore.BEADS_PER_STRING - 1) * SPACING_RATIO;
		float markerStart = left;
		float markerEnd = left;
		float markerRadius = 0f;
		float markerStep = 0f;
		float separatorX = left;
		float wireStart = left;
		if (markerSlots > 0) {
			float gap = 10f * density;
			float markerShare = Math.min(available * 0.10f, Math.max(40f * density, available * 0.08f));
			markerEnd = markerStart + markerShare;
			separatorX = markerEnd + gap / 2f;
			wireStart = markerEnd + gap;
		}
		float wireEnd = right;
		float onesAvailable = Math.max(0f, wireEnd - wireStart);
		float diameter = onesAvailable / onesPacked / 1.15f;
		diameter = Math.min(diameter, maxBead);
		if (diameter <= 0f) {
			return null;
		}
		if (markerSlots > 0) {
			markerRadius = diameter / 2f;
			markerStep = diameter * (1f + SPACING_RATIO);
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
		geo.separatorX = separatorX;
		geo.winBead = winBead;
		if (winBead) {
			float gap = 10f * density;
			geo.winWireStart = wireEnd + gap;
			geo.winWireEnd = getWidth() - getPaddingRight() - padEnd;
			geo.winLeftX = geo.winWireStart + geo.radius;
			geo.winRightX = geo.winWireEnd - geo.radius;
		}
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
		float separatorX;
		boolean winBead;
		float winWireStart;
		float winWireEnd;
		float winLeftX;
		float winRightX;

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
