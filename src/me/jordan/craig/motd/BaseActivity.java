package me.jordan.craig.motd;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.MenuItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

public class BaseActivity extends SherlockFragmentActivity {
	ViewPager pager;
	TabsAdapter ta;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pager = new ViewPager(this);
        pager.setId(3843989);
		setContentView(pager);
		
		ActionBar ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ta = new TabsAdapter(this, pager);
		ta.addTab(ab.newTab().setText(R.string.tab_countdown), CountdownFragment.class, null);
		ta.addTab(ab.newTab().setText(R.string.tab_news), ContentFragment.class, null);
		ta.addTab(ab.newTab().setText(R.string.tab_chat), ChatFragment.class, null);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		pager.setCurrentItem( getIntent().getIntExtra("page", sp.getInt("last_page", 0) ) );
	}

	@Override
	public void onStop(){
		super.onStop();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		sp.edit().putInt("last_page", pager.getCurrentItem()).commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}


}
