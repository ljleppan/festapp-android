package com.futurice.festapp;

import java.util.List;

import com.futurice.festapp.dao.NewsDAO;
import com.futurice.festapp.domain.News;
import com.futurice.festapp.ui.NewsAdapter;
import com.futurice.festapp.util.CalendarUtil;
import com.futurice.festapp.util.StringUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.futurice.festapp.R;

/**
 * View for showing a list of News-articles.
 * 
 * @author Pyry-Samuli Lahti / Futurice
 */
public class NewsListActivity extends Activity {
	
	private ListView newsList;
	private List<News> articles;
	
	private OnItemClickListener newsArticleClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int index, long arg) {
			Object o = av.getItemAtPosition(index);
			if (o instanceof News) {
				News article = (News) o;
				if (StringUtil.isNotEmpty(article.getContent())) {
					Intent i = new Intent(getBaseContext(), NewsContentActivity.class);
					i.putExtra("news.title", article.getTitle());
					i.putExtra("news.date", CalendarUtil.formatDateToUIString(article.getTime()));
					i.putExtra("news.content", article.getContent());
					startActivity(i);
					return;
				}
			}
			Toast.makeText(v.getContext(), getString(R.string.newsActivity_invalidUrl), Toast.LENGTH_LONG).show();
		}
	};
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		createNewsList();
	}
	
	private void createNewsList() {
		newsList = (ListView) findViewById(R.id.newsList);
		
		articles = NewsDAO.getAll(this);
	    
		newsList.setAdapter(new NewsAdapter(this, articles));
	    newsList.setOnItemClickListener(newsArticleClickListener);
	}
	
}
