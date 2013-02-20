package me.jordan.craig.motd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
//import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

@SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
@Deprecated
public class MenuFragment extends SherlockListFragment {

	private ListView lv;
	private View FragmentView;
    TextView user;
    long diff;
    long milliseconds;
    long endTime;
	private String[] options = new String[] { "News", "Settings", "About" };

	public MenuFragment() {
	}

	public String getUserProfileImageUrl() {
		String u = null;		
		return u;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentView = inflater.inflate(R.layout.menu_list, container, false);
		ImageView member = (ImageView) FragmentView.findViewById(R.id.member_image);
		user = (TextView) FragmentView.findViewById(R.id.user_name);
		
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        formatter.setLenient(false);


        String oldTime = "09.03.2013, 09:00";
        Date oldDate;
        try {
            oldDate = formatter.parse(oldTime);
             milliseconds = oldDate.getTime();

            //long startTime = System.currentTimeMillis();
            // do your work...
            long endTime=System.currentTimeMillis();

             diff = endTime-milliseconds;       

            Log.e("day", "miliday"+diff);
            long seconds = (long) (diff / 1000) % 60 ;
            Log.e("secnd", "miliday"+seconds);
            long minutes = (long) ((diff / (1000*60)) % 60);
            Log.e("minute", "miliday"+minutes);
            long hours   = (long) ((diff / (1000*60*60)) % 24);
            Log.e("hour", "miliday"+hours);
            long days = (int)((diff / (1000*60*60*24)) % 365);
            Log.e("days", "miliday"+days);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Long serverUptimeSeconds = (System.currentTimeMillis() - milliseconds) / 1000;


            String serverUptimeText = String.format("%d days %d hours %d minutes %d seconds",
            serverUptimeSeconds / 86400,
            ( serverUptimeSeconds % 86400) / 3600 ,
            ((serverUptimeSeconds % 86400) % 3600 ) / 60,
            ((serverUptimeSeconds % 86400) % 3600 ) % 60
            );


        Log.v("jjj", "miliday"+serverUptimeText);
        CountdownTimer counter = new CountdownTimer(milliseconds,1000);
        counter.start();
		
		//UrlImageViewHelper.setUrlDrawable(member, "http://www.marchofthedroids.co.uk/wp-content/uploads/2012/08/cropped-web_banner.png");
		return FragmentView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lv = getListView();		
		MenuAdapter adapter = new MenuAdapter(getActivity());
		adapter.add(new MenuItem((new Fragment()), options[0], android.R.drawable.ic_menu_more));
		adapter.add(new MenuItem((new Fragment()), options[1], android.R.drawable.ic_menu_view));
		adapter.add(new MenuItem((new Fragment()), options[2], android.R.drawable.ic_menu_info_details));
		lv.setAdapter(adapter);			
	}


	private class MenuItem {
		public Fragment frag;
		public String tag;
		public int iconRes;

		public MenuItem(Fragment frag, String tag, int iconRes) {
			this.frag = frag; 
			this.tag = tag; 
			this.iconRes = iconRes;
		}


		@SuppressWarnings("unused")
		public Fragment getFragment() {
			return this.frag;
		}

		public String getTag() {
			return this.tag;
		}

		public int getDrawable() {
			return this.iconRes;
		}
	}

	public class MenuAdapter extends ArrayAdapter<MenuItem> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);

			MenuItem m = getItem(position);

			title.setText(m.getTag());
			icon.setImageResource(m.getDrawable());


			return convertView;
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, final int pos, long id) {
		//Bundle args = new Bundle();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		switch(pos) {
		case 0:
			transaction.replace(R.id.frame_content, new ContentFragment());
			transaction.addToBackStack(null);
			transaction.commit();
			break;
		case 1:
			transaction.replace(R.id.frame_content, new SettingsFragment());
			transaction.addToBackStack(null);
			transaction.commit();
			break;
		case 2:
			transaction.replace(R.id.frame_content, new AboutFragment());
			transaction.addToBackStack(null);
			transaction.commit();
			break;
		}

	}
	
	public class CountdownTimer extends CountDownTimer {
	    public CountdownTimer(long millisInFuture, long countDownInterval) {
	        super(millisInFuture, countDownInterval);
	    }

	    @Override
	    public void onFinish() {
	        user.setText("done!");
	    }

	    @Override
	    public void onTick(long millisUntilFinished) {
	        //tv.setText("Left: " + millisUntilFinished / 1000);

	        long diff = endTime - millisUntilFinished; 
	        Log.e("left", "miliday"+diff);
	        //Log.e("hour", "miliday"+hours);
	        int days = (int)((diff / (1000*60*60*24)) % 365);
	        Log.v("days", "miliday"+days);


	        Long serverUptimeSeconds = 
	                (System.currentTimeMillis() - millisUntilFinished) / 1000;


	            String serverUptimeText = 
	            String.format("%d : %d : %d : %d",
	            serverUptimeSeconds / 86400,
	            ( serverUptimeSeconds % 86400) / 3600 ,
	            ((serverUptimeSeconds % 86400) % 3600 ) / 60,
	            ((serverUptimeSeconds % 86400) % 3600 ) % 60
	            );  

	            Log.v("new its", "miliday"+serverUptimeText);

	         // tv.setText(days +":"+hours+":"+minutes + ":" + seconds);

	            user.setText(serverUptimeText);
	    }
	}

}
