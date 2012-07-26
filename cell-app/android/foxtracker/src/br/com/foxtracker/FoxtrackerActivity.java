package br.com.foxtracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.commonsware.cwac.locpoll.LocationPoller;

public class FoxtrackerActivity extends Activity {
	private static final int PERIOD = 900000; // 15 minutes
	private PendingIntent pi = null;
	private AlarmManager mgr = null;

	private FoxTrackerSQLiteHelper dbHelper;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent i = new Intent(this, LocationPoller.class);
		i.putExtra(LocationPoller.EXTRA_INTENT, new Intent(this,
				LocationReceiver.class));
		i.putExtra(LocationPoller.EXTRA_PROVIDER, LocationManager.GPS_PROVIDER);
		pi = PendingIntent.getBroadcast(this, 0, i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(), PERIOD, pi);
		Log.d("FoxtrackerActivit", "Location polling every 15 minutes begun");
	}

	public void omgPleaseStop(View v) {
		mgr.cancel(pi);
		finish();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		dbHelper=new FoxTrackerSQLiteHelper(this);
	}
	
}