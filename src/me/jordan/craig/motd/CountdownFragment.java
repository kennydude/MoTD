package me.jordan.craig.motd;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CountdownFragment extends Fragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.countdown_fragment, container, false);
	}


	@Override
	public void onStart(){
		super.onStart();

		TextView countdown = (TextView)getView().findViewById(R.id.countdown);

		Calendar eventDate = new GregorianCalendar();
		eventDate.set(Calendar.YEAR, 2013);
		eventDate.set(Calendar.MONTH, 3);
		eventDate.set(Calendar.DAY_OF_MONTH,  9);
		eventDate.set(Calendar.HOUR, 12);

		Calendar now = Calendar.getInstance();

		// This is ugly, but meh
		if(now.get(Calendar.DAY_OF_MONTH) == eventDate.get(Calendar.DAY_OF_MONTH)){
			// TODAY
			int hours = eventDate.get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY);
			if(hours < 0 ){
				countdown.setText("NOW");
			} else{
				countdown.setText(hours + " hours");
			}
		} else{
			// A few days away
			int days = eventDate.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH) - 1; // we don't count today
			if(days < 0 ){
				countdown.setText("gone :(");
			} else{
				countdown.setText( days + " days");
			}
		}
	}
}
