package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BeadRackView extends View {

	private final Paint wirePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint beadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint overflowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final RectF beadRect = new RectF();

	private int score;

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

		setScore(0);
	}

	public void setScore(int score) {
		if (this.score == score) {
			return;
		}
		this.score = score;
		int overflow = BeadScore.overflowPoints(score);
		int left = BeadScore.onLeft(score);
		if (overflow > 0) {
			setContentDescription(overflow + " plus " + left + " beads");
		} else {
			setContentDescription(left + " beads");
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int leftCount = BeadScore.onLeft(score);
		int rightCount = BeadScore.BEADS_PER_STRING - leftCount;
		int overflow = BeadScore.overflowPoints(score);

		float density = getResources().getDisplayMetrics().density;
		float padY = 1.5f * density;
		float padEnd = 4f * density;
		float cy = getHeight() / 2f;
		float wireStart = getPaddingLeft();
		if (overflow > 0) {
			String label = "+" + overflow;
			canvas.drawText(label, wireStart, cy - (overflowPaint.ascent() + overflowPaint.descent()) / 2f,
					overflowPaint);
			wireStart += overflowPaint.measureText(label) + 6f * density;
		}

		float wireEnd = getWidth() - getPaddingRight() - padEnd;
		float available = Math.max(0f, wireEnd - wireStart);
		float spacingRatio = 0.18f;
		float packed = BeadScore.BEADS_PER_STRING + (BeadScore.BEADS_PER_STRING - 1) * spacingRatio;
		float diameter = available / packed / 2f;
		diameter = Math.min(diameter, getHeight() - 2f * padY);
		if (diameter <= 0f) {
			return;
		}
		float radius = diameter / 2f;
		float step = diameter * (1f + spacingRatio);

		canvas.drawLine(wireStart, cy, wireEnd, cy, wirePaint);

		for (int i = 0; i < leftCount; i++) {
			drawBead(canvas, wireStart + radius + i * step, cy, radius);
		}
		for (int i = 0; i < rightCount; i++) {
			drawBead(canvas, wireEnd - radius - i * step, cy, radius);
		}
	}

	private void drawBead(Canvas canvas, float cx, float cy, float radius) {
		beadRect.set(cx - radius, cy - radius, cx + radius, cy + radius);
		canvas.drawOval(beadRect, beadPaint);
		canvas.drawOval(beadRect, strokePaint);
		float highlight = radius * 0.35f;
		canvas.drawCircle(cx - radius * 0.25f, cy - radius * 0.25f, highlight, highlightPaint);
	}
}
