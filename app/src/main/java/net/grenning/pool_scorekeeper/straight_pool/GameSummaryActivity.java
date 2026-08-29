package net.grenning.pool_scorekeeper.straight_pool;

import net.grenning.pool_scorekeeper.PoolActivity;
import net.grenning.pool_scorekeeper.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

public class GameSummaryActivity extends PoolActivity {

	public static final String EXTRA_SUBJECT = "summarySubject";
	public static final String EXTRA_BODY = "summaryBody";

	private String subject;
	private String body;

	public static Intent intent(Context context, String subject, String body) {
		Intent intent = new Intent(context, GameSummaryActivity.class);
		intent.putExtra(EXTRA_SUBJECT, subject);
		intent.putExtra(EXTRA_BODY, body);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_summary);

		subject = getIntent().getStringExtra(EXTRA_SUBJECT);
		body = getIntent().getStringExtra(EXTRA_BODY);
		if (subject == null) {
			subject = getString(R.string.game_summary);
		}
		if (body == null) {
			body = "";
		}

		MaterialToolbar toolbar = findViewById(R.id.summaryToolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(v -> finish());

		((TextView) findViewById(R.id.summaryBody)).setText(body);
	}

	public void shareButtonClicked(View view) {
		shareSummary(this, subject, body);
	}

	static void shareSummary(Context context, String subject, String body) {
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/plain");
		send.putExtra(Intent.EXTRA_SUBJECT, subject);
		send.putExtra(Intent.EXTRA_TEXT, body);
		try {
			context.startActivity(Intent.createChooser(send, context.getString(R.string.share)));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(context, R.string.no_apps_to_share, Toast.LENGTH_SHORT).show();
		}
	}
}
