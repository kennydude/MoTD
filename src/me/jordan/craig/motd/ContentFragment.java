package me.jordan.craig.motd;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.widget.*;
import me.jordan.craig.models.Post;
import me.jordan.craig.utils.Utils;

import org.lucasr.smoothie.AsyncListView;
import org.lucasr.smoothie.ItemLoader;
import org.lucasr.smoothie.ItemManager;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;
import uk.co.senab.bitmapcache.CacheableImageView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

/**
 * News Feed
 * @author kennydude
 *
 */
public class ContentFragment extends Fragment {
	private View FragmentView;
	
	private AsyncListView news_feed;
	private ProgressBar ShowProgress;
	public PostAdapter postAdapter;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentView = inflater.inflate(R.layout.content_layout, container, false);
		news_feed = (AsyncListView) FragmentView.findViewById(R.id.list);
		return FragmentView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Set Smooth Loader stuff up
		ItemManager.Builder builder = new ItemManager.Builder(
				new PostItemManager(App.getInstance(getActivity()).getBitmapCache())
		);
        builder.setPreloadItemsEnabled(true).setPreloadItemsCount(5);
        builder.setThreadPoolSize(4);
		news_feed.setItemManager(builder.build());
		
		// Set adapter
		postAdapter = new PostAdapter(getActivity());
		news_feed.setAdapter(postAdapter);
		
		// Show load dialog
		ShowProgress = (ProgressBar) getView().findViewById(R.id.progress);
		ShowProgress.setVisibility(View.VISIBLE);
		
		new LoadFeedTask().execute("http://www.marchofthedroids.co.uk/feed/");
		news_feed.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(postAdapter.getItem(position).getUrl()));
				startActivity(intent);
			}
		});
	}
	
	public static class ViewHolder{
		public ViewHolder(View master){
			title = (TextView) master.findViewById(R.id.title);
			description = (TextView) master.findViewById(R.id.details);
			thumbnail = (CacheableImageView) master.findViewById(R.id.thumb);
		}
		
		public TextView title, description;
		public CacheableImageView thumbnail;
	}
	
	public class PostItemManager extends ItemLoader<String, CacheableBitmapDrawable> {
		public PostItemManager(BitmapLruCache mCache){
			this.mCache = mCache;
		}
		BitmapLruCache mCache;

		@Override
		public void displayItem(View itemView, CacheableBitmapDrawable result,
				boolean from_memory) {
			ViewHolder holder = (ViewHolder) itemView.getTag();
			if(result != null){
				holder.thumbnail.setImageDrawable(result);
			}
		}

		@Override
		public String getItemParams(Adapter adapter, int pos) {
			return ((Post)adapter.getItem(pos)).getThumbnail();
		}

		@Override
		public CacheableBitmapDrawable loadItem(String url) {
			CacheableBitmapDrawable wrapper = mCache.get(url);
	        if (wrapper == null) {
	            wrapper = mCache.put(url, Utils.loadImage(url));
	        }

	        return wrapper;
		}

		@Override
		public CacheableBitmapDrawable loadItemFromMemory(String url) {
			return mCache.getFromMemoryCache(url);
		}

	}
	
	public class PostAdapter extends ArrayAdapter<Post>{
		public PostAdapter(Context context) {
			super(context, -1);
		}
		
		@Override
		public View getView( int pos, View convertView, ViewGroup parent ){
			Post post = getItem(pos);
			ViewHolder holder;
			
			if(convertView == null){
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_layout, null);
				holder = new ViewHolder(convertView);
				
				convertView.setTag(holder);
			} else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.title.setText(post.getTitle());
			holder.description.setText(post.getPubDate());
			
			return convertView;
		}
	}
	
	class LoadFeedTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {

			SAXHelper sh = null;
			try {
				sh = new SAXHelper(urls[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			sh.parseContent("");
			return "";

		}

		protected void onPostExecute(String s) {
			postAdapter.notifyDataSetInvalidated();
			ShowProgress.setVisibility(View.GONE);
		}
	}
	
	class SAXHelper {
		public HashMap<String, String> userList = new HashMap<String, String>();
		private URL url2;

		public SAXHelper(String url1) throws MalformedURLException {
			this.url2 = new URL(url1);
		}

		public RSSHandler parseContent(String parseContent) {
			RSSHandler df = new RSSHandler();
			try {

				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				xr.setContentHandler(df);
				xr.parse(new InputSource(url2.openStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return df;
		}
	}

	class RSSHandler extends DefaultHandler {

		private Post currentPost = new Post();
		StringBuffer chars = new StringBuffer();

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts) {

			chars = new StringBuffer();
			if (localName.equalsIgnoreCase("item")) {

			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (localName.equalsIgnoreCase("title")
					&& currentPost.getTitle() == null) {
				currentPost.setTitle(chars.toString());

			}
			if (localName.equalsIgnoreCase("pubDate")
					&& currentPost.getPubDate() == null) {
				currentPost.setPubDate(chars.toString());

			}
			if (localName.equalsIgnoreCase("thumbnail")
					&& currentPost.getThumbnail() == null) {
				currentPost.setThumbnail(chars.toString());

			}
			if (localName.equalsIgnoreCase("link")
					&& currentPost.getUrl() == null) {
				currentPost.setUrl(chars.toString());
			}

			if (localName.equalsIgnoreCase("item")) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						postAdapter.add(currentPost);
					}
				});
				currentPost = new Post();
			}

		}

		@Override
		public void characters(char ch[], int start, int length) {
			chars.append(new String(ch, start, length));
		}

	}

}
