package me.jordan.craig.utils;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import me.jordan.craig.motd.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Not required.
 */
@Deprecated
public class EfficientAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<Post> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoaderConfiguration config;
	ViewHolder holder;
	Context cx;

	public EfficientAdapter(Activity a, ArrayList<Post> d) {

		activity = a;
		cx = a.getApplicationContext();
		data = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        config = new ImageLoaderConfiguration.Builder(cx).build();
        imageLoader.init(config);
	}
	
	@Override
	public int getCount() {
		return data.toArray().length;

	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public static class ViewHolder {
		public TextView label;
		public TextView addr;
		public ImageView image;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.post_layout, null);
			holder = new ViewHolder();
			holder.label = (TextView) vi.findViewById(R.id.title);
			holder.addr = (TextView) vi.findViewById(R.id.details);
			holder.image = (ImageView) vi.findViewById(R.id.thumb);
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		holder.label.setText(data.get(position).getTitle());
		holder.addr.setText(data.get(position).getPubDate());
		
		if (!(holder.image == null)) {
			imageLoader.displayImage((data.get(position).getThumbnail()), holder.image);
		} else {
			imageLoader.displayImage("http://camisetamassa.com.br/images/camisetas/mee/016/VETORES/NO%20RAGE.png", holder.image);
		}
		return vi;
	}

}

