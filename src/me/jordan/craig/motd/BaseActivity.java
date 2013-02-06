package me.jordan.craig.motd;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.*;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SlidingMenu sm = getSlidingMenu();
		setBehindContentView(R.layout.sliding_menu_layout);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.actionbar_home_width);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		FragmentTransaction menu = getSupportFragmentManager().beginTransaction();
		menu.add(R.id.frame, new MenuFragment());
		menu.commit();
		
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.frame_content, new ContentFragment())
		.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				toggle();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (getSlidingMenu().isMenuShowing()) {
			getSlidingMenu().showContent();
		}
	}


}
