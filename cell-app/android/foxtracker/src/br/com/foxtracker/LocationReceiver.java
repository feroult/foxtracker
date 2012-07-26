/***
  Copyright (c) 2010 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package br.com.foxtracker;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.locpoll.LocationPoller;

public class LocationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		Log.d("location receiver", new Date().toString());

		Bundle b = intent.getExtras();
		Location loc = (Location) b.get(LocationPoller.EXTRA_LOCATION);
		String msg;

		if (loc == null) {
			loc = (Location) b.get(LocationPoller.EXTRA_LASTKNOWN);

			if (loc == null) {
				msg = intent.getStringExtra(LocationPoller.EXTRA_ERROR);
			} else {
				msg = "TIMEOUT, lastKnown=" + loc.toString();
			}
		} else {
			msg = loc.toString();
		}

		if (msg == null) {
			msg = "Invalid broadcast received!";
		} else {
			FoxTrackerSQLiteHelper dbHelper = new FoxTrackerSQLiteHelper(
					context);
			List<String> l = dbHelper.getAllTracks();
			Log.d("location receiver", "tamanho da lista atual" + l.size());
			for (String s : l) {
				Log.d("location receiver", s);
			}
			if(l.size()>10 && isConnected(context)){
				Log.d("location receiver", "send to server !! Size " + l.size());
			}
			
			TrackData d = new TrackData();
			d.setLatitude(String.valueOf(loc.getLatitude()));
			d.setLongitude(String.valueOf(loc.getLongitude()));
			d.setStatus(TrackDataStatus.NOT_SENT_YET.toString());
			d.setTimestamp(Calendar.getInstance().getTime());
			
			dbHelper.addTrackData(d);
		}

		Log.d("location receiver", msg);

	}

	private static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}
}